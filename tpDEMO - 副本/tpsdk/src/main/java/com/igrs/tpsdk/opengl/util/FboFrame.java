/*
 *
 * TextureUtils.java
 *
 * Created by Wuwang on 2016/12/23
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.igrs.tpsdk.opengl.util;

import android.opengl.GLES20;

/**
 * Description:
 */
public class FboFrame {

    private final int[] fFrame = new int[1];
    private final int[] fTexture = new int[1];
    private final int[] frameColors = new int[1];

    public void initialize(int width, int height) {

        GLES20.glGenFramebuffers(1, fFrame, 0);
        GLES20.glGenTextures(1, fTexture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        // 当纹理坐标超出范围时，重复当前纹理
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        // 纹理放大和缩小均使用线性化插值
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void bindFrameTexture() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fTexture[0], 0);
    }

    public void unBindFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int getTextureId() {
        return fTexture[0];
    }

}
