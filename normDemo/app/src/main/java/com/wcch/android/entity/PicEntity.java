package com.wcch.android.entity;

import android.graphics.Bitmap;

public class PicEntity {
    private Bitmap bitmap;
    private String id;

    public PicEntity(Bitmap bitmap2, String str) {
        this.bitmap = bitmap2;
        this.id = str;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }
}
