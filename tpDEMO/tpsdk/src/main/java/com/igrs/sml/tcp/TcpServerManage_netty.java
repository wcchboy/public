package com.igrs.sml.tcp;


import static com.igrs.sml.tcp.TcpConst.tcp_time_out;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.igrs.sml.VideoManage;
import com.igrs.sml.event.TcpEvent;
import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;


public class TcpServerManage_netty extends Thread implements TcpManage {

    private boolean isExit = false;
    private Object LOCK_INIT = new Object();
    private boolean init_result = false;
    private int port;
    private String sev_dev_id;

    //id_msgList
    private HashMap<String, DataHandlerAdapter> deviceMap = new HashMap<>();


    private static TcpServerManage_netty instance = null;

    private TcpServerManage_netty() {
        L.i(TcpConst.TAG, "TcpServerManage_netty ->hashCode:" + hashCode());
    }

    public static TcpServerManage_netty getInstance() {
        if (instance == null) {
            synchronized (TcpServerManage_netty.class) {
                if (instance == null) {
                    instance = new TcpServerManage_netty();
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized void start() {
        L.e(TcpConst.TAG, "TcpServerManage please init first");
    }


    @Override
    public void run() {
        super.run();
        try {
            L.i(TcpConst.TAG, "服务器 init---------------" + port);
            isExit = false;
            //创建两个线程，一个负责接受客户端连接 一个负责传输数据
            NioEventLoopGroup pGroup = new NioEventLoopGroup();
            NioEventLoopGroup cGroup = new NioEventLoopGroup();
            //2.创建服务器辅助类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(pGroup, cGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_SNDBUF, 64 * 1024 * 1024) //设置缓冲区与发送区大小
                    .childOption(ChannelOption.SO_RCVBUF, 64 * 1024 * 1024) //设置缓冲区与发送区大小
                    .option(ChannelOption.SO_SNDBUF, 64 * 1024 * 1024)
                    .option(ChannelOption.SO_RCVBUF, 64 * 1024 * 1024)
                    .childOption(ChannelOption.SO_TIMEOUT, 25000) //收发超时
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  //无阻塞
                    .childOption(ChannelOption.TCP_NODELAY, true)  //无阻塞
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new IdleStateHandler(tcp_time_out, 6_000, 0, TimeUnit.MILLISECONDS));
                            socketChannel.pipeline().addLast(new ByteArrayEncoder());
                            socketChannel.pipeline().addLast(new DataHandlerAdapter());
                        }
                    });
            ChannelFuture cf = serverBootstrap.bind(port).sync();
            if (cf.isSuccess()) {
                L.i(TcpConst.TAG, "服务器启动成功---------------port:" + port);
                synchronized (LOCK_INIT) {
                    init_result = true;
                    LOCK_INIT.notify();
                }
            } else {
                L.i(TcpConst.TAG, "服务器启动失败---------------");
                synchronized (LOCK_INIT) {
                    init_result = false;
                    LOCK_INIT.notify();
                }
            }
            cf.channel().closeFuture().sync();
            pGroup.shutdownGracefully();
            cGroup.shutdownGracefully();
        } catch (Exception e) {
            e.printStackTrace();
            L.i(TcpConst.TAG, "服务器启动异常---------------e:" + e.toString());
            synchronized (LOCK_INIT) {
                init_result = false;
                LOCK_INIT.notify();
            }
        }
    }


    class DataHandlerAdapter extends ChannelInboundHandlerAdapter {

        private ChannelHandlerContext channelHandlerContext;

        private Object lock_readdata = new Object();
        private String dev_id, dev_name;

        private String ip;
        private CompositeByteBuf compositeByteBuf;

        public long creTime = 0;

