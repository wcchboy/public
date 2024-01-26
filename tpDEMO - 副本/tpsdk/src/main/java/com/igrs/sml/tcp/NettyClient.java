package com.igrs.sml.tcp;

import static com.igrs.sml.tcp.TcpConst.tcp_time_out;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.igrs.sml.event.TcpEvent;
import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClient implements TcpManage {

    private long lostTime = 0;

    private String ser_dev_id;
    private String my_dev_id;
    private int port;
    private String host = "";
    private boolean isExit = false;

    private final byte[] heart = {(byte) 0};

    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;

    private volatile ChannelFuture channelFuture;
    private volatile Channel clientChannel;

    private static NettyClient instance;
    private String TAG = "NettyClient";

    public static NettyClient getInstance() {
        if (instance == null) {
            synchronized (NettyClient.class) {
                if (instance == null) {
                    instance = new NettyClient();
                }
            }
        }
        return instance;
    }

    private NettyClient() {
        this(-1);
    }

    private NettyClient(int threads) {
        reTryCount = 4;
        workerGroup = threads > 0 ? new NioEventLoopGroup(threads) : new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_SNDBUF, 64 * 1024 * 1024) //设置缓冲区与发送区大小
                .option(ChannelOption.SO_RCVBUF, 64 * 1024 * 1024)
                .option(ChannelOption.SO_TIMEOUT, 25000) //收发超时
                .option(ChannelOption.SO_KEEPALIVE, true)  //无阻塞
                .option(ChannelOption.TCP_NODELAY, true)  //无阻塞
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .handler(new ClientHandlerInitializer(this));
    }
    private int reTryCount = 4;
    private long reTime = 0;
    public void connectAsync() {
        L.i("connectAsync 尝试连接到服务端: "+host+":"+port+" dif:"+(System.currentTimeMillis()-reTime)/1000);

        channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener((ChannelFutureListener) future -> {
            Throwable cause = future.cause();
            if (cause != null) {
                exceptionHandler(cause);
                if(reTryCount-->0){
                    reTime = System.currentTimeMillis();
                    int delay = (5 - reTryCount) ;
                    L.i("NettyClient 等待"+delay+"s 后重连");
                    channelFuture.channel().eventLoop().schedule(this::connectAsync, 2, TimeUnit.SECONDS);
                }else{
                    L.e("NettyClient 重连接失败...  dif:"+(System.currentTimeMillis()-lostTime)/1000);
                    EventBus.getDefault().postSticky(new TcpEvent(TcpEvent.TYPE_TCP_DISCONNECT, "连接断开"));
                    if (tcpStateCallback != null) tcpStateCallback.stateChange(ser_dev_id, false);
                    onDestroy();
                }
            } else {
                clientChannel = channelFuture.channel();
                if (clientChannel != null && clientChannel.isActive()) {
                    L.i("NettyClient connectAsync started !!! {"+clientChannel.localAddress()+"} connect to server" );
                }
            }
        });

//        channelFuture.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//
//            }
//        });
    }

    private void exceptionHandler(Throwable cause) {
        if (cause instanceof ConnectException) {
            L.e("NettyClient 连接异常:{"+cause.getMessage()+"}");
        } else if (cause instanceof ClosedChannelException) {
            L.e("NettyClient connect error:{client has destroy}");
        } else {
            L.e("NettyClient connect error:"+ cause);
        }
    }

    public Channel getChannel() {
        return clientChannel;
    }

    @Override
    public boolean init(String host, int port, String dev_id) {
        this.host = host;
        this.port = port;
        this.my_dev_id = dev_id;
        L.i("NettyClient init 0 尝试连接到服务端:dev_id "+dev_id);
        L.i("NettyClient init 0 尝试连接到服务端: "+host+":"+port);
        try {
            channelFuture = bootstrap.connect(host, port);
            L.i("NettyClient init  ---1");
            boolean notTimeout = channelFuture.awaitUninterruptibly(10, TimeUnit.SECONDS);
            L.i("NettyClient init  ---2 notTimeout:"+notTimeout);
            if (notTimeout) {
                clientChannel = channelFuture.channel();
                L.i("NettyClient init  ---3 clientChannel:"+clientChannel);
                if (clientChannel != null && clientChannel.isActive()) {
                    L.i("NettyClient started !!! {" + clientChannel.localAddress() + "} connect to server");
                    return true;
                }
                L.i("NettyClient init  ---4");
                Throwable cause = channelFuture.cause();
                L.i("NettyClient init  ---5");
                if (cause != null) {
                    exceptionHandler(cause);
                }
                L.i("NettyClient init  ---6");
            } else {
                L.e("NettyClient connect remote host["+clientChannel.remoteAddress()+"] timeout "+10+"s");
            }
        } catch (Exception e) {
            L.e("NettyClient connect e:"+e.toString());
            exceptionHandler(e);
            if(clientChannel!=null){
                clientChannel.close();
                clientChannel=null;
            }
        }
        L.i("NettyClient init  ---end");
        return false;
    }

    private void sendSys(){
        Log.d(TAG, "sendSys my_dev_id:"+my_dev_id);
        if (!TextUtils.isEmpty(my_dev_id)) {
            byte[] data = ("{\"cmd\":0,\"id\":\"" + my_dev_id + "\",\"name\":\""+ Build.BRAND + "-" + Build.MODEL + "-" + Build.VERSION.RELEASE+"\"}").getBytes();
            byte[] all = new byte[5 + data.length];
            all[0] = TcpConst.TYPY_SYS;
            byte[] length = Common.intToByteArray(data.length);
            System.arraycopy(length, 0, all, 1, 4);
            System.arraycopy(data, 0, all, 5, data.length);
            try {
                clientChannel.writeAndFlush(all);
                L.i("sendSys ->send sys:" + new String(data));
            } catch (Exception e) {
                e.printStackTrace();
                L.e("sendSys ->send sys e=" + e.toString());
            }
            reTryCount = 4;
        }else{
            L.e("sendSys ->send sys e= dev id is null");
        }
    }
    @Override
    public void onDestroy() {
        L.e("NettyClient   destroy--" + this.hashCode());
        isExit = true;
        try {
            if (clientChannel != null) {
                clientChannel.close();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        } catch (Exception e) {
        }
        synchronized (NettyClient.class) {
            instance = null;
        }
    }

    @Override
    public void sendCMDMessage(String dev_id, byte[] data) {
        L.i( "client->send_msg--->" + new String(data));
        sendMsg(TcpConst.TYPY_CMD, data);
    }



    @Override
    public void sendVideoMessage(String dev_id, byte[] data) {
         sendMsg(TcpConst.TYPY_VIDEO, data);
    }

    public void sendAudioMessage(String dev_id, byte[] data) {
        //L.i(TcpConst.TAG, "NettyClient sendAudioMessage:" + data.length);
        sendMsg(TcpConst.TYPY_AUDIO, data);
    }

    @Override
    public void sendTouchMsg(String dev_id, byte[] data) {
        sendMsg(TcpConst.TYPY_TOUCH, data);
    }

    private void sendMsg(byte type, byte[] data) {
        try {
            Log.d(TAG,"sendMsg-> type:" + type+" isActive:"+clientChannel.isActive()+" isOpen:"+clientChannel.isOpen());
            byte[] all = new byte[5 + data.length];
            all[0] = type;
            byte[] length = Common.intToByteArray(data.length);
            System.arraycopy(length, 0, all, 1, 4);
            System.arraycopy(data, 0, all, 5, data.length);
            clientChannel.writeAndFlush(all).addListener((future) -> {
                if (future.isSuccess()) {
                   // L.i("write->Success type:" + type);
                } else {
                    L.i("write->FAILED type:" + type);
                }
            });
        } catch (Exception e) {
            if(clientChannel!=null){
                L.e("NettyClient send "+clientChannel.hashCode()+" e:" + e.toString());
            }
            L.e("NettyClient send e:" + e.toString());
            e.printStackTrace();

        }
    }

    private TcpCallback tcpCMDCallback;
    private TcpCallback tcpSysCallback;
    private TcpCallback tcpVideoCallback;
    private TcpCallback tcpAudioCallback;
    private TcpCallback tcpTouchCallback;
    private TcpStateCallback tcpStateCallback;
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

    public void setTcpCMDCallback(TcpCallback tcpCMDCallback) {
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

    class ClientHandlerInitializer extends ChannelInitializer<SocketChannel> {
       private NettyClient client;

        public ClientHandlerInitializer(NettyClient client) {
            this.client = client;
        }

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            L.i("NettyClient initChannel--------" + hashCode());
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new IdleStateHandler(tcp_time_out, 6_000, 0, TimeUnit.MILLISECONDS));
            pipeline.addLast(new ByteArrayEncoder());
            pipeline.addLast(new SimpleClientHandler());
        }
    }

    public class SimpleClientHandler extends ChannelInboundHandlerAdapter {
        private CompositeByteBuf compositeByteBuf;
        private Object lock_readdata = new Object();
        public SimpleClientHandler() {
            L.i("NettyClient SimpleClientHandler------构造-----" + hashCode());
            compositeByteBuf = Unpooled.compositeBuffer();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            try{
                ByteBuf buf = (ByteBuf) msg;
                analysisByteBuffer(buf);
//                try{
//                    byte[] dst = new byte[buf.readableBytes()];
//                    buf.readBytes(dst);
//                    StringBuffer sb = new StringBuffer();
//                    sb.append(" [");
//                    byte[] temp = buf.array();
//                    for (int i = 0; i < temp.length; i++) {
//                        sb.append(temp[i]+",");
//                    }
//                    sb.append(" ]");
//                    L.i(sb.toString());
//                }catch (Exception e){
//                }finally {
//
//                }
            }catch (Exception e){
                e.printStackTrace();
                L.e("channelRead----e:"+e.toString());
            }

        }
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            clientChannel = ctx.channel();
            L.i("NettyClient channelActive 连接成功 local:" + ctx.channel().localAddress());
            try{
                Thread.sleep(500);
                sendSys();
            }catch (Exception e){
            }

        }
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            L.e("NettyClient channelInactive:"+ ctx.channel().localAddress()+"  "+hashCode());
            ctx.pipeline().remove(this);
            ctx.channel().close();
            reconnectionAsync(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if (cause instanceof IOException) {
                L.e("NettyClient exceptionCaught:客户端[" + ctx.channel().localAddress() + "]和远程断开连接");
            } else {
                L.e("NettyClient exceptionCaught:客户端[" + ctx.channel().localAddress() + "] cause:" + cause.toString());//+"\n"+ Log.getStackTraceString(new Exception("test log")));
            }
            ctx.pipeline().remove(this);
            ctx.close();
            reconnectionAsync(ctx);
        }
