package com.igrs.sml.tcp;



public interface TcpManage {
    public boolean init(String host, int port, String dev_id);
    public void onDestroy();
    public void sendCMDMessage(String dev_id, byte[] data);
    public void sendVideoMessage(String dev_id, byte[] data);
    public void sendAudioMessage(String dev_id, byte[] data);
    public void sendTouchMsg(String dev_id, byte[] data);
    public void setTcpCMDCallback(TcpCallback tcpCMDCallback);
    public void setTcpSysCallback(TcpCallback tcpSysCallback);
    public void setTcpVideoCallback(TcpCallback tcpVideoCallback);
    public void setTcpAudioCallback(TcpCallback tcpAudioCallback);
    public void setTcpTouchCallback(TcpCallback tcpTouchCallback);

    public void setTcpStateCallback(TcpStateCallback tcpStateCallback);

    //test
    public boolean initReceiving(String host, int port, String dev_id);
    public boolean initProjection (String host, int port, String dev_id);
}
