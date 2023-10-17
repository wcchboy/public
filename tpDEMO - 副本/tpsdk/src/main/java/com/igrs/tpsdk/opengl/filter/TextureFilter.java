/*
 *
 * TrackFilter.java
 *
 * Created by Wuwang on 2016/12/21
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.igrs.tpsdk.opengl.filter;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.igrs.tpsdk.opengl.util.EasyGlUtils;


/**
 * Description:
 */
public class TextureFilter extends AFilter {
    private final OesFilter mFilter;
    private int viewW = 0;
    private int viewH = 0;

    private final int[] fFrame = new int[1];
    private final int[] fTexture = new int[1];

    private SurfaceTexture mSurfaceTexture;
    private final GroupFilter mGroupFilter;                           //中间特效
    private final Beauty mBeautyFilter;
    public TextureFilter() {
        super();
        mFilter = new OesFilter();
        mGroupFilter = new GroupFilter();
        mBeautyFilter = new Beauty();
        mBeautyFilter.setFlag(3);
        mGroupFilter.addFilter(mBeautyFilter);
        //MatrixUtils.flip(mFilter.getMatrix(), false, true);
    }
    public SurfaceTexture getTexture() {
        return mSurfaceTexture;
    }

    @Override
    public int getOutputTexture() {
        return mGroupFilter.getOutputTexture();
    }

    @Override
    public void draw() {
        try {
            boolean a = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
            if (a) {
                GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            }
            if (mSurfaceTexture != null) {
                mSurfaceTexture.updateTexImage();
            }

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D, fTexture[0], 0);


            EasyGlUtils.bindFrameTexture(fFrame[0], fTexture[0]);
            GLES20.glViewport(x, y, w, h);
            mFilter.draw();
            EasyGlUtils.unBindFrameBuffer();
            if (a) {
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            }
            mGroupFilter.setTextureId(fTexture[0]);
            mGroupFilter.draw();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {


        }
    }
    int[] mCameraTexture = new int[1];
    @Override
    protected void onCreate() {
        mFilter.create();
        mGroupFilter.create();
        mBeautyFilter.create();


        GLES20.glGenTextures(1, mCameraTexture, 0);
        mSurfaceTexture = new SurfaceTexture(mCameraTexture[0]);
        mFilter.setTextureId(mCameraTexture[0]);
    }

    private int x,y,w=320,h=240;
    public void setPreviewSize(int width, int height) {
        if(1.0f*viewW/viewH==1.0f*width/height){
            x = 0;
            y = 0;
            w = viewW;
            h = viewH;
        }else{
            int tempW = (int)1.0f*viewH*width/height;
            x =(viewW - tempW)/2;
            y = 0;
            w = tempW;
            h = viewH;
        }
    }
    @Override
    protected void onSizeChanged(int viewW, int viewH) {
        if (this.viewW == 0 || this.viewH == 0) {
            x = 0;
            y = 0;
            w = viewW;
            h = viewH;
            this.viewW = viewW;
            this.viewH = viewH;
            mFilter.setSize(viewW, viewH);
            mGroupFilter.setSize(viewW, viewH);
            //创建FrameBuffer和Texture
            deleteFrameBuffer();
            GLES20.glGenFramebuffers(1, fFrame, 0);
            EasyGlUtils.genTexturesWithParameter(1, fTexture, 0, GLES20.GL_RGBA, viewW, viewH);
        }
    }

    public void adjustCodecSize(int width, int height) {
        mFilter.setSize(width, height);
        mGroupFilter.setSize(width, height);
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
    }

}
