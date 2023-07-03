package com.wcch.android.view;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.wcch.android.anim.BeToAnimationInterpolator;


/**
 * @author created by Lzq
 * @time：2021/8/4
 * @Des：
 */
public class ConnectLine extends View {
    private int x, y, nextX, nextY, incrementY, incrementX;

    public ConnectLine(Context context) {
        super(context);
    }

    public ConnectLine(Context context, int x, int y, int nextX, int nextY) {
        super(context);
        this.x = x;
        this.y = y;
        this.nextX = nextX;
        this.nextY = nextY;
        init();
    }

    private void init() {
        p = new Paint();
        p.setColor(Color.parseColor("#40FFFFFF"));
        p.setAntiAlias(true);
        p.setStrokeWidth(4.0f);


        ValueAnimator valueAnimatorX = ValueAnimator.ofFloat(x, nextX);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                incrementX = Math.round((Float) animation.getAnimatedValue());
                invalidate();
            }
        });

        ValueAnimator valueAnimatorY = ValueAnimator.ofInt(y, nextY);
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                incrementY = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new BeToAnimationInterpolator(0));//匀速
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.start();
    }


    Paint p;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(x, y, incrementX, incrementY, p);// 斜线

    }

}