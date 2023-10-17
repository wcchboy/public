package com.wcch.android.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.wcch.android.App
import com.wcch.android.soft.util.ConfigUtil
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.security.SecureRandom

object AndroidUtil {
    val TAG = AndroidUtil::class.java.simpleName
    fun getVersionName(context: Context?): String? {
        if (context == null) {
            return null
        }
        try {
            // 获取packagemanager的实例
            val packageManager: PackageManager = context.packageManager
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            val packInfo: PackageInfo = packageManager.getPackageInfo(
                context.packageName, 0
            )
            return packInfo.versionName
        } catch (e: Exception) {
        }
        return null
    }

    fun getVersionCode(context: Context?): Int {
        if (context == null) {
            return -1
        }
        try {
            // 获取packagemanager的实例
            val packageManager: PackageManager = context.packageManager
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            val packInfo: PackageInfo = packageManager.getPackageInfo(
                context.packageName, 0
            )
            return packInfo.versionCode
        } catch (e: Exception) {
        }
        return -1
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    val systemVersion: String
        get() = Build.VERSION.RELEASE

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    val systemModel: String
        get() = Build.MODEL

    fun startPackage(context: Context, packageName: String?) {
        val mainIntent = context.packageManager.getLaunchIntentForPackage(packageName!!)
            ?: return
        try {
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // launcher the package
            context.startActivity(mainIntent)
        } catch (noFound: ActivityNotFoundException) {
//            Toast.makeText(context, context.getResources().getString(R.string.no_package_found), Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "未发现软件", Toast.LENGTH_SHORT).show();
        }
    }

    fun isWifiApOpen(context: Context): Boolean {
        //     try {
        val manager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        //            //通过放射获取 getWifiApState()方法
//            Method method = manager.getClass().getDeclaredMethod("getWifiApState");
//            //调用getWifiApState() ，获取返回值
//            int state = (int) method.invoke(manager);
//            //通过放射获取 WIFI_AP的开启状态属性
//            Field field = manager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED");
//            //获取属性值
//            int value = (int) field.get(manager);
//            //判断是否开启
//            if (state == value) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
        return manager.isWifiEnabled()
        //    return false;
    }

    fun isHotPointOpen(context: Context): Boolean {
        try {
            val manager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            //通过放射获取 getWifiApState()方法
            val method: Method = manager.javaClass.getDeclaredMethod("getWifiApState")
            //调用getWifiApState() ，获取返回值
            val state = method.invoke(manager) as Int
            //通过放射获取 WIFI_AP的开启状态属性
            val field: Field = manager.javaClass.getDeclaredField("WIFI_AP_STATE_ENABLED")
            //获取属性值
            val value = field[manager] as Int
            //判断是否开启
            return state == value
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return false
    }

    //支持蓝牙模块
    @get:SuppressLint("MissingPermission")
    val isBlueToothOpen: Boolean
        get() {
            val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            //支持蓝牙模块
            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.isEnabled) {
                    return true
                }
            }
            return false
        }
    val connectWifiSsid: String
        get() {
            @SuppressLint("WifiManagerLeak") val wifiManager: WifiManager = App.getInstance().
            applicationContext.getSystemService(
                    Context.WIFI_SERVICE
                ) as WifiManager
            val wifiInfo: WifiInfo = wifiManager.getConnectionInfo()
         //   LogUtil.d("wifiInfo", wifiInfo.toString())
       //     LogUtil.d("SSID", wifiInfo.getSSID())
            return wifiInfo.ssid
        }

    fun isAppInstall(context: Context, packageName: String?): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName!!, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun getScreenWidth(context: Context): Int {
        val metrics = context.resources.displayMetrics
        return metrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val metrics = context.resources.displayMetrics
        return metrics.heightPixels
    }

    /**
     * 4位随机投屏码
     * @return 投屏码
     */
    fun getCastCode(): String? {
        val random = SecureRandom()
        val codeStr = StringBuilder()
        for (i in 0..3) {
            codeStr.append(random.nextInt(10))
        }
        return if ("0000" == codeStr.toString()) {
            "000" + random.nextInt(10)
        } else codeStr.toString()
    }

    fun checkAppInstalled(context: Context, pkgName: String?): Boolean {
        if (TextUtils.isEmpty(pkgName)) {
            return false
        }
        try {
            context.packageManager.getPackageInfo(pkgName!!, 0)
        } catch (x: java.lang.Exception) {
            return false
        }
        return true
    }

    fun getMachineName(): String? {
        var name: String? = null
        if (TextUtils.isEmpty(name)) {
            try {
                name = Settings.Secure.getString(App.getInstance().applicationContext.contentResolver, "bluetooth_name")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        if (TextUtils.isEmpty(name)) {
            try {
                name = Settings.Global.getString(App.getInstance().applicationContext.contentResolver, Settings.Global.DEVICE_NAME)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        try {
            if (TextUtils.isEmpty(name)) {
                name = Build.MODEL
            }
            if (TextUtils.isEmpty(name)) {
                name = Build.DEVICE
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return name
    }

    fun getAndroidID(context: Context): String? {
        var deviceId: String? = null
        try {
            deviceId = Settings.System.getString(context.contentResolver, Settings.System.ANDROID_ID)
        } catch (e: SecurityException) {
        }
        return deviceId
    }
    fun getDeviceName(): String? {
        var name: String? = null
        try {
            name = ConfigUtil.getInstance().getDeviceName()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (TextUtils.isEmpty(name)) {
            try {
                name = AndroidUtil.getMachineName()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return name
    }

}