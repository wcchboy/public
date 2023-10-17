package com.wcch.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author created by Lzq
 * @time：2021/9/16
 * @Des：
 */
public class TouchFrameLayout extends FrameLayout {

    public TouchFrameLayout(@NonNull Context context) {
        super(context);
    }

    public TouchFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TouchFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (onFrameLayoutTouchListener != null) onFrameLayoutTouchListener.onFrameLayoutTouched(this,ev);
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnFrameLayoutTouchListener {
        void onFrameLayoutTouched(TouchFrameLayout touchFrameLayout, MotionEvent event);
    }

    private OnFrameLayoutTouchListener onFrameLayoutTouchListener;

    public void setOnFrameLayoutTouchListener (OnFrameLayoutTouchListener onFrameLayoutTouchListener) {
        this.onFrameLayoutTouchListener = onFrameLayoutTouchListener;
    }
}
