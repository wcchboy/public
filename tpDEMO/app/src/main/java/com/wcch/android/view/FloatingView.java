package com.wcch.android.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.igrs.betotablet.Constants;
import com.igrs.betotablet.R;


public class FloatingView implements View.OnTouchListener, View.OnClickListener {

    private String TAG = "FloatingView";

    private static FloatingView floatingView;
    private Context mContext;
    private WindowManager manager;
    private WindowManager.LayoutParams params;
    private RelativeLayout view;
    private boolean isShowing = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private FloatingView() {
    }

    public static FloatingView getInstance() {
        if (floatingView == null) {
            synchronized (FloatingView.class) {
                floatingView = new FloatingView();
            }
        }
        return floatingView;
    }

    public void init(Context context) {
        this.mContext = context;
        manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        params.x = -1850;
        params.y = -100;
    }

    public void show() {
            if (view == null) {
                view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.view_floating, null);
                view.setOnTouchListener(this);
                view.setOnClickListener(this);
            }
        if (!isShowing) {
            mHandler.post(addRunnable);
            x(false);
        } else {
            //如果正在显示，刷新时长
            x(true);
        }
        isShowing = true;

    }


    private void x(boolean refresh) {
        if (!refresh) {
            mHandler.postDelayed(runnable, 10000);
        } else {
            mHandler.removeCallbacks(runnable);
            x(false);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            manager.removeViewImmediate(view);
            isShowing = false;
        }
    };

    Runnable addRunnable = new Runnable() {
        @Override
        public void run() {
            params.x = -1850;
            params.y = -100;
            manager.addView(view, params);
        }
    };

    @Override
    public void onClick(View v) {
        if (view == v) {
            //SoftWindowManager.getInstance().onW20StatusChange(Constants.SPLIT_MODE);
            mHandler.removeCallbacks(runnable);
            mHandler.post(runnable);
        }
    }


    //用于识别最小的滑动距离
    private int mSlop = ViewConfiguration.getWindowTouchSlop();
    private float startX;
    private float startY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (view == v) {
            x(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getRawX();
                    float moveY = event.getRawY();
                    float dx = (moveX - startX) / 2;
                    float dy = (moveY - startY) / 2;
                    if (Math.abs(dx) <= mSlop && Math.abs(dy) <= mSlop) {
                        return false;
                    }
                    params.x += dx;
                    params.y += dy;
                    if (params.x > 0) {
                        params.x = 0;
                    }
                    if (params.y > 0) {
                        params.y = 0;
                    }
                    manager.updateViewLayout(view, params);
                    startX = event.getRawX();
                    startY = event.getRawY();

                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
        }
        return false;
    }
}
