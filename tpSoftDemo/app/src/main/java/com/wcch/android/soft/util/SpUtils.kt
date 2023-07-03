package com.wcch.android.soft.util

import android.content.SharedPreferences
import com.wcch.android.App

/**
 * @author markLiu
 * @title SpUtils
 * @time 2021/2/2 15:21
 * @description sp存储
 */
class SpUtils {
    var sp: SharedPreferences? = null
        get() {
            if (field == null) field = App.getInstance().applicationContext!!.getSharedPreferences("set", 0)
            return field
        }
        private set

    fun containsKey(key: String?): Boolean {
        return sp!!.contains(key)
    }

    fun save(key: String?, value: Int) {
        sp!!.edit().putInt(key, value).apply()
    }

    fun getInt(key: String?): Int {
        return sp!!.getInt(key, -1)
    }

    fun save(key: String?, value: String?) {
        sp!!.edit().putString(key, value).apply()
    }

    fun getString(key: String?): String? {
        return sp!!.getString(key, "")
    }

    fun save(key: String?, value: Boolean) {
        sp!!.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String?): Boolean {
        return sp!!.getBoolean(key, false)
    }

    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return sp!!.getBoolean(key, defValue)
    }

    fun save(key: String?, value: Long) {
        sp!!.edit().putLong(key, value).apply()
    }

    fun getLong(key: String?): Long {
        return sp!!.getLong(key, -1)
    }

    companion object {
        @JvmStatic
        var instance: SpUtils? = null
            get() {
                if (field == null) field = SpUtils()
                return field
            }
            private set
    }
}