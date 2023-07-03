package com.igrs.cleardata.utils

import android.util.Log

/**
 *  clearData
 *
 *  Created by ryanWang on 2022/10/28
 *  Copyright © 2022年 IGRS. All rights reserved.
 *
 *  Describe: 日志输出
 */
object  LogUtils {
    private val SHOW_LOG=true
    fun d(tag:String,data:String){
        if (SHOW_LOG){
            Log.d(tag,data)
        }
    }
    fun v(tag:String,data:String){
        if (SHOW_LOG){
            Log.v(tag,data)
        }
    }
    fun e(tag:String,data:String){
        if (SHOW_LOG){
            Log.e(tag,data)
        }
    }
    fun w(tag:String,data:String){
        if (SHOW_LOG){
            Log.w(tag,data)
        }
    }
    fun p(data:String){
        if (SHOW_LOG){
            println(data)
        }
    }
}