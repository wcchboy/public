package com.wcch.android.soft.util;

import android.content.Context;
import android.content.Intent;


public class BroadCastUtils {
    public static final String PROJECTION_BROADCAST = "com.lenovo.wb.beto.broadcast";
    public static final String PROJECTION_FULL_SCREEN = "PROJECTION_FULL_SCREEN";
    public static final String PROJECTION_FAIL = "projectionFail";
    public static final String PROJECTION_SUCCESS = "projectionSuccess";
    public static final String PROJECTION_STOP = "projectionStop";
    public static final String PROJECTION_START = "projectionStart";

    public static void sendProjectionStatus(Context context,String action) {

        Intent intent = new Intent(PROJECTION_BROADCAST);
        intent.putExtra("action",action);
        LogUtil.e("zjx"," 发送投屏状态广播："+action);
        context.sendBroadcast(intent);
    }


}
