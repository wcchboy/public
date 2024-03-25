package com.wcch.android.entity;

import android.annotation.SuppressLint;

import com.dd.plist.ASCIIPropertyListParser;

import androidx.core.internal.view.SupportMenu;

public class PaichengEntity {
    @SuppressLint("RestrictedApi")
    private int color = SupportMenu.CATEGORY_MASK;
    private String data;
    private long endTime = 0;
    private String id;
    private boolean isDianpian = false;
    private int light = 5;
    private String newColor = "ffffff";
    private String scheduleName;
    private long startTime = 0;
    private String title;
    private int wedEnd = 1;
    private int wedStart = 1;
    private String xinhao = "PLAYER";

    public String getScheduleName() {
        return this.scheduleName;
    }

    public void setScheduleName(String str) {
        this.scheduleName = str;
    }

    public String getNewColor() {
        return this.newColor;
    }

    public void setNewColor(String str) {
        this.newColor = str;
    }

    public String toString() {
        return "PaichengEntity{id='" + this.id + '\'' + ", title='" + this.title + '\'' + ", data='" + this.data + '\'' + ", light=" + this.light + ", xinhao='" + this.xinhao + '\'' + ", wedStart=" + this.wedStart + ", wedEnd=" + this.wedEnd + ", color=" + this.color + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", isDianpian=" + this.isDianpian + ASCIIPropertyListParser.DICTIONARY_END_TOKEN;
    }

    public int getWedStart() {
        return this.wedStart;
    }

    public void setWedStart(int i) {
        this.wedStart = i;
    }

    public int getWedEnd() {
        return this.wedEnd;
    }

    public void setWedEnd(int i) {
        this.wedEnd = i;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int i) {
        this.color = i;
    }

    public String getXinhao() {
        return this.xinhao;
    }

    public void setXinhao(String str) {
        this.xinhao = str;
    }

    public int getLight() {
        return this.light;
    }

    public void setLight(int i) {
        this.light = i;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String str) {
        this.data = str;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long j) {
        this.startTime = j;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long j) {
        this.endTime = j;
    }

    public boolean isDianpian() {
        return this.isDianpian;
    }

    public void setDianpian(boolean z) {
        this.isDianpian = z;
    }
}
