package com.wcch.serialport.utils

import android.util.Log

/**
 *  Created by ryanWang on 2022/10/30
 *  Describe: 日志输出
 */
class  LogUtils {

    companion object {

        @JvmField
        var nameStatic: String = "LogUtils"

        private const val SHOW_LOG = true

        @JvmStatic
        fun d(tag: String, data: String) {
            if (SHOW_LOG) {
                Log.d(tag, data)
            }
        }
        @JvmStatic
        fun v(tag: String, data: String) {
            if (SHOW_LOG) {
                Log.v(tag, data)
            }
        }
        @JvmStatic
        fun e(tag: String, data: String) {
            if (SHOW_LOG) {
                Log.e(tag, data)
            }
        }
        @JvmStatic
        fun w(tag: String, data: String) {
            if (SHOW_LOG) {
                Log.w(tag, data)
            }
        }
        @JvmStatic
        fun p(data: String) {
            if (SHOW_LOG) {
                println(data)
            }
        }
    }
}