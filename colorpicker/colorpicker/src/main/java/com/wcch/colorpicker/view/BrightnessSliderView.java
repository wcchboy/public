package com.wcch.colorpicker.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.wcch.colorpicker.ColorSliderView;

import org.jetbrains.annotations.Nullable;

/**
 * 亮度滑块视图
 */
public class BrightnessSliderView extends ColorSliderView {

    public BrightnessSliderView(Context context)
    {
        super(context);
    }

    public BrightnessSliderView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BrightnessSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 用于根据颜色值解析出亮度值。在这里，通过将颜色转换为 HSV（色调、饱和度、亮度） 表示法，然后返回亮度值 hsv[2]。
     * @param color 通过将颜色转换为 HSV（色调、饱和度、亮度） 表示法
     * @return 返回亮度值 hsv[2]
     */
    @Override
    protected float resolveValue(int color)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[2];
    }

    /**
     * 配置画笔的方法，该方法用于设置画笔的渐变效果，以显示亮度值的变化。在这里，通过设置渐变色，从亮度值为 0 的颜色到亮度值为 1 的颜色。
     * @param colorPaint
     */
    protected void configurePaint(Paint colorPaint)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        hsv[2] = 0;
        int startColor = Color.HSVToColor(hsv);
        hsv[2] = 1;
        int endColor = Color.HSVToColor(hsv);
        Shader shader = new LinearGradient(0, 0, getWidth(), getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
        colorPaint.setShader(shader);
    }

    /**
     * 组装颜色的方法，根据当前的亮度值组装出颜色。同样，通过将颜色转换为 HSV 表示法，然后将亮度值设为 currentValue，
     * 最后通过 Color.HSVToColor 转换为颜色值。
     * @return
     */
    protected int assembleColor()
    {
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        hsv[2] = currentValue;
        return Color.HSVToColor(hsv);
    }
}
