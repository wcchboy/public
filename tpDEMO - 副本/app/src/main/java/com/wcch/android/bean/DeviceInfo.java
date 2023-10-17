package com.wcch.android.bean;

/**
 * @author created by Lzq
 * @time：2022/1/22
 * @Des：
 */
public class DeviceInfo {
    private String name;
    private String model;

    public DeviceInfo() {}

    public DeviceInfo(String name, String model) {
        this.name = name;
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
