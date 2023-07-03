package com.igrs.betotablet.utils

import android.content.Context
import android.text.TextUtils
import com.wcch.android.soft.util.SpUtils
import java.util.regex.Pattern

/**
 * @author markLiu
 * @title SettingUtils
 * @time 2021/2/2 15:37
 * @description
 */
class SettingUtils {
    private val SHARE_SPEAKER = "share_speaker"
    private val SHARE_MAC = "share_mac"
    private val SHARE_CAMERA = "share_camera"
    private val SHARE_HID = "share_hid"
    private val SHARE_PROJECTION = "share_projection"
    private val SHARE_YK = "share_yk"
    private val SHARE_SPC = "share_spc"
    private val MEETING_ROOM_SELECTED = "meeting_room"
    private val FINISH_GUIDE = "finish_guide" //是否完成开机指引
    private val ENTER_SETTING = "finish_guide" //是否完成开机指引
    private val FIRST_TIME_LOGIN = "first_time_login"
    var shareSpeakerStatus: Boolean
        get() = SpUtils.instance!!.getBoolean(SHARE_SPEAKER, true)
        set(enable) {
            SpUtils.instance!!.save(SHARE_SPEAKER, enable)
        }
    var shareMacStatus: Boolean
        get() = SpUtils.instance!!.getBoolean(SHARE_MAC, true)
        set(enable) {
            SpUtils.instance!!.save(SHARE_MAC, enable)
        }
    var shareCameraStatus: Boolean
        get() = SpUtils.instance!!.getBoolean(SHARE_CAMERA, true)
        set(enable) {
            SpUtils.instance!!.save(SHARE_CAMERA, enable)
        }
    var shareHidStatus: Boolean
        get() = SpUtils.instance!!.getBoolean(SHARE_HID, true)
        set(enable) {
            SpUtils.instance!!.save(SHARE_HID, enable)
        }
    var shareProjectionStatus: Boolean
        get() = SpUtils.instance!!.getBoolean(SHARE_PROJECTION, true)
        set(enable) {
            SpUtils.instance!!.save(SHARE_PROJECTION, enable)
        }
    var shareYkStatus: Boolean
        get() = SpUtils.instance!!.getBoolean(SHARE_YK, true)
        set(enable) {
            SpUtils.instance!!.save(SHARE_YK, enable)
        }
    var shareSpcStatus: Boolean
        get() = SpUtils.instance!!.getBoolean(SHARE_SPC, true)
        set(enable) {
            SpUtils.instance!!.save(SHARE_SPC, enable)
        }
    var meetingRoom: Int
        get() = SpUtils.instance!!.getInt(MEETING_ROOM_SELECTED)
        set(meetingRoom) {
            SpUtils.instance!!.save(MEETING_ROOM_SELECTED, meetingRoom)
        }

    fun setFinishGuide() {
        SpUtils.instance!!.save(FINISH_GUIDE, true)
    }

    //   boolean aBoolean = SpUtils.getInstance().getBoolean(FINISH_GUIDE,false );
    val finishGuide: Boolean
        get() =//   boolean aBoolean = SpUtils.getInstance().getBoolean(FINISH_GUIDE,false );
            SpUtils.instance!!.getBoolean(FINISH_GUIDE, true)

    fun setEnterSetting() {
        SpUtils.instance!!.save(ENTER_SETTING, true)
    }

    fun setFalseEnterSetting() {
        SpUtils.instance!!.save(ENTER_SETTING, false)
    }

    val enterSetting: Boolean
        get() = SpUtils.instance!!.getBoolean(ENTER_SETTING, false)

    fun setFirstTimeLogin() {
        SpUtils.instance?.save(FIRST_TIME_LOGIN, false)
    }

    fun getFirstTimeLogin(): Boolean{
        return SpUtils.instance!!.getBoolean(FIRST_TIME_LOGIN, true)
    }
    /**
     * 校验文件名是否合法：不超过50个字符，只支持中文，英文，下划线，空格
     */
    private val MAX_FILE_NAME_LENGTH = 30
    private fun checkName(fileName: String): Boolean {
        if (TextUtils.isEmpty(fileName)) {
//            CustomToast.getInstance().showErr(getContext().getString(R.string.toast_name_no_empty));
            return false
        }
        if (fileName.length > MAX_FILE_NAME_LENGTH) {
//            CustomToast.getInstance().showErr(getContext().getString(R.string.toast_name_max_length));
            return false
        }
        return if (!matchFileName(fileName)) {
//            CustomToast.getInstance().showErr(getContext().getString(R.string.toast_name_rule));
            false
        } else matchFileName(fileName)
    }

    /**
     * 检测字符串中只能包含中文、字母、数字、下划线、空格
     *
     * @param fileName
     * @return
     */
    private fun matchFileName(fileName: String): Boolean {
        val format = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w _]"
        val pattern = Pattern.compile(format)
        val matcher = pattern.matcher(fileName)
        return !matcher.find()
    }

    companion object {
        var instance: SettingUtils? = null
            get() {
                if (field == null) field = SettingUtils()
                return field
            }
            private set

        fun getVersionName(context: Context?): String? {
            if (context == null) {
                return null
            }
            try {
                // 获取packagemanager的实例
                val packageManager = context.packageManager
                // getPackageName()是你当前类的包名，0代表是获取版本信息
                val packInfo = packageManager.getPackageInfo(
                    context.packageName, 0
                )
                return packInfo.versionName
            } catch (e: Exception) {
            }
            return null
        }

    }
}