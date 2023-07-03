/*
 *
 * AiyaFilter.java
 *
 * Created by Wuwang on 2016/11/19
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.igrs.tpsdk.opengl.filter;

import android.opengl.GLES20;
import android.view.Surface;

import com.igrs.sml.util.L;
import com.igrs.tpsdk.opengl.util.FboFrame;
import com.igrs.tpsdk.opengl.util.MatrixUtils;


/**
 * Description:
 */
public class CameraFilter extends OesFilter {

    private final FboFrame fboFrame = new FboFrame();

    public CameraFilter() {
        super();
        MatrixUtils.flip(getMatrix(), false, true);
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        super.onSizeChanged(width, height);
        fboFrame.initialize(width, height);
    }

    @Override
    public void draw() {
        boolean a = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        if (a) {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
        fboFrame.bindFrameTexture();
        super.draw();
        fboFrame.unBindFrameBuffer();
        if (a) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }
    }

    @Override
    public int getOutputTexture() {
        return fboFrame.getTextureId();
    }

    public void setLocalViewRotation(boolean is_CAMERA_FACING_FRONT, int screenOrientation) {
        float[] coord = null;
        L.e("CameraFilter->setLocalViewRotation-----flag:" + is_CAMERA_FACING_FRONT+"->"+screenOrientation);
        switch (screenOrientation) {
            case Surface.ROTATION_0: {
                L.i("设置竖屏");
                if (is_CAMERA_FACING_FRONT) {
                    coord = new float[]{
                            0.0f, 0.0f,
                            1.0f, 0.0f,
                            0.0f, 1.0f,
                            1.0f, 1.0f
                    };
                } else {
                    coord = new float[]{
                            1.0f, 1.0f,
                            0.0f, 1.0f,
                            1.0f, 0.0f,
                            0.0f, 0.0f,
                    };
                }
            }
            break;
            case Surface.ROTATION_90: {
                L.i("设置横屏");
                if (is_CAMERA_FACING_FRONT) {
                    coord = new float[]{
                            1.0f, 0.0f,
                            1.0f, 1.0f,
                            0.0f, 0.0f,
                            0.0f, 1.0f,
                    };
                } else {
                    coord = new float[]{
                            1.0f, 0.0f,
                            1.0f, 1.0f,
                            0.0f, 0.0f,
                            0.0f, 1.0f,
                    };
                }
            }
            break;
            case Surface.ROTATION_180: {
                L.i("反向竖屏");
                if (is_CAMERA_FACING_FRONT) {
                    coord = new float[]{
                            0.0f, 1.0f,
                            0.0f, 0.0f,
                            1.0f, 1.0f,
                            1.0f, 0.0f,
                    };
                } else {
                    coord = new float[]{
                            0.0f, 1.0f,
                            0.0f, 0.0f,
                            1.0f, 1.0f,
                            1.0f, 0.0f,
                    };
                }
            }
            break;
            case Surface.ROTATION_270: {
                L.i("反向横屏");
                if (is_CAMERA_FACING_FRONT) {
                    coord = new float[]{
                            0.0f, 1.0f,
                            0.0f, 0.0f,
                            1.0f, 1.0f,
                            1.0f, 0.0f,
                    };
                } else {
                    coord = new float[]{
                            0.0f, 1.0f,
                            0.0f, 0.0f,
                            1.0f, 1.0f,
                            1.0f, 0.0f,
                    };
                }
            }
            break;
            default:
                break;
        }
        if (coord != null) {
            mTexBuffer.clear();
            mTexBuffer.put(coord);
            mTexBuffer.position(0);
        }
    }
}
