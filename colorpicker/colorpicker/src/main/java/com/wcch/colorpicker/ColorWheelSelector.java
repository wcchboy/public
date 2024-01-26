package com.wcch.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;


import org.jetbrains.annotations.Nullable;

import static com.wcch.colorpicker.Constants.SELECTOR_RADIUS_DP;

/**
 *  色轮选择器
 *  用于在颜色环中选择颜色。这里是一个瞄准镜的样子
 *
 */
public class ColorWheelSelector extends View {

    private Paint selectorPaint;//是一个画笔，用于绘制选择器的线和圆
    private float selectorRadiusPx = SELECTOR_RADIUS_DP * 3;//用于设置选择器的半径。
    private PointF currentPoint = new PointF();//是选择器的当前位置，即选择器的中心点坐标。

    public ColorWheelSelector(Context context) {
        this(context, null);
    }

    public ColorWheelSelector(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorWheelSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        selectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectorPaint.setColor(Color.BLACK);
        selectorPaint.setStyle(Paint.Style.STROKE);
        selectorPaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //通过 Canvas 的 drawLine 和 drawCircle 方法绘制了选择器的线和圆。setSelectorRadiusPx 方法用于设置选择器的半径，而 setCurrentPoint 方法用于设置选择器的当前位置。
        canvas.drawLine(currentPoint.x - selectorRadiusPx, currentPoint.y,
                currentPoint.x + selectorRadiusPx, currentPoint.y, selectorPaint);
        canvas.drawLine(currentPoint.x, currentPoint.y - selectorRadiusPx,
                currentPoint.x, currentPoint.y + selectorRadiusPx, selectorPaint);
        canvas.drawCircle(currentPoint.x, currentPoint.y, selectorRadiusPx * 0.66f, selectorPaint);
    }

    public void setSelectorRadiusPx(float selectorRadiusPx) {
        this.selectorRadiusPx = selectorRadiusPx;
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
