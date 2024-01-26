package com.wcch.android.anim;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.wcch.android.App;
import com.wcch.android.R;

/**
 * @author created by Lzq
 * @time：2021/8/6
 * @Des：
 */
public class BeToAnimationUtil {

    public static int DEVICE_FOUND = 0;
    public static int DEVICE_CIRCLE_IN = 1;
    public static int DOCK_IN = 2;
    public static int DOCK_OUT = 3;
    public static int DEVICE_CIRCLE_OUT = 4;
    public static int DOCK_DEVICE_IMAGE_IN = 5;
    public static int DOCK_DEVICE_IMAGE_OUT = 6;
    public static int DOCK_DEVICE_IMAGE_BG_IN = 7;
    public static int DOCK_DEVICE_IMAGE_BG_OUT = 8;
    public static int SOFT_SETTING_IN = 9;
    public static int SOFT_SETTING_OUT = 10;
    public static int SLIDE_BAR_IN = 11;
    public static int SLIDE_BAR_OUT = 12;


    public static Animation getAnimation(int type) {
        Animation animation = null;
        if (type == DEVICE_FOUND) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.device_found_animation);
        else if (type == DEVICE_CIRCLE_IN) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.device_circle_animation);
        else if (type == DEVICE_CIRCLE_OUT) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.device_circle_out_animation);
        else if (type == DOCK_IN) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.dock_in_animation);
        else if (type == DOCK_OUT) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.dock_out_animation);
        else if (type == DOCK_DEVICE_IMAGE_IN) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.dock_device_in_animation);
        else if (type == DOCK_DEVICE_IMAGE_OUT) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.dock_device_out_animation);
        else if (type == DOCK_DEVICE_IMAGE_BG_IN) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.dock_device_bg_in);
        else if (type == DOCK_DEVICE_IMAGE_BG_OUT) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.dock_device_bg_out);
        else if (type == SOFT_SETTING_IN) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.setting_activity_in);
        else if (type == SOFT_SETTING_OUT) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.setting_activity_out);
        else if (type == SLIDE_BAR_IN) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.slide_bar_in_animation);
        else if (type == SLIDE_BAR_OUT) animation = AnimationUtils.loadAnimation(App.getInstance().getApplicationContext(), R.anim.slide_bar_out_animation);
        animation.setInterpolator(new BeToAnimationInterpolator(0));
        return animation;
    }
}
