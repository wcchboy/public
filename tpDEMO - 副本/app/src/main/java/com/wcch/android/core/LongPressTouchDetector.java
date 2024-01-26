package com.wcch.android.core;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

/**
 * @author markLiu
 * @title LongPressTouchDetector
 * @time 2021/4/9 9:28
 * @description 长按手势
 **/
public class LongPressTouchDetector {
    private Handler mHandler;
    private Runnable mRunnable;
    private long mPressOutTime;
    private OnLongPressListener listener;
    private float mDownX, mDownY;

    public LongPressTouchDetector(long pressOutTime, OnLongPressListener listener) {
        this.mPressOutTime = pressOutTime;
        this.listener = listener;
        init();
    }

    private void init() {
        mHandler = new Handler(Looper.getMainLooper());
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (listener != null)
                    listener.onLongPress(mDownX, mDownY);
            }
        };
    }

    private float getDistance(float x1, float y1, float x2, float y2) {
        float distance = (float) Math.sqrt(Math.pow((double) (x1 - x2), 2) + Math.pow((double) (y1 - y2), 2));
        return distance;
    }

    public void onTouch(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mDownX = event.getX();
            mDownY = event.getY();
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, mPressOutTime);
        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            float distance = getDistance(event.getX(), event.getY(), mDownX, mDownY);
            if (distance > 20)
                mHandler.removeCallbacks(mRunnable);
        } else
            mHandler.removeCallbacks(mRunnable);
    }


    public interface OnLongPressListener {
        void onLongPress(float x, float y);
    }
}
