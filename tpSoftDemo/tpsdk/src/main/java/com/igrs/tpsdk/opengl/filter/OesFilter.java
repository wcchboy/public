/*
 *
 * CameraFilter.java
 * 
 * Created by Wuwang on 2016/11/19
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.igrs.tpsdk.opengl.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.util.Arrays;

/**
 * Description:
 */
public class OesFilter extends AFilter {

    private int mHCoordMatrix;
    private float[] mCoordMatrix= Arrays.copyOf(OM,16);


    String vertexShaderSource =
            "attribute vec4 vPosition;\n" +
                    "attribute vec2 vCoord;\n" +
                    "uniform mat4 vMatrix;\n" +
                    "uniform mat4 vCoordMatrix;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    gl_Position = vMatrix*vPosition;\n" +
                    "    textureCoordinate = (vCoordMatrix*vec4(vCoord,0,1)).xy;\n" +
                    "}";

    String fragmentShaderSource =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform samplerExternalOES vTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D( vTexture, textureCoordinate );\n" +
                    "}";


    public OesFilter() {
        super();
    }


    @Override
    protected void onCreate() {
        createProgram(vertexShaderSource,fragmentShaderSource);
        mHCoordMatrix= GLES20.glGetUniformLocation(mProgram,"vCoordMatrix");
    }

    public void setCoordMatrix(float[] matrix){
        this.mCoordMatrix=matrix;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniformMatrix4fv(mHCoordMatrix,1,false,mCoordMatrix,0);
    }

    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,getTextureId());
        GLES20.glUniform1i(mHTexture,0);
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }



}
