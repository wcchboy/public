package com.igrs.sml;

import android.content.Intent;
import android.media.projection.MediaProjection;

public class RuntimeInfo {

    public static Intent resultData;

    public static String p2pName = "";
    public static String p2pMac = "";

    public static String networkname="";
    public static boolean isGroupOwner=false;

    public static String ser_ip="";
    public static int ser_port;
    public static String ser_mac="";

    public static String ser_ssid="";
    public static String ser_pwd="";

    public static int info_signalLevel;
    public static int info_speed;
    public static int info_fps;
    public static int info_bit;

    public static boolean is24G;
    public static boolean is5G;


    public static Intent screenRecordIntent = null;

    public static float screenRecordSize = 1536f;

    public static float mScreenWidth=864;//MyApplication
    public static float mScreenHeight=1536;

}
