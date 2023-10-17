package com.wcch.android.soft.model.event;

public class HostChangedEvent {

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private String host;
    private String port;
    public HostChangedEvent(String host){
        this.host = host;
    }
    public HostChangedEvent(String host,String port){
        this.host = host;
        this.port = port;
    }
}
