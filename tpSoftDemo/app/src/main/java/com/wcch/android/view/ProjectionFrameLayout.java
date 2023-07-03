package com.wcch.android.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.igrs.betotablet.TabletApp;
import com.igrs.betotablet.utils.AndroidUtil;


/**
 * @author created by Lzq
 * @time：2021/9/8
 * @Des：
 */
public class ProjectionFrameLayout extends FrameLayout {

    private static final String TAG = ProjectionFrameLayout.class.getSimpleName();

    public ProjectionFrameLayout(Context context) {
        super(context);
    }

    public ProjectionFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProjectionFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProjectionFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    private int fullModeWidth = AndroidUtil.INSTANCE.getScreenWidth(TabletApp.Companion.getApplication());

    private int fullModeHeight = AndroidUtil.INSTANCE.getScreenHeight(TabletApp.Companion.getApplication());

    private int fpModeWidth = fullModeWidth / 2;
    private int fpModeHeight = fullModeHeight / 2;


    public void setScreenInfo(int width, int height) {
        fullModeWidth = width;
        fullModeHeight = height;
        fpModeWidth = fullModeWidth / 2;
        fpModeHeight = fullModeHeight / 2;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        windowTouched(ev);
        return false;
    }

    /**
     * 投屏窗口被选中
     *
     * @param ev
     */
    private void windowTouched(MotionEvent ev) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int width = childAt.getWidth();
            int height = childAt.getHeight();
            LayoutParams lp = (LayoutParams) childAt.getLayoutParams();
            int leftMargin = lp.leftMargin;
            int topMargin = lp.topMargin;
            Rect rect = new Rect();
            rect.set(leftMargin, topMargin, leftMargin + width, topMargin + height);
            if (rect.contains((int) ev.getX(), (int) ev.getY())) {
                bringChildToFront(childAt);
                if (onWindowSelectedListener != null) {
                    onWindowSelectedListener.onWindowSelected(childAt);
                }
            }
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        layoutView();
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        layoutView();
    }


    private void layoutView() {
        int childCount = getChildCount();

        if (childCount == 1) {
            View childAt = getChildAt(0);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            layoutParams.width = fullModeWidth;
            layoutParams.height = fullModeHeight;
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = 0;
            childAt.setLayoutParams(layoutParams);
        }

        if (childCount == 2) {
            View childAt0 = getChildAt(0);
            LayoutParams layoutParams = (LayoutParams) childAt0.getLayoutParams();
            layoutParams.width = fpModeWidth;
            layoutParams.height = fpModeHeight;
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = fpModeHeight / 2;
            childAt0.setLayoutParams(layoutParams);

            View childAt1 = getChildAt(1);
            LayoutParams layoutParams1 = (LayoutParams) childAt1.getLayoutParams();
            layoutParams1.width = fpModeWidth;
            layoutParams1.height = fpModeHeight;
            layoutParams1.leftMargin = fpModeWidth;
            layoutParams1.topMargin = fpModeHeight / 2;
            childAt1.setLayoutParams(layoutParams1);
        }

        if (childCount == 3) {
            View childAt0 = getChildAt(0);
            LayoutParams layoutParams = (LayoutParams) childAt0.getLayoutParams();
            layoutParams.width = fpModeWidth;
            layoutParams.height = fpModeHeight;
            layoutParams.leftMargin = fpModeWidth / 2;
            layoutParams.topMargin = 0;
            childAt0.setLayoutParams(layoutParams);

            View childAt1 = getChildAt(1);
            LayoutParams layoutParams1 = (LayoutParams) childAt1.getLayoutParams();
            layoutParams1.width = fpModeWidth;
            layoutParams1.height = fpModeHeight;
            layoutParams1.leftMargin = 0;
            layoutParams1.topMargin = fpModeHeight;
            childAt1.setLayoutParams(layoutParams1);

            View childAt2 = getChildAt(2);
            LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
            layoutParams2.width = fpModeWidth;
            layoutParams2.height = fpModeHeight;
            layoutParams2.leftMargin = fpModeWidth;
            layoutParams2.topMargin = fpModeHeight;
            childAt2.setLayoutParams(layoutParams2);
        }

        if (childCount == 4) {
            View childAt0 = getChildAt(0);
            LayoutParams layoutParams = (LayoutParams) childAt0.getLayoutParams();
            layoutParams.width = fpModeWidth;
            layoutParams.height = fpModeHeight;
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = 0;
            childAt0.setLayoutParams(layoutParams);

            View childAt1 = getChildAt(1);
            LayoutParams layoutParams1 = (LayoutParams) childAt1.getLayoutParams();
            layoutParams1.width = fpModeWidth;
            layoutParams1.height = fpModeHeight;
            layoutParams1.leftMargin = fpModeWidth;
            layoutParams1.topMargin = 0;
            childAt1.setLayoutParams(layoutParams1);

            View childAt2 = getChildAt(2);
            LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
            layoutParams2.width = fpModeWidth;
            layoutParams2.height = fpModeHeight;
            layoutParams2.leftMargin = 0;
//            layoutParams2.gravity = Gravity.BOTTOM;
            layoutParams2.topMargin = fpModeHeight;
            childAt2.setLayoutParams(layoutParams2);

            View childAt3 = getChildAt(3);
            LayoutParams layoutParams3 = (LayoutParams) childAt3.getLayoutParams();
            layoutParams3.width = fpModeWidth;
            layoutParams3.height = fpModeHeight;
            layoutParams3.leftMargin = fpModeWidth;
            layoutParams3.topMargin = fpModeHeight;
            childAt3.setLayoutParams(layoutParams3);
        }
    }

    WindowParams params;

    /**
     * 全屏
     *
     * @param frameLayout
     */
    public void windowFullScreen(RelativeLayout frameLayout) {
        int childCount = getChildCount();
        if (childCount == 0) return;
        for (int i = 0; i < childCount; i++) {
            RelativeLayout childAt = (RelativeLayout) getChildAt(i);
            if (frameLayout == childAt) {
                //窗口全屏
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int width = layoutParams.width;
                int height = layoutParams.height;
                int leftMargin = layoutParams.leftMargin;
                int topMargin = layoutParams.topMargin;
                params = new WindowParams(width, height, leftMargin, topMargin);

                layoutParams.leftMargin = 0;
                layoutParams.topMargin = 0;
                layoutParams.width = fullModeWidth;
                layoutParams.height = fullModeHeight;
                frameLayout.setLayoutParams(layoutParams);
                bringChildToFront(frameLayout);
                updateViewLayout(frameLayout, frameLayout.getLayoutParams());
                return;
            }
        }
    }

    public void exitFullScreen(RelativeLayout frameLayout) {
        if (params == null) return;
        int childCount = getChildCount();
        if (childCount == 0) return;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (frameLayout == childAt) {
                //窗口全屏
                LayoutParams layoutParams = (LayoutParams) frameLayout.getLayoutParams();
                int width = params.getWidth();
                int height = params.getHeight();
                int leftMargin = params.leftMargin;
                int topMargin = params.topMargin;

                layoutParams.width = width;
                layoutParams.height = height;
                layoutParams.leftMargin = leftMargin;
                layoutParams.topMargin = topMargin;
                frameLayout.setLayoutParams(layoutParams);
                params = null;
                return;
            }
        }
    }

    public static class WindowParams {
        private int width;
        private int height;
        private int leftMargin;
        private int topMargin;

        public WindowParams(int width, int height, int leftMargin, int topMargin) {
            this.width = width;
            this.height = height;
            this.leftMargin = leftMargin;
            this.topMargin = topMargin;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getLeftMargin() {
            return leftMargin;
        }

        public void setLeftMargin(int leftMargin) {
            this.leftMargin = leftMargin;
        }

        public int getTopMargin() {
            return topMargin;
        }

        public void setTopMargin(int topMargin) {
            this.topMargin = topMargin;
        }
    }

    public interface OnWindowSelectedListener {
        void onWindowSelected(View view);
    }

    private OnWindowSelectedListener onWindowSelectedListener;

    public void setOnWindowSelectedListener(OnWindowSelectedListener onWindowSelectedListener) {
        this.onWindowSelectedListener = onWindowSelectedListener;
    }
}
