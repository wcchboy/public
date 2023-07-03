package com.wcch.android.msg;

/**
 * @author created by Lzq
 * @time：2022/5/5
 * @Des：发送端切换显示模式
 */
public class Msg18 {

//    WINDOW_MODE = 1000;//窗口模式
//    FULL_SCREEN_MODE = 1001;//全屏模式
//    DF_MODE = 1002;//等分模式
    private int mode;
    private String taskId;
    private String ip;



    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
