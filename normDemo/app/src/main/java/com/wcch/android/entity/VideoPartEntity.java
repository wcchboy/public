package com.wcch.android.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoPartEntity implements Serializable {
    private static final long serialVersionUID = 1;
    private int height = 62;
    private String id;
    private List<MatterEntity> list1 = new ArrayList();
    private List<MatterEntity> list2 = new ArrayList();
    private int mode = 0;
    private double multiple = 1.0d;
    private String radio = "0:0";
    private int range = 0;
    private int type = 2;
    private int videoPart1 = 1920;
    private int videoPart10 = 19200;
    private int videoPart2 = 3840;
    private int videoPart3 = 5760;
    private int videoPart4 = 7680;
    private int videoPart5 = 9600;
    private int videoPart6 = 11520;
    private int videoPart7 = 13440;
    private int videoPart8 = 15360;
    private int videoPart9 = 17280;
    private int viewId;
    private int voice = 0;
    private int width = 1920;

    public int getMode() {
        return this.mode;
    }

    public void setMode(int i) {
        this.mode = i;
    }

    public String getRadio() {
        return this.radio;
    }

    public void setRadio(String str) {
        this.radio = str;
    }

    public double getMultiple() {
        return this.multiple;
    }

    public void setMultiple(double d) {
        this.multiple = d;
    }

    public int getVoice() {
        return this.voice;
    }

    public void setVoice(int i) {
        this.voice = i;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int i) {
        this.width = i;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public int getVideoPart1() {
        return this.videoPart1;
    }

    public void setVideoPart1(int i) {
        this.videoPart1 = i;
    }

    public int getVideoPart2() {
        return this.videoPart2;
    }

    public void setVideoPart2(int i) {
        this.videoPart2 = i;
    }

    public int getVideoPart3() {
        return this.videoPart3;
    }

    public void setVideoPart3(int i) {
        this.videoPart3 = i;
    }

    public int getVideoPart4() {
        return this.videoPart4;
    }

    public void setVideoPart4(int i) {
        this.videoPart4 = i;
    }

    public int getVideoPart5() {
        return this.videoPart5;
    }

    public void setVideoPart5(int i) {
        this.videoPart5 = i;
    }

    public int getVideoPart6() {
        return this.videoPart6;
    }

    public void setVideoPart6(int i) {
        this.videoPart6 = i;
    }

    public int getVideoPart7() {
        return this.videoPart7;
    }

    public void setVideoPart7(int i) {
        this.videoPart7 = i;
    }

    public int getVideoPart8() {
        return this.videoPart8;
    }

    public void setVideoPart8(int i) {
        this.videoPart8 = i;
    }

    public int getVideoPart9() {
        return this.videoPart9;
    }

    public void setVideoPart9(int i) {
        this.videoPart9 = i;
    }

    public int getVideoPart10() {
        return this.videoPart10;
    }

    public void setVideoPart10(int i) {
        this.videoPart10 = i;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int i) {
        this.range = i;
    }

    public List<MatterEntity> getList2() {
        return this.list2;
    }

    public void setList2(List<MatterEntity> list) {
        this.list2 = list;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public int getViewId() {
        return this.viewId;
    }

    public void setViewId(int i) {
        this.viewId = i;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public List<MatterEntity> getList1() {
        return this.list1;
    }

    public void setList1(List<MatterEntity> list) {
        this.list1 = list;
    }
}
