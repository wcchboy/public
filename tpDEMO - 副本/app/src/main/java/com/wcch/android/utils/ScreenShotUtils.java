package com.wcch.android.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.wcch.android.App;
import com.wcch.android.R;
import com.wcch.android.config.Config;
import com.wcch.android.soft.util.LogUtil;
import com.wcch.android.view.ToastUtils;
import com.wcch.android.window.LockScreenHelper;
import com.wcch.android.window.Window;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScreenShotUtils {

    static  int shotCount =0;

    static  boolean mIsDraged;

    static  boolean isShoted;

    private static final String whiteboardPackageName;
    static {
        whiteboardPackageName = "com.lenovo.whiteboard";
    }

    public static void setIsDraged(boolean isDraged){
        mIsDraged =isDraged;
    }

    public static void setIsShoted(boolean isShoted){
        isShoted =isShoted;
    }

    public static void screenShot(Context context, Window window, OnScreenShotFinishListener onScreenShotFinishListener) {

        new Thread(() -> {
            Bitmap bitmap;
            bitmap = screenShot(Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT);

            FrameLayout floatWindowView = window.getCardView();
            int[] location = new int[2];
            floatWindowView.getLocationInWindow(location);
            WindowManager.LayoutParams lp = (WindowManager.LayoutParams) window.getFloatWindowView().getLayoutParams();

            int left = location[0] + lp.x;
            int top = location[1] + lp.y;
            if (left < 0) left = 0;
            if (top < 0) top = 0;
            LogUtil.e("zjx","floatWindowView1 is getWidth "+floatWindowView.getWidth()+  "getHeight "+floatWindowView.getHeight());
            LogUtil.e("zjx","floatWindowView1 is left "+left+  "top "+top);
           bitmap = Bitmap.createBitmap(bitmap, left, top, floatWindowView.getWidth(), floatWindowView.getHeight());
          //  bitmap = Bitmap.createBitmap(bitmap, left, top, 50, 400);
            LogUtil.e("zjx","bitmap : left "+left+  "top: "+top+ "width:"+floatWindowView.getWidth()+" height is "+floatWindowView.getHeight());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (bitmap.getConfig().ordinal() == Bitmap.Config.HARDWARE.ordinal())
                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            }
            if (mIsDraged){
                LogUtil.e("zjx","当拖动时，重新计算偏移位置");
                shotCount=0;
            }
            shotCount++;
          //  int x = (left + floatWindowView.getWidth());
            // int y = top;
            int x = (left + floatWindowView.getWidth() + (shotCount*60));

            int y = top+shotCount*60-60;

            //insert to whiteBoard  todo 如果是在白板内 截图 ，保存到screenShot 文件夹后，插入到白板
            saveBitmapFile(context, bitmap, x, y,onScreenShotFinishListener);
            mIsDraged =false;
           //  saveBitmapFile1(context, bitmap, x, y,onScreenShotFinishListener);

        }).start();

    }

    private static void saveBitmapFile(Context context, Bitmap bitmap, int x, int y,OnScreenShotFinishListener onScreenShotFinishListener) {

      String path=   getDeviceRootPath();
      LogUtil.e("path:"+path);
   File screenShot = new File(path+ File.separator+"ScreenShot");
//     screenShot.mkdirs();
//      File screenShot = context.getExternalFilesDir("screenShot");
//        if (screenShot.exists()) {
//            for (File file : screenShot.listFiles()) {
//                file.delete();
//            }
//        }
     //   File screenShot;
        if (!screenShot.exists()){
            boolean isSuccess=    screenShot.mkdirs();
            LogUtil.e("isSuccess:"+isSuccess);
        }


        String filePath = screenShot.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".png";
        LogUtil.e("zjx","截图保存路径："+filePath);
        File file = new File(filePath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

          //  boolean b = AndroidUtil.checkAppInstalled(App.getInstance(), "com.lenovo.whiteboard");
            //是否在白板内
            boolean b = SystemHelper.isWhiteBoardForeground(App.getInstance().getApplicationContext());
            if (!b) {
                if (onScreenShotFinishListener != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onScreenShotFinishListener.onScreenFinish(1);
                            return;
                        }
                    });
                }
             //   ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS, App.getInstance().getString(R.string.screen_shot_path) + screenShot.getAbsolutePath());
                ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS, App.getInstance().getApplicationContext().getString(R.string.screen_shot_path) +"/ScreenShot");
                return;
            } else {
                Intent intent = new Intent();
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                String activityName = "com.lenovo.whiteboard.activity.WhiteboardMainActivity";
                intent.putExtra("FROM_PROJECTION", true);
                LogUtil.e("zjx","白板内的x:"+x+"  y:"+y);
                intent.putExtra("X", x);
                intent.putExtra("Y", y);
                LogUtil.e("zjx","白板内的x:"+x+"  y:"+y);
              //白板需要根据分辨率 判断截图图片插入的大小
                  intent.putExtra("resolution", 2);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(new ComponentName(whiteboardPackageName, activityName));
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if (onScreenShotFinishListener != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onScreenShotFinishListener.onScreenFinish(0);
                        return;
                    }
                });
            }
           return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //失败
        if (onScreenShotFinishListener != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onScreenShotFinishListener.onScreenFinish(2);
                }
            });
        }
    }


    //白板内截图
    private  static  void saveBitmapFileForInWhiteBoard(Context context, Bitmap bitmap, int x, int y,OnScreenShotFinishListener onScreenShotFinishListener){

        File screenShot = context.getExternalFilesDir("screenShot");
        if (screenShot.exists()) {
            for (File file : screenShot.listFiles()) {
                file.delete();
            }
        }
        screenShot.mkdirs();
        String filePath = screenShot.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".png";
        File file = new File(filePath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

                boolean b = AndroidUtil.INSTANCE.checkAppInstalled(App.getInstance().getApplicationContext(), whiteboardPackageName);
            if (!b) {
                if (onScreenShotFinishListener != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onScreenShotFinishListener.onScreenFinish(1);
                            return;
                        }
                    });
                }
                ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS, App.getInstance().getApplicationContext().getString(R.string.screen_shot_path) + screenShot.getAbsolutePath());
                return;
            } else {
                Intent intent = new Intent();
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                String activityName = "com.lenovo.whiteboard.activity.WhiteboardMainActivity";
                intent.putExtra("FROM_PROJECTION", true);
                // TODO: 2022/9/5  修改插入对位置
                intent.putExtra("X", x);
                intent.putExtra("Y", y);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(new ComponentName(whiteboardPackageName, activityName));
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("zjx", "跳转白板异常");
                }
            }


            if (onScreenShotFinishListener != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onScreenShotFinishListener.onScreenFinish(0);
                        return;
                    }
                });
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //失败
        if (onScreenShotFinishListener != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onScreenShotFinishListener.onScreenFinish(2);
                }
            });
        }

    }

    //白板外截图
    private  static  void saveBitmapFileForOutWhiteBoard(Context context, Bitmap bitmap, OnScreenShotFinishListener onScreenShotFinishListener){

        File screenShot = context.getExternalFilesDir("screenShot");
        if (screenShot.exists()) {
            for (File file : screenShot.listFiles()) {
                file.delete();
            }
        }
        screenShot.mkdirs();
        String filePath = screenShot.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".png";
        File file = new File(filePath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            if (onScreenShotFinishListener != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onScreenShotFinishListener.onScreenFinish(1);
                        return;
                    }
                });
            }
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS, App.getInstance().getApplicationContext().getString(R.string.screen_shot_path) + screenShot.getAbsolutePath());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //失败
        if (onScreenShotFinishListener != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onScreenShotFinishListener.onScreenFinish(2);
                }
            });
        }

    }

    //白板内和白板外截图的综合方法
    private  static  void saveBitmapFile1(Context context, Bitmap bitmap, int x, int y,OnScreenShotFinishListener onScreenShotFinishListener){

        boolean b = AndroidUtil.INSTANCE.checkAppInstalled(App.getInstance().getApplicationContext(), whiteboardPackageName);
        if (!b){
            saveBitmapFileForOutWhiteBoard(context,bitmap,onScreenShotFinishListener);
        }else {
            boolean isWhiteBoardForeground = !SystemHelper.isWhiteBoardForeground(App.getInstance().getApplicationContext());
            if (!isWhiteBoardForeground){
                saveBitmapFileForOutWhiteBoard(context,bitmap,onScreenShotFinishListener);
            }else {
                saveBitmapFileForInWhiteBoard(context,bitmap,x,y,onScreenShotFinishListener);
            }
        }



        File screenShot = context.getExternalFilesDir("screenShot");
        if (screenShot.exists()) {
            for (File file : screenShot.listFiles()) {
                file.delete();
            }
        }
        screenShot.mkdirs();
        String filePath = screenShot.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".png";
        File file = new File(filePath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();



            if (!b) {
                if (onScreenShotFinishListener != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onScreenShotFinishListener.onScreenFinish(1);
                            return;
                        }
                    });
                }
                ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS, App.getInstance().getApplicationContext().getString(R.string.screen_shot_path) + screenShot.getAbsolutePath());
                return;
            } else {
                boolean isWhiteBoardForeground = !SystemHelper.isWhiteBoardForeground(App.getInstance().getApplicationContext());
                if (isWhiteBoardForeground){
                    Intent intent = new Intent();
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    String activityName = "com.lenovo.whiteboard.activity.WhiteboardMainActivity";
                    intent.putExtra("FROM_PROJECTION", true);
                    // TODO: 2022/9/5  修改插入对位置

                    intent.putExtra("X", x);
                    intent.putExtra("Y", y);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(new ComponentName(whiteboardPackageName, activityName));
                    context.startActivity(intent);

                    if (onScreenShotFinishListener != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                onScreenShotFinishListener.onScreenFinish(0);
                                return;
                            }
                        });
                    }
                    return;
                }else {                         //白板内安装，但不在前台 ， 截图保存在文件管理器的screenShot内
                    if (onScreenShotFinishListener != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                onScreenShotFinishListener.onScreenFinish(1);
                                return;
                            }
                        });
                    }
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS, App.getInstance().getApplicationContext().getString(R.string.screen_shot_path) + screenShot.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //失败
        if (onScreenShotFinishListener != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onScreenShotFinishListener.onScreenFinish(2);
                }
            });
        }
    }

    private  static  void saveBitmapFile2(Context context, Bitmap bitmap, int x, int y,OnScreenShotFinishListener onScreenShotFinishListener){

        File screenShot = context.getExternalFilesDir("screenShot");
        if (screenShot.exists()) {
            for (File file : screenShot.listFiles()) {
                file.delete();
            }
        }
        screenShot.mkdirs();
        String filePath = screenShot.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".png";
        File file = new File(filePath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

            boolean b = AndroidUtil.INSTANCE.checkAppInstalled(App.getInstance().getApplicationContext(), whiteboardPackageName);

            if (!b) {
                if (onScreenShotFinishListener != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onScreenShotFinishListener.onScreenFinish(1);
                            return;
                        }
                    });
                }
                ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS, App.getInstance().getApplicationContext().getString(R.string.screen_shot_path) + screenShot.getAbsolutePath());
                return;
            } else {
                boolean isWhiteBoardForeground = !SystemHelper.isWhiteBoardForeground(App.getInstance().getApplicationContext());
                if (isWhiteBoardForeground){
                    Intent intent = new Intent();
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    String activityName = "com.lenovo.whiteboard.activity.WhiteboardMainActivity";
                    intent.putExtra("FROM_PROJECTION", true);
                    // TODO: 2022/9/5  修改插入对位置
                    intent.putExtra("X", x);
                    intent.putExtra("Y", y);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(new ComponentName(whiteboardPackageName, activityName));
                    context.startActivity(intent);

                    if (onScreenShotFinishListener != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                onScreenShotFinishListener.onScreenFinish(0);
                                return;
                            }
                        });
                    }
                    return;
                }else {                         //白板内安装，但不在前台 ， 截图保存在文件管理器的screenShot内
                    if (onScreenShotFinishListener != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                onScreenShotFinishListener.onScreenFinish(1);
                                return;
                            }
                        });
                    }
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.SUCCESS, App.getInstance().getApplicationContext().getString(R.string.screen_shot_path) + screenShot.getAbsolutePath());
                }

            }



        } catch (IOException e) {
            e.printStackTrace();
        }
        //失败
        if (onScreenShotFinishListener != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onScreenShotFinishListener.onScreenFinish(2);
                }
            });
        }

    }



    /**
     * 修改bitmap透明度
     *
     * @param sourceImg
     * @param number
     * @return
     */
    private static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {

            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

        }

        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

                .getHeight(), Bitmap.Config.ARGB_8888);

        return sourceImg;
    }

    private static Bitmap screenShot(int x, int y) {
        Class<?> cls = null;
        Bitmap bitmap = null;
        try {
            cls = Class.forName("android.view.SurfaceControl");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Method method = cls.getMethod("screenshot", int.class, int.class);
            Object obj = method.invoke(null, x, y);
            bitmap = (Bitmap) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static Bitmap screenShotForS2(int x, int y) {

            Class<?> cls = null;
            Bitmap bitmap = null;
            try {
                cls = Class.forName("android.view.SurfaceControl");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (Build.VERSION.SDK_INT <= 28) {
                    Method method = cls.getMethod("screenshot", int.class, int.class);
                    Object obj = method.invoke(null, x, y);
                    bitmap = (Bitmap) obj;
                } else if (Build.VERSION.SDK_INT <= 30) {
                    Method method = cls.getMethod("screenshot", Rect.class, int.class, int.class, int.class);
                    Object obj = method.invoke(null, new Rect(0, 0, x, y), x, y, 0);
                    bitmap = (Bitmap) obj;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;

    }

    public interface OnScreenShotFinishListener {
        //result：0：成功  1：成功无白板  2：失败
        void onScreenFinish(int result);
    }


    public static String  getDeviceRootPath() {
        StorageManager storageManager = (StorageManager) App.getInstance().getApplicationContext().getApplicationContext().
                getSystemService(Context.STORAGE_SERVICE);

        Class<?> storageClass = null;
        try {
            storageClass = Class.forName("android.os.storage.StorageManager");
            Method method = storageClass.getDeclaredMethod("getVolumeList");
            method.setAccessible(true);
                StorageVolume[] volumes = (StorageVolume[]) method.invoke(storageManager);

            Class<?> volumeClass = Class.forName("android.os.storage.StorageVolume");

            if (volumes.length<0 ) return "";
            StorageVolume   volume = volumes[0];
            @SuppressLint("SoonBlockedPrivateApi") Method getpath = volumeClass.getDeclaredMethod("getPath");
            String path = (String) getpath.invoke(volume);
             return  path;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
        e.printStackTrace();
    } catch (InvocationTargetException e) {
        e.printStackTrace();

    }
         return null;
    }


    /**
     * 锁屏
     */
    static LockScreenHelper lockScreenHelper = new LockScreenHelper();

    /*public static void lockScreen() {
        if (!lockScreenHelper.isShow()) {
            lockScreenHelper.show(App.getInstance().getApplicationContext());
        }
    }*/

}

