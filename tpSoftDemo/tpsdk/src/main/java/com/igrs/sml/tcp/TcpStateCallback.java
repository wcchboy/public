package com.igrs.sml.tcp;

public interface TcpStateCallback {
    public void stateChange(String dev_id,boolean isConnected);
}
