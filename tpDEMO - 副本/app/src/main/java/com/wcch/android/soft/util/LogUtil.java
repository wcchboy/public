package com.wcch.android.soft.util;

import android.util.Log;


/**
 * @author created by Lzq
 * @time：2022/6/8
 * @Des：
 */
public class LogUtil {

    static String TAG = "Zjx";
    private static final boolean DEBUG = true;
    public static void d(String txt) {
        if (DEBUG) {
            Log.d(TAG, txt);
        }
    }

    public static void d(String tag,String txt) {
        if (DEBUG) {
            Log.d(tag, txt);
        }
    }

    public static void i(String txt) {
        if (DEBUG) {
            Log.i(TAG, txt);
        }
    }

    public static void i(String tag,String txt) {
        if (DEBUG) {
            Log.i(tag, txt);
        }
    }

    public static void e(String txt) {
        if (DEBUG) {
            Log.e(TAG, txt);
        }
//        Log.e(TAG, txt);
    }

    public static void e(String tag,String txt) {
        if (DEBUG) {
            Log.e(tag, txt);
        }
    }
}
