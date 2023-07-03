package com.wcch.android.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Create by RyanWang
 *
 * @date
 * @Dis
 */
public class WifiTools {
    private static final String TAG = "WifiTools";
    public static boolean getAp5GEnable(Context context) {

        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Method method;
        try {
            method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
             /* if(method != null) {
                method.setAccessible(true);
                return (Boolean) method.invoke(mWifiManager);
            }else{
                return false;
            }*/
            WifiConfiguration apConfig = (WifiConfiguration) method.invoke(mWifiManager);
            Field stateField = apConfig.getClass().getField("apBand");


            if (stateField!=null) {
                stateField.setAccessible(true);
                int state = stateField.getInt(apConfig);
                int state1=(int)stateField.get(apConfig);
                Log.e(TAG, "--》state："+state);
                Log.e(TAG, "--》state1："+state1);
            }

        }catch (Exception e) {
            System.out.println("e:"+e.getMessage());
            e.printStackTrace();
        }


        return false;
    }
}
