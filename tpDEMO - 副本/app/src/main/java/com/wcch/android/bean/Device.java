package com.wcch.android.bean;


import com.igrs.betotablet.bean.DeviceType;
import com.lenovo.betoservice.module.beto.model.BeToPhysicalDevice;

/**
 * @author created by Lzq
 * 时间：2021/8/2
 * Des：设备对象
 */
public class Device {

    private boolean localDevice;//是否是本机
    private DeviceType type;
    private boolean centerDevice;//是否是中心设备
    private DeviceLocation deviceLocation;//设备位置
    private DeviceView deviceView;//设备view
    private boolean diffusePoint;//扩散点
    private long showTime;//显示时间
    private boolean isShowing;
    private boolean selected;//被选中
    private BeToPhysicalDevice physicalDevice;

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }


    public boolean isCenterDevice() {
        return centerDevice;
    }

    public void setCenterDevice(boolean centerDevice) {
        this.centerDevice = centerDevice;
    }


    public DeviceLocation getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(DeviceLocation deviceLocation) {
        this.deviceLocation = deviceLocation;
    }

    public DeviceView getDeviceView() {
        return deviceView;
    }

    public void setDeviceView(DeviceView deviceView) {
        this.deviceView = deviceView;
    }

    public boolean isDiffusePoint() {
        return diffusePoint;
    }

    public void setDiffusePoint(boolean diffusePoint) {
        this.diffusePoint = diffusePoint;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isLocalDevice() {
        return localDevice;
    }

    public void setLocalDevice(boolean localDevice) {
        this.localDevice = localDevice;
    }

    public BeToPhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    public void setPhysicalDevice(BeToPhysicalDevice physicalDevice) {
        this.physicalDevice = physicalDevice;
    }
}