//
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//            compositeByteBuf = Unpooled.compositeBuffer();
            L.e("handlerAdded：  hashCode:" + hashCode());
        }
//
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            L.e("连接断开 handlerRemoved   hashCode:" + hashCode());
//            ctx.pipeline().remove(this);
//            ctx.close();
//            reconnectionAsync(ctx);
        }


        private void reconnectionAsync(ChannelHandlerContext ctx) {
            if (!isExit) {
                lostTime = System.currentTimeMillis();
                ctx.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        L.i("1s之后重新建立连接");
                        if (clientChannel == null || !clientChannel.isActive()) {
                            EventBus.getDefault().postSticky(new TcpEvent(TcpEvent.TYPE_TCP_RETRY, "尝试重连"));
                            connectAsync();
                        }
                    }
                }, 1, TimeUnit.SECONDS);
            }else{
                L.i("reconnectionAsync but exit");
            }
        }
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            //L.e("Override ip:" + host + " evt:" + evt +" "+ hashCode());
            if (evt instanceof IdleStateEvent) {
                if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                    ctx.channel().writeAndFlush(heart).addListener((future) -> {
                        if (!future.isSuccess()) {
                            L.e("Override ip:" + host + " evt:" + evt + " " + hashCode() + " write->FAILED");
                            ctx.channel().close();
                        }
                    });
                } else if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                    ctx.channel().close();
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        public void analysisByteBuffer(ByteBuf attachment) {
            synchronized (lock_readdata) {
                compositeByteBuf.addComponent(true, attachment);
                //L.i(TcpConst.TAG, "Server ->attachment readableBytes:" + attachment.readableBytes() + " readableBytes:" + compositeByteBuf.readableBytes());
                while (compositeByteBuf.readableBytes() > 1) {
                    byte type = compositeByteBuf.getByte(0);
                    compositeByteBuf.readerIndex(1);
                    if (type == TcpConst.TYPY_HEARTBEAT) {
                        L.i(TcpConst.TAG, "rev-------心跳--readableBytes:"+compositeByteBuf.readableBytes()+"------>" + hashCode());
                        compositeByteBuf.discardReadBytes();
                    } else {
                        break;
                    }
                }

                while (!isExit && compositeByteBuf.readableBytes() > 5) {
                    compositeByteBuf.readerIndex(0);
                    byte type = compositeByteBuf.getByte(0);
                    compositeByteBuf.readerIndex(1);
                    if (type == TcpConst.TYPY_HEARTBEAT) {
                        L.i(TcpConst.TAG, "rev---blocksize----心跳-------->" + hashCode());
                        compositeByteBuf.discardReadBytes();
                        continue;
                    }
                    // L.i(TcpConst.TAG, "------ type:" + type + " readableBytes:" + compositeByteBuf.readableBytes() + " readerIndex:" + compositeByteBuf.readerIndex());
                    byte[] blocklength = new byte[4];
                    compositeByteBuf.readBytes(blocklength);

                    int blocksize = Common.byteArrayToInt(blocklength);
                    if (blocksize < 0 || blocksize > 7753705) {
                        //L.i(TcpConst.TAG, "------blocklength:" + blocklength[0] + " " + blocklength[1] + " " + blocklength[2] + " " + blocklength[3] + " ");
                        onDestroy();
                        L.i(TcpConst.TAG, "------错误 数据长度 blocksize:" + blocksize + " readableBytes:" + compositeByteBuf.readableBytes());
                        break;
                    }
                    if (blocksize > compositeByteBuf.readableBytes()) {
                        break;
                    } else {
                        // L.i(TcpConst.TAG, "------ 数据收够 blocksize:" + blocksize + " remaining:" + compositeByteBuf.readableBytes());
                    }

                    byte[] readbytes = new byte[blocksize];
                    compositeByteBuf.readBytes(readbytes);
                    compositeByteBuf.discardReadBytes();
                    if (type == TcpConst.TYPY_SYS || type == TcpConst.TYPY_CMD || type == TcpConst.TYPY_VIDEO || type == TcpConst.TYPY_AUDIO || type == TcpConst.TYPY_TOUCH) {
                        rev_data(type, readbytes);
                    }
                }
                // L.i(TcpConst.TAG, "---getdata end ---------readableBytes：" + compositeByteBuf.readableBytes() + "\n");
            }

        }

        private void rev_data(byte type, byte[] data) {
            //L.i(TcpConst.TAG, "ser rev_msg->typ:" + type + "   data  size=" + data.length);
            if (type == TcpConst.TYPY_SYS && tcpSysCallback != null) {
                try {
                    JSONObject json = new JSONObject(new String(data));
                    int cmd = json.getInt("cmd");
                    if (cmd == 0) {
                        String dev_id = json.getString("id");
                        ser_dev_id = json.getString("name");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e("TYPY_SYS rev_cmd->e:" + e.toString());
                }
                tcpSysCallback.rev_msg(ser_dev_id, TcpConst.TYPY_SYS, data);
            } else if (type == TcpConst.TYPY_VIDEO && tcpVideoCallback != null) {
                tcpVideoCallback.rev_msg(ser_dev_id, type, data);
            } else if (type == TcpConst.TYPY_TOUCH && tcpTouchCallback != null) {
                tcpTouchCallback.rev_msg(ser_dev_id, type, data);
            } else if (type == TcpConst.TYPY_CMD && tcpCMDCallback != null) {
                tcpCMDCallback.rev_msg(ser_dev_id, type, data);
            }
        }


    }
}