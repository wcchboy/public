package com.wcch.android.window;

import android.content.Context;

import com.wcch.android.core.LongPressTouchDetector;


/**
 * @author markLiu
 * @title LockScreenHelper
 * @time 2021/4/9 10:36
 * @description
 **/
public class LockScreenHelper {
    //private XToast mLockToast;
    private LongPressTouchDetector mLongPressTouchDetector;
    private Context mContext;

    public LockScreenHelper() {
/*        mLongPressTouchDetector = new LongPressTouchDetector(3 * 1000L, (x,y) -> {
            mLockToast.cancel();
//            CustomToast.getInstance().showNormal("已解锁", 1500);
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS, TabletApp.Companion.getApplication().getString(R.string.unlock));
        });*/
    }

    private void initToast(Context context) {
        mContext = context;
       /* mLockToast = new XToast((Application) context)
//                .setWindowType(WindowManager.LayoutParams.TYPE_PHONE)
                .clearWindowFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                .setView(R.layout.layout_lock_screen)
                .setWidth(-1)
                .setHeight(-1)
                // 设置外层不能被触摸
                .setOutsideTouchable(true)
                .setOnClickListener(R.id.tv_lock1, (toast, view) -> {
                    mLockToast.cancel();
//                    CustomToast.getInstance().showNormal("已解锁", 1500);
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS,TabletApp.Companion.getApplication().getResources().getString(R.string.unlock));
                })
                .setOnClickListener(R.id.tv_lock2, (toast, view) -> {
                    mLockToast.cancel();
//                    CustomToast.getInstance().showNormal("已解锁", 1500);
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS,TabletApp.Companion.getApplication().getResources().getString(R.string.unlock));
                })
                .setOnTouchListener(R.id.container, new XToast.OnTouchListener() {
                    @Override
                    public boolean onTouch(XToast toast, View view, MotionEvent event) {
//                        mLongPressTouchDetector.onTouch(event);
                        return false;
                    }
                })
                // 设置窗口背景阴影强度
                .setBackgroundDimAmount(0f);*/
    }

    public void show(Context context) {
        /*if (mLockToast == null)
            initToast(context);
        mLockToast.show();*/
    }

    /*public boolean isShow(){
        if (mLockToast == null)
            return false;
        return mLockToast.isShow();
    }*/
}
