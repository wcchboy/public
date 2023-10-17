/*
 *
 * FastDrawerHelper.java
 * 
 * Created by Wuwang on 2016/11/17
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.igrs.tpsdk.opengl.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.Matrix;
import android.text.TextUtils;

/**
 * Description:
 */
public enum MatrixUtils {
    ;
    public static final int TYPE_FITXY=0;
    public static final int TYPE_CENTERCROP=1;
    public static final int TYPE_CENTERINSIDE=2;
    public static final int TYPE_FITSTART=3;
    public static final int TYPE_FITEND=4;

    MatrixUtils(){

    }

    /**
     * use {@link #getMatrix} instead
     */
    @Deprecated
    public static void getShowMatrix(float[] matrix,int imgWidth,int imgHeight,int viewWidth,int
        viewHeight){
        if(imgHeight>0&&imgWidth>0&&viewWidth>0&&viewHeight>0){
            float sWhView=(float)viewWidth/viewHeight;
            float sWhImg=(float)imgWidth/imgHeight;
            float[] projection=new float[16];
            float[] camera=new float[16];
            if(sWhImg>sWhView){
                Matrix.orthoM(projection,0,-sWhView/sWhImg,sWhView/sWhImg,-1,1,1,3);
            }else{
                Matrix.orthoM(projection,0,-1,1,-sWhImg/sWhView,sWhImg/sWhView,1,3);
            }
            Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
            Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        }
    }

    public static void getMatrix(float[] matrix,int type,int imgWidth,int imgHeight,int viewWidth,
                                 int viewHeight){
        if(imgHeight>0&&imgWidth>0&&viewWidth>0&&viewHeight>0){
            float[] projection=new float[16];
            float[] camera=new float[16];
            if(type==TYPE_FITXY){
                Matrix.orthoM(projection,0,-1,1,-1,1,1,3);
                Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
                Matrix.multiplyMM(matrix,0,projection,0,camera,0);
            }
            float sWhView=(float)viewWidth/viewHeight;
            float sWhImg=(float)imgWidth/imgHeight;
            if(sWhImg>sWhView){
                switch (type){
                    case TYPE_CENTERCROP:
                        Matrix.orthoM(projection,0,-sWhView/sWhImg,sWhView/sWhImg,-1,1,1,3);
                        break;
                    case TYPE_CENTERINSIDE:
                        Matrix.orthoM(projection,0,-1,1,-sWhImg/sWhView,sWhImg/sWhView,1,3);
                        break;
                    case TYPE_FITSTART:
                        Matrix.orthoM(projection,0,-1,1,1-2*sWhImg/sWhView,1,1,3);
                        break;
                    case TYPE_FITEND:
                        Matrix.orthoM(projection,0,-1,1,-1,2*sWhImg/sWhView-1,1,3);
                        break;
                }
            }else{
                switch (type){
                    case TYPE_CENTERCROP:
                        Matrix.orthoM(projection,0,-1,1,-sWhImg/sWhView,sWhImg/sWhView,1,3);
                        break;
                    case TYPE_CENTERINSIDE:
                        Matrix.orthoM(projection,0,-sWhView/sWhImg,sWhView/sWhImg,-1,1,1,3);
                        break;
                    case TYPE_FITSTART:
                        Matrix.orthoM(projection,0,-1,2*sWhView/sWhImg-1,-1,1,1,3);
                        break;
                    case TYPE_FITEND:
                        Matrix.orthoM(projection,0,1-2*sWhView/sWhImg,1,-1,1,1,3);
                        break;
                }
            }
            Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
            Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        }
    }

    public static void getCenterInsideMatrix(float[] matrix,int imgWidth,int imgHeight,int viewWidth,int
            viewHeight){
        if(imgHeight>0&&imgWidth>0&&viewWidth>0&&viewHeight>0){
            float sWhView=(float)viewWidth/viewHeight;
            float sWhImg=(float)imgWidth/imgHeight;
            float[] projection=new float[16];
            float[] camera=new float[16];
            if(sWhImg>sWhView){
                Matrix.orthoM(projection,0,-1,1,-sWhImg/sWhView,sWhImg/sWhView,1,3);
            }else{
                Matrix.orthoM(projection,0,-sWhView/sWhImg,sWhView/sWhImg,-1,1,1,3);
            }
            Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
            Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        }
    }

    public static float[] rotate(float[] m,float angle){
        Matrix.rotateM(m,0,angle,0,0,1);
        return m;
    }

    public static float[] flip(float[] m,boolean x,boolean y){
        if(x||y){
            Matrix.scaleM(m,0,x?-1:1,y?-1:1,1);
        }
        return m;
    }

    public static float[] scale(float[] m,float x,float y){
        Matrix.scaleM(m,0,x,y,1);
        return m;
    }

    public static float[] getOriginalMatrix(){
        return new float[]{
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1
        };
    }

    public static Bitmap setTitle1(String title) {
        if (TextUtils.isEmpty(title)) {
            title = "未填写姓名";
        } else if (title.length() > 17) {
            //title = title.substring(0, 16);
        } else {
            title = title + "".trim();
        }
        Paint p = new Paint();//Paint.ANTI_ALIAS_FLAG
        p.setColor(Color.WHITE);
        p.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        //p.setTextSize(34);
        p.setTextSize(24);
        p.setAntiAlias(true);//抗锯齿
        p.setDither(true);//防抖动
        p.setSubpixelText(true);//设置次像素级抗锯齿，根据程序所运行的设备的屏幕类型，来进行针对性的次像素级的抗锯齿计算，从而达到更好的抗锯齿效果。
        Rect rect = new Rect();
        p.getTextBounds(title, 0, title.length(), rect);

        Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888/*, true*/);
        Canvas canvasTemp = new Canvas(bitmap);
        int x=bitmap.getWidth()  - rect.width() - rect.left;
        int y=bitmap.getHeight() - rect.height() - rect.top;

        canvasTemp.drawText(title,x , y, p);

        return bitmap;
    }
    public static Bitmap setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            title = "未填写姓名";
        } else if (title.length() > 17) {
            title = title.substring(0, 16);
        } else {
            title = title + "".trim();
        }
        Paint p = new Paint();//Paint.ANTI_ALIAS_FLAG
        p.setColor(Color.WHITE);
        p.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        p.setTextSize(24);
        p.setAntiAlias(true);//抗锯齿
        p.setDither(true);//防抖动
        p.setSubpixelText(true);//设置次像素级抗锯齿，根据程序所运行的设备的屏幕类型，来进行针对性的次像素级的抗锯齿计算，从而达到更好的抗锯齿效果。
        Rect rect = new Rect();
        p.getTextBounds(title, 0, title.length(), rect);

        Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888/*, true*/);
        Canvas canvasTemp = new Canvas(bitmap);
        int x=bitmap.getWidth()  - rect.width() - rect.left;
        int y=bitmap.getHeight() - rect.height() - rect.top;

        canvasTemp.drawText(title,x , y, p);

        return horverImage(bitmap,false,true);
    }
    public static Bitmap horverImage(Bitmap bitmap, boolean H, boolean V)
    {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        if (H)
            matrix.postScale(-1, 1);   //水平翻转H
        if (V)
            matrix.postScale(1, -1);   //垂直翻转V
        if (H && V)
            matrix.postScale(-1, -1);   //水平&垂直翻转HV
        return Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
    }
}
