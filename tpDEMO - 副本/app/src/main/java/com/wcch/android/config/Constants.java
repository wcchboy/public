package com.wcch.android.config;

import com.wcch.android.App;

import java.io.File;

/**
 * @author created by Lzq
 * @time：2021/8/4
 * @Des：
 */
public interface Constants {

    int WINDOW_WIDTH = 2120;//窗口宽
    int WINDOW_HEIGHT = 1360;//窗口高
    int LOTTIE_WIDTH = WINDOW_WIDTH * 2;//lottie控件宽
    int LOTTIE_HEIGHT = WINDOW_HEIGHT * 2;//lottie控件高

    String ROOT_PATH = App.getInstance().getApplicationContext().getExternalCacheDir().getAbsolutePath();
    String UPDATE_FILE_PATH = App.getInstance().getApplicationContext().getExternalCacheDir().getAbsolutePath() + File.separator + "update.apk";

    //设备类型
    int DEVICE_LFD = 1;//大屏
    int DEVICE_MC = 2;//八爪鱼
    int DEVICE_PC = 3;//PC

}
