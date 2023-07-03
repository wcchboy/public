package com.wcch.android.soft.util;

import android.graphics.Bitmap;

import java.lang.reflect.Method;


public class ScreenShot {

    public static Bitmap screenShot(int x, int y) {
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

//    public static void screenShot(DragWindow window, DoodleView doodleView, int x, int y) {
//        Bitmap bitmap = ScreenCaptureV2Activity.screenShot(ResolutionConfig.ScreenConfig.SCREEN_WIDTH, ResolutionConfig.ScreenConfig.SCREEN_HEIGHT);
//
//        FrameLayout scaleWindow = window.getScaleWindow();
//        int[] location = new int[2];
//        scaleWindow.getLocationOnScreen(location);
//
//        int left = location[0];
//        int top = location[1];
//        if (left < 0) left = 0;
//        if (top < 0) top = 0;
//
//        bitmap = Bitmap.createBitmap(bitmap, left, top, scaleWindow.getWidth(), scaleWindow.getHeight());
//        bitmap = getTransparentBitmap(bitmap,100);
//        insertImage(doodleView, bitmap, scaleWindow.getWidth() + left + 60 + x, top + y);
//    }

    /**
     * 修改bitmap透明度
     * @param sourceImg
     * @param number
     * @return
     */
//    private static Bitmap getTransparentBitmap(Bitmap sourceImg, int number){
//        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
//
//        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg
//
//                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
//
//        number = number * 255 / 100;
//
//        for (int i = 0; i < argb.length; i++) {
//
//            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
//
//        }
//
//        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg
//
//                .getHeight(), Bitmap.Config.ARGB_8888);
//
//        return sourceImg;
//    }
//
//    private static int mBitmapPositionOffset = 0;
//    private static float mHisScreenTransX = 0f;
//    private static float mHisScreenTransY = 0f;
//
//    private static void insertImage(DoodleView mDoodleView, Bitmap bitmap, int x1, int y1) {
////        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
////        bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
//        int bitmapMaxSize = Math.max(bitmap.getWidth(), bitmap.getHeight());
//        if (bitmapMaxSize > ResolutionConfig.BitmapConfig.MAX_SIZE) {
//            float scale = ResolutionConfig.BitmapConfig.MAX_SIZE / bitmapMaxSize;
//            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), true);
//        }
//        //写死屏幕尺寸
//        int x = ResolutionConfig.ScreenConfig.SCREEN_WIDTH;
//        int y = ResolutionConfig.ScreenConfig.SCREEN_HEIGHT;
//
//        if (isContinuousAddBitmap(mDoodleView, x, y))
//            mBitmapPositionOffset += ResolutionConfig.BitmapConfig.POSITION_OFFSET;
//        else
//            mBitmapPositionOffset = 0;
//
//        mHisScreenTransX = mDoodleView.getAllTranX();
//        mHisScreenTransY = mDoodleView.getAllTranY();
//
//        IDoodleSelectableItem item = new DoodleBitmap(mDoodleView, bitmap, 1, mDoodleView.toX(x1), mDoodleView.toY(y1));
//        boolean isAdded = mDoodleView.addItem(item);
//        if (isAdded) {
//            DoodleOptionPath opPath = new DoodleOptionPath(mDoodleView);
//            opPath.addSelectItem(item);
//            mDoodleView.addItem(opPath);
//            mDoodleView.setItemSelect(opPath);
//            mDoodleView.refresh();
//        }
//    }
//
//    public static boolean isContinuousAddBitmap(DoodleView mDoodleView, int x, int y) {
//
//        IDoodleItem item = mDoodleView.getLastItem();
//
//        if (item != null
//                && item.getPivotX() == (x / 2 + mBitmapPositionOffset - mHisScreenTransX)
//                && item.getPivotY() == (y / 2 + mBitmapPositionOffset - mHisScreenTransY)
//                && mDoodleView.getAllTranX() == mHisScreenTransX
//                && mDoodleView.getAllTranY() == mHisScreenTransY)
//            return true;
//
//        return false;
//    }
}
