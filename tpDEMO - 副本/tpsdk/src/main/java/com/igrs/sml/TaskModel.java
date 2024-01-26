package com.igrs.sml;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;

public class TaskModel implements Serializable {

    //10
    public int cmd;

    public String taskId;// long
    //public String targetIp;//  目标IP
    public Integer targetPort;// 目标端口
    public String targetName;//目标名称

    public int deviceType=0;//0 默认 ；1 Android Phone，2 iPhone，3:PC(Win);4:Android Pad;5:iPad
    public String deviceName;//目标名称

    public int taskType;//0->全屏投屏;1->窗口投屏;2->扩展屏投屏;3->区域投屏;4->遥控;5->文件传输
    public int taskState;//0->启动;1->停止;2->释放

    public int activeState=0;//0 未激活; 1 已激活

    //hWnd HWND  当 taskType==1 时有效
    //area RECT  当 taskType==3 时有效
    public Integer audio; //0 : Mic+系统声音 1 : 指定应用声音 2 : Mic声音
    public Integer mute;//bool  false : 打开声音 true : 静音
    public Float volume;// float 音量

    public Integer resolution_x;// 水平分辨率  1920/1280
    public Integer resolution_y;// 垂直分辨率 1080/720
    public Integer fps=22;// fps
    public Integer dataType;// 数据类型 0:video;1:audio;2:h264
    public int useCustomSource;// bool 是否接收外部数据源推送的 RGB 图像
    public int useCustomSink;// bool 是否使用将解码后 RGB 图像推送给外部渲染 器
    public String expand;//	string	Y	扩展字段

    public transient String dev_id;//	系统设备ID(系统内用)


    public int retry;//0->默认（新任务）;1->重试（重连接任务）

}


