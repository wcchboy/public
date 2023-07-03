package com.wcch.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.igrs.betotablet.soft.entity.SwitchApp;
import com.igrs.sml.RuntimeInfo;
import com.igrs.sml.TaskModel;
import com.igrs.sml.event.TcpEvent;
import com.igrs.sml.util.ANRException;
import com.igrs.sml.util.BaseUtil;
import com.igrs.sml.util.CrashHandler;
import com.igrs.sml.util.L;
import com.tencent.bugly.crashreport.CrashReport;
import com.wcch.android.soft.entity.Device;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * tpDEMO
 *
 * @author Created by RyanWang on 2023/3/11
 * Copyright © 2023年 IGRS. All rights reserved.
 * Describe:
 */
public class App extends Application {

    private static App instance;

    public Device current_device;
    public TaskModel lastTaskModel;

    private final Stack<WeakReference<Activity>> mActivityStack = new Stack<>();
    private int mFinalCount;
    public AtomicBoolean isScreenRecord = new AtomicBoolean(false);
    // 单例模式中获取唯一的MyApplication实例
    public static App getInstance() {
        if (null == instance) {
            instance = new App();
        }
        return instance;
    }
    public static class ANRWatchDog extends Thread {
        public static final int MESSAGE_WATCHDOG_TIME_TICK = 0;
        /**
         * 判定Activity发生了ANR的时间，必须要小于5秒，否则等弹出ANR，可能就被用户立即杀死了。
         */
        public static final int ACTIVITY_ANR_TIMEOUT = 4000;


        private  int lastTimeTick = -1;
        private  int timeTick = 0;

        private final Handler watchDogHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                timeTick++;
                timeTick = timeTick % Integer.MAX_VALUE;
            }
        };
        @Override
        public void run() {
            while (true) {
                watchDogHandler.sendEmptyMessage(MESSAGE_WATCHDOG_TIME_TICK);
                try {
                    Thread.sleep(ACTIVITY_ANR_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //如果相等，说明过了ACTIVITY_ANR_TIMEOUT的时间后watchDogHandler仍没有处理消息，已经ANR了
                if (timeTick == lastTimeTick) {
                    throw new ANRException();
                } else {
                    lastTimeTick = timeTick;
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        //new ANRWatchDog().start();
        super.onCreate();
        instance = this;
        L.i("MyApplication----------onCreate  MANUFACTURER:" + android.os.Build.MANUFACTURER+" SDK_INT:"+android.os.Build.VERSION.SDK_INT);
        L.i("MyApplication----------onCreate  BRAND:" + android.os.Build.BRAND);
        L.i("MyApplication----------onCreate  DEVICE:" + android.os.Build.DEVICE);
        L.i("MyApplication----------onCreate  BOARD:" + android.os.Build.BOARD);
        L.i("MyApplication----------onCreate  PRODUCT:" + android.os.Build.PRODUCT);
        L.i("MyApplication----------onCreate  MODEL:" + android.os.Build.MODEL);
        L.i("MyApplication----------onCreate  USER:" + android.os.Build.USER);
        L.i("MyApplication----------onCreate  VERSION:" + android.os.Build.VERSION.RELEASE);
        L.i("MyApplication----------onCreate  DEVICE_NAME:" + Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME));

        L.i("MyApplication----------onCreate  bluetooth_name:" + Settings.Secure.getString(getContentResolver(), "bluetooth_name"));
        L.i("MyApplication----------onCreate  isPad:" + BaseUtil.isPad(this) );


        String deviceName = "";
        try{
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Object object = (Object) cls.newInstance();
            Method getName = cls.getDeclaredMethod("get", String.class);
            deviceName = (String) getName.invoke(object, "persist.sys.device_name");
            L.i("MyApplication----------onCreate  deviceName:" + deviceName);
        } catch (Exception e){
            e.printStackTrace();
        }

        L.setIsDebug(BuildConfig.DEBUG);

        CrashHandler.getInstance().init(this);
        CrashReport.initCrashReport(getApplicationContext(), "f89ba24581", true);

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point outSize = new Point();
        display.getRealSize(outSize);

        int width = outSize.x;
        int height = outSize.y;

        if (width > height) {
            int temp = width;
            width = height;
            height = temp;
        }
        if(height< RuntimeInfo.screenRecordSize){
            RuntimeInfo.screenRecordSize = height;
        }
        RuntimeInfo.mScreenWidth = (int) (RuntimeInfo.screenRecordSize*width/height);
        RuntimeInfo.mScreenHeight = (int) RuntimeInfo.screenRecordSize;
        L.i("MyApplication----------onCreate  dev:" + width+"x"+height+" scr:"+RuntimeInfo.mScreenWidth+"x"+RuntimeInfo.mScreenHeight);

        LitePal.initialize(this);
        EventBus.getDefault().register(this);
        registerActivityLifecycle();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public synchronized void onTcpEventBus(TcpEvent event) {
        L.i("MyApplication->onTcpEventBus->" + event.msg);
        EventBus.getDefault().removeStickyEvent(event);
        if (event.code == TcpEvent.TYPE_TCP_CONNECT) {
        }else  if (event.code == TcpEvent.TYPE_TCP_RETRY) {
            try{

            }catch (Exception e){
            }
        } else if (event.code == TcpEvent.TYPE_TCP_DISCONNECT) {
            current_device = null;
            return;
        }

    }

    public boolean isAppForeground(){
        return mFinalCount == 1;
    }

    public Activity currentActivity() {
        if (!mActivityStack.isEmpty()) {
            return mActivityStack.lastElement().get();
        }
        return null;
    }

    public void finishActivity(String className) {
        for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
            WeakReference<Activity> activityReference = it.next();
            Activity temp = activityReference.get();
            // 清理掉已经释放的activity
            if (temp == null) {
                it.remove();
                continue;
            }
            if (temp.getClass().getSimpleName().equals(className)) {
                it.remove();
                temp.finish();
            }
        }
    }

    private void registerActivityLifecycle() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                mActivityStack.add(new WeakReference<>(activity));
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                mFinalCount++;
                if (mFinalCount == 1) {
                    //说明是从后台到前台
                    EventBus.getDefault().post(new SwitchApp(true));
                }
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                mFinalCount--;
                if (mFinalCount == 0) {
                    //说明是从前台到后台
                    EventBus.getDefault().post(new SwitchApp(false));
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                    WeakReference<Activity> activityReference = it.next();
                    Activity temp = activityReference.get();
                    // 清理掉已经释放的activity
                    if (temp == null) {
                        it.remove();
                        continue;
                    }
                    if (temp == activity) {
                        it.remove();
                    }
                }
            }
        });
    }
}
