package com.wcch.android;

import android.app.Application;

import leakcanary.LeakCanary;

/**
 * Created by  Ryan on 2023/12/4.
 */
public class MyApplicaion extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //2.0以下
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);*/
    }

    private void test(String s){

    }
}