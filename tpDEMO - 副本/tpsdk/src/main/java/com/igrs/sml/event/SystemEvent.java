package com.igrs.sml.event;

import com.igrs.sml.TaskModel;

public class SystemEvent {

    public int type;
    public int value;
    public byte[] data;


    public static final int TYPE_CHECK_STATE = 10000;

    public static final int TYPE_VIDEO_SIZE = 10001;
    public static final int TYPE_VIDEO_FPS = 10002;
    public static final int TYPE_VIDEO_BITRATE = 10003;
    public static final int TYPE_VIDEO_REQ_KEY = 10004;

    public static final int TYPE_REC_START = 10011;
    public static final int TYPE_REC_STOP = 10012;
    public static final int TYPE_REC_FAILED = 10013;
    public static final int TYPE_REC_EXIT = 10014;

    public static final int TYPE_TCP_FAIL = 10021;
    public static final int TYPE_TCP_SUCCESS = 10022;

    public static final int TYPE_WIFI_DISABLED = 10030;
    public static final int TYPE_WIFI_ENABLED = 10031;
    public static final int TYPE_BT_TCP_CONNECT = 10032;

    public static final int TYPE_SIZE_CHANGE = 10040;


    public TaskModel taskModel;

    public SystemEvent(int type){
        this.type = type;
    }
    /////////////////////////////////
    public SystemEvent(int type,byte[] data){
        this.type = type;
        this.data = data;
    }
    public SystemEvent(int type,int value){
        this.type = type;
        this.value = value;
    }
    public SystemEvent(int type,TaskModel taskModel){
        this.type = type;
        this.taskModel = taskModel;
    }
    /////////////////////////////////
}
