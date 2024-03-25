package com.wcch.android.entity;

import java.io.Serializable;

public class CoordinateEntity implements Serializable {
    private static final long serialVersionUID = 1;
    private float x1;
    private float x2;
    private float y1;
    private float y2;

    public CoordinateEntity(float f, float f2, float f3, float f4) {
        this.x1 = f;
        this.x2 = f3;
        this.y1 = f2;
        this.y2 = f4;
    }

    public float getX1() {
        return this.x1;
    }

    public void setX1(float f) {
        this.x1 = f;
    }

    public float getX2() {
        return this.x2;
    }

    public void setX2(float f) {
        this.x2 = f;
    }

    public float getY1() {
        return this.y1;
    }

    public void setY1(float f) {
        this.y1 = f;
    }

    public float getY2() {
        return this.y2;
    }

    public void setY2(float f) {
        this.y2 = f;
    }
}
