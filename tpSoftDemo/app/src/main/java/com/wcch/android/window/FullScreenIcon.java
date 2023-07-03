package com.wcch.android.window;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * @author created by Lzq
 * @time：2021/9/9
 * @Des：
 */
public class FullScreenIcon {
//    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private long lastTouchTime;

    public FullScreenIcon() {
    }


    public FullScreenIcon(LinearLayout linearLayout, long lastTouchTime) {
//        this.linearLayout = linearLayout;
        this.lastTouchTime = lastTouchTime;
    }

    public FullScreenIcon(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    public FullScreenIcon(RelativeLayout relativeLayout, long lastTouchTime) {
        this.relativeLayout = relativeLayout;
        this.lastTouchTime = lastTouchTime;
    }


    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    public void setRelativeLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    public long getLastTouchTime() {
        return lastTouchTime;
    }

    public void setLastTouchTime(long lastTouchTime) {
        this.lastTouchTime = lastTouchTime;
    }

}
