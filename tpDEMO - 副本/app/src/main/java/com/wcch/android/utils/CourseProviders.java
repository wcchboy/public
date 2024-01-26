package com.wcch.android.utils;

import android.net.Uri;

public class CourseProviders {

    public static final String AUTHORITIES = "com.lenovo.beto.provider";
    public static final String COURSE_PATH = "course";
    public static String AGREEMENT_AUTHORITIES = "content://" + AUTHORITIES + "/agreement";
    public static String DEVICE_INFO_AUTHORITIES = "content://" + AUTHORITIES + "/info";
    public static String SERVICE_AUTHORITIES = "content://" + AUTHORITIES + "/service";
    public static Uri URI_AGREEMENT = Uri.parse(AGREEMENT_AUTHORITIES);
    public static Uri URI_DEVICE_INFO = Uri.parse(DEVICE_INFO_AUTHORITIES);
    public static Uri URI_SERVICE = Uri.parse(SERVICE_AUTHORITIES);


    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITIES);
    public static final Uri COURSE_URI = Uri.withAppendedPath(BASE_URI, COURSE_PATH);

    public static final int STATUS_MATCHER_CODE = 100;//uri匹配码


    public static class CourseColumn {

        public static final String AGREEMENT = "agreement";//是否允许协议
        public static final String ALL_MUTE = "all_mute";
    }
}