package com.wcch.android.entity;

import android.graphics.Bitmap;

public class PictureEntity {
    private Bitmap bitmap;
    private String path;

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }
}
