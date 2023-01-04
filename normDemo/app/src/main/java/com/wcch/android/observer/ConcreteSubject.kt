package com.wcch.android.observer

/**
 *  normDemo
 *
 *  @author Created by RyanWang on 2022/11/10
 *  Copyright © 2022年 IGRS. All rights reserved.
 *  Describe:具体观察者实现方法
 */
object ConcreteSubject:MySubject() {
    override fun register(observer: MyObserver) {
        subjectList.add(observer)
    }

    override fun unRegister(observer: MyObserver) {
        subjectList.remove(observer)
    }

    //调用集合所有的方法并调用
    override fun notifyObservers(obj: Object) {
        for (observer in subjectList) {
            observer.update(obj)
        }
    }
}