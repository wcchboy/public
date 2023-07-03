package com.wcch.android.msg;

public class ServiceInfo {
    private String packageName;
    private String className;
    private int type;

    public ServiceInfo(int type, String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
        this.type = type;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
