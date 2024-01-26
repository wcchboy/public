package com.wcch.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import org.jetbrains.annotations.Nullable;

/**
 *色轮调色板： 圆形的色板
 * 该视图以圆形的形式展示了颜色，其中包括一个色相（Hue）环和一个饱和度（Saturation）环。这种颜色选择器通常被用于选择颜色的色相和饱和度值。
 */
public class ColorWheelPalette extends View {

    private float radius;//圆的半径
    private float centerX;//圆心的坐标
    private float centerY;//圆心的坐标

    private Paint huePaint;//用于绘制色相环的画笔。
    private Paint saturationPaint;//用于绘制饱和度环的画笔

    public ColorWheelPalette(Context context) {
        this(context, null);
    }

    public ColorWheelPalette(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorWheelPalette(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        huePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//该标志用于抗锯齿，使得图形边缘更加平滑
        saturationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//该标志用于抗锯齿，使得图形边缘更加平滑
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int netWidth = w - getPaddingLeft() - getPaddingRight();
        int netHeight = h - getPaddingTop() - getPaddingBottom();
        radius = Math.min(netWidth, netHeight) * 0.5f;
        if (radius < 0) return;
        centerX = w * 0.5f;
        centerY = h * 0.5f;
        //huePaint 使用 SweepGradient 渲染色相环，通过设置颜色数组来定义渐变色
        Shader hueShader = new SweepGradient(centerX, centerY,
                new int[]{Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED},
                null);
        huePaint.setShader(hueShader);
        // saturationPaint 使用 RadialGradient 渲染饱和度环，创建一个从中心到边缘的径向渐变。
        Shader saturationShader = new RadialGradient(centerX, centerY, radius,
                Color.WHITE, 0x00FFFFFF, Shader.TileMode.CLAMP);
        saturationPaint.setShader(saturationShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //通过 canvas.drawCircle 绘制了色相环和饱和度环，形成了整个颜色选择器的效果
        canvas.drawCircle(centerX, centerY, radius, huePaint);
        canvas.drawCircle(centerX, centerY, radius, saturationPaint);
    }
}
