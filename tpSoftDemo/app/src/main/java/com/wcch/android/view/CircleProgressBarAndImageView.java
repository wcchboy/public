package com.wcch.android.view;

/**
 *
 * @author Created by RyanWang on 2023/1/19
 * Copyright © 2023年 IGRS. All rights reserved.
 * Describe:
 */

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.wcch.android.R;

/**
 * 圆形进度条中间含旋转的图片
 */
public class CircleProgressBarAndImageView extends RelativeLayout {
    private Context context;
    private CircleProgressBar progressBar;
    private CircleImageView imageView;
    private View view;
    private int imageWidth,imageHeight;
    public CircleProgressBarAndImageView(Context context) {
        super(context);
    }
    public CircleProgressBarAndImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressBarAndImageView, 0 , 0);
        imageWidth = typeArray.getColor(R.styleable.CircleProgressBarAndImageView_CircleProgressbar_imageWidth, 58);//获取自定义图片宽度
        imageHeight = typeArray.getColor(R.styleable.CircleProgressBarAndImageView_CircleProgressbar_imageHeight, 58);//获取自定义图片高度

        view= LayoutInflater.from(context).inflate(R.layout.audioplayer_progress_image,this,true);
        progressBar=view.findViewById(R.id.cicle_progressBar);
        imageView=view.findViewById(R.id.cicle_image);
    }

    /**
     * 设置进度
     * @param i
     */
    public void setProgress(int i){
        progressBar.setProgress(i);
    }
    /**
     * 圆形图片开始旋转
     */
    ObjectAnimator objectAnimator;
    public void imageStartAnimation() {
        imageStopAnimation();

        if(objectAnimator==null) {
            objectAnimator = ObjectAnimator.ofFloat(
                    imageView, "rotation", 0f, 360f);
            objectAnimator.setDuration(7000);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
        objectAnimator.start();
    }

    /**
     * 圆形图片停止旋转
     */
    public void imageStopAnimation() {
        if(objectAnimator!=null) {
            objectAnimator.end();
        }
    }
    /**
     * 给圆形图片控件设置图片
     * @param drawable
     */
    public void setDrawable(Drawable drawable){
        imageView.setImageDrawable(drawable);
    }
}