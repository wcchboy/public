package com.wcch.android.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.igrs.cleardata.Const
import com.wcch.android.R
import com.wcch.android.databinding.ActivityMainBinding
import com.wcch.android.entity.FileBean
import com.wcch.android.entity.ScrGroupReqBean
import com.wcch.android.helper.SpiHelper
import com.wcch.android.upgrade.OnlineUpgrade
import com.wcch.android.utils.*
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
        fastJsonTest()

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
        binding.testBtnSc.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                startActivity(Intent(applicationContext,ScTestActivity::class.java))
            }
        })

        binding.testBtnFile.setOnClickListener { //调用系统文件管理器打开指定路径目录
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            //intent.setDataAndType(Uri.fromFile(dir.getParentFile()), "file/*.txt");
            //intent.setType("file/*.txt"); //华为手机mate7不支持
            //intent.setDataAndType(Uri.fromFile(dir.getParentFile()), "file/*.txt");
            //intent.setType("file/*.txt"); //华为手机mate7不支持
//            intent.setType("");
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, MimeType.PPT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            startActivityForResult(intent, 0)

            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "audio/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "audio/*"

            val chooserIntent = Intent.createChooser(getIntent, "")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            //mActivity.startActivityForResult(chooserIntent, AppConstant.SET_WALLPAPER_RESULT_CODE)


            val requestCode = 100
            //intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(chooserIntent, requestCode)
        }

       /* var adapter = SignalSourceAdapter()
        binding.signalSourceRv.layoutManager = LinearLayoutManager(this)
        binding.signalSourceRv.adapter = adapter*/

        SpiHelper.getInstance().ConnectToHost()

        test3()
    }

    object MimeType {
        const val DOC = "application/msword"
        const val DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        const val XLS = "application/vnd.ms-excel application/x-excel"
        const val XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        const val PPT = "application/vnd.ms-powerpoint"
        const val PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        const val PDF = "application/pdf"
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
        //com.endo.common.utilcode.util.LogUtils.d()
        LogUtils.d(TAG, "test3")
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath+"/" //自定义底图保存路径
        BmpUtils.toBmp(path,"city")
        LogUtils.d(TAG, "test3 end")
    }

    fun fastJsonTest(){
        val bean:FileBean = FileBean("test",3333,3333,3312331)
        var str : String = JSON.toJSONString(bean)

        LogUtils.d("fastJsonTest","fast test str : $str")
        val gson = Gson()
        val req = ScrGroupReqBean()
        req.setName("test1") //屏幕分组名称

        req.setLogCols(3) //每个物理屏幕划分成逻辑屏幕的列数

        req.setLogRows(3) //每个物理屏幕划分成逻辑屏幕的行数

        req.setEnable(1) //屏幕的使能标志，取值0/1

        req.setPhyRows(3) //物理屏幕行数

        req.setPhyCols(3) //物理屏幕列数

        req.setDisplayMode(42) //显示模式

        val str3 = JSON.toJSONString(req)
        val str2 = gson.toJson(req)
        LogUtils.d(TAG, "---- STR :$str3")
        LogUtils.d(TAG, "---- STR2 :$str2")

    }

}