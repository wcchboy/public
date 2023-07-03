package com.wcch.android.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.igrs.cleardata.Const
import com.wcch.android.R
import com.wcch.android.databinding.ActivityMainBinding
import com.wcch.android.service.ProjectionService
import com.wcch.android.soft.util.LogUtil
import com.wcch.android.utils.LogUtils
import com.wcch.android.utils.PermissionUtil


class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private var havePermission = false//有悬浮框权限
    private val MSG_PROGRESS_UPDATE = 0x110
    private val TAG="MainActivity"
    var startI = 0;

    private val mHandler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()



    }

    private fun requestPermission() {
        requestPermissions()
        reqFloatParameter()
    }
    private fun requestPermissions(){
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M) {
            if(!PermissionUtil.getInstance().checkPermissions(this,Const.REQ_PERMISSIONS)){
                //PermissionUtil.getInstance().requestPermissions(this,Const.BETO_PERMISSIONS,Const.REQ_PERMISSION_CODE)
                ActivityCompat.requestPermissions(this,Const.REQ_PERMISSIONS, Const.REQ_PER_CODE)
            }
        }
    }
    private fun reqFloatParameter(){
        if (!PermissionUtil.getInstance().checkFloatPermission(this)){
            showNormalDialog()
        }else{
            LogUtil.d(TAG, "有了悬浮框的权限")
            //启动软投屏等相关功能的服务
            startProjectionService()
        }
    }

    private fun showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        var normalDialog: AlertDialog.Builder  = AlertDialog.Builder(this)

        normalDialog.setIcon(R.mipmap.icon)
        normalDialog.setTitle(getString(R.string.app_name) + " " + getString(R.string.suspended_permission))
        //normalDialog.setMessage("投屏需要有悬浮框权限，去设置开启?");
        normalDialog.setPositiveButton(getString(R.string.toset), object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                //启动软投屏等相关功能的服务
                requestSettingCanDrawOverlays()
            }
        })
        normalDialog.setNegativeButton("关闭", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
            }
        })

        // 显示
        normalDialog.show();

    }
    //权限打开
    private fun requestSettingCanDrawOverlays() {
        val sdkInt = Build.VERSION.SDK_INT
        if (sdkInt >= Build.VERSION_CODES.O) { //8.0以上
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivityForResult(intent, Const.REQUEST_DIALOG_PERMISSION)
        } else if (sdkInt >= Build.VERSION_CODES.M) { //6.0-8.0
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, Const.REQUEST_DIALOG_PERMISSION)
        } else { //4.4-6.0以下
            //无需处理了
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Const.REQ_PER_CODE) {
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    LogUtils.d("zzp", "没有获取权限" + permissions[i])
                } else {
                    LogUtils.d("zzp", "获取权限成功" + permissions[i])
                    reqFloatParameter()
                    havePermission = true
                }
            }
        }else  if (requestCode == Const.REQ_PER_CODE) {

        }
    }

    private fun startProjectionService(){
        val intent = Intent(this, ProjectionService::class.java)
        Looper.myQueue().addIdleHandler {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            false
        }
    }

}