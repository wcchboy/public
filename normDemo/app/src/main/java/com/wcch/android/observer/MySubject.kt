package com.wcch.android.observer

/**
 *  normDemo
 *
 *  @author Created by RyanWang on 2022/11/10
 *  Copyright © 2022年 IGRS. All rights reserved.
 *  Describe:观察者基类
 */
open abstract class MySubject {
    //被观察者的集合
    protected val subjectList = arrayListOf<MyObserver>()

    //注册被观察者，添加到集合
    abstract fun register(observer:MyObserver)
    //删除集合
    abstract fun unRegister(observer: MyObserver)
    //发送消息
    abstract fun notifyObservers(obj: Object)
}