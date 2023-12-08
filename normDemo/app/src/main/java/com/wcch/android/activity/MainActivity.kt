package com.wcch.android.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.DisplayMetrics
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.igrs.cleardata.Const
import com.wcch.android.R
import com.wcch.android.databinding.ActivityMainBinding
import com.wcch.android.upgrade.OnlineUpgrade
import com.wcch.android.utils.LogUtils
import com.wcch.android.utils.PermissionUtil
import com.wcch.android.utils.StringUtil
import com.wcch.android.utils.WifiTools
import com.wcch.android.view.PopupMenu


class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private var havePermission = false
    private val MSG_PROGRESS_UPDATE = 0x110
    private val TAG="MainActivity"
    var startI = 0;

    private lateinit var mPopupMenu: PopupMenu

    private val mHandler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            var progress =binding.dialogSv.progress
            //println("progress: $progress")
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
        binding.mettingSettingSureBtn.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                //var path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
               /* val path: string =  System.Environment.GetFolderPath(System.Environment.SpecialFolder.Personal)
                println("----------path: $path")*/
                //com.wcch.android.utils.FileUtils.unZipTest()

                //Test
                WifiTools.getAp5GEnable(applicationContext);
            }
        })


        var result=StringUtil.intToByteArray(7271072);
        //println("test--->"+StringUtil.bytes2Int32Be(result,0))


        var test  = OnlineUpgrade()
        test.test()

        test.downloadTest(this,"","")


        initPopTest1()

        test2()

        binding.testBtn1.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                startActivity(Intent(applicationContext,YaoKongQiActivity::class.java))
            }
        })
    }
    private fun startAn(){

        binding.tP1.setProgress(startI)
        if (startI<=100) {
            //println("startI:$startI")
            startI++
        }



    }

    private val USB_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.MANAGE_EXTERNAL_STORAGE)
    private fun requestPermission() {/*       if (!PermissionUtil.getInstance().checkPermissions(this, Const.permissions)) {
            PermissionUtil.getInstance()
                .checkAskPermissions(this, Const.permissions, Const.REQ_PER_CODE)
        }*//*    if (  PermissionUtil.getInstance()
                .checkAskPermissions(this, Const.permissions, Const.REQ_PER_CODE)){
        }*/

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //PermissionUtil.getInstance().requestPermissions(this, Const.permissions, Const.REQ_PER_CODE)
            //PermissionUtil.getInstance().requestPermissions(this, Const.permissions, Const.REQ_PER_CODE)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001);
            }


        }*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PermissionUtil.getInstance().requestPermissions(this, USB_PERMISSIONS,2)

            val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permission != PackageManager.PERMISSION_GRANTED) { // 请求权限
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 3)
            }
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

    private fun initPopTest1(){
        // initialize popup menu (force measure to get width)

        val menuLayout: View = layoutInflater.inflate(R.layout.toolbar_menu, null)
        menuLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        mPopupMenu = PopupMenu(menuLayout as ViewGroup)
        mPopupMenu.setMenuItemBackgroundColor(-0x4e207d)
        mPopupMenu.setMenuItemHoverBackgroundColor(0x22000000)

        mPopupMenu.setOnMenuItemSelectedListener(object : PopupMenu.OnMenuItemSelectedListener{
            override fun onMenuItemSelected(menuItem: View?) {
                Toast.makeText(this@MainActivity, "Menu item clicked", Toast.LENGTH_SHORT).show()
            }
        })

        // show or dismiss popup menu when clicked

        // show or dismiss popup menu when clicked
        val offsetX = 0f
        val offsetY = 0f
        val menuWidth = menuLayout.measuredWidth.toFloat()
        val menu = findViewById<View>(R.id.toolbar_menu)
        menu.setOnClickListener {
            if (mPopupMenu.isShowing()) {
                mPopupMenu.dismiss()
            } else {
                // based on bottom-left, need take menu width and menu icon width into account
                mPopupMenu.show(menu, (menu.width - offsetX - menuWidth).toInt(), offsetY.toInt())
            }
        }



        /*// initialize popup menu (force measure to get width)
        View menuLayout = getLayoutInflater().inflate(R.layout.toolbar_menu, null);
        menuLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPopupMenu = new PopupMenu((ViewGroup)menuLayout);
        mPopupMenu.setMenuItemBackgroundColor(0xffb1df83);
        mPopupMenu.setMenuItemHoverBackgroundColor(0x22000000);
        mPopupMenu.setOnMenuItemSelectedListener(new PopupMenu.OnMenuItemSelectedListener() {
            @Override
            public void onMenuItemSelected(View menuItem) {
                Toast.makeText(MainActivity.this, "Menu item clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // show or dismiss popup menu when clicked
        final float offsetX = 0;
        final float offsetY = 0;
        final float menuWidth = menuLayout.getMeasuredWidth();
        final View menu = findViewById(R.id.toolbar_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupMenu.isShowing()) {
                    mPopupMenu.dismiss();
                } else {
                    // based on bottom-left, need take menu width and menu icon width into account
                    mPopupMenu.show(menu, (int) (menu.getWidth() - offsetX - menuWidth), (int) offsetY);
                }
            }
        });*/
    }


    fun test2(){
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels


        val resources = resources
        val displayMetrics2 = resources.displayMetrics

        val screenWidth2 = displayMetrics2.widthPixels
        val screenHeight2 = displayMetrics2.heightPixels

        print("screenWidth:$screenWidth  screenHeight:$screenHeight screenWidth2:$screenWidth2  screenHeight2:$screenHeight2")

    }

    fun test3(){
        com.endo.common.utilcode.util.LogUtils.d()
    }

}