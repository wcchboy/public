package com.wcch.android.msg;

import java.util.List;

/**
 * @author created by Lzq
 * @time：2022/5/5
 * @Des：广播同步投屏状态信息
 */
public class Msg19 extends Msg{

    //WINDOW_MODE = 1000;//窗口模式
    //FULL_SCREEN_MODE = 1001;//全屏模式
    //DF_MODE = 1002;//等分模式

    private int currentMode;//当前显示模式
    private int activeState;//0:未激活  1，激活
    private int isAndroid;// 0:Android  1：非Android
    private String deviceName;// 0:Android  1：非Android
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

    public int getIsAndroid() {
        return isAndroid;
    }

    public void setIsAndroid(int isAndroid) {
        this.isAndroid = isAndroid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getActiveState() {
        return activeState;
    }

    public void setActiveState(int activeState) {
        this.activeState = activeState;
    }

    public static class ProjectionStatus {

        private int fullScreen;//是否全屏  0：非全屏   1：全屏
        private int width;//窗口宽
        private int height;//窗口高
        private int encodeType;//编码方式 0:H265硬编码   1：H264硬编码  2：软编码
        private String deviceName;//设备名称
        private String taskId;//软投屏通过taskid区分
        private String ip;//W20 通过ip区分
        private int fps;  //帧率

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

        public int getFps() {
            return fps;
        }

        public void setFps(int fps) {
            this.fps = fps;
        }

        @Override
        public String toString() {
            return "ProjectionStatus{" +
                    "fullScreen=" + fullScreen +
                    ", width=" + width +
                    ", height=" + height +
                    ", encodeType=" + encodeType +
                    ", deviceName='" + deviceName + '\'' +
                    ", taskId='" + taskId + '\'' +
                    ", ip='" + ip + '\'' +
                    ", fps=" + fps +
                    '}';
        }
    }
}
