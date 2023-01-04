package com.wcch.android.observer

/**
 *  normDemo
 *
 *  @author Created by RyanWang on 2022/11/10
 *  Copyright © 2022年 IGRS. All rights reserved.
 *  Describe:观察者接口
 */
open interface  MyObserver {
    fun update(obj: Object)
}