package com.wcch.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.wcch.android.R;


/**
 *
 */
public class ArrowRectangleView extends ViewGroup {
    // arrow's width
    private int mArrowWidth = 40;
    // arrow's height
    private int mArrowHeight= 20;
    // rectangle round corner's radius
    private int mRadius = 18;
    // background color
    private int mBackgroundColor = Color.WHITE;
    // arrow's horizontal offset relative to RIGHT side
    private int mArrowOffset = 0;
    // shadow color
    private int mShadowColor = Color.BLACK;
    // shadow thickness
    private int mShadowThickness = 0;

    public ArrowRectangleView(Context context) {
        this(context, null);
    }

    public ArrowRectangleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowRectangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ArrowRectangleView, defStyleAttr, 0);
        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ArrowRectangleView_arrow_width:
                    mArrowWidth = a.getDimensionPixelSize(attr, mArrowWidth);
                    break;
                case R.styleable.ArrowRectangleView_arrow_height:
                    mArrowHeight = a.getDimensionPixelSize(attr, mArrowHeight);
                    break;
                case R.styleable.ArrowRectangleView_arrow_radius:
                    mRadius = a.getDimensionPixelSize(attr, mRadius);
                    break;
                case R.styleable.ArrowRectangleView_background_color:
                    mBackgroundColor = a.getColor(attr, mBackgroundColor);
                    break;
                case R.styleable.ArrowRectangleView_arrow_offset:
                    mArrowOffset = a.getDimensionPixelSize(attr, mArrowOffset);
                    break;
                case R.styleable.ArrowRectangleView_shadow_color:
                    mShadowColor = a.getColor(attr, mShadowColor);
                    break;
                case R.styleable.ArrowRectangleView_shadow_thickness:
                    mShadowThickness = a.getDimensionPixelSize(attr, mShadowThickness);
                    break;
                default:
                    break;
            }
        }

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int maxWidth = 0;
        // reserve space for the arrow and round corners
        int maxHeight = mArrowHeight + mRadius;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                maxHeight = maxHeight + child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            }
        }

        maxWidth = maxWidth + getPaddingLeft() + getPaddingRight() + mShadowThickness;
        maxHeight = maxHeight + getPaddingTop() + getPaddingBottom() + mShadowThickness;

        setMeasuredDimension(maxWidth, maxHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int topOffset = t + mArrowHeight + mRadius/2;
        int top = 0;
        int bottom = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            top = topOffset + i * child.getMeasuredHeight();
            bottom = top + child.getMeasuredHeight();
            child.layout(l, top, r - mRadius/2 - mShadowThickness, bottom);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // disable h/w acceleration for blur mask filter
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mBackgroundColor);
        paint.setStyle(Paint.Style.FILL);

        // set Xfermode for source and shadow overlap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

        // draw round corner rectangle
        paint.setColor(mBackgroundColor);
        canvas.drawRoundRect(new RectF(0, mArrowHeight, getMeasuredWidth() - mShadowThickness, getMeasuredHeight() - mShadowThickness), mRadius, mRadius, paint);
        int startPoint = getMeasuredWidth()/2;
        // draw arrow
        Path path = new Path();
        //int startPoint = getMeasuredWidth() - mArrowOffset;
        path.moveTo(startPoint, mArrowHeight);
        path.lineTo(startPoint + mArrowWidth, mArrowHeight);
        path.lineTo(startPoint + mArrowWidth / 2, 0);
        path.close();
        canvas.drawPath(path, paint);


        Paint paintStroke = new Paint();
        paintStroke.setAntiAlias(true);
        //paintStroke.setColor(mBackgroundColor);
        paintStroke.setStyle(Paint.Style.STROKE);


        // draw round corner rectangle
        paintStroke.setColor(mShadowColor);
        paintStroke.setStrokeWidth(1.0f);
        canvas.drawRoundRect(new RectF(0, mArrowHeight, getMeasuredWidth() - mShadowThickness, getMeasuredHeight() - mShadowThickness), mRadius, mRadius, paintStroke);

        /*画一个空心三角形*/

        Path pathStroke=new Path();
        /**
         * 移动画笔
         * x：终点x坐标值
         * y：终点y坐标值
         */
        pathStroke.moveTo(startPoint, mArrowHeight);
        /**
         * 绘制直线
         * x：终点x坐标值
         * y：终点y坐标值
         */
        pathStroke.lineTo(startPoint + mArrowWidth, mArrowHeight);
        pathStroke.lineTo(startPoint + mArrowWidth / 2, 0);
        pathStroke.close();
        canvas.drawPath(pathStroke, paintStroke);

        paintStroke.setColor(mBackgroundColor);
        /*画一个直线*/
        Path path4=new Path();
        path4.moveTo(startPoint, mArrowHeight);
        path4.lineTo(startPoint + mArrowWidth, mArrowHeight);
        path4.close();
        canvas.drawPath(path4,paintStroke);

        paintStroke.setColor(getResources().getColor(R.color.red_900));
        paintStroke.setStrokeWidth(5.0f);
        /*画一个直线*/
        Path path5=new Path();
        path5.moveTo(getMeasuredWidth(), mArrowHeight);
        path5.lineTo(startPoint+mArrowWidth, mArrowHeight);//上右
        path5.lineTo(startPoint+mArrowWidth/ 2, 0);//上右-三角右
        path5.lineTo(startPoint, mArrowHeight);//上右-三角左
        path5.lineTo(0, mArrowHeight);//上左
        path5.lineTo(0, getMeasuredHeight());//左
        path5.lineTo(getMeasuredWidth(), getMeasuredHeight());//左

        //RectF oval = new RectF(0, 0, 8, getMeasuredHeight());
        //RectF oval = new RectF(0, getMeasuredHeight()/2, getMeasuredWidth(), getMeasuredHeight());
       /* RectF oval = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        //第二个参数表示绘制的起点位置，0度为钟表三点位置，第三个参数表示绘制的度数
        第一个参数：oval为确定圆弧区域的矩形，圆弧的中心点为矩形的中心点
        //第二个参数：startAngle为圆弧的开始角度（时钟3点的方向为0度，顺时钟方向为正）
        //第三个参数：sweepAngle为圆弧的扫过角度（正数为顺时钟方向，负数为逆时钟方向）
        //第四个参数：useCenter表示绘制的圆弧是否与中心点连接成闭合区域
        //第五个参数：paint为绘制圆弧的画笔

        path5.arcTo(oval, 0+4, 4);*/


        path5.close();//闭环
        canvas.drawPath(path5,paintStroke);


        // draw shadow
        if (mShadowThickness > 0) {
            paint.setMaskFilter(new BlurMaskFilter(mShadowThickness, BlurMaskFilter.Blur.OUTER));
            paint.setColor(mShadowColor);
            canvas.drawRoundRect(new RectF(mShadowThickness, mArrowHeight + mShadowThickness, getMeasuredWidth() - mShadowThickness, getMeasuredHeight() - mShadowThickness), mRadius, mRadius, paint);
        }

        super.dispatchDraw(canvas);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
