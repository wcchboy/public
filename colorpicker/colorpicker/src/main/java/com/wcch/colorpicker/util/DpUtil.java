package com.wcch.colorpicker.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by RyanWang on 2023/12/25.
 *
 * @Description:
 */
public class DpUtil {
    public static float dpToPixels(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

}
