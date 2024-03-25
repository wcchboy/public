package com.wcch.android.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.exifinterface.media.ExifInterface;

public class DateUtils {
    public static final String ymd = "yyyy-MM-dd";
    public static final String ymdhms = "yyyy-MM-dd HH:mm:ss";

    public static String monthNumToMonthName(String str) {
        if ("1".equals(str)) {
            return "一月份";
        }
        if (ExifInterface.GPS_MEASUREMENT_2D.equals(str)) {
            return "二月份";
        }
        if (ExifInterface.GPS_MEASUREMENT_3D.equals(str)) {
            return "三月份";
        }
        if ("4".equals(str)) {
            return "四月份";
        }
        if ("5".equals(str)) {
            return "五月份";
        }
        if ("6".equals(str)) {
            return "六月份";
        }
        if ("7".equals(str)) {
            return "七月份";
        }
        if ("8".equals(str)) {
            return "八月份";
        }
        if ("9".equals(str)) {
            return "九月份";
        }
        if ("10".equals(str)) {
            return "十月份";
        }
        if ("11".equals(str)) {
            return "十一月份";
        }
        return "12".equals(str) ? "十二月份" : str;
    }

    public static String getTomorrow() {
        Object obj;
        Calendar instance = Calendar.getInstance();
        instance.add(5, 1);
        int i = instance.get(1);
        int i2 = instance.get(2) + 1;
        int i3 = instance.get(5);
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append("-");
        if (i2 > 9) {
            obj = Integer.valueOf(i2);
        } else {
            obj = "0" + i2;
        }
        sb.append(obj);
        sb.append("-");
        sb.append(i3);
        return sb.toString();
    }

    public static int getYear() {
        return Calendar.getInstance().get(1);
    }

    public static String getToday() {
        Object obj;
        Calendar instance = Calendar.getInstance();
        int i = instance.get(1);
        int i2 = instance.get(2) + 1;
        int i3 = instance.get(5);
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append("-");
        if (i2 > 9) {
            obj = Integer.valueOf(i2);
        } else {
            obj = "0" + i2;
        }
        sb.append(obj);
        sb.append("-");
        sb.append(i3);
        return sb.toString();
    }

    public static List<Integer> getDateForString(String str) {
        String[] split = str.split("-");
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(Integer.parseInt(split[0])));
        arrayList.add(Integer.valueOf(Integer.parseInt(split[1])));
        arrayList.add(Integer.valueOf(Integer.parseInt(split[2])));
        return arrayList;
    }

    public static String dateStr2Timestamp(String str) {
        long j;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            j = simpleDateFormat.parse(str).getTime() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
            j = 0;
        }
        return String.valueOf(j);
    }

    public static List<Integer> getDateForLong(long j) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ymd);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String[] split = simpleDateFormat.format(new Date(j)).split("-");
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(Integer.parseInt(split[0])));
        arrayList.add(Integer.valueOf(Integer.parseInt(split[1])));
        arrayList.add(Integer.valueOf(Integer.parseInt(split[2])));
        return arrayList;
    }

    public static Calendar getCalendar(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance;
    }

    public static String formatDate(String str, String str2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str2);
        try {
            return simpleDateFormat.format(simpleDateFormat.parse(str));
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public static String formatDate(long j, String str) {
        try {
            return new SimpleDateFormat(str).format(new Date(j));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Date formatDateStr(String str, String str2) {
        try {
            return new SimpleDateFormat(str2).parse(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getMonthDays(int i, int i2) {
        switch (i2 + 1) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                return ((i % 4 != 0 || i % 100 == 0) && i % 400 != 0) ? 28 : 29;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return -1;
        }
    }

    public static int getFirstDayWeek(int i, int i2) {
        Calendar instance = Calendar.getInstance();
        instance.set(i, i2, 1);
        Log.d("DateView", "DateView:First:" + instance.getFirstDayOfWeek());
        return instance.get(7);
    }

    public static String getDayWeek(int i, int i2, int i3) {
        Calendar instance = Calendar.getInstance();
        instance.set(i, i2, i3);
        Log.d("DateView", "DateView:First:" + instance.getFirstDayOfWeek());
        switch (instance.get(7)) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
        }
    }
}
