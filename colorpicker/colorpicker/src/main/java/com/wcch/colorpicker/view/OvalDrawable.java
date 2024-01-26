package com.wcch.colorpicker.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
/**
 * Created by RyanWang on 2023/12/22.
 *
 * @Description:
 */
public class OvalDrawable extends Drawable {

    private Paint paint;

    public OvalDrawable(int backgroundColor) {
        paint = new Paint();
        paint.setColor(backgroundColor); // 设置椭圆的背景颜色
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        float centerX = bounds.centerX();
        float centerY = bounds.centerY();
        float halfWidth = bounds.width() / 2f;
        float halfHeight = bounds.height() / 4f; // 控制椭圆的高度

        canvas.drawOval(centerX - halfWidth, centerY - halfHeight, centerX + halfWidth, centerY + halfHeight, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // 设置透明度
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // 设置颜色过滤器
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        // 返回透明度
        return paint.getAlpha();
    }
}
