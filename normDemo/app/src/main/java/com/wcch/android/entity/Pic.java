package com.wcch.android.entity;

public class Pic {
    private long date = 0;
    private int id = 0;
    private String name = null;
    private String path = null;
    private long size = 0;

    public Pic(int i, String str, String str2, long j, long j2) {
        this.id = i;
        this.path = str;
        this.name = str2;
        this.size = j;
        this.date = j2;
    }

    public Pic() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
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

    public String toString() {
        return "Video [id=" + this.id + ", path=" + this.path + ", name=" + this.name + ", size=" + this.size + ", date=" + this.date + "]";
    }
}
