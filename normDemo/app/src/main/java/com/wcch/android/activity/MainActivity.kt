package com.wcch.android.activity

import android.app.Activity
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import com.igrs.cleardata.Const
import com.wcch.android.R
import com.wcch.android.databinding.ActivityMainBinding
import com.wcch.android.utils.LogUtils
import com.wcch.android.view.HorizontalProgressBarWithNumber


class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private var havePermission = false
    private val MSG_PROGRESS_UPDATE = 0x110
    private val TAG="MainActivity"
    var startI = 0;

    private val mHandler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            var progress =binding.dialogSv.progress
            println("progress: $progress")
            binding.dialogSv.progress = ++progress
            if (progress >= 100) {
                this.removeMessages(MSG_PROGRESS_UPDATE)
            }
            startAn()

            this.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100)
        }
    }
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
        mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
        binding.tP1.imageStartAnimation()
    }
    private fun startAn(){

        binding.tP1.setProgress(startI)
        if (startI<=100) {
            println("startI:$startI")
            startI++
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