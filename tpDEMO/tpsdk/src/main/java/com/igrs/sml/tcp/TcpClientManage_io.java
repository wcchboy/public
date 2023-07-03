package com.igrs.sml.tcp;

import android.os.Build;
import android.text.TextUtils;

import com.igrs.sml.event.TcpEvent;
import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;

import org.greenrobot.eventbus.EventBus;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpClientManage_io extends Thread implements TcpManage{


    private int port = 12345;
    private String host = "192.168.3.77";
    private String dev_id = "";

    private boolean isExit = false;

    private static TcpClientManage_io instance;

    private boolean isConnect = false;
    private Object LOCK_connect = new Object();

    private ReceiveRunable receiveRunable;
    private SendRunable sendRunable;

    private TcpStateCallback tcpStateCallback;
    public LinkedBlockingQueue<byte[]> messageQueue = new LinkedBlockingQueue<>();

    public static TcpClientManage_io getInstance() {
        if (instance == null) {
            synchronized (TcpClientManage_io.class) {
                if (instance == null) {
                    instance = new TcpClientManage_io();
                }
            }
        }
        return instance;
    }

    private TcpClientManage_io() {
        super.start();
    }

    @Override
    public synchronized void start() {
    }

    public void connect(String host, int port,String dev_id) {
        this.host = host;
        this.port = port;
        this.dev_id = dev_id;
         retry();
    }

    private boolean retry() {
        try {
            disconnect();
            L.e(TcpConst.TAG, "TcpClientManage  retry--" + this.hashCode());
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            Socket socket = new Socket(host, port);
            socket.setKeepAlive(true);
            // 是否开启Nagle算法   据说这个关闭了速度可以提升1倍，但是我试几乎没有发现变化
            socket.setTcpNoDelay(true);
            // 设置接收发送缓冲器大小
            socket.setReceiveBufferSize(64 * 1024 * 1024);
            socket.setSendBufferSize(64 * 1024 * 1024);
            // 设置性能参数：短链接，延迟，带宽的相对重要性
            socket.setPerformancePreferences(2, 1, 0);
            socket.setTrafficClass(0x10);    // low delay
            socket.setOOBInline(true);//允许发送紧急数据
            sendRunable = new SendRunable(socket,dev_id,messageQueue);

            receiveRunable = new ReceiveRunable(socket,dev_id);
            receiveRunable.setTcpCallback(new TcpCallback() {
                @Override
                public void rev_msg(String dev_id,byte type, byte[] data) {
                    if (type == TcpConst.TYPY_CMD && tcpCMDCallback != null) {
                        L.e(TcpConst.TAG, "cmd rev_cmd->dev_id:" + dev_id + " cmd:" + new String(data));
                        tcpCMDCallback.rev_msg(dev_id,type, data);
                    } else if (type == TcpConst.TYPY_VIDEO && tcpVideoCallback != null) {
                        //L.i(TcpConst.TAG,"rev_cmd->ip:"+ip+" cmd:"+new String(data_buffer));
                        tcpVideoCallback.rev_msg(dev_id,type, data);
                    } else if (type == TcpConst.TYPY_AUDIO && tcpAudioCallback != null) {
                        //L.i(TcpConst.TAG,"rev_cmd->ip:"+ip+" cmd:"+new String(data_buffer));
                        tcpAudioCallback.rev_msg(dev_id,type, data);
                    } else if (type == TcpConst.TYPY_TOUCH && tcpTouchCallback != null) {
                        L.i(TcpConst.TAG, "touch rev_cmd->ip:" + dev_id + " cmd:" + new String(data));
                        tcpTouchCallback.rev_msg(dev_id,type, data);
                    }else if (type == TcpConst.TYPY_SYS && tcpSysCallback != null) {
                        L.i(TcpConst.TAG, "sys rev_cmd->ip:" + dev_id + " cmd:" + new String(data));
                        tcpSysCallback.rev_msg(dev_id,type, data);
                    }
                }

                public void stateChange(String ip, boolean isConnected) {
                    synchronized (LOCK_connect) {
                        isConnect = isConnected;
                        LOCK_connect.notify();
                    }
                }
            });

            new Thread(sendRunable).start();
            new Thread(receiveRunable).start();

            synchronized (LOCK_connect) {
                isConnect = true;
                LOCK_connect.notify();
            }
            sendSys();
            return true;
        } catch (Exception e) {
            L.e(TcpConst.TAG, "e---" + e.toString());
        }
        return false;
    }

    private void sendSys(){
        if (!TextUtils.isEmpty(dev_id)) {
            byte[] data = ("{\"cmd\":0,\"id\":\"" + dev_id + "\",\"name\":\""+ Build.BRAND + "-" + Build.MODEL + "-" + Build.VERSION.RELEASE+"\"}").getBytes();
            try {
                sendMsg(TcpConst.TYPY_SYS, data);
                L.i("sendSys ->send sys:" + new String(data)+" "+hashCode());
            } catch (Exception e) {
                e.printStackTrace();
                L.e("sendSys ->send sys e=" + e.toString());
            }
        }else{
            L.e("sendSys ->send sys e= dev id is null"+" "+hashCode());
        }
    }


    private void disconnect() {
        if (receiveRunable != null) {
            receiveRunable.onDestroy();
            receiveRunable = null;
        }
        if (sendRunable != null) {
            sendRunable.onDestroy();
            sendRunable = null;
        }
        synchronized (LOCK_connect) {
            isConnect = false;
        }
    }


    @Override
    public boolean init(String host, int port, String dev_id) {
        this.host = host;
        this.port = port;
        this.dev_id = dev_id;
        return true;
    }

    public synchronized void onDestroy() {
        L.e(TcpConst.TAG, "TcpClientManage   destroy--" + this.hashCode());
        isExit = true;
        disconnect();
        synchronized (TcpClientManage_io.class) {
            instance = null;
        }
        EventBus.getDefault().postSticky(new TcpEvent(TcpEvent.TYPE_TCP_DISCONNECT, dev_id, ""));
    }

    @Override
    public void sendCMDMessage(String dev_id, byte[] data) {
        L.e(TcpConst.TAG, "TcpClientManage cmd send_msg:" + new String(data));
        sendMsg(TcpConst.TYPY_CMD, data);
    }

    @Override
    public void sendVideoMessage(String dev_id, byte[] data) {
        sendMsg(TcpConst.TYPY_VIDEO, data);
    }

    @Override
    public void sendAudioMessage(String dev_id, byte[] data) {
        sendMsg(TcpConst.TYPY_AUDIO, data);
    }

    @Override
    public void sendTouchMsg(String dev_id, byte[] data) {
        //L.i(TcpConst.TAG, "TcpClientManage send touch length:" + data.length);
        sendMsg(TcpConst.TYPY_TOUCH, data);
    }
    private final byte[] heart = {(byte) 0};

    @Override
    public void run() {
        super.run();
        L.i(TcpConst.TAG, "TcpClientManage---run start");
        int reTryTime = 1000;
        while (!isExit) {
            try {
                synchronized (LOCK_connect) {
                    if (!isConnect) {
                        L.i(TcpConst.TAG, "TcpClientManage---run wait connect reTryTime："+reTryTime);
                        LOCK_connect.wait(reTryTime);
                        L.i(TcpConst.TAG, "TcpClientManage---run connected=" + isConnect);
                    }
                }
                try {
                    sendMsg(TcpConst.TYPY_HEARTBEAT,heart);
                    reTryTime = 1000;
                    Thread.sleep(5000);
                    L.i(TcpConst.TAG, "--------心跳--------");
                } catch (Exception e) {
                    L.e(TcpConst.TAG, "-TcpClientManage-------sockect 断开--------count:"+(reTryTime/1000));
                    reTryTime+=2000;
                    if(reTryTime>15_000){
                        break;
                    }
                    retry();
                }
            } catch (Exception e) {
            }
        }
        L.e(TcpConst.TAG, "TcpClientManage---run out\n-------\n-------\n-------\n");
        onDestroy();
    }

    private void sendMsg(byte type, byte[] data) {
        try {
            byte[] all = new byte[5 + data.length];
            all[0] = type;
            byte[] length = Common.intToByteArray(data.length);
            System.arraycopy(length, 0, all, 1, 4);
            System.arraycopy(data, 0, all, 5, data.length);
            messageQueue.put(all);
        } catch (Exception e) {
            e.printStackTrace();
            L.e(TcpConst.TAG, "TcpClientManage send e:" + e.toString());
        }
       // L.i(TcpConst.TAG, "TcpClientManage send size:" + messageQueue.size());
    }

    private TcpCallback tcpCMDCallback;
    private TcpCallback tcpVideoCallback;
    private TcpCallback tcpAudioCallback;
    private TcpCallback tcpTouchCallback;
    private TcpCallback tcpSysCallback;

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

    @Override
    public void setTcpStateCallback(TcpStateCallback tcpStateCallback) {

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