        @SuppressLint("HandlerLeak")
        private Handler cmdHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case -1:
                        if (deviceMap.size() == 0) {
                            break;
                        }
                        L.e("----------s-TcpServerManage_netty-cmdHandler---------size:" + deviceMap.size());
                        Iterator<Map.Entry<String, DataHandlerAdapter>> iterator = deviceMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            DataHandlerAdapter handlerAdapter = iterator.next().getValue();
                            L.i("DataHandlerAdapter -----" + " " + handlerAdapter.dev_id + " " + handlerAdapter.ip + "  " + handlerAdapter.hashCode());
                        }
                        if (!TextUtils.isEmpty(dev_id)) {
                            DataHandlerAdapter handlerAdapter = deviceMap.get(dev_id);
                            if (handlerAdapter != null) {
                                if (handlerAdapter.hashCode() == DataHandlerAdapter.this.hashCode()) {
                                    L.e("DataHandlerAdapter " + handlerAdapter.dev_id + " 超时掉线--" + handlerAdapter.ip + "---" + this.hashCode());
                                    deviceMap.remove(dev_id);
                                    if (tcpStateCallback != null)
                                        tcpStateCallback.stateChange(dev_id, false);
                                    dev_id = null;
                                } else {
                                    L.i("DataHandlerAdapter " + handlerAdapter.dev_id + " map old dev 超时掉线--" + handlerAdapter.ip + "---this:" + this.hashCode() + " dis:" + handlerAdapter.hashCode() + " ");
                                }
                            }
                        }
                        L.i("----------e-TcpServerManage_netty-cmdHandler------"+"---this:" + this.hashCode()+"---size:" + deviceMap.size());
                        break;
                    case 0:
                        if (tcpCMDCallback != null)
                            tcpCMDCallback.rev_msg(dev_id, TcpConst.TYPY_CMD, (byte[]) msg.obj);
                        break;
                    case 1:
                        if (tcpStateCallback != null) tcpStateCallback.stateChange(dev_id, true);
                        break;
                }
            }
        };


        public DataHandlerAdapter() {
            L.e("DataHandlerAdapter 构造  hashCode:" + this.hashCode());
            creTime = System.currentTimeMillis();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            L.e("我是服务器 侦测到一个不活跃的频道 ip:" + ip + " dev_id:" + dev_id + " dev_name:" + dev_name + " this hashCode:" + this.hashCode() + " ctx " + ctx.hashCode() + " dt:" + Common.dataTimeToString(creTime));
            //onDestroy();
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            this.channelHandlerContext = ctx;
            compositeByteBuf = Unpooled.compositeBuffer();
            //连接成功
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            ip = socketAddress.toString();
            String connectAddress = socketAddress.getAddress().getHostAddress();
            L.e("连接成功：" + connectAddress + " ip:" + ip + " deviceMap 共有:" + deviceMap.size() + "个连接 hashCode:" + hashCode()+" id:"+ctx.channel().id());
            cmdHandler.removeMessages(-1);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            L.e("连接断开 ip:" + ip + " dev_id:" + dev_id + " dev_name:" + dev_name + " hashCode:" + hashCode()+" id:"+ctx.channel().id());
            //断开通知
            cmdHandler.sendEmptyMessageDelayed(-1, 10000);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
           // L.e("Override ip:" + ip + " dev_id:" + dev_id + " dev_name:" + dev_name +" map:"+deviceMap.size()+ " evt:" + evt + " hashCode:" + hashCode());
            if (evt instanceof IdleStateEvent) {
                if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                    channelHandlerContext.channel().writeAndFlush(heart).addListener((future) -> {
                        if (!future.isSuccess()) {
                            L.e("writer_idle 心跳失败->dev_id:" + dev_id + " dev_name:" + dev_name+"---this:" + this.hashCode());
                            cmdHandler.sendEmptyMessage(-1);
                            ctx.channel().close();
                        }
                    });
                } else if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                    L.e("reader_idle 心跳超时 ip:" + ip + " dev_id:" + dev_id + " dev_name:" + dev_name + " hashCode:" + hashCode());
                    cmdHandler.sendEmptyMessage(-1);
                    ctx.channel().close();
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        long exceptionTime = 0;

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            //L.e("exceptionCaught ip:" + ip + " " + cause.toString() + " hashCode:" + hashCode()+"\n"+Log.getStackTraceString(new Exception("test")));
            L.e("exceptionCaught ip:" + ip + " " + cause.toString() + " hashCode:" + hashCode());
            exceptionTime = System.currentTimeMillis();
        }

        private int d_count=0;
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try{
                ByteBuf buf = (ByteBuf) msg;
                analysisByteBuffer(buf);
            }catch (Exception e){
            }
