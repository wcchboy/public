package com.wcch.android.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PicToVideoEntity implements Serializable {
    private static final long serialVersionUID = 1;
    private List<MatterEntity> bgList = new ArrayList();
    private int color = -1;
    private int height;
    private String name = "";
    private int poi;
    private int size;
    private String text = "";
    private String typeface = "normal";
    private int width;

    public List<MatterEntity> getBgList() {
        return this.bgList;
    }

    public void setBgList(List<MatterEntity> list) {
        this.bgList = list;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String str) {
        this.text = str;
    }

    public int getPoi() {
        return this.poi;
    }

    public void setPoi(int i) {
        this.poi = i;
    }

    public String getTypeface() {
        return this.typeface;
    }

    public void setTypeface(String str) {
        this.typeface = str;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int i) {
        this.size = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
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

    public int getColor() {
        return this.color;
    }

    public void setColor(int i) {
        this.color = i;
    }
}
