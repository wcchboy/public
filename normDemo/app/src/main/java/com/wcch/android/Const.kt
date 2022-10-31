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
    /*val permissions = arrayOf<String>(
        Manifest.permission.DELETE_CACHE_FILES,
        Manifest.permission.CLEAR_APP_CACHE,
        Manifest.permission.CLEAR_APP_USER_DATA
    )*/
    /*val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.DELETE_CACHE_FILES,
        Manifest.permission.CLEAR_APP_CACHE
    )
*/
    val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

}