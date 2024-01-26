package com.wcch.android.utils

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * @title 权限类
 */
class PermissionUtil private constructor(){

    companion object {
        fun getInstance() = Helper.instance
        const val PERMISSION_GRANTED = -1
    }

    private object Helper {
        val instance = PermissionUtil()
    }
    /**
     * 权限检查
     * @param neededPermissions 需要的权限
     * @return 是否全部被允许
     */
    fun checkPermissions(context: Context,neededPermissions: Array<String>?): Boolean {
        if (neededPermissions == null || neededPermissions.isEmpty()) {
            return true
        }
        var allGranted = true
        for (neededPermission in neededPermissions) {
            allGranted = allGranted and (ContextCompat.checkSelfPermission(context,
                neededPermission!!) == PackageManager.PERMISSION_GRANTED)
        }
        return allGranted
    }

    /**
     * @title 獲取權限
     * @param context
     * @param permissionName
     * @return
     */
    fun permissionGranted(context : Context, permissionName : String) : Boolean{
        val sdkVerion : Int = getCurrentTargetSdkVersion(context = context)
        val isGranted : Int

        isGranted = if (sdkVerion < Build.VERSION_CODES.M) {
            PermissionChecker.checkSelfPermission(context, permissionName)
        }else
            ContextCompat.checkSelfPermission(context, permissionName)
        return isGranted == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentTargetSdkVersion(context : Context) : Int{
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.applicationInfo.targetSdkVersion
        }catch (e : PackageManager.NameNotFoundException){
            e.printStackTrace()
        }
        return -1
    }

    /**
     * @title 授權
     * @param activity
     * @param permissionName
     * @param permissionCode
     */
    fun requestPermissions(activity : Activity, permissionName : String, permissionCode : Int){
        requestPermissions(activity = activity, permissionNames = arrayOf(permissionName), permissionCode = permissionCode)
    }

    /**
     * @title 授權
     * @param activity
     * @param permissionName
     * @param permissionCode
     */
    fun requestPermissions(activity : Activity, permissionNames : Array<String>, permissionCode : Int){
        ActivityCompat.requestPermissions(activity, permissionNames, permissionCode)
    }



    /**
     * @title 获取权限后是否开启
     * @param result
     * @return
     */
    fun checkRequestResult(result : IntArray) : Boolean{
        return result.isNotEmpty() && result[0] == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 获取批量权限后是否开启
     * @param results
     * @return 全部开通返回-1，有一个未开通返回当前下标
     */
    fun checkRequestResults(results : IntArray?): Int{
        if (results != null && results.isNotEmpty()) {
            for (index in 0 until results.size) {
                if (results[index] == PackageManager.PERMISSION_GRANTED) {
                    continue
                }else
                    return index
            }
        }
        return PERMISSION_GRANTED
    }

    /**
     * @title 电话状态权限
     */
    fun checkPhoneStatePermission(activity: Activity, requestCode : Int) : Boolean {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE)
        return checkAskPermissions(activity = activity, permissionNames = permissions, requestCode = requestCode)
    }

    /**
     * @title 定位权限
     */
    fun checkLocationPermission(activity: Activity, requestCode : Int) : Boolean {
        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        return checkAskPermissions(activity = activity, permissionNames = permissions, requestCode = requestCode)
    }

    /**
     * @title 文件权限
     */
    fun checkFileAccessPermission(activity: Activity, requestCode : Int) : Boolean {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return checkAskPermissions(activity = activity, permissionNames = permissions, requestCode = requestCode)
    }

    /**
     * @title 电话权限
     */
    fun checkCameraPermission(activity: Activity, requestCode : Int) : Boolean {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        return checkAskPermissions(activity = activity, permissionNames = permissions, requestCode = requestCode)
    }

    /**
     * @title 通讯录权限
     */
    fun checkContactPermission(activity: Activity, requestCode : Int) : Boolean {
        val permissions = arrayOf(Manifest.permission.READ_CONTACTS)
        return checkAskPermissions(activity = activity, permissionNames = permissions, requestCode = requestCode)
    }

    /**
     * @title 检查有没有权限，没有权限则请求权限
     */
    fun checkAskPermissions(activity: Activity, permissionNames: Array<String>, requestCode : Int) : Boolean {
        for (permissionName in permissionNames) {
            if (!permissionGranted(context = activity, permissionName = permissionName)) {
                requestPermissions(activity = activity, permissionNames = permissionNames, permissionCode = requestCode)
                return false
            }
        }
        return true
    }



    //判断是否开启悬浮窗权限
    fun checkFloatPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return true
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                var cls = Class.forName("android.content.Context")
                val declaredField: Field = cls.getDeclaredField("APP_OPS_SERVICE")
                declaredField.isAccessible = true
                var obj: Any? = declaredField.get(cls) as? String ?: return false
                val str2 = obj as String
                obj = cls.getMethod("getSystemService", String::class.java).invoke(context, str2)
                cls = Class.forName("android.app.AppOpsManager")
                val declaredField2: Field = cls.getDeclaredField("MODE_ALLOWED")
                declaredField2.isAccessible = true
                val checkOp: Method = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String::class.java)
                val result = checkOp.invoke(obj, 24, Binder.getCallingUid(), context.packageName) as Int
                result == declaredField2.getInt(cls)
            } catch (e: Exception) {
                false
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appOpsMgr = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager ?: return false
                val mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", Process.myUid(), context.packageName)
                mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED
            } else {
                Settings.canDrawOverlays(context)
            }
        }
    }

}
