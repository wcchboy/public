package com.wcch.android.entity;

import com.dd.plist.ASCIIPropertyListParser;

public class VideoEntity {
    private long duration;
    private String height;
    private String rotation;
    private String width;

    public String toString() {
        return "VideoEntity{duration=" + this.duration + ", width='" + this.width + '\'' + ", height='" + this.height + '\'' + ", rotation='" + this.rotation + '\'' + ASCIIPropertyListParser.DICTIONARY_END_TOKEN;
    }

    public String getRotation() {
        return this.rotation;
    }

    public void setRotation(String str) {
        this.rotation = str;
    }

    public VideoEntity() {
    }

    public VideoEntity(long j, String str, String str2) {
        this.duration = j;
        this.width = str;
        this.height = str2;
    }

    public VideoEntity(long j, String str, String str2, String str3) {
        this.duration = j;
        this.width = str;
        this.height = str2;
        this.rotation = str3;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getWidth() {
        return this.width;
    }

    public String getHeight() {
        return this.height;
    }
}
