package com.igrs.sml.util;

import android.Manifest;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

import com.igrs.sml.tcp.TcpConst;
import com.igrs.tpsdk.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BaseUtil {

    public static void screenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
        L.i("BaseUtil->isScreenOn:-----------:" + isScreenOn);
        if (!isScreenOn) {

            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, ":bright");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
            // 屏幕解锁
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
            // 屏幕锁定
            keyguardLock.reenableKeyguard();
            keyguardLock.disableKeyguard(); // 解锁
        }
    }

    /**
     * 是否是平板
     *
     * @param context 上下文
     * @return 是平板则返回true，反之返回false
     */
    public static boolean isPad(Context context) {
        boolean isPad = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        if(isPad){
            return true;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y); // 屏幕尺寸


        L.e("BaseUtil-->isPad->screenInches:" + screenInches + " isPad:" + isPad + " screenLayout:" + context.getResources().getConfiguration().screenLayout);
        if (screenInches >= 7.0) {
            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * 判断是否为合法IP
     **/
    public static boolean isCorrectIp(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public static boolean connectWifi(Context context, String targetSsid, String targetPsd, String enc) {
        L.e("BaseUtil-->connectWifi start ssid:" + targetSsid + " targetPsd:" + targetPsd);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            L.e("BaseUtil-->connectWifi ACCESS_FINE_LOCATION:");
            return false;
        }
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //调用WifiManager的setWifiEnabled方法设置wifi的打开或者关闭，只需把下面的state改为布尔值即可（true:打开 false:关闭）
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+targetSsid+"\""))
            {
                L.i("BaseUtil-->connectWifi existingConfig  remove:" + existingConfig.networkId+"\n"+existingConfig);
                mWifiManager.removeNetwork(existingConfig.networkId);
                //return existingConfig;
            }
        }


        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + targetSsid + "\"";
        String psd = "\"" + targetPsd + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (enc) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            case "OPEN":
                //开放网络
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi
        int wcgID = mWifiManager.addNetwork(conf);

        boolean result = mWifiManager.disconnect();
        L.i("BaseUtil-->connectWifi disconnect result:" + result);
        result = mWifiManager.enableNetwork(wcgID, true);
        L.i("BaseUtil-->connectWifi enableNetwork result:" + result);
        result = mWifiManager.reconnect();
        L.i("BaseUtil-->connectWifi reconnect result:" + result);
        return result;
    }

    public static boolean gpsEnabled(Context context){
        LocationManager mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        boolean enabled = true;
        if (Build.VERSION.SDK_INT >= 31) {
//            if (mLocationManager.hasProvider(LocationManager.GPS_PROVIDER)) {
//                enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            }
        } else {
            final List<String> providers = mLocationManager.getAllProviders();
            if (providers != null && providers.contains(LocationManager.GPS_PROVIDER)) {
                enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        }
        return enabled;
    }

    public static void createNotificationChannel(Service service,String title) {


        Notification.Builder builder = new Notification.Builder(service.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = service.getPackageManager().getLaunchIntentForPackage(service.getPackageName()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(service, 0, nfIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(service, 0, nfIntent, PendingIntent.FLAG_ONE_SHOT);
        }

        //builder.setContentIntent(PendingIntent.getActivity(service, 0, nfIntent, 0)) // 设置PendingIntent
        builder.setContentIntent(pendingIntent) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(service.getResources(), R.drawable.ic_ul_desk)) // 设置下拉列表中的图标(大图标)
                .setSmallIcon(R.drawable.ic_ul_desk) // 设置状态栏内的小图标
                .setContentText(title) // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        /*以下是对Android 8.0的适配*/
        //普通notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        //前台服务notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) service.getSystemService(service.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        service.startForeground(110, notification);
    }
    static int c = 0;
    public static boolean checkIsIFrame(byte[] data) {
        c++;
        if (data == null || data.length < 5) {
            return false;
        }
        int i=TcpConst.h264_has_time?8:0;
        byte nalu = data[4+i];
        if (data[0+i] == 0x0 && data[1+i] == 0x0 && data[2+i] == 0x0 && data[3+i] == 0x1
                && ((nalu & 0x0f) == 0x9 ||(nalu & 0x0f) == 0x7  || (nalu & 0x0f) == 0x6 || (nalu & 0x0f) == 0x5 || nalu == 0x41 || nalu  == 0x27 || nalu == 0x40)) {
            L.e(c+" checkTimeIsIFrame--->data:"+nalu);
            c=0;
            return true;
        }else{
           // L.i(c+" checkTimeIsIFrame-->break->data:"+nalu);
            if(c==300){
                c=0;
            }
            return false;
        }
    }


    public static boolean checkIsIFrame2(byte[] data) {

        if (data == null || data.length < 5) {
            return false;
        }
        int i=0;
        byte nalu = data[4+i];
        if (data[0+i] == 0x0 && data[1+i] == 0x0 && data[2+i] == 0x0 && data[3+i] == 0x1
                && ((nalu & 0x0f) == 0x9 ||(nalu & 0x0f) == 0x7  || (nalu & 0x0f) == 0x6 || (nalu & 0x0f) == 0x5 || nalu == 0x41 || nalu  == 0x27 || nalu == 0x40)) {
            return true;
        }else{
            return false;
        }
    }


}
