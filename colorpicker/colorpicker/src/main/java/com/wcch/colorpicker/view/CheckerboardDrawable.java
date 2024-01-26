package com.wcch.colorpicker.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 棋盘式绘图
 * 就是透明度的一个黑白的固定一些绘图，但是会随着选择颜色而变化一些颜色
 */
public class CheckerboardDrawable extends Drawable {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int size;
    private int colorOdd;
    private int colorEven;
    private float cornerRadius; // 圆角半径

    public static CheckerboardDrawable create() {
        return new CheckerboardDrawable(new Builder());
    }

    private CheckerboardDrawable(Builder builder) {
        this.size = builder.size;
        this.colorOdd = builder.colorOdd;
        this.colorEven = builder.colorEven;
        this.cornerRadius = builder.cornerRadius; // 获取圆角半径
        configurePaint();
    }

    private void configurePaint() {
        Bitmap bitmap = Bitmap.createBitmap(size * 2, size * 2, Bitmap.Config.ARGB_8888);

        Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setStyle(Paint.Style.FILL);

        Canvas canvas = new Canvas(bitmap);

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(false); // 关闭抗锯齿效果
        
    }

    @Override
    public void draw(@NotNull Canvas canvas) {
 		/********这一段是绘制圆角矩形****/
        RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        paint.setColor(colorOdd);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        /********这里绘制矩形代码****/
        //canvas.drawPaint(paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public static final class Builder {

        private int size = 40;
        private int colorOdd = 0xFFC2C2C2;
        private int colorEven = 0xFFF3F3F3;
        private float cornerRadius = 0; // 默认圆角半径为0

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder colorOdd(int color) {
            colorOdd = color;
            return this;
        }

        public Builder colorEven(int color) {
            colorEven = color;
            return this;
        }

        public Builder cornerRadius(float cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        public CheckerboardDrawable build() {
            return new CheckerboardDrawable(this);
        }
    }
}