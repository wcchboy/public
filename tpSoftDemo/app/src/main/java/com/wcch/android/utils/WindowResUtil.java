package com.wcch.android.utils;

import android.text.TextUtils;

import com.wcch.android.R;
import com.wcch.android.config.Config;


/**
 * @author created by Lzq
 * @time：2022/3/25
 * @Des：
 */
public class WindowResUtil {
    public static int getWindowBg(int type, int ori) {
        int res = -1;
        if (type == Config.TYPE_ANDROID_PHONE) {
            switch (ori) {
                case 0:
                    res = R.drawable.android_phone_bg_0;
                    break;
                case 1:
                    res = R.drawable.android_phone_bg_1;
                    break;
                case 2:
                    res = R.drawable.android_phone_bg_2;
                    break;
                default:
                    res = R.drawable.android_phone_bg_3;
                    break;
            }
        } else if (type == Config.TYPE_IOS_PHONE) {
            switch (ori) {
                case 0:
                    res = R.drawable.ios_phone_0;
                    break;
                case 1:
                    res = R.drawable.ios_phone_1;
                    break;
                case 2:
                    res = R.drawable.ios_phone_2;
                    break;
                default:
                    res = R.drawable.ios_phone_3;
                    break;
            }
        } else if (type == Config.TYPE_ANDROID_PAD) {
            switch (ori) {
                case 0:
                    res = R.drawable.android_pad_0;
                    break;
                case 1:
                    res = R.drawable.android_pad_1;
                    break;
                case 2:
                    res = R.drawable.android_pad_2;
                    break;
                default:
                    res = R.drawable.android_pad_3;
                    break;
            }
        } else if (type == Config.TYPE_IOS_PAD) {
            switch (ori) {
                case 0:
                    res = R.drawable.ios_pad_0;
                    break;
                case 1:
                    res = R.drawable.ios_pad_1;
                    break;
                case 2:
                    res = R.drawable.ios_pad_2;
                    break;
                default:
                    res = R.drawable.ios_pad_3;
                    break;
            }
        } else {
            res = R.drawable.pc_bg;
        }
        return res;
    }

    public static int getWindowTypeIcon(int type) {
        if (type == Config.TYPE_ANDROID_PHONE || type == Config.TYPE_IOS_PHONE) {
            return R.drawable.icon_type_phone;
        } else if (type == Config.TYPE_ANDROID_PAD || type == Config.TYPE_IOS_PAD) {
            return R.drawable.icon_type_pad;
        } else {
            return R.drawable.icon_type_pc;
        }
    }

    public static int getDeviceDefaultName(int type) {
        if (type == Config.TYPE_ANDROID_PHONE) {
            return R.string.unknow_android_device;
        } else if (type == Config.TYPE_IOS_PHONE) {
            return R.string.unknow_ios_device;
        } else {
            return R.string.unknow_pc_name;
        }
    }

    public static String deviceNameFilter(String deviceName) {
        try {
            if (TextUtils.isEmpty(deviceName)) return null;
            int i = deviceName.indexOf("_");
            int j = deviceName.lastIndexOf("_");
            if (i < 0 || j < 0) return deviceName;
            return deviceName.substring(i + 1, j);
        } catch (Exception e) {
            return "";
        }

    }
}
