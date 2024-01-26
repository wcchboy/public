package com.wcch.colorpicker.util;

import android.util.Log;

/**
 * Created by RyanWang on 2023/12/22.
 *
 * @Description:
 */
public class LogUtil {
    static String TAG = "LogUtil";

    private static final boolean SHOW_LOG = true;

    public static void d(String txt)
    {
        if (SHOW_LOG)
        {
            Log.d(TAG, txt);
        }
    }

    public static void d(String tag, String txt)
    {
        if (SHOW_LOG)
        {
            Log.d(tag, txt);
        }
    }

    public static void i(String txt)
    {
        if (SHOW_LOG)
        {
            Log.i(TAG, txt);
        }
    }

    public static void i(String tag, String txt)
    {
        if (SHOW_LOG)
        {
            Log.i(tag, txt);
        }
    }

    public static void e(String txt)
    {
        if (SHOW_LOG)
        {
            Log.e(TAG, txt);
        }
    }

    public static void e(String tag, String txt)
    {
        if (SHOW_LOG)
        {
            Log.e(tag, txt);
        }
    }

    public static void v(String tag, String txt)
    {
        if (SHOW_LOG)
        {
            Log.v(tag, txt);
        }
    }

    public static void w(String tag, String txt)
    {
        if (SHOW_LOG)
        {
            Log.w(tag, txt);
        }
    }
}
