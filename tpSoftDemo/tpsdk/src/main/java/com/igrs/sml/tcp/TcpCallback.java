package com.igrs.sml.tcp;

public interface TcpCallback {
    public void rev_msg(final String dev_id,final byte type, final byte[] data);
}
