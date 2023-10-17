package com.igrs.cleardata

import android.Manifest

/**
 *  clearData
 *
 *  Created by RyanWang on 2022/10/28
 *  Copyright © 2022年 IGRS. All rights reserved.
 *
 *  Describe:
 */
object Const {
    const val REQ_PER_CODE = 1001
    //usb display 50需要申请的权限返回码
    const val REQ_PERMISSION_CODE = 3001
    const val REQUEST_DIALOG_PERMISSION=3002

    val REQ_PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.SYSTEM_ALERT_WINDOW,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.RECORD_AUDIO)

}