package com.wcch.android.utils;

import android.os.Environment;

import java.io.File;

public class RouteInfo {
    public static String BASE_ABSEN_URL = "ABSEN_LED_Controller";
    public static String BASE_LY_URL = "LY_LED_Controller";
    public static String BASE_MIILAN_LIVE_URL = "MiiLan_Live";
    public static String BASE_MIILAN_URL = "MiiLan_LED_Controller";
    public static String BASE_NORMAL_URL = "NORMAL_LED_Controller";
    public static String BASE_QL_URL = "QL_LED_Controller";
    public static String BASE_SHARE_ABSEN_URL = "absen_player_download";
    public static String BASE_SHARE_LY_URL = "ly_player_download";
    public static String BASE_SHARE_MIILAN_URL = "miilan_player_download";
    public static String BASE_SHARE_NORMAL_URL = "miilan_neutral_download.html";
    public static String BASE_SHARE_QL_URL = "ql_player_download";
    public static String BASE_SHARE_URL = "http://www.miilan.com/";
    public static String BASE_SHARE_XTA_URL = "xta_player_download";
    public static String BASE_SHARE_YD_URL = "yd_player_download";
    public static String BASE_SHARE_ZM_URL = "zm_player_download";
    public static String BASE_TEST_URL = "http://a.miilan.com:21080/update/";
    public static final String URL = "https://miilan.oss-cn-shenzhen.aliyuncs.com/";
    public static String BASE_URL = URL;
    public static String BASE_XTA_URL = "XTA_LED_Controller";
    public static String BASE_YD_URL = "YD_LED_Controller";
    public static String BASE_ZM_URL = "ZM_LED_Controller";
    public static String CHANNEL_ID = "MiiLan Notify ID";
    public static String CHANNEL_NAME = "MiiLan Notify";
    public static String DEVICE_IP = "192.168.43.1";
    public static String PLAYER_OSS = "Player_Data/";
    public static String ROOT = (Environment.getExternalStorageDirectory().toString() + File.separator + "PPMiiLan" + File.separator);
    public static String ROOT2 = (File.separator + "mnt" + File.separator + "ssd" + File.separator);
    public static String ROOT2_AUTH = (ROOT2 + File.separator + "auth" + File.separator);
    public static String ROOT2_BUG_PATH = (ROOT2 + File.separator + "bug" + File.separator);
    public static String ROOT2_CACHE = (ROOT2 + File.separator + "cache" + File.separator);
    public static String ROOT2_MODEL = (ROOT2 + File.separator + "model" + File.separator);
    public static String ROOT2_MUSIC = (ROOT2 + File.separator + "music" + File.separator);
    public static String ROOT2_PIC = (ROOT2 + File.separator + "pic" + File.separator);
    public static String ROOT2_PIC_CACHE = (ROOT2 + File.separator + "pic_cache" + File.separator);
    public static String ROOT2_PORT = (ROOT2 + File.separator + "port" + File.separator);
    public static String ROOT2_PPT = (ROOT2 + File.separator + "ppt" + File.separator);
    public static String ROOT2_SCREENSHOT = (ROOT2 + File.separator + "screenshot" + File.separator);
    public static String ROOT2_VIDEO = (ROOT2 + File.separator + "video" + File.separator);
    public static String ROOT2_VIDEO_CACHE = (ROOT2 + File.separator + "video_cache" + File.separator);
    public static String ROOT_AUTH = (ROOT + File.separator + "auth" + File.separator);
    public static String ROOT_BUG_PATH = (ROOT + File.separator + "bug" + File.separator);
    public static String ROOT_CACHE = (ROOT + File.separator + "cache" + File.separator);
    public static String ROOT_MODEL = (ROOT + File.separator + "model" + File.separator);
    public static String ROOT_MUSIC = (ROOT + File.separator + "music" + File.separator);
    public static String ROOT_PIC = (ROOT + File.separator + "pic" + File.separator);
    public static String ROOT_PIC_CACHE = (ROOT + File.separator + "pic_cache" + File.separator);
    public static String ROOT_PORT = (ROOT + File.separator + "port" + File.separator);
    public static String ROOT_PPT = (ROOT + File.separator + "ppt" + File.separator);
    public static String ROOT_SCREENSHOT = (ROOT + File.separator + "screenshot" + File.separator);
    public static String ROOT_TEMP;
    public static String ROOT_VIDEO = (ROOT + File.separator + "video" + File.separator);
    public static String ROOT_VIDEO_CACHE = (ROOT + File.separator + "video_cache" + File.separator);
    public static String SYSTEM_AUTHORIZE_FILE_NAME = "license.miilan";
    public static String SYSTEM_PORT_FILE_NAME = "port.p";
    public static String SYSTEM_PROGRAM_USB = "MP";
    public static String SYSTEM_XINHAO_FILE_NAME = "xinhao.p";

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(ROOT);
        sb.append(File.separator);
        sb.append("temp");
        sb.append(File.separator);
        ROOT_TEMP = sb.toString();
    }
}
