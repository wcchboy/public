package com.igrs.cleardata.utils;

import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * @Description:
 * @Authot:Charles
 * @CreateDate:2020/7/24
 **/
public class ClearCacheUtils {

    /**
     * 安卓8.0之后
     * 安卓8.0系统之后，如果调用上面第一种方法会提示，clearApplicationUserData方法找不到异常，因此需要做兼容
     * @param context
     * @param pkgName
     * @param observer
     */
    public static void clearCache(Context context, String pkgName, IPackageDataObserver.Stub observer) {
        try {
            if (observer != null) {
                PackageManager pm = context.getPackageManager();
                Method deleteApplicationCacheFiles = pm.getClass()
                        .getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                deleteApplicationCacheFiles.invoke(pm, pkgName, observer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安卓8.0之前
     * 1.首先需要自己创建几个AIDL文件，方法在前言连接里有，这里就不再介绍。
     * 2.需要用到PackageManager里的clearApplicationUserData方法，但是改方法已被隐藏不能直接调用，因此需要通过反射来做
     * @param packageName
     * @return
     */
    public static boolean deleteAppData(String packageName) {
        boolean isSuccess = false;
        Method clearMethod;
        Object am = null;
        IPackageDataObserver.Stub mStub = new IPackageDataObserver.Stub() {
            @Override
            public void onRemoveCompleted(String paramAnonymousString, boolean paramAnonymousBoolean) {
            }
        };
        try {
            Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            // android.app.IActivityManager
            am = activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);

            clearMethod = am.getClass().getMethod("clearApplicationUserData", String.class, boolean.class, IPackageDataObserver.class, int.class);
            if (clearMethod != null) {
                Log.e("ClearCacheUtils", "clearMethod 9.0 ");
                clearMethod.setAccessible(true);
                isSuccess = (boolean) clearMethod.invoke(am, packageName, true, mStub, 0);
            }

        } catch (Exception localException) {
            localException.printStackTrace();
            Log.e("ClearCacheUtils", "Exception:" + localException.getMessage());
            Log.e("ClearCacheUtils", "clearMethod <9.0 ");
            try {
                clearMethod = am.getClass().getMethod("clearApplicationUserData", String.class, IPackageDataObserver.class, int.class);
                if (clearMethod != null) {
                    clearMethod.setAccessible(true);
                    isSuccess = (boolean) clearMethod.invoke(am, packageName, mStub, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                shellRun("pm clear " + packageName);
            }
        }
        return isSuccess;
    }

    private static String shellRun(String command) {
        Process process = null;
        BufferedReader bufferedReader = null;
        String result = "";
        try {
            process = Runtime.getRuntime().exec(command);
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }


}
