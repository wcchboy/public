package com.wcch.android.config;


public class Config {
    public static final boolean is4K = true;

    public static final int MAX_WINDOW_COUNT = 1;//支持最大投屏、遥控数量
    public static final int SCREEN_WIDTH = is4K ? 3840 : 1920;
    public static final int SCREEN_HEIGHT = is4K ? 2160 : 1080;

    public static final int FP_SCREEN_WIDTH = is4K ? 1920 : 960;
    public static final int FP_SCREEN_HEIGHT = is4K ? 1080 : 540;

    public static final int WINDOW_DEFAULT_WIDTH = is4K ? 1004 : 503;//投屏窗口初始化宽度
    public static final int WINDOW_DEFAULT_HEIGHT = is4K ? 672 : 336;//投屏窗口初始化高度

    public static final int WINDOW_CARD_VIEW_WIDTH = is4K ? 1004 : 503;//投屏窗口初始化宽度
    public static final int WINDOW_CARD_VIEW_HEIGHT = is4K ? 584 : 292;//投屏窗口初始化高度
    public static final int PHONE_DEFAULT_CARD_VIEW_WIDTH = is4K ? 416 : 208;//投屏窗口初始化宽度
    public static final int PHONE_DEFAULT_CARD_VIEW_HEIGHT = is4K ? 864 : 432;//投屏窗口初始化高度

    public static final int PHONE_WINDOW_DEFAULT_WIDTH = is4K ? 464 : 232;//手机投屏窗口初始化宽
    public static final int PHONE_WINDOW_DEFAULT_HEIGHT = is4K ? 952 : 476;//手机投屏窗口初始化高


    //设备类型  1、deviceType 设备类型，1 Android Phone，2 iPhone，3，PC(Win)，4 Android Pad，5 iPad
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_ANDROID_PHONE = 1;
    public static final int TYPE_IOS_PHONE = 2;
    public static final int TYPE_PC = 3;
    public static final int TYPE_ANDROID_PAD = 4;
    public static final int TYPE_IOS_PAD = 5;

    public static final int WINDOW_MODE = 1000;//窗口模式
    public static final int FULL_SCREEN_MODE = 1001;//全屏模式
    public static final int DF_MODE = 1002;//等分模式
    public static final int FULL_SCREEN_SMALL_WINDOW_MODE = 1003;//全屏后台窗口


    public static final int PHONE_WINDOW_MIN_WIDTH_VERTICAL = is4K ? 404 : 202;//手机窗口竖直方向最小宽度
    public static final int PHONE_WINDOW_MIN_HEIGHT_VERTICAL = is4K ? 852 : 426;//手机窗口竖直方向最小高度
    public static final int PHONE_WINDOW_MAX_WIDTH_VERTICAL = is4K ? 792 : 396;//手机窗口竖直方向最大宽度
    public static final int PHONE_WINDOW_MAX_HEIGHT_VERTICAL = is4K ? 1646 : 823;//手机窗口竖直方向最大高度

    public static final int PHONE_WINDOW_MIN_WIDTH_HORIZONTAL = is4K ? 852 : 426;//手机窗口水平方向最小宽度
    public static final int PHONE_WINDOW_MIN_HEIGHT_HORIZONTAL = is4K ? 404 : 202;//手机窗口水平方向最小高度
    public static final int PHONE_WINDOW_MAX_WIDTH_HORIZONTAL = is4K ? 2800 : 1400;//手机窗口水平方向最大宽度
    public static final int PHONE_WINDOW_MAX_HEIGHT_HORIZONTAL = is4K ? 1349 : 675;//手机窗口水平方向最大高度
    public static final int WINDOW_TOOL_BAR_HEIGHT = is4K ? 88 : 44;//窗口控制条高度
    public static final int WINDOW_TOOL_BAR_MIN_WIDTH = is4K ? 464 : 232;//窗口控制条最小宽度


    public static final int WINDOW_DEFAULT_MARGIN = 60;//投屏窗口初始化margin
    public static final int WINDOW_MIN_WIDTH = 300;
    public static final int WINDOW_MIN_HEIGHT = 220;

    public static String PHONE_APK_FILE_DOWNLOAD_ADDRESS = "https://oss.igrsservice.com/QKCast/QKCast.html";
   // public static String PC_EXE_FILE_DOWNLOAD_ADDRESS = "https://smartdisplay.lenovo.com/installer/betosetup.exe";
    public static String PC_EXE_FILE_DOWNLOAD_ADDRESS = "https://smartdisplay.lenovo.com/installer/betosetup_screen.exe";
    public static String PC_BETO_FILE_DOWNLOAD_ADDRESS = "beto.lenovo.com";
    public static String PC_PUBLIC_FILE_DOWNLOAD_ADDRESS = "qkcast.igrssz.com";


    public static final float RAT = (float) ((SCREEN_HEIGHT * 1.0) / (SCREEN_WIDTH * 1.0));//屏幕高宽比


    /****************************************************************** new config ******************************************************************/

    //当窗口数量是一个或者两个时，PC窗口的默认宽度。
    public static final int PC_DEFAULT_BIG_WINDOW_WIDTH = is4K ? 1825 : 913;
    //当窗口数量三个或四个时，PC窗口more宽度
    public static final int PC_DEFAULT_SMALL_WINDOW_WIDTH = is4K ? 1523 : 762;

    //当窗口是一个或者两个时，手机竖屏状态下窗口默认宽度
    public static final int PHONE_DEFAULT_BIG_WINDOW_VERTICAL_WIDTH = is4K ? 647 : 324;
    //当窗口是三个或者四个时，手机竖屏状态下窗口默认宽度
    public static final int PHONE_DEFAULT_SMALL_WINDOW_VERTICAL_WIDTH = is4K ? 443 : 222;

    //当窗口是一个或者两个时，手机横屏状态下窗口默认宽度
    public static final int PHONE_DEFAULT_BIG_WINDOW_HOR_WIDTH = is4K ? 1389 : 695;
    //当窗口是三个或者四个时，手机横屏状态下窗口默认宽度
    public static final int PHONE_DEFAULT_SMALL_WINDOW_HOR_WIDTH = is4K ? 920 : 460;


    public static final int PAD_DEFAULT_BIG_WINDOW_VERTICAL_WIDTH = is4K ? 914 : 457;
    public static final int PAD_DEFAULT_SMALL_WINDOW_VERTICAL_WIDTH = is4K ? 914 : 457;

    public static final int PAD_DEFAULT_BIG_WINDOW_HOR_WIDTH = is4K ? 1366 : 683;
    public static final int PAD_DEFAULT_SMALL_WINDOW_HOR_WIDTH = is4K ? 1366 : 683;



    //PC窗口缩放的最小和最大值
    public static final int PC_WINDOW_MIN_WIDTH = is4K ? 864 : 432;
    public static final int PC_WINDOW_MAX_WIDTH = is4K ? 2900 : 1450;
    //手机窗口竖屏最小和最大值
    public static final int PHONE_WINDOW_VERTICAL_MIN_WIDTH = is4K ? 416 : 208;
    public static final int PHONE_WINDOW_VERTICAL_MAX_WIDTH = is4K ? 808 : 404;
    //手机窗口横屏最小和最大值
    public static final int PHONE_WINDOW_HOR_MIN_WIDTH = is4K ? 846 : 423;
    public static final int PHONE_WINDOW_HOR_MAX_WIDTH = is4K ? 2800 : 1400;

    //等分模式分隔线宽度
    public static final int DIVIDER_LINE_WIDTH = is4K ? 2 : 1;


    public static final String PROJECTION = "PROJECTION";


}
