package com.wcch.android.utils;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;

import java.util.List;

/**
 * 系统帮助类
 */
public class SystemHelper
{
    /**
     * 判断本地是否已经安装好了指定的应用程序包
     *
     * @param packageNameTarget ：待判断的 App 包名，如 微博 com.sina.weibo
     * @return 已安装时返回 true,不存在时返回 false
     */
    public static boolean appIsExist(Context context, String packageNameTarget) {
        if (!"".equals(packageNameTarget.trim())) {
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
            for (PackageInfo packageInfo : packageInfoList) {
                String packageNameSource = packageInfo.packageName;
                if (packageNameSource.equals(packageNameTarget)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public static boolean setTopApp(Context context )
    {
        if (!isRunningForeground(context))
        {
            /**获取ActivityManager*/
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

            /**获得当前运行的task(任务)*/
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList)
            {
                ComponentName cpn = taskInfo.topActivity;

                /**找到本应用的 task，并将它切换到前台*/
                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName()))
                {
                    //activityManager.moveTaskToFront(taskInfo.id, 0);
                    activityManager.moveTaskToFront(taskInfo.id , ActivityManager.MOVE_TASK_WITH_HOME);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public static boolean setTopApp(Context context , String className)
    {
        if (!isRunningForeground(context))
        {
            /**获取ActivityManager*/
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

            /**获得当前运行的task(任务)*/
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList)
            {
                ComponentName cpn = taskInfo.topActivity;
                String cpnName = cpn.getClassName();
                if (className.equals(cpnName) == false ) {
                    continue;
                }

                /**找到本应用的 task，并将它切换到前台*/
                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName()))
                {
                    //activityManager.moveTaskToFront(taskInfo.id, 0);
                    activityManager.moveTaskToFront(taskInfo.id , ActivityManager.MOVE_TASK_WITH_HOME);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public static boolean isActivityRunning(Context context , String className)
    {
        if (!isRunningForeground(context))
        {
            /**获取ActivityManager*/
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

            /**获得当前运行的task(任务)*/
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList)
            {
                ComponentName cpn = taskInfo.topActivity;
                String cpnName = cpn.getClassName();
                if (className.equals(cpnName) == true || taskInfo.topActivity.getPackageName().equals(context.getPackageName()  ) == true   ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void moveActivityToBack(Activity _activity)
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        _activity.startActivity(intent);
    }

    /**
     * 判断本应用是否已经位于最前端
     *
     * @param context
     * @return 本应用已经位于最前端时，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        /**枚举进程*/
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Display GetDisplay( Context _context )
    {
        DisplayManager manager = (DisplayManager) _context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = manager.getDisplays();
        if(displays.length <= 1)
            return null;

        return displays[ displays.length - 1];
    }

    public static boolean  isActivityForeground(Context context, String className)
    {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            String cpnName = cpn.getClassName();
            if (className.equals(cpnName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWhiteBoardForeground(Context context) {
        return (SystemHelper.isActivityForeground(context, "com.lenovo.whiteboard.activity.WhiteboardMainActivity") ||
                SystemHelper.isActivityForeground(context, "com.lenovo.whiteboard.activity.MainActivity"));
    }
    /**
     * 启动本地安装好的第三方 APP
     * 注意：此种当时启动第三方 APP 时，如果第三方 APP 当时没有运行，则会启动它
     * 如果被启动的 APP 本身已经在运行，则直接将它从后台切换到最前端
     *
     * @param packageNameTarget :App 包名、如
     *                          微博 com.sina.weibo、
     *                          飞猪 com.taobao.trip、
     *                          QQ com.tencent.mobileqq、
     *                          腾讯新闻 com.tencent.news
     */
    private void startLocalApp(Context context , String packageNameTarget) {

        Log.i("Wmx logs::", "-----------------------开始启动第三方 APP=" + packageNameTarget);

        if (appIsExist(context , packageNameTarget))
        {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageNameTarget);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

            /**android.intent.action.MAIN：打开另一程序
             */
            intent.setAction("android.intent.action.MAIN");
            /**
             * FLAG_ACTIVITY_SINGLE_TOP:
             * 如果当前栈顶的activity就是要启动的activity,则不会再启动一个新的activity
             */
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
        else {
//            Toast.makeText(context.getApplicationContext(), "被启动的 APP 未安装", Toast.LENGTH_SHORT).show();
        }
    }

}
