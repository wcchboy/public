package com.igrs.cleardata

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import com.igrs.cleardata.databinding.ActivityMainBinding
import com.igrs.cleardata.utils.KeyCodeHelper
import com.igrs.cleardata.utils.LogUtils
import com.igrs.cleardata.utils.PermissionUtil
import java.math.BigInteger


class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private var havePermission = false
    val pageName = "com.android.chrome"//"com.igrs.cleardata"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()
        binding.activityBtn1.setOnClickListener { view ->
            if (havePermission){
                //清除数据
               /* val s = ClearCacheUtils.deleteAppData(pageName)
                println("sss:$s")*/
                test()
            }
        }
        // 键盘的字母事件无法监听到。好像是输入法那边拦截还是咋啦
        binding.activityEventBtn.setOnKeyListener(object :OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                 println("keyCode:$keyCode")
                val k = KeyCodeHelper()
                event?.let {
                    when(it.action) {
                        KeyEvent.ACTION_DOWN->{
                            println("按下了  keyCode:$keyCode")
                            val s = k.onKeyPressed(keyCode)
                            println("s:$s")
                            val aa = KeyCodeHelper.getKeyCodeForMap(keyCode)
                            val bb = aa.toInt()
                            println("aa:$aa")
                            println("bb:$bb")


                            return true
                        }
                        KeyEvent.ACTION_UP->{
                            println("抬起了  keyCode:$keyCode")
                            val s = k.onKeyUp(keyCode)
                            println("s:$s")
                            val aa = KeyCodeHelper.getKeyCodeForMap(keyCode)
                            val bb = aa.toInt()
                            println("aa:$aa")
                            println("bb:$bb")

                            return true
                        }

                        else -> {
                            return false

                        }
                    }
                }

                return false
            }
        })

        binding.activityEventBtn.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                println("beforeTextChanged")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("ontextChanged")
            }

            override fun afterTextChanged(s: Editable?) {
                println("afterTextChanged")
            }
        })
    }


    private fun requestPermission() {
 /*       if (!PermissionUtil.getInstance().checkPermissions(this, Const.permissions)) {
            PermissionUtil.getInstance()
                .checkAskPermissions(this, Const.permissions, Const.REQ_PER_CODE)
        }*/
    /*    if (  PermissionUtil.getInstance()
                .checkAskPermissions(this, Const.permissions, Const.REQ_PER_CODE)){
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionUtil.getInstance().requestPermissions(this, Const.permissions, Const.REQ_PER_CODE)
            //PermissionUtil.getInstance().requestPermissions(this, Const.permissions, Const.REQ_PER_CODE)
            /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001);
            }*/


        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Const.REQ_PER_CODE) {
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    LogUtils.d("zzp", "没有获取权限" + permissions[i])
                } else {
                    LogUtils.d("zzp", "获取权限成功" + permissions[i])
                    havePermission=true
                }
            }
        }
    }

    fun test(){
        val s = Integer.toHexString(10)
        println("16:$s")
        val s1 = encodeHEX(17)
        println("17:$s1")
        val s2 = decodeHEX("b")
        println("18:$s2")
    }

    //將10進制轉換為16進制
    fun encodeHEX(numb: Int?): String? {
        return Integer.toHexString(numb!!)
    }

    //將16進制字符串轉換為10進制數字
    fun decodeHEX(hexs: String?): Int {
        val bigint = BigInteger(hexs, 16)
        return bigint.toInt()
    }

}