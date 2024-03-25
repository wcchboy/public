package com.wcch.android.entity;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppInfo {
    private String apkName;
    private String apkPackageName;
    private long apkSize;
    private ApplicationInfo applicationInfo;
    private Drawable icon;
    private boolean isRom;
    private boolean isUserApp;
    private int versionCode = 0;

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIcon(Drawable drawable) {
        this.icon = drawable;
    }

    public String getApkName() {
        return this.apkName;
    }

    public void setApkName(String str) {
        this.apkName = str;
    }

    public long getApkSize() {
        return this.apkSize;
    }

    public void setApkSize(long j) {
        this.apkSize = j;
    }

    public boolean isUserApp() {
        return this.isUserApp;
    }

    public void setIsUserApp(boolean z) {
        this.isUserApp = z;
    }

    public boolean isRom() {
        return this.isRom;
    }

    public void setIsRom(boolean z) {
        this.isRom = z;
    }

    public String getApkPackageName() {
        return this.apkPackageName;
    }

    public void setApkPackageName(String str) {
        this.apkPackageName = str;
    }

    public ApplicationInfo getApplicationInfo() {
        return this.applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo2) {
        this.applicationInfo = applicationInfo2;
    }

    public int getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(int i) {
        this.versionCode = i;
    }
}
