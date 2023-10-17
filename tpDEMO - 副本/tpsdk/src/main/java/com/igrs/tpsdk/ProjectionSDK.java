package com.igrs.tpsdk;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.igrs.sml.AudioManage;
import com.igrs.sml.VideoManage;
import com.igrs.sml.tcp.NettyClient;
import com.igrs.sml.tcp.TcpCallback;
import com.igrs.sml.tcp.TcpClientManage_io;
import com.igrs.sml.tcp.TcpManage;
import com.igrs.sml.tcp.TcpServerManage_netty;
import com.igrs.sml.tcp.TcpStateCallback;
import com.igrs.sml.util.L;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.LongUnaryOperator;

public class ProjectionSDK implements TcpManage {

    private final String version = "1.1.1018";
    public boolean isM2 = false;
    private String TAG ="ProjectSDK";

    //屏幕采集的数据
    public LinkedBlockingQueue<byte[]> screenH264Queue = new LinkedBlockingQueue<>();

    private static ProjectionSDK instance = null;
    public TcpManage tcpManage;

    private ProjectionSDK() {
        L.e("ProjectionSDK->new->this:  " + hashCode());
    }

    public static ProjectionSDK getInstance() {
        if (instance == null) {
            synchronized (ProjectionSDK.class) {
                if (instance == null) {
                    instance = new ProjectionSDK();
                }
            }
        }
        return instance;
    }
    /*public ProjectionSDK() {
        L.e("ProjectionSDK->new->this:  " + hashCode());
    }*/
    public boolean init(String host, int port, String dev_id) {
        L.e("ProjectionSDK->init->host:" + host + " port:" + port + " dev_id:" + dev_id + "  hashCode:" + hashCode());
        boolean result = false;
        if (!TextUtils.isEmpty(host) && port > 0) {
            Log.d(TAG, "tcpManage =NettyClient.getInstance()");
           tcpManage = NettyClient.getInstance();
            tcpManage.setTcpVideoCallback(VideoManage.getInstance());
            tcpManage.setTcpAudioCallback(AudioManage.getInstance());
            result = tcpManage.init(host, port, dev_id);
        } else {
            //接收端
            Log.d(TAG, "tcpManage =TcpServerManage_netty.getInstance()");
            tcpManage = TcpServerManage_netty.getInstance();
            tcpManage.setTcpVideoCallback(VideoManage.getInstance());
            tcpManage.setTcpAudioCallback(AudioManage.getInstance());
            result = tcpManage.init(host, port, dev_id);
        }

        /*tcpManage.setTcpVideoCallback(VideoManage.getInstance());
        tcpManage.setTcpAudioCallback(AudioManage.getInstance());*/
        return result;
    }

    /**
     * 接收端
     * @param host
     * @param port
     * @param dev_id
     * @return
     */
    @Override
    public boolean initReceiving(String host, int port, String dev_id) {
        L.e("ProjectionSDK->initReceiving->host:" + host + " port:" + port + " dev_id:" + dev_id + "  hashCode:" + hashCode());
        tcpManage = TcpServerManage_netty.getInstance();
        boolean result = tcpManage.init(host, port, dev_id);
        tcpManage.setTcpVideoCallback(VideoManage.getInstance());
        tcpManage.setTcpAudioCallback(AudioManage.getInstance());
        return result;
    }

    /**
     * 投屏端
     * @param host
     * @param port
     * @param dev_id
     * @return
     */
    @Override
    public boolean initProjection(String host, int port, String dev_id) {
        L.e("ProjectionSDK->initProjection->host:" + host + " port:" + port + " dev_id:" + dev_id + "  hashCode:" + hashCode());
        tcpManage = NettyClient.getInstance();
        boolean result = tcpManage.init(host, port, dev_id);
        tcpManage.setTcpVideoCallback(VideoManage.getInstance());
        tcpManage.setTcpAudioCallback(AudioManage.getInstance());
        return result;
    }

    @Override
    public void onDestroy() {
        if (tcpManage != null) tcpManage.onDestroy();
    }

    @Override
    public void sendCMDMessage(String dev_id, byte[] data) {
        Log.d(TAG, "sendCMDMessage");
        if (tcpManage != null) tcpManage.sendCMDMessage(dev_id, data);
    }

    @Override
    public void sendVideoMessage(String dev_id, byte[] data) {
        //L.e("ProjectionSDK->dev_id:" +dev_id+" tcpManage is null="+(tcpManage==null));
        if (tcpManage != null) tcpManage.sendVideoMessage(dev_id, data);
    }

    @Override
    public void sendAudioMessage(String dev_id, byte[] data) {
        if (tcpManage != null) tcpManage.sendAudioMessage(dev_id, data);
    }

    @Override
    public void sendTouchMsg(String dev_id, byte[] data) {
        if (tcpManage != null) tcpManage.sendTouchMsg(dev_id, data);
    }

    @Override
    public void setTcpCMDCallback(TcpCallback tcpCMDCallback) {
        Log.d(TAG, "setTcpCMDCallback tcpManage!=NULL:"+(tcpManage != null));
        if (tcpManage != null) tcpManage.setTcpCMDCallback(tcpCMDCallback);
    }

    @Override
    public void setTcpSysCallback(TcpCallback tcpSysCallback) {
        Log.d(TAG, "setTcpSysCallback tcpManage!=NULL:"+(tcpManage != null));
        if (tcpManage != null) tcpManage.setTcpSysCallback(tcpSysCallback);
    }

    @Override
    public void setTcpVideoCallback(TcpCallback tcpVideoCallback) {
        if (tcpManage != null) tcpManage.setTcpVideoCallback(tcpVideoCallback);
    }

    @Override
    public void setTcpAudioCallback(TcpCallback tcpAudioCallback) {
        if (tcpManage != null) tcpManage.setTcpAudioCallback(tcpAudioCallback);
    }

    @Override
    public void setTcpTouchCallback(TcpCallback tcpTouchCallback) {
        if (tcpManage != null) tcpManage.setTcpTouchCallback(tcpTouchCallback);
    }

    @Override
    public void setTcpStateCallback(TcpStateCallback tcpStateCallback) {
        if (tcpManage != null) tcpManage.setTcpStateCallback(tcpStateCallback);
    }

    public String getVersion() {
        return version;
    }
}
