package com.wcch.android.bean;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author created by Lzq
 * @time：2021/8/4
 * @Des：
 */
public class DeviceView {
    private LinearLayout deviceView;
    private ImageView ivDevice;
    private ImageView ivDeviceCircle;
    private TextView tvDeviceName;
    private String hid;
    private boolean isLocalDevice;

    public LinearLayout getDeviceView() {
        return deviceView;
    }

    public void setDeviceView(LinearLayout deviceView) {
        this.deviceView = deviceView;
    }

    public ImageView getIvDevice() {
        return ivDevice;
    }

    public void setIvDevice(ImageView ivDevice) {
        this.ivDevice = ivDevice;
    }

    public ImageView getIvDeviceCircle() {
        return ivDeviceCircle;
    }

    public void setIvDeviceCircle(ImageView ivDeviceCircle) {
        this.ivDeviceCircle = ivDeviceCircle;
    }

    public TextView getTvDeviceName() {
        return tvDeviceName;
    }

    public void setTvDeviceName(TextView tvDeviceName) {
        this.tvDeviceName = tvDeviceName;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public boolean isLocalDevice() {
        return isLocalDevice;
    }

    public void setLocalDevice(boolean localDevice) {
        isLocalDevice = localDevice;
    }
}
