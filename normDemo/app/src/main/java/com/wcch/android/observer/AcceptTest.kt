package com.wcch.android.observer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.wcch.android.R

/**
 *  normDemo
 *
 *  @author Created by RyanWang on 2022/11/10
 *  Copyright © 2022年 IGRS. All rights reserved.
 *  Describe:
 */
class AcceptTest: AppCompatActivity(),MyObserver {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun update(obj: Object) {
        //接收到
        //textView.setText(obj as String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //注册
        ConcreteSubject.register(this)
        /*button.setOnClickListener {
            startActivity(Intent(this,NextActivity::class.java))
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        //解除注册
        ConcreteSubject.unRegister(this)
    }

}