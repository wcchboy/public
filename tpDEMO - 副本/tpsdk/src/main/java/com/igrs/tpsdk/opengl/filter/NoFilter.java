/*
 *
 * NoFilter.java
 * 
 * Created by Wuwang on 2016/11/19
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.igrs.tpsdk.opengl.filter;


import com.igrs.sml.util.L;

/**
 * Description:
 */
public class NoFilter extends AFilter {


    String vertexShaderSource =
            "attribute vec4 vPosition;\n" +
                    "attribute vec2 vCoord;\n" +
                    "uniform mat4 vMatrix;\n" +
                    "\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    gl_Position = vMatrix*vPosition;\n" +
                    "    textureCoordinate = vCoord;\n" +
                    "}";

    String fragmentShaderSource =
            "precision mediump float;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform sampler2D vTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D( vTexture, textureCoordinate );\n" +
                    "}";

    public NoFilter() {
        super();
    }

    @Override
    protected void onCreate() {
        createProgram(vertexShaderSource,fragmentShaderSource);
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }

    private float[] coord;
    public void setLocalViewMirror(boolean localViewMirror){
        L.e("NoFilter-----setLocalViewMirror:" + localViewMirror);
        if(localViewMirror){
            coord=new float[]{
                    1.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,
            };
        }else{
            coord=new float[]{
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f
            };
        }
        mTexBuffer.clear();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }

}
