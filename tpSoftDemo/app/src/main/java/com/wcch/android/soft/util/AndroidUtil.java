package com.wcch.android.soft.util;

import static android.content.Context.WIFI_SERVICE;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import com.wcch.android.App;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AndroidUtil {
    public final static String TAG = AndroidUtil.class.getSimpleName();


    public static void setWallpaper(Context context, String fileDest) {
        try {
            WallpaperManager wpm = (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
            if (fileDest != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(fileDest);
                wpm.setBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getVersionCode(Context context) {
        if (context == null) {
            return -1;
        }
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int version = packInfo.versionCode;
            return version;
        } catch (Exception e) {

        }
        return -1;
    }

    public static boolean runCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;

        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            Log.d(TAG, command);
            process = Runtime.getRuntime().exec(command);
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
            process.waitFor();
            String msg = "success:" + successMsg + "\n error:" + errorMsg;

        } catch (Exception e) {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
                if (os != null) {
                    os.close();
                }

                // process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public static boolean checkAppInstalled(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        try {
            context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (Exception x) {
            return false;
        }
        return true;
    }

    public static void cleanMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                    info.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE) {
                activityManager.killBackgroundProcesses(info.processName);
            }
        }
    }

    public static void setTime(Context context, Date date) {
        try {
            long when = date.getTime();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setTime(when);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public static void setTimeZone(Context context, String timezone) {
        try {
            AlarmManager timeZone = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            timeZone.setTimeZone(timezone);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void reboot(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_REBOOT);
            intent.putExtra("nowait", 1);
            intent.putExtra("interval", 1);
            intent.putExtra("window", 0);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        runCommand("reboot");
    }
    public static final String ACTION_REQUEST_SHUTDOWN="android.intent.action.ACTION_REQUEST_SHUTDOWN";
    public static final String EXTRA_KEY_CONFIRM="android.intent.extra.KEY_CONFIRM";
    public static void shutdown(Context context) {
        Intent newIntent = new Intent(ACTION_REQUEST_SHUTDOWN);//之所以能够在源码中查看，但是调用的时候不显示，是因为这个不对上层开放
        newIntent.putExtra(EXTRA_KEY_CONFIRM, false);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
        //runCommand("reboot -p");
    }

    public String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String deviceId = null;
        try {
            deviceId = telephonyManager.getDeviceId();
        } catch (SecurityException e) {

        }
        if (deviceId == null) {
            deviceId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        }
        return deviceId;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !(inetAddress instanceof Inet6Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
            Logger.getLogger(TAG).log(Level.SEVERE, ex.toString());
        }
        return null;
    }

    /**
     * 获取CPU序列�?
     *
     * @return CPU序列�?16�? 读取失败�?0000000000000000"
     */
    public static String getCPUSerial() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        if (true) {
            return cpuAddress;
        }
        try {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列�?
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    // 查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        // 提取序列�?
                        strCPU = str.substring(str.indexOf(":") + 1,
                                str.length());
                        // 去空�?
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    // 文件结尾
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认�?
            ex.printStackTrace();
            Logger.getLogger(TAG).log(Level.SEVERE, ex.toString());
        }
        return cpuAddress;
    }


    public static void setDefaultLauncher(Context context, String packageName) {

        try {
            // get default component

            //清除当前默认launcher
            ArrayList<IntentFilter> intentList = new ArrayList<IntentFilter>();
            ArrayList<ComponentName> cnList = new ArrayList<ComponentName>();
            context.getPackageManager().getPreferredActivities(intentList, cnList, null);
            IntentFilter dhIF = null;
            for (int i = 0; i < cnList.size(); i++) {
                dhIF = intentList.get(i);
                if (dhIF.hasAction(Intent.ACTION_MAIN) && dhIF.hasCategory(Intent.CATEGORY_HOME)) {
                    context.getPackageManager().clearPackagePreferredActivities(cnList.get(i).getPackageName());
                }
            }

            //获取所有launcher activity
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            List<ResolveInfo> list = new ArrayList<ResolveInfo>();
            try {
                list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            } catch (Exception e) {
//                    throw new RuntimeException("Package manager has died", e);
            }
            // get all components and the best match
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MAIN);
            filter.addCategory(Intent.CATEGORY_HOME);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            final int N = list.size();

            //设置默认launcher
            ComponentName launcher = null;//new ComponentName(packageName, className);

            ComponentName[] set = new ComponentName[N];
            int defaultMatch = 0;
            for (int i = 0; i < N; i++) {
                ResolveInfo r = list.get(i);
                set[i] = new ComponentName(r.activityInfo.packageName, r.activityInfo.name);
                if (packageName.equals(r.activityInfo.packageName)) {
                    launcher = new ComponentName(packageName, r.activityInfo.name);
                    defaultMatch = r.match;
                }
            }

            try {
                if (launcher != null) {
                    context.getPackageManager().addPreferredActivity(filter, defaultMatch, set, launcher);
                }
            } catch (Exception e) {
//                    throw new RuntimeException("Package manager has died", e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String deviceId = null;
        try {
            deviceId = telephonyManager.getDeviceId();
        } catch (SecurityException e) {

        }
        return deviceId;
    }

    public static String getAndroidID(Context context) {
        String deviceId = null;
        try {

            deviceId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        } catch (SecurityException e) {

        }
        return deviceId;
    }

    public static String getVersionName(Context context) {
        if (context == null) {
            return null;
        }
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    public static long getFreeSpace(String path) {

        StatFs statFs = new StatFs(path);
        long blocSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availaBlock = statFs.getAvailableBlocks();
        long total = totalBlocks * blocSize;
        long availableSpare = availaBlock * blocSize;
        return availableSpare;
    }

    public static long getTotalSpace(String path) {

        StatFs statFs = new StatFs(path);
        long blocSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availaBlock = statFs.getAvailableBlocks();
        long total = totalBlocks * blocSize;
        long availableSpare = availaBlock * blocSize;
        return total;
    }

    public static void startPackage(Context context, String packageName) {
        Intent mainIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            // launcher the package
            context.startActivity(mainIntent);

        } catch (ActivityNotFoundException noFound) {
            Toast.makeText(context, "Package not found!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //    public static void screenOff(Context cxt) {
//
//        if (cxt == null)
//            return;
//        try {
//            WifiManager wifiManager = (WifiManager) cxt
//                    .getSystemService(Context.WIFI_SERVICE);
//            if (wifiManager != null && wifiLock == null) {
//                wifiLock = wifiManager.createWifiLock("wifi lock");
//                wifiLock.acquire();
//            }
//            Intent intent = new Intent("android.adw.intent.action.sleep");
//            cxt.sendBroadcast(intent);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void screenOn(Context cxt) {
//        if (cxt == null)
//            return;
//
//        try {
//            WifiManager wifiManager = (WifiManager) cxt
//                    .getSystemService(Context.WIFI_SERVICE);
//            if (wifiManager != null && wifiLock != null) {
//                wifiLock.release();
//                wifiLock = null;
//            }
//            Intent intent = new Intent("android.adw.intent.action.wakeup");
//            cxt.sendBroadcast(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void saveBitmap(Bitmap bitmap, String filename) {
        try (FileOutputStream out = new FileOutputStream(filename)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Rect GetUniformRectWithBound(Rect rtBound, Size szRatio) {
        Rect rt = new Rect();
        if ((double) rtBound.width() / (double) rtBound.height() >= (double) szRatio.getWidth() / (double) szRatio.getHeight()) {
            //框比内容水平方向更扁，以边界框高度为基准计算
            double w = (double) rtBound.height() * (double) szRatio.getWidth() / (double) szRatio.getHeight();
            double left = rtBound.left + (rtBound.width() - w) / 2.0;
            double top = rtBound.top;
            return new Rect((int) (left), (int) top, (int) (w + left), (int) (rtBound.height() + top));
        } else {
            //框比内容水平方向更窄，以边界框宽度为基准计算
            double h = (double) rtBound.width() * (double) szRatio.getHeight() / (double) szRatio.getWidth();
            double top = rtBound.top + (rtBound.height() - h) / 2.0;
            return new Rect(rtBound.left, (int) (top), rtBound.width(), (int) (h + top));
        }
    }

    public static void saveThumbnail(Bitmap bitmap, String filename, int maxWidth, int maxHeight) {
        Rect thumbRect = GetUniformRectWithBound(new Rect(0, 0, maxWidth, maxHeight), new Size(bitmap.getWidth(), bitmap.getHeight()));
        Bitmap thumb = Bitmap.createScaledBitmap(bitmap, thumbRect.width(), thumbRect.height(), true);
        try (FileOutputStream out = new FileOutputStream(filename)) {
            thumb.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ResolveInfo findAppByPackageName(Context context, String mPackageName) {
        ResolveInfo newAppInfo = null;
        // 用于存放临时应用程序
        List<ResolveInfo> mTempAllApps;

        PackageManager TempPackageManager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(mPackageName);
        mTempAllApps = TempPackageManager.queryIntentActivities(mainIntent, 0);
        if (mTempAllApps == null || mTempAllApps.size() <= 0) {
            return null;
        }
        newAppInfo = mTempAllApps.get(0);

        return newAppInfo;
    }

    public static ResolveInfo findHomeAppByPackageName(Context context, String mPackageName) {
        ResolveInfo newAppInfo = null;
        // 用于存放临时应用程序
        List<ResolveInfo> mTempAllApps;

        PackageManager TempPackageManager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_HOME);
        mainIntent.setPackage(mPackageName);
        mTempAllApps = TempPackageManager.queryIntentActivities(mainIntent, 0);
        if (mTempAllApps == null || mTempAllApps.size() <= 0) {
            return null;
        }
        newAppInfo = mTempAllApps.get(0);

        return newAppInfo;
    }
    public static void screenOn(Context context) {
        if(wifiLock != null) {
            wifiLock.release();
            wifiLock = null;
        }
        // turn on screen
        Log.v("ProximityActivity", "ON!");
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "androidmanage:tag");
        wakeLock.acquire();
        wakeLock.release();

        try {
            powerManager.getClass().getMethod("wakeUp", new Class[]{long.class}).invoke(powerManager, SystemClock.uptimeMillis());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public static WifiManager.WifiLock wifiLock;
    public static PowerManager.WakeLock wakeLock;
    public static void screenOff(Context context){
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(WIFI_SERVICE);
        if (wifiManager != null && wifiLock == null) {
            wifiLock = wifiManager.createWifiLock("wifi lock");
            wifiLock.acquire();
        }
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "androidmanage:screenoff");
            wakeLock.acquire();
        }
        try {
            powerManager.getClass().getMethod("goToSleep", new Class[]{long.class}).invoke(powerManager, SystemClock.uptimeMillis());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static String getSSID(Context context){
        WifiConfiguration wifiConfiguration = getWifiConfiguration(context);
        return wifiConfiguration.SSID;
    }

    public static String getDeviceName(Context context){
        String name = SystemProperties.read("persist.sys.devicename");
        return name;
    }
    public static WifiConfiguration getWifiConfiguration(Context context){
        WifiConfiguration mWifiConfig = null;
        try{
            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            mWifiConfig = (WifiConfiguration) method.invoke(wifiManager);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return mWifiConfig;
    }
    public static class SystemProperties {

        private static String GETPROP_EXECUTABLE_PATH = "/system/bin/getprop";
        private static String TAG = "MyApp";

        public static String read(String propName) {
            Process process = null;
            BufferedReader bufferedReader = null;

            try {
                process = new ProcessBuilder().command(GETPROP_EXECUTABLE_PATH, propName).redirectErrorStream(true).start();
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = bufferedReader.readLine();
                if (line == null){
                    line = ""; //prop not set
                }
                Log.i(TAG,"read System Property: " + propName + "=" + line);
                return line;
            } catch (Exception e) {
                Log.e(TAG,"Failed to read System Property " + propName,e);
                return "";
            } finally{
                if (bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {}
                }
                if (process != null){
                    process.destroy();
                }
            }
        }
    }



    public static String getMachineName() {
        String name = null;

        if (TextUtils.isEmpty(name)) {
            try {
                name = Settings.Secure.getString(App.getInstance().getApplicationContext().getContentResolver(), "bluetooth_name");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(name)) {
            try {
                name = Settings.Global.getString(App.getInstance().getApplicationContext().getContentResolver(), Settings.Global.DEVICE_NAME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (TextUtils.isEmpty(name)) {
                name = Build.MODEL;
            }
            if (TextUtils.isEmpty(name)) {
                name = Build.DEVICE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }

    public static String getDeviceName() {
        String name = null;

        try {
            name = ConfigUtil.getInstance().getDeviceName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(name)) {
            try {
                name = getMachineName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return name;
    }
}
