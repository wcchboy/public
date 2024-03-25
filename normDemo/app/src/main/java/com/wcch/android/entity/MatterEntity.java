package com.wcch.android.entity;

import com.dd.plist.ASCIIPropertyListParser;

import java.io.Serializable;

public class MatterEntity implements Serializable {
    private static final long serialVersionUID = 1;
    private long file_size;
    private String format = "mp4";
    private String id;
    private int number;
    private String picTx;
    private String rate = "1920:1080";
    private boolean select = false;
    private long time = 99999;
    private String title;
    private int type;
    private String url = "";

    public String toString() {
        return "MatterEntity{id='" + this.id + '\'' + ", url='" + this.url + '\'' + ", type=" + this.type + ", time=" + this.time + ", number=" + this.number + ", title='" + this.title + '\'' + ASCIIPropertyListParser.DICTIONARY_END_TOKEN;
    }

    public long getFile_size() {
        return this.file_size;
    }

    public void setFile_size(long j) {
        this.file_size = j;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String str) {
        this.format = str;
    }

    public boolean isSelect() {
        return this.select;
    }

    public void setSelect(boolean z) {
        this.select = z;
    }

    public String getPicTx() {
        return this.picTx;
    }

    public void setPicTx(String str) {
        this.picTx = str;
    }

    public String getRate() {
        return this.rate;
    }

    public void setRate(String str) {
        this.rate = str;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int i) {
        this.number = i;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long j) {
        this.time = j;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }
}
