/*
 *
 * GrayFilter.java
 * 
 * Created by Wuwang on 2016/12/14
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.igrs.tpsdk.opengl.filter;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Description:
 */
public class BlurFilter extends AFilter {
    String vertexShader = "attribute vec4 aPosition;\n" +
            "attribute vec2 aTexCoord;\n" +
            "varying vec2 vTexCoord;\n" +
            "void main() {\n" +
            "    vTexCoord = vec2(aTexCoord.x,1.0 - aTexCoord.y);\n" +
            "    gl_Position = aPosition;\n" +
            "}";
    String fragmentShader = "varying highp vec2 vTexCoord;\n" +
            "uniform sampler2D sTexture;\n" +
            "uniform highp float widthOfset;\n" +
            "uniform highp float heightOfset;\n" +
            "uniform highp float gaussianWeights[961];\n" +
            "uniform highp int blurRadius;\n" +
            "void main() {\n" +
            "    if(blurRadius == 0){\n" +
            "        gl_FragColor = texture2D(sTexture,vTexCoord);\n" +
            "    }else{\n" +
            "        highp vec2 offset = vec2(widthOfset,heightOfset);\n" +
            "        highp vec4 sum = vec4(0.0);\n" +
            "        highp int x = 0;\n" +
            "        for (int i = -blurRadius; i <= blurRadius; i++) {\n" +
            "            for (int j = -blurRadius; j <= blurRadius; j++) {\n" +
            "                highp float weight = gaussianWeights[x];\n" +
            "                sum += (texture2D(sTexture, vTexCoord+offset*vec2(i,j))*weight);\n" +
            "                x++;\n" +
            "            }\n" +
            "        }\n" +
            "        gl_FragColor = vec4((sum.rgb - vec3(0.2)), sum.w);\n" +
            "    }\n" +
            "}";


    private int aPositionHandle;
    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;
    private int widthOfsetHandle;
    private int heightOfsetHandle;
    private int gaussianWeightsHandle;
    private int blurRadiusHandle;

    private final int[] bos = new int[2];

    private final int[] frameBuffers = new int[1];
    private final int[] textures = new int[1];

    public BlurFilter(){
        super();
    }
    @Override
    protected void onCreate() {
        createProgram(vertexShader, fragmentShader);

        aPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        uTextureSamplerHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");
        aTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");

        widthOfsetHandle = GLES20.glGetUniformLocation(mProgram, "widthOfset");
        heightOfsetHandle = GLES20.glGetUniformLocation(mProgram, "heightOfset");
        gaussianWeightsHandle = GLES20.glGetUniformLocation(mProgram, "gaussianWeights");
        blurRadiusHandle = GLES20.glGetUniformLocation(mProgram, "blurRadius");
        float[] vertexData = {
                1f, -1f, 0f,
                -1f, -1f, 0f,
                1f, 1f, 0f,
                -1f, 1f, 0f
        };

        float[] textureVertexData = {
                1f, 1f,
                0f, 1f,
                1f, 0f,
                0f, 0f,
        };
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);


        FloatBuffer textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);

        GLES20.glGenBuffers(2, bos, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bos[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bos[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureVertexData.length * 4, textureVertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glGenTextures(textures.length, textures, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glGenFramebuffers(frameBuffers.length,frameBuffers,0);
        gaussianWeights();
    }


    private FloatBuffer gaussianWeightsBuffer;
    public void gaussianWeights(){
        if(blurRadius == 0){
            return;
        }
        long ttt = System.currentTimeMillis();
        float sumOfWeights = 0.0f;
        int g = 0;
        int tx = blurRadius*2+1;
        if(sigma == 0){
            sigma = 0.3f*((tx-1)*0.5f - 1f) + 0.8f;
        }
        float[] gaussianWeights = new float[tx*tx];
        for (int x = -blurRadius; x <= blurRadius; x++) {
            for (int y = -blurRadius; y <= blurRadius; y++) {
                int s = x*x+y*y;
                float a = (float) ((1.0f / 2.0f * Math.PI * Math.pow(sigma, 2.0f)) * Math.exp(-s / (2.0f * Math.pow(sigma, 2.0f))));
                gaussianWeights[g] = a;
                sumOfWeights+=a;
                g++;
            }
        }
        for (int x = 0; x < tx*tx; ++x) {
            gaussianWeights[x] = gaussianWeights[x]/sumOfWeights;
        }
        gaussianWeightsBuffer = ByteBuffer.allocateDirect(gaussianWeights.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(gaussianWeights);
        gaussianWeightsBuffer.position(0);
        Log.d("==================","time = "+(System.currentTimeMillis() - ttt));
    }

    public void setBlurRadius(int blurRadius) {
        this.blurRadius = blurRadius;
    }


    public void setSigma(double sigma) {
        this.sigma = sigma;
    }


    @Override
    protected void onSizeChanged(int width, int height) {
        int blurScaleWidth = (int) (width*0.03f);
        int blurScaleHeight = (int) (height*0.03f);
        setScaleSize(blurScaleWidth,blurScaleHeight);
        //setScaleSize(width,height);
    }

    private double sigma = 8;
    private int blurRadius = 3;
    private int scaleWidth,scaleHeight;
    public void setScaleSize(int width,int height){
        scaleWidth = width;
        scaleHeight = height;
        Log.d("qf","2 setScaleSize ->width:"+scaleWidth+" scaleHeight:"+scaleHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textures[0], 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        gaussianWeights();
    }

    @Override
    public void draw() {
        //super.draw();

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
        GLES20.glViewport(0,0,scaleWidth,scaleHeight);
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTextureId());
        GLES20.glUniform1i(uTextureSamplerHandle, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bos[0]);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bos[1]);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glUniform1f(widthOfsetHandle, 1.0f/scaleWidth);
        GLES20.glUniform1f(heightOfsetHandle, 1.0f/scaleHeight);
        GLES20.glUniform1i(blurRadiusHandle, blurRadius);

        int tx = blurRadius*2+1;
        GLES20.glUniform1fv(gaussianWeightsHandle,tx*tx,gaussianWeightsBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public int getOutputTexture() {
        return textures[0];
    }

    @Override
    protected void onSetExpandData() {

    }


}
