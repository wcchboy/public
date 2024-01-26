package com.igrs.sml.callback;

public interface IEventListener {
    void eventListener(long taskId, int msgId, String msgData, int msgLength);
    void desk_size_change_callback(String peerIp, int new_w, int new_h);
}