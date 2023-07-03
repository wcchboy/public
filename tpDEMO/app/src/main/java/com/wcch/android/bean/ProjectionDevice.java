package com.wcch.android.bean;

/**
 * @author created by Lzq
 * @time：2022/1/18
 * @Des：
 */
public class ProjectionDevice {
    private Device device;
    private boolean isNowProjection;

    public ProjectionDevice() {}

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean isNowProjection() {
        return isNowProjection;
    }

    public void setNowProjection(boolean nowProjection) {
        isNowProjection = nowProjection;
    }
}
