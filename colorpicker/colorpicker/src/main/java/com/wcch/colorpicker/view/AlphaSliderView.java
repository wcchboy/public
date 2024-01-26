package com.wcch.colorpicker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.wcch.colorpicker.ColorSliderView;
import com.wcch.colorpicker.util.DpUtil;

import org.jetbrains.annotations.Nullable;

/**
 * 透明度滑块视图
 * 控制颜色的透明度（Alpha）值
 */
public class AlphaSliderView extends ColorSliderView {
    private static final String TAG = AlphaSliderView.class.getSimpleName();
    private Bitmap backgroundBitmap;
    private Canvas backgroundCanvas;

    public AlphaSliderView(Context context)
    {
        super(context);
    }

    public AlphaSliderView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AlphaSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 当视图大小改变时，创建了一个用于绘制背景的 Bitmap 和相应的 Canvas。这个背景用于显示透明度变化的效果
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        //这个是一个矩形的
        backgroundBitmap = Bitmap.createBitmap((int) (w - 2 * selectorSize),
                (int) (h - selectorSize), Bitmap.Config.ARGB_8888);
        backgroundCanvas = new Canvas(backgroundBitmap);


    }

    /**
     * 使用 CheckerboardDrawable 绘制了一个棋盘格样式的背景，然后通过 canvas.drawBitmap 将背景绘制到当前视图上。
     * 最后，调用 super.onDraw(canvas) 来绘制选择器和其他可能的内容。
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
       /* Drawable drawable = CheckerboardDrawable.create();
        //设置了可绘制对象的边界。这个方法将可绘制对象的边界限制在画布的整个区域内，从 (0, 0) 到画布的宽度和高度。
        drawable.setBounds(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
        //用于绘制背景或其他图形
        drawable.draw(backgroundCanvas);
        canvas.drawBitmap(backgroundBitmap, selectorSize, selectorSize, null);*/

        Drawable drawable = new CheckerboardDrawable.Builder().cornerRadius(8).build();
        drawable.setBounds(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
        //用于绘制背景或其他图形
        drawable.draw(backgroundCanvas);
        // 绘制底色的圆角矩形
        /*float width = getWidth();
        float height = getHeight();
        float cornerRadius = 20; // 圆角半径，根据您的需求设置

        Path roundedRectPath = new Path();
        RectF rect = new RectF(0, 0, width, height);
        roundedRectPath.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#FF0000")); // 设置底色，根据您的需求设置

        canvas.drawPath(roundedRectPath, backgroundPaint);
*/



        canvas.drawBitmap(backgroundBitmap, selectorSize, selectorSize, null);

       /* Drawable drawable = CheckerboardDrawable.create();
        drawable.setBounds(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());

        // 创建一个 Bitmap，作为绘制带圆角矩形的画布
        Bitmap roundedBitmap = Bitmap.createBitmap(
                backgroundCanvas.getWidth(),
                backgroundCanvas.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas roundedCanvas = new Canvas(roundedBitmap);

        // 绘制带圆角的矩形
        float cornerRadius = 20f; // 你可以根据需要调整圆角半径
        RectF rectF = new RectF(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roundedCanvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

        // 将 CheckerboardDrawable 绘制到带圆角的矩形上
        drawable.draw(roundedCanvas);

        // 将带圆角的矩形绘制到目标 Canvas 上
        canvas.drawBitmap(roundedBitmap, selectorSize, selectorSize, null);*/


        // 创建一个简单的画笔对象
        /*Paint paint = new Paint();
        // 创建一个矩形
        RectF rect = new RectF(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());

        // 设置圆角的半径，这里使用8dp作为示例，您可以根据需要调整
        float cornerRadius = DpUtil.dpToPixels(getContext(), 8);

        // 创建一个棋盘状的可绘制对象
        Drawable checkerboardDrawable = CheckerboardDrawable.create();
        checkerboardDrawable.setBounds((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);

        // 绘制圆角矩形
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

        // 绘制棋盘状背景
        checkerboardDrawable.draw(canvas);

        // 绘制位图
        canvas.drawBitmap(backgroundBitmap, selectorSize, selectorSize, null);*/


        super.onDraw(canvas);
    }

    /**
     * 通过 resolveValue 方法，根据颜色值解析出透明度值。在这里，通过将颜色转换为透明度值（0-255 范围），然后将其归一化为范围 [0, 1]。
     *
     * @param color
     * @return
     */
    @Override
    protected float resolveValue(int color)
    {
        return Color.alpha(color) / 255.f;
    }

    /**
     * 通过 configurePaint 方法，设置画笔的渐变效果，显示透明度值的变化。这里通过线性渐变设置从完全透明到完全不透明的颜色变化
     * 不加这个代码就一直是黑色，没有渐变和颜色变化
     *
     * @param colorPaint
     */
    protected void configurePaint(Paint colorPaint)
    {
        //用于存储颜色的HSV（色相、饱和度、明度）值。
        float[] hsv = new float[3];
        //将基础颜色（baseColor）转换为HSV表示，并将结果存储在hsv数组中。
        Color.colorToHSV(baseColor, hsv);
        // 使用HSVToColor方法将HSV值转换为颜色，并将色相（H）设置为0，得到渐变的起始颜色。
        int startColor = Color.HSVToColor(0, hsv);
        //将色相设置为255，得到渐变的结束颜色。
        int endColor = Color.HSVToColor(255, hsv);
        //创建一个线性渐变（LinearGradient）着色器，该渐变从视图的左上角（0, 0）到右下角（getWidth(), getHeight()）进行渐变，
        // 颜色范围从startColor到endColor，并且使用CLAMP模式，即在渐变边界之外的区域使用边界颜色。
        Shader shader = new LinearGradient(0, 0, getWidth(), getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
        //将创建的渐变着色器设置为colorPaint画笔的着色器，以便在绘制时应用颜色渐变效果。
        colorPaint.setShader(shader);
    }

    /**
     * 通过 assembleColor 方法，组装颜色。根据当前的透明度值 (currentValue) 和基础颜色 (baseColor)，组装出新的颜色。
     * 这里通过将透明度值转换为 0-255 范围内的整数，然后将其应用到颜色中。
     *
     * @return
     */
    protected int assembleColor()
    {
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        int alpha = (int) (currentValue * 255);
        return Color.HSVToColor(alpha, hsv);
    }
}
