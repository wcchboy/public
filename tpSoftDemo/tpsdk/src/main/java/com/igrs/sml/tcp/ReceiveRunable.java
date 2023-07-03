package com.igrs.sml.tcp;

import android.text.TextUtils;
import android.util.Log;

import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ReceiveRunable implements Runnable {

    private Socket socket;
    private DataInputStream dis;

    public String ip, dev_id;
    private long connectTime = 0;
    private boolean isExit = false;
    private TcpCallback tcpCallback;


    public ReceiveRunable(Socket s, String dev_id) {
        this.socket = s;
        this.dev_id = dev_id;
        try {
            ip = socket.getInetAddress().getHostAddress();
            dis = new DataInputStream(socket.getInputStream());
            connectTime = System.currentTimeMillis();
            L.i("ReceiveRunable->id:" + dev_id+" ip:"+ip);
        } catch (Exception e) {
            e.printStackTrace();
            L.i(TcpConst.TAG, "ReceiveRunable{}  -e:" + e.toString());
        }
    }

    public void setTcpCallback(TcpCallback tcpCallback) {
        this.tcpCallback = tcpCallback;
    }

    @Override
    public void run() {
        int count = 0;
        long lastTime = System.currentTimeMillis();
        while (!isExit) {
            try {
                byte type = dis.readByte();
                lastTime = System.currentTimeMillis();
                if (type == TcpConst.TYPY_HEARTBEAT) {
                    if (count++ % 2 == 0) L.i(TcpConst.TAG, "ReceiveRunable 收到心跳->" + ip+" dev_id:"+dev_id);
                    continue;
                } else if (type == TcpConst.TYPY_SYS || type == TcpConst.TYPY_CMD || type == TcpConst.TYPY_VIDEO || type == TcpConst.TYPY_AUDIO || type == TcpConst.TYPY_TOUCH) {
                    try {
                        byte[] length_buffer = new byte[4];
                        dis.readFully(length_buffer);
                        int length = Common.byteArrayToInt(length_buffer);

                        if (length > 1 && length < 7753705) {//;Integer.MAX_VALUE) {
                            byte[] data_buffer = new byte[length];
                            dis.readFully(data_buffer);
                            if (tcpCallback != null) {
                                tcpCallback.rev_msg(dev_id, type, data_buffer);
                            }
                        } else {
                            L.e(TcpConst.TAG, "ReceiveRunable " + ip + " 未知数据---length:" + length);
                        }
                    } catch (Exception e) {
                        L.e(TcpConst.TAG, "ReceiveRunable " + ip + " 未知数据---e:" + e.toString());
                        e.printStackTrace();
                    }
                } else {
                    L.e(TcpConst.TAG, "ReceiveRunable " + ip + " 未知数据---type:" + type);
                }
            } catch (Exception e) {
                onDestroy();
                break;
            }
        }
        L.e(TcpConst.TAG, "ReceiveRunable 用户 " + ip + " 断开连接" + " dif:" + (System.currentTimeMillis() - connectTime));
        if (tcpCallback != null) {
            //tcpCallback.stateChange(dev_id, false);
        }
    }
    public void onDestroy() {
        isExit = true;
        if (dis != null) {
            try {
                dis.close();
            } catch (IOException ie) {
            }
            dis = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ie) {
            }
            socket = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ie) {
            }
            socket = null;
        }
    }
}
