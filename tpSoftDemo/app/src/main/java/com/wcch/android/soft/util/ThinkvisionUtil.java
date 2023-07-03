package com.wcch.android.soft.util;


import android.content.Context;

public class ThinkvisionUtil {

    private static String TAG = "ThinkvisionUtil";
    public  static String getAPName(Context context){

//        String key_feiyu_base = IstCommonManager.getInstance().SharedPreferences_getString("KEY_FEIYU_BASE", "");
//        try {
//            JSONObject object = new JSONObject(key_feiyu_base);
//            return object.getString("apSsid");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
        return NetStatusManager.getInstance().getApName(context);
    }
}
