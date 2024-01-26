package com.wcch.android.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wcch.android.R;
import com.wcch.android.utils.DisplayUtil;

public class ToastUtils {

    private WindowManager windowManager;
    private WindowManager.LayoutParams windowParms;
    private Context mContext;
    private static ToastUtils toastUtil;
    private RelativeLayout toastView;
    private ImageView ivIcon;
    private TextView tvMsg;
    private boolean isShowing = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static ToastUtils getInstance() {
        if (toastUtil == null) {
            synchronized (ToastUtils.class) {
                toastUtil = new ToastUtils();
            }
        }
        return toastUtil;
    }

    private ToastUtils() {
    }


    public void init(Context mContext) {
        //windowmanager准备
        this.mContext = mContext;
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowParms = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowParms.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            windowParms.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        windowParms.format = PixelFormat.TRANSLUCENT;// 支持透明
        windowParms.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowParms.height = DisplayUtil.dip2px(mContext,20);
        windowParms.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowParms.gravity = Gravity.BOTTOM;
        windowParms.y = DisplayUtil.dip2px(mContext,27);
    }

    private LinearLayout llToast;

    public void showToast(final ToastType type, final String text) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            show(type,text);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                   show(type,text);
                }
            });
        }
    }

    public void show(ToastType type, String text) {

        if (toastView == null) {
            toastView = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.item_toast, null);
            ivIcon = toastView.findViewById(R.id.iv_toast_icon);
            tvMsg = toastView.findViewById(R.id.tv_msg);
            llToast = toastView.findViewById(R.id.ll_toast);
        }

        tvMsg.setText(text);
        if (type == ToastType.ERROR) {
            ivIcon.setVisibility(View.VISIBLE);
            ivIcon.setImageResource(R.drawable.icon_internet);
        } else if (type == ToastType.WARNING) {
            ivIcon.setVisibility(View.VISIBLE);
            ivIcon.setImageResource(R.drawable.icon_internet);
        } else if (type == ToastType.SUCCESS) {
            ivIcon.setVisibility(View.VISIBLE);
            ivIcon.setImageResource(R.drawable.icon_internet);
        } else if (type == ToastType.NORMAL) {
            ivIcon.setVisibility(View.GONE);
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
            mHandler.postDelayed(runnable, 2000);
        } else {
            mHandler.removeCallbacks(runnable);
            x(false);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            windowManager.removeViewImmediate(toastView);
            isShowing = false;
        }
    };

    Runnable addRunnable = new Runnable() {
        @Override
        public void run() {
            windowManager.addView(toastView, windowParms);
        }
    };

    public enum ToastType {
        NORMAL,
        WARNING,
        ERROR,
        SUCCESS
    }

}
