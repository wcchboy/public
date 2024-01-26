package com.wcch.android;

import java.io.File;

public interface Constants {
    String PROJECTION_TYPE = "PROJECTION_TYPE";
    String TASK_TYPE = "TASK_TYPE";
    String PROJECTION_DEV_ID = "PROJECTION_DEV_ID";
    String PROJECTION_DLNA_URI = "PROJECTION_DLNA_URI";
    String DLNA_PROJECTION_TASK = "TP_PROJECTION_TASK";

    String SOFT_PROJECTION_ID = "SOFT_PROJECTION_ID";
    String TP_PROJECTION_TASK = "TP_PROJECTION_TASK";
    String W20_IP = "W20_IP";
    String W20_NAME = "W20_NAME";

    String AUTO_FILE = "projection_auto_file";
    String CAST_CODE = "cast_code";
    String SHOW_CAST_CODE = "show_cast_code";
    String AUTO_OPEN_FILE = "auto_open_file";

    int PROJECTION_SOFT = 0;
    int PROJECTION_W20 = 1;
    int PROJECTION_DLNA = 2;

    //投屏窗口装太
    int UN_WORKING = 1000;//未工作
    int READY = 1001;//准备就绪
    int WORKING = 1002;//正在工作
    int FULL_SCREEN_MODE = 1003;//全屏模式
    int SPLIT_MODE = 1004;//分屏模式
    int MIN_MODE = 1005;//最小化窗口
    int FINISH = 1006;//结束投屏
    int ERROR = 1007;//异常

    int WINDOW_WIDTH = 2840;
    int WINDOW_HEIGHT = 1080;

    String DOWNLOAD_ADDRESS = "https://www.lenovo.com.cn";

    String CHECKING_FRAGMENT = "checking_fragment";
    String DOWNLOADING_FRAGMENT = "download_fragment";
    String DOWNLOAD_FAIL_FRAGMENT = "download_fail_fragment";

    String TITLE_PROJECTION_WINDOW = "TITLE_PROJECTION_WINDOW";
    String TITLE_PROJECTION_CAST_CODE = "TITLE_PROJECTION_CAST_CODE";
    String ROOT_PATH = App.getInstance().getApplicationContext().getExternalCacheDir().getAbsolutePath();
    String UPDATE_FILE_PATH = App.getInstance().getApplicationContext().getExternalCacheDir().getAbsolutePath() + File.separator + "update.apk";
    int MD5080_ADD = 1001;
    int MD5080_REMOVE = 1002;



}