//            ByteBuf buf = (ByteBuf) msg;
//            try{
//                d_count++;
//                byte[] dst = new byte[buf.readableBytes()];
//                buf.readBytes(dst);
//                StringBuffer sb = new StringBuffer();
//                sb.append( " dev_id:" + dev_id +" "+this.hashCode()+" c:"+d_count +" [");
//                for (int i = 0; i < dst.length; i++) {
//                    sb.append(dst[i]+",");
//                }
//                sb.append(" ]");
//                L.i(sb.toString());
//            }catch (Exception e){
//                e.printStackTrace();
//                L.e("e->"+e.toString());
//            }finally {
//                buf.readerIndex(0);
//                analysisByteBuffer(buf);
//            }

        }



        public boolean write(byte[] data) {
            //ChannelFuture futureq = channelHandlerContext.channel().writeAndFlush(data);
            channelHandlerContext.channel().writeAndFlush(data).addListener((future) -> {
                if (future.isSuccess()) {
                    //L.i("write->Success "+new String(data));
                } else {
                    L.i("write->FAILED "+new String(data));
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < data.length; i++) {
                        sb.append(data[i]+" ");
                    }
                    L.touch("write->FAILED->"+sb.toString());
                }
            });
            return true;
        }

        public void onDestroy() {
            L.i("onDestroy ip:" + ip + " dev_id:" + dev_id + " dev_name:" + dev_name + " hashCode:" + hashCode());
            channelHandlerContext.channel().close();
            compositeByteBuf.clear();
            DataHandlerAdapter old = deviceMap.get(dev_id);
            if (old != null) {
                if (old.creTime == this.creTime) {
                    L.e("onDestroy ip:" + ip + " dev_id:" + dev_id + " dev_name:" + dev_name + " remove and clean hashCode:" + hashCode());
                    VideoManage.getInstance().clean(dev_id);
                    deviceMap.remove(dev_id);
                } else {
                    L.e("onDestroy ip:" + ip + " dev_id:" + dev_id + " dev_name:" + dev_name + "don't  remove and clean has new client:" + old.creTime + " dif:" + (old.creTime - creTime));
                }
            }
        }

        private final byte[] heart = {(byte) 0};

        private int data_count = 0;

        public void analysisByteBuffer(ByteBuf attachment) {
            synchronized (lock_readdata) {
                //L.i(TcpConst.TAG, "Server ->attachment readableBytes:" + attachment.readableBytes() + "allBuffer readableBytes:" + compositeByteBuf.readableBytes());
                compositeByteBuf.addComponent(true, attachment);
                //L.i(TcpConst.TAG, "Server ->allBuffer readableBytes:" + compositeByteBuf.readableBytes());
                while (compositeByteBuf.readableBytes() > 1) {
                    byte type = compositeByteBuf.getByte(0);
                    compositeByteBuf.readerIndex(1);
                    if (type == TcpConst.TYPY_HEARTBEAT) {
                        //L.i(TcpConst.TAG, "rev-------心跳-------ip:" + ip + " dev_id:" + dev_id + " dev_name:" + dev_name+" ---this:" + this.hashCode()+" id:"+channelHandlerContext.channel().id());
                        write(heart);
                        compositeByteBuf.discardReadBytes();//扔掉已读数据
                    } else {
                        break;
                    }
                }

                while (compositeByteBuf.readableBytes() > 5) {
                    compositeByteBuf.readerIndex(0);
                    byte type = compositeByteBuf.getByte(0);
                    compositeByteBuf.readerIndex(1);
                    if (type == TcpConst.TYPY_HEARTBEAT) {
                        //L.e(TcpConst.TAG, "rev---blocksize----心跳-------ip:" + ip + " dev_id:" + dev_id + " dev_name:" + dev_name + " readableBytes:" + compositeByteBuf.readableBytes()+"---this:" + this.hashCode());
                        write(heart);
                        compositeByteBuf.discardReadBytes();
                        continue;
                    }
                   byte[] blocklength = new byte[4];
                    compositeByteBuf.readBytes(blocklength);
                    int blocksize = Common.byteArrayToInt(blocklength);

                   //L.i(TcpConst.TAG, "------ type:" + type + " readableBytes:" + compositeByteBuf.readableBytes() + " readerIndex:" + compositeByteBuf.readerIndex()+" blocksize:"+blocksize+" this:"+this.hashCode());
                    if (blocksize < 0 || blocksize > 7753705) {//1803491748
                        L.e(TcpConst.TAG, data_count + " type:" + type + " sev------错误 数据长度 blocksize:" + blocksize + " blocklength:" + " " + blocklength[0] + " " + blocklength[1] + " " + blocklength[2] + " " + blocklength[3] + "  dev_id:" + dev_id+" readableBytes:"+compositeByteBuf.readableBytes());
                        try{
                            compositeByteBuf.readerIndex(0);
                            byte[] temp = compositeByteBuf.array();
                            StringBuffer sb = new StringBuffer();
                            sb.append("l:" + temp.length + " [");
                            for (int i = 0; i < temp.length; i++) {
                                sb.append(temp[i] + ",");
                            }
                            sb.append("---------\n---------");
                            byte[] temp2 = attachment.array();
                            for (int i = 0; i < temp2.length; i++) {
                                sb.append(temp2[i] + ",");
                            }
                            sb.append(" ]");
                            L.i(TcpConst.TAG, data_count + " blocksize->" + sb.toString()+" this:"+this.hashCode());
                        }catch (Exception e){
                        }

                        channelHandlerContext.channel().close();
                        compositeByteBuf.clear();
                        break;
                    }
                    if (blocksize > compositeByteBuf.readableBytes()) {
                        //L.i(TcpConst.TAG, data_count+"------ 数据未收够 break blocksize:" + blocksize + " readableBytes:" + compositeByteBuf.readableBytes() + " dif:" + (blocksize - compositeByteBuf.readableBytes())+" this:"+this.hashCode());
                        break;
                    } else {
                        data_count++;
                        //L.i(TcpConst.TAG, data_count+" type:"+type+" ------ 数据收够 blocksize:" + blocksize+" dev_id:"+dev_id+" this:"+this.hashCode());
                    }

                    byte[] readbytes = new byte[blocksize];
                    compositeByteBuf.readBytes(readbytes);
                    compositeByteBuf.discardReadBytes();
                    if (type == TcpConst.TYPY_SYS || type == TcpConst.TYPY_CMD || type == TcpConst.TYPY_VIDEO || type == TcpConst.TYPY_AUDIO || type == TcpConst.TYPY_TOUCH) {
                        rev_data(type, readbytes);
                    }
                }
                //L.i(TcpConst.TAG, "---getdata end ---------readableBytes：" + compositeByteBuf.readableBytes() + "\n");
            }
        }

        private void rev_data(byte type, byte[] data) {
            //L.i(TcpConst.TAG, "ser rev_msg->typ:" + type + " dev_id->" + dev_id + " data  size=" + data.length);
            if (type == TcpConst.TYPY_SYS) {
                L.i("TYPY_SYS rev_cmd->id:" + dev_id + " dev_name:" + dev_name + " cmd:" + new String(data) + " hashCode:" + hashCode());
                try {
                    JSONObject json = new JSONObject(new String(data));
                    int cmd = json.getInt("cmd");
                    if (cmd == 0) {
                        dev_id = json.getString("id");
                        try {
                            dev_name = json.getString("name");
                        } catch (Exception e) {
                        }
                        EventBus.getDefault().postSticky(new TcpEvent(TcpEvent.TYPE_TCP_CONNECT, dev_id, ""));
                        try{
                            DataHandlerAdapter old = deviceMap.remove(dev_id);
                            if(old!=null){
                                old.channelHandlerContext.close();
                            }
                        }catch (Exception e){
                        }
                        deviceMap.put(dev_id, this);

                        byte[] sev_data = ("{\"cmd\":0,\"id\":\"" + dev_id + "\",\"name\":\"" + sev_dev_id + "\",\"version\":\""+ ProjectionSDK.getInstance().getVersion() +"\"}").getBytes();
                        byte[] sys_data = new byte[5 + sev_data.length];
                        sys_data[0] = TcpConst.TYPY_SYS;
                        byte[] length = Common.intToByteArray(sev_data.length);
                        System.arraycopy(length, 0, sys_data, 1, 4);
                        System.arraycopy(sev_data, 0, sys_data, 5, sev_data.length);
                        L.i("cmd send_sys_msg:" + new String(sev_data) + " hashCode:" + hashCode());
                        write(sys_data);
                        cmdHandler.sendEmptyMessage(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    channelHandlerContext.channel().close();
                    compositeByteBuf.clear();
                    cmdHandler.sendEmptyMessage(-1);
                    L.e("TYPY_SYS rev_cmd->e:" + e.toString());
                }
            } else {
                if (TextUtils.isEmpty(dev_id)) {
                    L.i("rev_cmd->dev_id is null close " + " hashCode:" + hashCode());
                    onDestroy();
                }
            }

            //L.i("rev_cmd->dev_id:"+dev_id +" type:"+type+ " "+new String(data)+" tcpVideoCallback:"+tcpVideoCallback+" hashCode:" + hashCode());

            if (type == TcpConst.TYPY_VIDEO && tcpVideoCallback != null) {
                tcpVideoCallback.rev_msg(dev_id, type, data);
            } else if (type == TcpConst.TYPY_AUDIO && tcpAudioCallback != null) {
                tcpAudioCallback.rev_msg(dev_id, type, data);
            } else if (type == TcpConst.TYPY_TOUCH && tcpTouchCallback != null) {
                tcpTouchCallback.rev_msg(dev_id, type, data);
            } else if (type == TcpConst.TYPY_CMD && tcpCMDCallback != null) {
                //tcpCMDCallback.rev_msg(dev_id, TcpConst.TYPY_CMD, data);
                cmdHandler.sendMessage(cmdHandler.obtainMessage(0, data));
            }
        }
    }


    @Override
    public boolean init(String host, int port, String sev_dev_id) {
        this.port = port;
        this.sev_dev_id = sev_dev_id;
        try {
            isExit = true;
            super.start();
        } catch (Exception e) {
        }
        synchronized (LOCK_INIT) {
            if (!init_result) {
                try {
                    //L.i("tcp ser init wait....");
                    LOCK_INIT.wait(2000);
                } catch (Exception e) {
                }
            }
            //L.i("tcp ser init result = " + init_result + " \n" + Log.getStackTraceString(new Exception("test log")));
            return init_result;
        }
    }

    public void onDestroy() {
        isExit = true;
        synchronized (TcpServerManage_netty.class) {
            instance = null;
        }
    }

    public void sendCMDMessage(String dev_id, byte[] data) {
        sendMsg(TcpConst.TYPY_CMD, dev_id, data);
    }

    public void sendVideoMessage(String dev_id, byte[] data) {
        sendMsg(TcpConst.TYPY_VIDEO, dev_id, data);
    }

    public void sendAudioMessage(String dev_id, byte[] data) {
        sendMsg(TcpConst.TYPY_AUDIO, dev_id, data);
    }

    int touch_count=0;
    public void sendTouchMsg(String dev_id, byte[] data) {
        touch_count++;
        sendMsg(TcpConst.TYPY_TOUCH, dev_id, data);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            sb.append(data[i]+" ");
        }
        L.touch(touch_count+"->"+sb.toString());
    }

    private void sendMsg(byte type, String dev_id, byte[] data) {
        DataHandlerAdapter deviceClient = deviceMap.get(dev_id);//.get(dev_id);
        if (deviceClient != null) {
            if (type == TcpConst.TYPY_CMD) {
                L.i("cmd send_msg:" + new String(data) + " hashCode:" + hashCode());
            }
            byte[] all = new byte[5 + data.length];
            all[0] = type;
            byte[] length = Common.intToByteArray(data.length);
            System.arraycopy(length, 0, all, 1, 4);
            System.arraycopy(data, 0, all, 5, data.length);
            deviceClient.write(all);



        } else {
            String msg = "";
            for (Map.Entry<String, DataHandlerAdapter> entry : deviceMap.entrySet()) {
                msg += entry.getKey() + " ";
            }
            L.e("error not find " + dev_id +" type:"+type+ " deviceMap:"  + msg + " hashCode:" + hashCode());
        }
    }

    private TcpCallback tcpSysCallback;
    private TcpCallback tcpCMDCallback;
    private TcpCallback tcpVideoCallback;
    private TcpCallback tcpAudioCallback;
    private TcpCallback tcpTouchCallback;
    private TcpStateCallback tcpStateCallback;

    public void setTcpCMDCallback(TcpCallback tcpCMDCallback) {
        //L.e("netty->setTcpCMDCallback:  " + tcpCMDCallback);
        this.tcpCMDCallback = tcpCMDCallback;
    }

    @Override
    public void setTcpSysCallback(TcpCallback tcpSysCallback) {
        this.tcpSysCallback = tcpSysCallback;
    }

    public void setTcpVideoCallback(TcpCallback tcpVideoCallback) {
        this.tcpVideoCallback = tcpVideoCallback;
    }

    public void setTcpAudioCallback(TcpCallback tcpAudioCallback) {
        this.tcpAudioCallback = tcpAudioCallback;
    }

    public void setTcpTouchCallback(TcpCallback tcpTouchCallback) {
        this.tcpTouchCallback = tcpTouchCallback;
    }

    @Override
    public void setTcpStateCallback(TcpStateCallback tcpStateCallback) {
        this.tcpStateCallback = tcpStateCallback;
    }

    @Override
    public boolean initReceiving(String host, int port, String dev_id) {
        return false;
    }

    @Override
    public boolean initProjection(String host, int port, String dev_id) {
        return false;
    }

}
