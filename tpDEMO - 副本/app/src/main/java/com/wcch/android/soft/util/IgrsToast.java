package com.wcch.android.soft.util;

import android.app.Activity;
import android.app.Application;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.hjq.xtoast.XToast;
import com.igrs.betotablet.R;
import com.igrs.betotablet.soft.util.MyToast;

public class IgrsToast {

    private XToast toast;
    private MyToast myToast;
    private volatile static IgrsToast instance;

    public static IgrsToast getInstance() {
        if (instance == null) {
            synchronized (IgrsToast.class) {
                if (instance == null) {
                    instance = new IgrsToast();
                }
            }
        }
        return instance;
    }

    private IgrsToast() {

    }

    public IgrsToast init(final Application c) {
        if (Settings.canDrawOverlays(c)) {
            if (toast != null) {
                toast.cancel();
            }
            if (myToast != null) {
                myToast.cancel();
            }
            toast = new XToast<>(c).setContentView(R.layout.igrs_toast);
            toast.findViewById(R.id.layout_toast).setZ(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, c.getResources().getDisplayMetrics()));
            // 设置动画样式
            //toast.setAnimStyle(R.style.BottomAnimStyle)
            // 设置外层是否能被触摸
            //.setOutsideTouchable(false)
            // 设置窗口背景阴影强度
            //.setBackgroundDimAmount(0.5f)
            toast.setGravity(Gravity.BOTTOM)
                    .show();
        } else {
            if (myToast != null) {
                myToast.cancel();
            }
            myToast = new MyToast(c);
            myToast.setContentView(R.layout.igrs_toast);
            myToast.setGravity(Gravity.BOTTOM);
        }
        return getInstance();
    }
    public void showToast(final Application c, String msg) {
        init(c);
        if (toast != null) {
            toast.findViewById(android.R.id.icon).setVisibility(View.GONE);
            toast.findViewById(R.id.progressBar).setVisibility(View.GONE);
            toast.setDuration(1500)
                    .setText(android.R.id.message, msg)
                    .show();
        } else {
            myToast.findViewById(android.R.id.icon).setVisibility(View.GONE);
            myToast.findViewById(R.id.progressBar).setVisibility(View.GONE);
            myToast.setDuration(Toast.LENGTH_SHORT);
            myToast.setText(msg);
            myToast.show();
        }
    }
    public void showToast(final Activity c, String msg) {
        showToast(c.getApplication(),msg);
    }
    public void showToast(final Application c, String msg, int duration) {
        init(c);
        if (toast != null) {
            toast.findViewById(android.R.id.icon).setVisibility(View.GONE);
            toast.findViewById(R.id.progressBar).setVisibility(View.GONE);
            toast.setDuration(duration)
                    .setText(android.R.id.message, msg)
                    .show();
        } else {
            myToast.findViewById(android.R.id.icon).setVisibility(View.GONE);
            myToast.findViewById(R.id.progressBar).setVisibility(View.GONE);
            if (duration > 2) {
                myToast.setDuration(Toast.LENGTH_LONG);
            } else {
                myToast.setDuration(Toast.LENGTH_SHORT);
            }
            myToast.setText(msg);
            myToast.show();
        }
    }

    public void showToast_error(final Application c, String msg, int duration) {
       // L.i("showToast_error ->msg:"+msg+"\n"+ Log.getStackTraceString(new Exception("test")));
        init(c);
        if (toast != null) {
            toast.findViewById(android.R.id.icon).setVisibility(View.VISIBLE);
            toast.findViewById(R.id.progressBar).setVisibility(View.GONE);
            toast.setDuration(duration)
                    .setImageDrawable(android.R.id.icon, R.drawable.ic_x_red)
                    .setText(android.R.id.message, msg)
                    .show();
        } else {
            myToast.findViewById(android.R.id.icon).setVisibility(View.VISIBLE);
            myToast.findViewById(R.id.progressBar).setVisibility(View.GONE);
            if (duration > 2) {
                myToast.setDuration(Toast.LENGTH_LONG);
            } else {
                myToast.setDuration(Toast.LENGTH_SHORT);
            }
            myToast.setImageDrawable(R.drawable.ic_x_red);
            myToast.setText(msg);
            myToast.show();
        }
    }
    public void showToast_error(final Activity c, String msg, int duration) {
        showToast_error(c.getApplication(),  msg,  duration);
    }
    public void showToast_icon(final Application c, String msg, int duration,int icon) {
        init(c);
        if (toast != null) {
            toast.findViewById(android.R.id.icon).setVisibility(View.VISIBLE);
            toast.findViewById(R.id.progressBar).setVisibility(View.GONE);
            toast.setDuration(duration)
                    .setImageDrawable(android.R.id.icon, icon)
                    .setText(android.R.id.message, msg)
                    .show();
        } else {
            myToast.findViewById(android.R.id.icon).setVisibility(View.VISIBLE);
            myToast.findViewById(R.id.progressBar).setVisibility(View.GONE);
            if (duration > 2) {
                myToast.setDuration(Toast.LENGTH_LONG);
            } else {
                myToast.setDuration(Toast.LENGTH_SHORT);
            }
            myToast.setImageDrawable(icon);
            myToast.setText(msg);
            myToast.show();
        }
    }

    public void showToast_icon(final Activity c, String msg, int duration,int icon) {
        showToast_icon(c.getApplication(),msg,duration,icon);
    }

    public void showToast_load(final Application c, String msg, int duration) {
        init(c);
        if (toast != null) {
            toast.findViewById(android.R.id.icon).setVisibility(View.GONE);
            toast.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            toast.setDuration(duration)
                    .setText(android.R.id.message, msg)
                    .show();
        } else {
            myToast.findViewById(android.R.id.icon).setVisibility(View.GONE);
            myToast.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            if (duration > 2) {
                myToast.setDuration(Toast.LENGTH_LONG);
            } else {
                myToast.setDuration(Toast.LENGTH_SHORT);
            }
            myToast.setText(msg);
            myToast.show();
        }
    }
    public void close() {
        if (toast != null) {
            toast.cancel();
        }
        if (myToast != null) {
            myToast.cancel();
        }
    }
}
