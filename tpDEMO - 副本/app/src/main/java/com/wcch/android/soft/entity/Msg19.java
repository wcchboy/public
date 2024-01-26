package com.wcch.android.soft.entity;

import java.util.List;

/**
 * @author created by Lzq
 * @time：2022/5/5
 * @Des：广播同步投屏状态信息
 */
public class Msg19{

    //WINDOW_MODE = 1000;//窗口模式
    //FULL_SCREEN_MODE = 1001;//全屏模式
    //DF_MODE = 1002;//等分模式

    public static final int MODE_WINDOW = 1000;
    public static final int MODE_FULL_SCREEN = 1001;
    public static final int MODE_DF = 1002;

    private int currentMode;//当前显示模式
    public int isAndroid;//0：Android通道  1：非Android通道
    public int activeState;//0 未激活; 1 已激活
    private List<ProjectionStatus> statusList;//窗口状态
    public List<ProjectionStatus> getList() {
        return statusList;
    }

    public void setList(List<ProjectionStatus> list) {
        this.statusList = list;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
    }

    public static class ProjectionStatus {

        private int fullScreen;//是否全屏  0：非全屏   1：全屏
        private int width;//窗口宽
        private int height;//窗口高
        private int fps=22;//帧率
        private int encodeType;//编码方式 0:H265硬编码  1：H264硬编码  2：软编码
        public int deviceType=0;//0 默认 ；1 Android Phone，2 iPhone，3:PC(Win);4:Android Pad;5:iPad
        private String deviceName;//设备名称
        private String taskId;//软投屏通过taskid区分
        private String ip;//W20 通过ip区分

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getFps() {
            return fps;
        }

        public void setFps(int fps) {
            this.fps = fps;
        }

        public int getEncodeType() {
            return encodeType;
        }

        public void setEncodeType(int encodeType) {
            this.encodeType = encodeType;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public int getFullScreen() {
            return fullScreen;
        }

        public void setFullScreen(int fullScreen) {
            this.fullScreen = fullScreen;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

    }
}
