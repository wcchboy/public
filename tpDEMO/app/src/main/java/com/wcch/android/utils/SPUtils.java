package com.wcch.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SPUtils {


    private static Context mContext;
    private static SharedPreferences mSharedPreferences;


    private static synchronized SharedPreferences getPreferneces() {
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences("projection", MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    public static void initSP(Context context) {
        mContext = context;
    }

    //保存字符串
    public static void putString(String key, String value) {
        getPreferneces().edit().putString(key, value).commit();
    }
    //读取字符串
    public static String getString(String key) {
        return getPreferneces().getString(key, null);
    }

    //读取字符串
    public static String getString(String key, String def) {
        return getPreferneces().getString(key, def);
    }
    //保存整型值
    public static void putInt(String key, int value) {
        getPreferneces().edit().putInt(key, value).commit();
    }
    //读取整型值
    public static int getInt(String key) {
        return getPreferneces().getInt(key, -1);
    }
    public static int getInt(String key, int defValue) {
        return getPreferneces().getInt(key, defValue);
    }
    //保存布尔值
    public static void putBool(String key, Boolean value) {
        getPreferneces().edit().putBoolean(key, value).commit();
    }
    public static void putLong(String key, long value) {
        getPreferneces().edit().putLong(key, value).commit();
    }
    public static long getResumeSize(String key) {
        return getPreferneces().getLong(key, 10);
    }

    public static long getLong(String key) {
        return getPreferneces().getLong(key, 0);
    }
    //读取布尔值
    public static boolean getBool(String key, boolean defValue) {
        return getPreferneces().getBoolean(key, defValue);
    }
    //移除字段
    public static void removeString(String key) {
        getPreferneces().edit().remove(key).commit();
    }
    //清空保存在默认SharePreference下的所有数据
    private static void clear() {
        getPreferneces().edit().clear().commit();
    }

    public static void setLogStatus(boolean value) {
        mSharedPreferences.edit().putBoolean("CAN_PRINT_LOG",value).commit();
    }

    public static boolean getLogStatus() {
        return mSharedPreferences.getBoolean("CAN_PRINT_LOG",false);
    }
}
