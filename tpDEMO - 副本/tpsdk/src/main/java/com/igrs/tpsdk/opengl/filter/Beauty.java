/*
 *
 * Beauty.java
 * 
 * Created by Wuwang on 2016/11/18
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.igrs.tpsdk.opengl.filter;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Description:
 */
public class Beauty extends AFilter {

    private int gHaaCoef;
    private int gHmixCoef;
    private int gHiternum;
    private int gHWidth;
    private int gHHeight;

    private float aaCoef;
    private float mixCoef;
    private int iternum;

    private int mWidth=720;
    private int mHeight=1280;


    String vertex="attribute vec4 vPosition;\n" +
            "attribute vec2 vCoord;\n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 blurCoord1s[14];\n" +
            "const highp float mWidth=720.0;\n" +
            "const highp float mHeight=1280.0;\n" +
            "uniform mat4 vMatrix;\n" +
            "void main( )\n" +
            "{\n" +
            "    gl_Position = vMatrix*vPosition;\n" +
            "    textureCoordinate = vCoord;\n" +
            "\n" +
            "    highp float mul_x = 2.0 / mWidth;\n" +
            "    highp float mul_y = 2.0 / mHeight;\n" +
            "\n" +
            "    // 14个采样点\n" +
            "    blurCoord1s[0] = vCoord + vec2( 0.0 * mul_x, -10.0 * mul_y );\n" +
            "    blurCoord1s[1] = vCoord + vec2( 8.0 * mul_x, -5.0 * mul_y );\n" +
            "    blurCoord1s[2] = vCoord + vec2( 8.0 * mul_x, 5.0 * mul_y );\n" +
            "    blurCoord1s[3] = vCoord + vec2( 0.0 * mul_x, 10.0 * mul_y );\n" +
            "    blurCoord1s[4] = vCoord + vec2( -8.0 * mul_x, 5.0 * mul_y );\n" +
            "    blurCoord1s[5] = vCoord + vec2( -8.0 * mul_x, -5.0 * mul_y );\n" +
            "    blurCoord1s[6] = vCoord + vec2( 0.0 * mul_x, -6.0 * mul_y );\n" +
            "    blurCoord1s[7] = vCoord + vec2( -4.0 * mul_x, -4.0 * mul_y );\n" +
            "    blurCoord1s[8] = vCoord + vec2( -6.0 * mul_x, 0.0 * mul_y );\n" +
            "    blurCoord1s[9] = vCoord + vec2( -4.0 * mul_x, 4.0 * mul_y );\n" +
            "    blurCoord1s[10] = vCoord + vec2( 0.0 * mul_x, 6.0 * mul_y );\n" +
            "    blurCoord1s[11] = vCoord + vec2( 4.0 * mul_x, 4.0 * mul_y );\n" +
            "    blurCoord1s[12] = vCoord + vec2( 6.0 * mul_x, 0.0 * mul_y );\n" +
            "    blurCoord1s[13] = vCoord + vec2( 4.0 * mul_x, -4.0 * mul_y );\n" +
            "}";
    String fragment="precision lowp float;\n" +
            "precision lowp int;\n" +
            "uniform sampler2D vTexture;\n" +
            "uniform int iternum;\n" +
            "uniform float aaCoef; //参数\n" +
            "uniform float mixCoef; //混合系数\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "varying highp vec2 blurCoord1s[14];\n" +
            "const float distanceNormalizationFactor = 4.0;    //标准化距离因子常量\n" +
            "const mat3 saturateMatrix = mat3(1.1102,-0.0598,-0.061,-0.0774,1.0826,-0.1186,-0.0228,-0.0228,1.1772);\n" +
            "\n" +
            "void main( ) {\n" +
            "\n" +
            "    vec3 centralColor;\n" +
            "    float central;\n" +
            "    float gaussianWeightTotal;\n" +
            "    float sum;\n" +
            "    float sampleColor;\n" +
            "    float distanceFromCentralColor;\n" +
            "    float gaussianWeight;\n" +
            "\n" +
            "    //通过绿色通道来磨皮\n" +
            "    //取得当前点颜色的绿色通道\n" +
            "    central = texture2D( vTexture, textureCoordinate ).g;\n" +
            "    //高斯权重\n" +
            "    gaussianWeightTotal = 0.2;\n" +
            "    //绿色通道色彩记数\n" +
            "    sum = central * 0.2;\n" +
            "\n" +
            "    // 计算各个采样点处的高斯权重，包括密闭性和相似性\n" +
            "    for (int i = 0; i < 6; i++) {\n" +
            "        //采样点的绿色通道\n" +
            "        sampleColor = texture2D( vTexture, blurCoord1s[i] ).g;\n" +
            "        //采样点和计算点的颜色差\n" +
            "        distanceFromCentralColor = min( abs( central - sampleColor ) * distanceNormalizationFactor, 1.0 );\n" +
            "        //高斯权重\n" +
            "        gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n" +
            "        //高斯权重总和\n" +
            "        gaussianWeightTotal += gaussianWeight;\n" +
            "        //绿色通道色彩记数累计\n" +
            "        sum += sampleColor * gaussianWeight;\n" +
            "    }\n" +
            "    for (int i = 6; i < 14; i++) {\n" +
            "        //采样点的绿色通道\n" +
            "        sampleColor = texture2D( vTexture, blurCoord1s[i] ).g;\n" +
            "        //采样点和计算点的颜色差\n" +
            "        distanceFromCentralColor = min( abs( central - sampleColor ) * distanceNormalizationFactor, 1.0 );\n" +
            "        //高斯权重\n" +
            "        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);\n" +
            "        //高斯权重总和\n" +
            "        gaussianWeightTotal += gaussianWeight;\n" +
            "        //绿色通道色彩记数累计\n" +
            "        sum += sampleColor * gaussianWeight;\n" +
            "    }\n" +
            "\n" +
            "    //采样后的绿色通道色彩均值\n" +
            "    sum = sum / gaussianWeightTotal;\n" +
            "\n" +
            "    //取得当前点的颜色\n" +
            "    centralColor = texture2D( vTexture, textureCoordinate ).rgb;\n" +
            "    //采样值\n" +
            "    sampleColor = centralColor.g - sum + 0.5;\n" +
            "    //迭代计算\n" +
            "    for (int i = 0; i < iternum; ++i) {\n" +
            "        if (sampleColor <= 0.5) {\n" +
            "            sampleColor = sampleColor * sampleColor * 2.0;\n" +
            "        }else {\n" +
            "            sampleColor = 1.0 - ((1.0 - sampleColor)*(1.0 - sampleColor) * 2.0);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    float aa = 1.0 + pow( centralColor.g, 0.3 )*aaCoef;\n" +
            "    vec3 smoothColor = centralColor*aa - vec3( sampleColor )*(aa - 1.0);\n" +
            "    smoothColor = clamp( smoothColor, vec3( 0.0 ), vec3( 1.0 ) );\n" +
            "    smoothColor = mix( centralColor, smoothColor, pow( centralColor.g, 0.33 ) );\n" +
            "    smoothColor = mix( centralColor, smoothColor, pow( centralColor.g, mixCoef ) );\n" +
            "    gl_FragColor = vec4( pow( smoothColor, vec3( 0.96 ) ), 1.0 );\n" +
            "    vec3 satcolor = gl_FragColor.rgb * saturateMatrix;\n" +
            "    gl_FragColor.rgb = mix( gl_FragColor.rgb, satcolor, 0.23 );\n" +
            "\n" +
            "}";

