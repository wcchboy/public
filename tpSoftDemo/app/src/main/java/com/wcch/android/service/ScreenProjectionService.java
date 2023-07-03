package com.wcch.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.igrs.betotablet.IScreenConnectService;
import com.wcch.android.soft.util.LogUtil;


/**
 * create by LZQ on 2021-1-24
 * 投屏服务：每次主动投屏时初始化SDK。
 * 和接收端投屏冲突了。所以需要另外起一个进程
 */
public class ScreenProjectionService extends Service {
    private String TAG = "ScreenProjectionService";

    private final Gson gson = new Gson();
    boolean running = false;
    private boolean isInitSoftProjection = false;

    IBinder binder = new IScreenConnectService.Stub() {
        @Override
        public void connectDeviceByIp(String ip) throws RemoteException {

        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "ScreenProjectionService onCreate");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    /**
     * 初始化软投屏
     */
    private final int PORT = 12345;
    private boolean isExit = false;

    public void initSoftProjection() {
    }


    /**
     * 当服务被销毁时，发送一条广播，重新拉起服务
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }








}