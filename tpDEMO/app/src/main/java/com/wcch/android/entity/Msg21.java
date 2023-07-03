package com.wcch.android.entity;

/**
 * 大屏端设备信息
 */
public class Msg21 {

    private int cmd;
    private int versionCode;
    private String versionName;

    private int deviceType;
    private String deviceName;
    private String deviceModel;
    private String deviceMac;
    private String ip;
    private int port;
    private String apIp;//仅联想投屏有
    private String hotspot;//仅联想投屏有
    private int activeState; //接收端激活类型：0: 未激活 1: 激活
    private boolean isSupportControl;//接收端是否支持遥控：true 支持 false 不支持
    private boolean isSupportWindowMode; //接收端是否支持窗口模式：true 支持 false 不支持


    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getApIp() {
        return apIp;
    }

    public void setApIp(String apIp) {
        this.apIp = apIp;
    }

    public String getHotspot() {
        return hotspot;
    }

    public void setHotspot(String hotspot) {
        this.hotspot = hotspot;
    }

    public int getActiveState() {
        return activeState;
    }

    public void setActiveState(int activeState) {
        this.activeState = activeState;
    }

    public boolean isSupportControl() {
        return isSupportControl;
    }

    public void setSupportControl(boolean supportControl) {
        isSupportControl = supportControl;
    }

    public boolean isSupportWindowMode() {
        return isSupportWindowMode;
    }

    public void setSupportWindowMode(boolean supportWindowMode) {
        isSupportWindowMode = supportWindowMode;
    }

    @Override
    public String toString() {
        return "Msg21{" +
                "cmd=" + cmd +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceMac='" + deviceMac + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", apIp='" + apIp + '\'' +
                ", hotspot='" + hotspot + '\'' +
                ", activeState=" + activeState +
                ", isSupportControl=" + isSupportControl +
                ", isSupportWindowMode=" + isSupportWindowMode +
                '}';
    }
}
