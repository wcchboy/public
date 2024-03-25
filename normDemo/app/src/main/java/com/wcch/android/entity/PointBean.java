package com.wcch.android.entity;

public class PointBean {
    public double x = -1.0d;
    public double y = -1.0d;

    public double getX() {
        return this.x;
    }

    public void setX(double d) {
        this.x = d;
    }

    public void set(double d, double d2) {
        this.x = d;
        this.y = d2;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double d) {
        this.y = d;
    }

    public PointBean() {
    }

    public PointBean(double d, double d2) {
        this.x = d;
        this.y = d2;
    }
}
