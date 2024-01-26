package com.wcch.android.window;

/**
 * @author created by Lzq
 * @time：2022/4/15
 * @Des：设备屏幕旋转实体类。因设备屏幕旋转两个回调onDeskSizeChange、onScreenOri之间异步执行问题
 */
public class DeskSizeChangeBean {
    private String ip;
    private int width = 0;
    private int height = 0;
    private int ori = -1;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

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

    public int getOri() {
        return ori;
    }

    public void setOri(int ori) {
        this.ori = ori;
    }
}
