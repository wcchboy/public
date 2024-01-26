package com.wcch.colorpicker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.wcch.colorpicker.R;



/**
 *  色轮选择器
 *  用于在颜色环中选择颜色。
 *
 */
public class ColorWheelSelectorByImage extends View {

    private Drawable selectorDrawable; // 用于绘制选择器的小图片
    private int selectorSize; // 图片大小
    private PointF currentPoint = new PointF(); // 选择器的当前位置，即选择器的中心点坐标。

    public ColorWheelSelectorByImage(Context context) {
        this(context, null);
    }

    public ColorWheelSelectorByImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorWheelSelectorByImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化选择器图片
        selectorDrawable = context.getDrawable(R.drawable.color_selection_circle_919); // 替换为你的选择器图片资源
        selectorSize = selectorDrawable.getIntrinsicWidth(); // 使用图片的宽度作为大小
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 获取视图中心坐标
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // 计算图片的绘制位置
        int left = (int) (currentPoint.x - selectorSize / 2);
        int top = (int) (currentPoint.y - selectorSize / 2);
        int right = left + selectorSize;
        int bottom = top + selectorSize;

        // 绘制图片
        selectorDrawable.setBounds(left, top, right, bottom);
        selectorDrawable.draw(canvas);
    }
    public void setIconSize(int selectorSize) {
        this.selectorSize = selectorSize;
    }

    /**
     * 提供了 setCurrentPoint 方法，用于设置选择器的当前位置，并调用 invalidate() 使得视图得以刷新。
     * @param currentPoint
     */
    public void setCurrentPoint(PointF currentPoint) {
        this.currentPoint = currentPoint;
        invalidate();
    }
}
