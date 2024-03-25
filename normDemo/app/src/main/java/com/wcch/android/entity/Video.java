package com.wcch.android.entity;

public class Video {
    private long date = 0;
    private long duration = 0;
    private int id = 0;
    private String name = null;
    private String path = null;
    private String resolution = null;
    private long size = 0;

    public Video(int i, String str, String str2, String str3, long j, long j2, long j3) {
        this.id = i;
        this.path = str;
        this.name = str2;
        this.resolution = str3;
        this.size = j;
        this.date = j2;
        this.duration = j3;
    }

    public Video() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getResolution() {
        return this.resolution;
    }

    public void setResolution(String str) {
        this.resolution = str;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long j) {
        this.size = j;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long j) {
        this.date = j;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long j) {
        this.duration = j;
    }

    public String toString() {
        return "Video [id=" + this.id + ", path=" + this.path + ", name=" + this.name + ", resolution=" + this.resolution + ", size=" + this.size + ", date=" + this.date + ", duration=" + this.duration + "]";
    }
}
