package com.wcch.android.bean;

/**
 * @author created by Lzq
 * @time：2021/8/2
 * @Des：
 */
public class DeviceLocation {
    private int cocX;//圆心X坐标
    private int cocY;//圆心Y坐标
    private float radius;//半径
    private int l,t,r,b;//位置左、上、右、下

    public int getCocX() {
        return cocX;
    }

    public void setCocX(int cocX) {
        this.cocX = cocX;
    }

    public int getCocY() {
        return cocY;
    }

    public void setCocY(int cocY) {
        this.cocY = cocY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
