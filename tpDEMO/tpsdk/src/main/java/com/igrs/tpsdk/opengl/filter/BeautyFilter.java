package com.igrs.tpsdk.opengl.filter;///*
// *
// * Beauty.java
// *
// * Created by Wuwang on 2016/11/18
// * Copyright © 2016年 深圳哎吖科技. All rights reserved.
// */
//package com.igrs.opengl.filter;
//
//import android.opengl.GLES11Ext;
//import android.opengl.GLES20;
//import android.opengl.Matrix;
//import android.util.Log;
//
//
//
///**
// * Description:
// */
//public class BeautyFilter extends AFilter {
//
//
//
//
//    private int mParamsLocation;
//    private int mGLAttribPosition;
//    private int mTexturetransformMatrixlocation;
//    private int mSingleStepOffsetLocation;
//
//    private int mGLUniformTexture;
//    private int mGLAttribTextureCoordinate;
//
//    public BeautyFilter() {
//        super();
//        setFlag(0);
//    }
//
//    String vertex="#version 300 es\n" +
//            "\n" +
//            "layout(location=0) in vec4 position;\n" +
//            "layout(location=1) in vec4 inputTextureCoordinate;\n" +
//            "\n" +
//            "uniform mat4 textureTransform;\n" +
//            "out vec2 textureCoordinate;\n" +
//            "\n" +
//            "void main() {\n" +
//            "    gl_Position = position;\n" +
//            "    textureCoordinate = (textureTransform *inputTextureCoordinate).xy;\n" +
//            "}";
//    String fragment="#version 300 es\n" +
//            "#extension GL_OES_EGL_image_external_essl3 : require\n" +
//            "\n" +
//            "precision mediump float;\n" +
//            "\n" +
//            "in mediump vec2 textureCoordinate;\n" +
//            "\n" +
//            "uniform samplerExternalOES inputImageTexture;\n" +
//            "uniform vec2 singleStepOffset;\n" +
//            "uniform mediump float params;\n" +
//            "\n" +
//            "const highp vec3 W = vec3(0.299,0.587,0.114);\n" +
//            "//创建20个点取数据\n" +
//            "vec2 blurCoordinates[20];\n" +
//            "\n" +
//            "out vec4 glFragColor;\n" +
//            "\n" +
//            "//强光处理\n" +
//            "float hardLight(float color)\n" +
//            "{\n" +
//            "\tif(color <= 0.5)\n" +
//            "\t\tcolor = color * color * 2.0;\n" +
//            "\telse  //叠加混合模式\n" +
//            "\t\tcolor = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);\n" +
//            "\treturn color;\n" +
//            "}\n" +
//            "\n" +
//            "void main(){\n" +
//            "\n" +
//            "    vec3 centralColor = texture(inputImageTexture, textureCoordinate).rgb;\n" +
//            "    if(params != 0.0){\n" +
//            "        //取出20个点的绿色通道值\n" +
//            "        blurCoordinates[0] = textureCoordinate.xy + singleStepOffset * vec2(0.0, -10.0);\n" +
//            "        blurCoordinates[1] = textureCoordinate.xy + singleStepOffset * vec2(0.0, 10.0);\n" +
//            "        blurCoordinates[2] = textureCoordinate.xy + singleStepOffset * vec2(-10.0, 0.0);\n" +
//            "        blurCoordinates[3] = textureCoordinate.xy + singleStepOffset * vec2(10.0, 0.0);\n" +
//            "        blurCoordinates[4] = textureCoordinate.xy + singleStepOffset * vec2(5.0, -8.0);\n" +
//            "        blurCoordinates[5] = textureCoordinate.xy + singleStepOffset * vec2(5.0, 8.0);\n" +
//            "        blurCoordinates[6] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, 8.0);\n" +
//            "        blurCoordinates[7] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, -8.0);\n" +
//            "        blurCoordinates[8] = textureCoordinate.xy + singleStepOffset * vec2(8.0, -5.0);\n" +
//            "        blurCoordinates[9] = textureCoordinate.xy + singleStepOffset * vec2(8.0, 5.0);\n" +
//            "        blurCoordinates[10] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, 5.0);\n" +
//            "        blurCoordinates[11] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, -5.0);\n" +
//            "        blurCoordinates[12] = textureCoordinate.xy + singleStepOffset * vec2(0.0, -6.0);\n" +
//            "        blurCoordinates[13] = textureCoordinate.xy + singleStepOffset * vec2(0.0, 6.0);\n" +
//            "        blurCoordinates[14] = textureCoordinate.xy + singleStepOffset * vec2(6.0, 0.0);\n" +
//            "        blurCoordinates[15] = textureCoordinate.xy + singleStepOffset * vec2(-6.0, 0.0);\n" +
//            "        blurCoordinates[16] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, -4.0);\n" +
//            "        blurCoordinates[17] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, 4.0);\n" +
//            "        blurCoordinates[18] = textureCoordinate.xy + singleStepOffset * vec2(4.0, -4.0);\n" +
//            "        blurCoordinates[19] = textureCoordinate.xy + singleStepOffset * vec2(4.0, 4.0);\n" +
//            "\n" +
//            "        float sampleColor = centralColor.g * 20.0;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[0]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[1]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[2]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[3]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[4]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[5]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[6]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[7]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[8]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[9]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[10]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[11]).g;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[12]).g * 2.0;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[13]).g * 2.0;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[14]).g * 2.0;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[15]).g * 2.0;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[16]).g * 2.0;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[17]).g * 2.0;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[18]).g * 2.0;\n" +
//            "        sampleColor += texture(inputImageTexture, blurCoordinates[19]).g * 2.0;\n" +
//            "\n" +
//            "        sampleColor = sampleColor / 48.0;\n" +
//            "        //用原图绿色通道值减去SampleColor,加上0.5（即128），得到PS中的高反差保留\n" +
//            "        float highPass = centralColor.g - sampleColor + 0.5;\n" +
//            "        //进行5次强光处理（叠加混合模式）\n" +
//            "        for(int i = 0; i < 5;i++)\n" +
//            "        {\n" +
//            "            highPass = hardLight(highPass);\n" +
//            "        }\n" +
//            "        //计算原图的灰度值\n" +
//            "        float luminance = dot(centralColor, W);\n" +
//            "        //将灰度值作为阈值，用于排除非皮肤部分，根据灰度值计算，将\n" +
//            "        float alpha = pow(luminance, params);\n" +
//            "\n" +
//            "        vec3 smoothColor = centralColor + (centralColor-vec3(highPass))*alpha*0.1;\n" +
//            "        //以灰度值作为透明度将原图与混合后结果进行滤色、柔光等混合，并调节饱和度\n" +
//            "        glFragColor = vec4(mix(smoothColor.rgb, max(smoothColor, centralColor), alpha), 1.0);\n" +
//            "    }else{\n" +
//            "        glFragColor = vec4(centralColor.rgb,1.0);\n" +
//            "    }\n" +
//            "}";
//
//    @Override
//    protected void onCreate() {
//        createProgram(vertex, fragment);
//
//
//        //获取顶点着色器
//        mGLAttribPosition = GLES20.glGetAttribLocation(mProgram,"position");
//        if (mGLAttribPosition <0){
//            L.e("mGLAttribPosition is illegal.");
//        }
//        //获取混合顶点着色器
//        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mProgram,"inputTextureCoordinate");
//        if (mGLAttribTextureCoordinate < 0){
//            L.e("mGLAttribTexureCoordinate is illegal.");
//        }
//
//        //获取纹理统一变量索引
//        mGLUniformTexture = GLES20.glGetUniformLocation(mProgram,"inputImageTexture");
//
//
////    mMatrixLoc = glGetUniformLocation(mGLProgId,"textureTransform");
//        //初始化成功标志
//
//        mTexturetransformMatrixlocation = GLES20.glGetUniformLocation(mProgram,"textureTransform");
//        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(mProgram,"singleStepOffset");
//        mParamsLocation = GLES20.glGetUniformLocation(mProgram,"params");
////    glUniform1f(mParamsLocation,0.0f);
//
//    }
//
//    int beautyLevel=0;
//    @Override
//    public void setFlag(int flag) {
//        this.beautyLevel = flag;
//    }
//
//
//    private void setBeautyLevelOnDraw(int beautyLevel){
//        switch (beautyLevel){
//            case 0:
//                GLES20.glUniform1f(mParamsLocation,0.0f);
//                break;
//            case 1:
//                GLES20.glUniform1f(mParamsLocation,1.0f);
//                break;
//            case 2:
//                GLES20.glUniform1f(mParamsLocation,0.8f);
//                break;
//            case 3:
//                GLES20.glUniform1f(mParamsLocation,0.6f);
//                break;
//            case 4:
//                GLES20.glUniform1f(mParamsLocation,0.4f);
//                break;
//            case 5:
//                GLES20.glUniform1f(mParamsLocation,0.33f);
//                break;
//            default:
//                break;
//        }
//    }
//
//    private int mWidth;
//    private int mHeight;
//    @Override
//    protected void onSizeChanged(int width, int height) {
//        Log.i("beauty-filter:", "zxq_dbg: beauty-filter onSizeChanged "+width+"x"+height);
//        this.mWidth=width;
//        this.mHeight=height;
//    }
//
//
//    int mFrameBuffer;
//    int mFrameBufferTextures;
//    public void draw(int textureId, float[] mMatrix) {
//            //视口切换
//            GLES20.glViewport(0,0,mWidth,mHeight);
//            //绑定帧缓冲id
//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFrameBuffer);
//            GLES20.glUseProgram(mProgram);
//
//            //顶点缓冲
//            GLES20.glVertexAttribPointer(mGLAttribPosition,2,GLES20.GL_FLOAT,GLES20.GL_FALSE,0,mGLCubeBuffer);
//            GLES20.glEnableVertexAttribArray(mGLAttribPosition);
//            GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate,2,GLES20.GL_FLOAT,GLES20.GL_FALSE,0,mGLTextureBuffer);
//            GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
//            GLES20.glUniformMatrix4fv(mTexturetransformMatrixlocation,1,GLES20.GL_FALSE,matrix);
//            //设置美颜等级
//            setBeautyLevelOnDraw(beautyLevel);
//            GLES20.glUniform2f(mSingleStepOffsetLocation,2.0f/mWidth,2.0f/mHeight);
//            //加载矩阵
//            //    glUniformMatrix4fv(mMatrixLoc,1,GL_FALSE,matrix);
//
//            if (textureId != -1){
//                //绑定纹理
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);
//                GLES20.glUniform1i(mGLUniformTexture,0);
//            }
//            //绘制图像（长方形）
//            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
//            //关闭顶点缓冲
//            GLES20.glDisableVertexAttribArray(mGLAttribPosition);
//            GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
//            //切换回默认纹理
//            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0);
//            //切换回默认帧缓冲
//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
//    }
//
//    @Override
//    protected void onSetExpandData() {
//        super.onSetExpandData();
////        GLES20.glUniform1i(gHWidth,mWidth);
////        GLES20.glUniform1i(gHHeight,mHeight);
////        GLES20.glUniform1f(gHaaCoef,aaCoef);
////        GLES20.glUniform1f(gHmixCoef,mixCoef);
////        GLES20.glUniform1i(gHiternum,iternum);
//    }
//}
