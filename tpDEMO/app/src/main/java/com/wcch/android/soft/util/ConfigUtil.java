package com.wcch.android.soft.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.igrs.betotablet.TabletApp;
import com.igrs.sml.util.Common;

public class ConfigUtil {

    private volatile static ConfigUtil instance;

    public static ConfigUtil getInstance() {
        if (instance == null) {
            synchronized (ConfigUtil.class) {
                if (instance == null) {
                    instance = new ConfigUtil();
                }
            }
        }
        return instance;
    }

    private ConfigUtil() {

    }


    public void setIdentification() {
        SharedPreferences config = TabletApp.Companion.getApplication().getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor edit = config.edit();
        String md5 = Common.getMD5Str("igrs"+System.currentTimeMillis()).toLowerCase();
        edit.putString("identification", md5.substring(0,8));
        //edit.putString("identification", Build.BRAND + "-" + Build.MODEL + "-" + Build.VERSION.RELEASE);
        edit.commit();
    }

    public static String getIdentification() {
        SharedPreferences config = TabletApp.Companion.getApplication().getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
        return config.getString("identification",null);
    }

    public void setNeedScreenIntent(boolean agreement) {
        SharedPreferences config = TabletApp.Companion.getApplication().getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor edit = config.edit();
        edit.putBoolean("ScreenIntent", agreement);
        edit.commit();
    }

    public boolean getNeedScreenIntent() {
        SharedPreferences config = TabletApp.Companion.getApplication().getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
        return config.getBoolean("ScreenIntent",true);
    }


    public void setIgnore_battery(boolean ignore) {
        SharedPreferences config = TabletApp.Companion.getApplication().getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor edit = config.edit();
        edit.putBoolean("ignore_battery", ignore);
        edit.commit();
    }

    public boolean getIgnore_battery() {
        SharedPreferences config = TabletApp.Companion.getApplication().getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
        return config.getBoolean("ignore_battery",false);
    }

    public String getDeviceName() {
        SharedPreferences config = TabletApp.Companion.getApplication().getSharedPreferences("settingConfig", Context.MODE_PRIVATE);
        return config.getString("igrs_device_name", null);
    }

    public void setDeviceName(String deviceName) {
        SharedPreferences config = TabletApp.Companion.getApplication().getSharedPreferences("settingConfig", Context.MODE_PRIVATE);
        config.edit()
            .putString("igrs_device_name", deviceName)
            .apply();
    }

    public boolean getAutoProjection() {
        SharedPreferences config = TabletApp.Companion.getApplication().getSharedPreferences("settingConfig", Context.MODE_PRIVATE);
        return config.getBoolean("igrs_auto_projection", true);
    }

    public void setAutoProjection(boolean autoProjection) {
        SharedPreferences config = TabletApp.Companion.getApplication().getSharedPreferences("settingConfig", Context.MODE_PRIVATE);
        config.edit()
            .putBoolean("igrs_auto_projection", autoProjection)
            .apply();
    }
}
