package com.wcch.android.view;

/**
 * normDemo
 *
 * @author Created by RyanWang on 2023/1/19
 * Copyright © 2023年 IGRS. All rights reserved.
 * Describe:
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.wcch.android.R;


/**
 * 圆形进度条
 */
public class CircleProgressBar extends View {
    // 画圆环的画笔
    private Paint ringPaintProgress;
    //圆环外面的阴影
    private Paint mRingPaintBg;
    // 画字体的画笔
    // 圆环颜色
    private int ringColor,strokeBgColor;
    // 半径
    private float radius;
    // 圆环宽度
    private float strokeWidth;
    // 总进度
    private int totalProgress = 100;
    // 当前进度
    private int currentProgress=0;
    // 透明度
    private int alpha = 255;
    private int width,height;
    private RectF mRectF;
    private int imageWidth,imageHeight;
    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        initVariable();
    }
    //获取自定义的一些参数
    private void initAttrs(Context context, AttributeSet attrs) {
        //圆形进度条
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, 0 , 0);
        //圆进度条半径
        radius = typeArray.getDimension(R.styleable.CircleProgressBar_CircleProgressbar_radius, 70);
        //圆进度条笔划宽度
        strokeWidth = typeArray.getDimension(R.styleable.CircleProgressBar_CircleProgressbar_strokeWidth, 6);
        //圆形进度条环颜色
        ringColor = typeArray.getColor(R.styleable.CircleProgressBar_CircleProgressbar_ringColor, 0xFF4800);
        //背景颜色
        strokeBgColor = typeArray.getColor(R.styleable.CircleProgressBar_CircleProgressbar_strokeBgColor, Color.GRAY);
    }

    private void initVariable() {
        ringPaintProgress = new Paint();
        ringPaintProgress.setAntiAlias(true);
        ringPaintProgress.setDither(true);
        ringPaintProgress.setColor(ringColor);
        ringPaintProgress.setStyle(Paint.Style.STROKE);
        ringPaintProgress.setStrokeCap(Paint.Cap.ROUND);
        ringPaintProgress.setStrokeWidth(strokeWidth);


        mRingPaintBg = new Paint();
        mRingPaintBg.setAntiAlias(true);
        mRingPaintBg.setColor(strokeBgColor);
        //mRingPaintBg.setMaskFilter(new BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mRingPaintBg.setColor(Color.argb((float) 0.07,0,0,0));
        }
        mRingPaintBg.setStyle(Paint.Style.STROKE);
        mRingPaintBg.setStrokeWidth(4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //画阴影
            mRingPaintBg.setShadowLayer(3f, 0, 0, Color.GRAY);
        }
    }
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取当前View的宽高
        width=this.getWidth();
        height=this.getHeight();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (currentProgress >= 0) {
            mRectF = new RectF(getWidth() / 2 - radius, getHeight() / 2 - radius, getWidth() / 2 + radius, getHeight() / 2 + radius);
            canvas.drawArc(mRectF, -90, 360, false, mRingPaintBg);//画圆环背景包含阴影
            //画进度条
            canvas.drawArc(mRectF, -90, ((float) currentProgress / totalProgress) * 360, false, ringPaintProgress);//画进度条
        }
    }

    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(int progress) {
        currentProgress = progress;
        postInvalidate();//设置完进度重新刷新绘制
    }

}