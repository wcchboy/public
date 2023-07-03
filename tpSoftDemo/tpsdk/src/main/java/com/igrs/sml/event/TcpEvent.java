package com.igrs.sml.event;

public class TcpEvent {


    public static final int TYPE_TCP_RETRY = 10080;
    public static final int TYPE_TCP_CONNECT = 10081;
    public static final int TYPE_TCP_DISCONNECT = 10082;

    public int code;
    public String dev_id;
    public String msg;
    public TcpEvent(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public TcpEvent(int code,String dev_id, String msg){
        this.code = code;
        this.dev_id = dev_id;
        this.msg = msg;
    }
}
