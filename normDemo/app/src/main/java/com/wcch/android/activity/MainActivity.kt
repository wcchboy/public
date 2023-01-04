package com.wcch.android.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.igrs.cleardata.Const
import com.wcch.android.databinding.ActivityMainBinding
import com.wcch.android.utils.LogUtils
import com.wcch.android.utils.PermissionUtil


class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private var havePermission = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()
        binding.activityBtn1.setOnClickListener { view ->
            if (havePermission) { //清除数据
                //val s = ClearCacheUtils.deleteAppData("com.igrs.cleardata")
                //println("sss:$s")
            }
        }
    }


    private fun requestPermission() {/*       if (!PermissionUtil.getInstance().checkPermissions(this, Const.permissions)) {
            PermissionUtil.getInstance()
                .checkAskPermissions(this, Const.permissions, Const.REQ_PER_CODE)
        }*//*    if (  PermissionUtil.getInstance()
                .checkAskPermissions(this, Const.permissions, Const.REQ_PER_CODE)){
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //PermissionUtil.getInstance().requestPermissions(this, Const.permissions, Const.REQ_PER_CODE)
            //PermissionUtil.getInstance().requestPermissions(this, Const.permissions, Const.REQ_PER_CODE)
            /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001);
            }*/


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
                    havePermission = true
                }
            }
        }
    }


}