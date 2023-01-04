package com.wcch.android.observer

/**
 *  normDemo
 *
 *  @author Created by RyanWang on 2022/11/10
 *  Copyright © 2022年 IGRS. All rights reserved.
 *  Describe:使用方式
 */
class SendTest {
    //发送消息
    fun sendMsg(){
        //发送消息
        ConcreteSubject.notifyObservers("text发送的消息" as Object)
    }
}