    public Beauty() {
        super();
    }

    @Override
    protected void onCreate() {
        createProgram(vertex, fragment);
        gHaaCoef= GLES20.glGetUniformLocation(mProgram,"aaCoef");
        gHmixCoef= GLES20.glGetUniformLocation(mProgram,"mixCoef");
        gHiternum= GLES20.glGetUniformLocation(mProgram,"iternum");
        gHWidth= GLES20.glGetUniformLocation(mProgram,"mWidth");
        gHHeight= GLES20.glGetUniformLocation(mProgram,"mHeight");
    }

    @Override
    public void setFlag(int flag) {
        //super.setFlag(flag);
        switch (flag){
            case 1:
                a(1,0.19f,0.54f);
                break;
            case 2:
                a(2,0.29f,0.54f);
                break;
            case 3:
                a(3,0.17f,0.39f);
                break;
            case 4:
                a(3,0.25f,0.54f);
                break;
            case 5:
                a(4,0.13f,0.54f);
                break;
            case 6:
                a(4,0.29f,0.69f);
                break;
            default:
                a(0,0f,0f);
                break;
        }
    }

    private void a(int a,float b,float c){
        this.iternum=a;
        this.aaCoef=b;
        this.mixCoef=c;
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        Log.i("beauty-filter:", "zxq_dbg: beauty-filter onSizeChanged "+width+"x"+height);
        this.mWidth=width;
        this.mHeight=height;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniform1i(gHWidth,mWidth);
        GLES20.glUniform1i(gHHeight,mHeight);
        GLES20.glUniform1f(gHaaCoef,aaCoef);
        GLES20.glUniform1f(gHmixCoef,mixCoef);
        GLES20.glUniform1i(gHiternum,iternum);
    }
}
