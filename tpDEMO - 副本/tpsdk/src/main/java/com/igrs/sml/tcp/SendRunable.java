package com.igrs.sml.tcp;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class SendRunable implements Runnable {

    private Socket socket;
    private DataOutputStream dos;
    public String ip, dev_id;
    private boolean isExit = false;
    private LinkedBlockingQueue<byte[]> messageQueue;

    public SendRunable(Socket s, String dev_id, LinkedBlockingQueue<byte[]> messageQueue) {
        this.socket = s;
        this.dev_id = dev_id;
        this.messageQueue = messageQueue;
        try {
            L.i("SendRunable->id:" + dev_id);
            dos = new DataOutputStream(socket.getOutputStream());
            ip = socket.getInetAddress().getHostAddress();
            L.i(TcpConst.TAG, "SendRunable->id:" + dev_id+" ip:"+ip);
        } catch (Exception e) {
            e.printStackTrace();
            L.i(TcpConst.TAG, "SendRunable{}  -e:" + e.toString());
        }
    }


    @Override
    public void run() {
        L.e(TcpConst.TAG, "SendRunable start " + ip + " isExit:" + isExit+" dos:"+dos);
        int count = 0;
        while (!isExit) {
            try {
                if (dos != null) {
                    //L.i(TcpConst.TAG, "SendRunable will wait send " + ip);
                    byte[] data = messageQueue.take();
                    //L.i(TcpConst.TAG, "SendRunable will send data:" + data);
                    sendMessage(data);
                    if (messageQueue.size() > 100 && count++ % 10 == 0) {
                        L.i(TcpConst.TAG, "SendRunable send " + ip + " data=" + data.length);
                    }
                } else {
                    L.e(TcpConst.TAG, "SendRunable send " + ip + " messageQueue.size=" + messageQueue.size() + " msg->dos is null");
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e(TcpConst.TAG, "SendRunable->SendRunable:" + ip + " messageQueue.size=" + messageQueue.size() + " e:" + e.toString() + "\n" + Log.getStackTraceString(e));
                onDestroy();
            }
        }
    }

    public void sendMessage(byte[] data) throws Exception {
        if (dos != null) {
            L.i(TcpConst.TAG,new String(data));
            dos.write(data);
            dos.flush();
        } else {
            L.e(TcpConst.TAG, "SendRunable send " + ip + " messageQueue.size=" + messageQueue.size() + " msg->dos is null");
        }
    }

    public void onDestroy() {
        isExit = true;
        if (dos != null) {
            try {
                dos.close();
            } catch (IOException ie) {
            }
            dos = null;
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
