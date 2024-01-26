//package com.igrs.tpsdk.service;
//
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.PixelFormat;
//import android.graphics.Rect;
//import android.hardware.input.InputManager;
//import android.os.Build;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.SystemClock;
//import android.provider.Settings;
//import android.view.Display;
//import android.view.Gravity;
//import android.view.InputDevice;
//import android.view.InputEvent;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.Surface;
//import android.view.View;
//import android.view.WindowManager;
//
//import androidx.annotation.RequiresApi;
//
//
//import com.igrs.sml.EncodeThread;
//import com.igrs.sml.event.SystemEvent;
//import com.igrs.sml.event.TouchEventCallback;
//import com.igrs.sml.util.L;
//import com.igrs.sml.util.SurfaceControl;
//import com.igrs.tpsdk.R;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.nio.ByteBuffer;
//
//
//public class ScrcpyService extends Service {
//
//    private WindowManager windowManager;
//    private WindowManager.LayoutParams layoutParams;
//    private View displayView;
//
//    private EncodeThread videoEncoder;
//
//
//    private int width;
//    private int height;
//
////    private int mScreenWidth = 1920;
////    private int mScreenHeight = 1080;
////    private final int mScreenWidth = 640;
////    private final int mScreenHeight = 360;
//
//    private  int mScreenWidth = 1280;
//    private  int mScreenHeight = 720;
//    private float width_scale = 1;
//    private float height_scale = 1;
//
//    View imageView = null;
//
//    private boolean isRecord = false;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        L.e("ScrcpyService->onCreate this:" + this.hashCode());
//        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        layoutParams = new WindowManager.LayoutParams();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        }
//        layoutParams.format = PixelFormat.RGBA_8888;
//        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//
//        Display display = windowManager.getDefaultDisplay();
//        width = display.getWidth();
//        height = display.getHeight();
//        if (width < height) {
//            int temp = width;
//            width = height;
//            height = temp;
//        }
//        try {
//            videoEncoder = new EncodeThread("test");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void showFloatingWindow() {
//        L.i("ScrcpyService->showFloatingWindow  width:" + width + " height:" + height + " width_scale:" + width_scale);
//        if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this)) {
//            LayoutInflater layoutInflater = LayoutInflater.from(this);
//            displayView = layoutInflater.inflate(R.layout.float_image, null);
//            layoutParams.width = 1;
//            layoutParams.height = 1;
//            layoutParams.x = 0;
//            layoutParams.y = 0;
//            windowManager.addView(displayView, layoutParams);
//            imageView = displayView.findViewById(R.id.img_dot);
//            handler.sendEmptyMessage(0);
//        }
//        EventBus.getDefault().register(this);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onSystemEventBus(final SystemEvent event) {
//        if (event != null) {
//            EventBus.getDefault().removeStickyEvent(event);
//            switch (event.type) {
//                case SystemEvent.TYPE_VIDEO_FPS: {
//                    try {
//                        //videoEncoder.adjustFps(event.taskModel.fps);
//                    } catch (Exception e) {
//                        L.e("TYPE_VIDEO_FPS e->" + e.toString());
//                    }
//                }
//                break;
//                case SystemEvent.TYPE_VIDEO_SIZE: {
//                    mScreenWidth = event.taskModel.resolution_x;
//                    mScreenHeight = event.taskModel.resolution_y;
//                    startScy();
//                }
//                break;
//            }
//        }
//    }
//
//    private void startScy() {
//        try {
//            width_scale = 1.f * width / mScreenWidth;
//            height_scale = 1.f * height / mScreenHeight;
//            L.i("ScrcpyService->showFloatingWindow  width:" + width + " height:" + height + " width_scale:" + width_scale + " height_scale:" + height_scale);
//            if (display != null) {
//                SurfaceControl.destroyDisplay(display);
//                display = null;
//            }
//            Surface surface = videoEncoder.reset(mScreenWidth, mScreenHeight);
//            try {
//                display = SurfaceControl.createDisplay("scrcpy", true);
//                if (display != null) {
//                    Rect layerStackRect = new Rect(0, 0, mScreenWidth, mScreenHeight);
//                    Rect displayRect = new Rect(0, 0, mScreenWidth, mScreenHeight);
//                    SurfaceControl.openTransaction();
//                    SurfaceControl.setDisplaySurface(display, surface);
//                    SurfaceControl.setDisplayProjection(display, 0, layerStackRect, displayRect);
//                    SurfaceControl.setDisplayLayerStack(display, 0);
//                }
//            } finally {
//                SurfaceControl.closeTransaction();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            L.e("TYPE_VIDEO_SIZE e->" + e.toString());
//        }
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        L.e("ScrcpyService->onStartCommand");
//        if (isRecord) {
//            return Service.START_NOT_STICKY;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            showFloatingWindow();
//        }
//        try {
//            startScy();
//            QCSDK.getInstance().setTouchEventCallback(new TouchEventCallback() {
//                @Override
//                public void onTouchEventCallback(String peerIp, byte[] event) {
//                    if (event.length > 3) {
//                        ByteBuffer buffer = ByteBuffer.allocate(event.length);
//                        buffer.put(event);
//                        buffer.flip();
//                        try {
//                            doMotionEvent(peerIp, buffer, width_scale, height_scale);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            L.e("SDKService onTuuchEventCallback->handleMessage=e:" + e + " event:" + event.length);
//                        }
//                        // handler.sendMessage(handler.obtainMessage(100, event));
//                    }
//                }
//            });
//            isRecord = true;
//            videoEncoder.setOnEnCodeAvailableCallBack(new EncoderH264CallBack() {
//                @Override
//                public void encoderSuccess(byte[] data, boolean isKey) {
//                    //FileUtils.writeFileToSDCard(data,"h264","test.h264",true,true);
//                    //QCSDK.getInstance().sendDeskFrame(data, true);
//                }
//            });
//            videoEncoder.startRecord();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return Service.START_NOT_STICKY;
//    }
//
//    static boolean hasDown = false;
//    public synchronized long doMotionEvent(String peerIp, ByteBuffer byteBuffer, float width_scale, float height_scale) {
//        byteBuffer.getInt();//time
//        int action = byteBuffer.getInt();
//        //L.i("SDKService doMotionEvent->------" + peerIp + "------action=" + action);
//        //-100 切换至后台 ；-101 切换至前台
//        if (action == -100) {
//            QCSDK.getInstance().setSendDeskSwitch(peerIp, false);
//            return 0;
//        } else if (action == -101) {
//            QCSDK.getInstance().setSendDeskSwitch(peerIp, true);
//            videoEncoder.requestKey();
//            return 0;
//        }
//        if (action == MotionEvent.ACTION_UP) {
//            hasDown = false;
//        }
//        byteBuffer.getInt();//pointerCount
//        byteBuffer.getInt();//pid
//
//        int x = byteBuffer.getInt();
//        int y = byteBuffer.getInt();
//
//
//        L.i("fps", peerIp + " AcsInput action:" + action + " width_scale:" + width_scale + " height_scale:" + height_scale + " ox:" + x + " oy:" + y);
//
//        if (x < 0 || x > mScreenWidth || y < 0 || y > mScreenHeight) {
//            L.e(peerIp + " AcsInput action:" + action + " width_scale:" + width_scale + " height_scale:" + height_scale + " ox:" + x + " oy:" + y + " ========================return \n\n\n\n error ");
//            return 0;
//        }
//        if (action == MotionEvent.ACTION_UP) {
//            hasDown = false;
//        } else if (action == KeyEvent.KEYCODE_CALL) {
//            return 0;
//        } else if (action == KeyEvent.KEYCODE_BACK) {
//            try {
//                // L.i("SDKService onTuuchEventCallback->keyevent:"+new String(event));
//                String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
//                Runtime.getRuntime().exec(keyCommand);
//            } catch (IOException e) {
//            }
//            return 0;
//        } else if (action == MotionEvent.ACTION_DOWN) {
//            hasDown = true;
//        } else if (action == MotionEvent.ACTION_MOVE) {
//            if (!hasDown) {
//                return 0;
//            }
//        } else {
//            L.e(peerIp + " AcsInput action:" + action + " width_scale:" + width_scale + " height_scale:" + height_scale + " ox:" + x + " oy:" + y + " =======unknow action=================return \n\n\n\n error ");
//            return 0;
//        }
//
//        MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[1];
//        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
//        pointerProperties[0] = new MotionEvent.PointerProperties();
//        pointerProperties[0].id = 0;
//        pointerProperties[0].toolType = 1;
//        pointerCoords[0] = new MotionEvent.PointerCoords();
//        pointerCoords[0].x = x * width_scale;
//        pointerCoords[0].y = y * height_scale;
//
//        //L.d("AcsInput  a:" + action + " ws:" + width_scale + " hs:" + height_scale + " ox:" + x + " oy:" + y + " x = " + pointerCoords[0].x + " y:" + pointerCoords[0].y);
//        long eventTime = SystemClock.uptimeMillis();
//        MotionEvent event = MotionEvent.obtain(eventTime, eventTime, action, 1, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
//        invokeInjectInputEvent(event);
//        return 0;
//    }
//
//    public void invokeInjectInputEvent(InputEvent event) {
//        InputManager im = (InputManager) getSystemService(Context.INPUT_SERVICE);
//
//        Class[] paramTypes = new Class[2];
//        paramTypes[0] = InputEvent.class;
//        paramTypes[1] = Integer.TYPE;
//
//        Object[] params = new Object[2];
//        params[0] = event;
//        params[1] = 0;
//
//        try {
//            Method hiddenMethod = im.getClass().getMethod("injectInputEvent", paramTypes);
//            hiddenMethod.invoke(im, params);
//        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//            L.e("----e:" + e.toString());
//            e.printStackTrace();
//        }
//    }
//
//    private Handler handler = new Handler(new Handler.Callback() {
//        int cnt = 0;
//        boolean isShow = false;
//        int count = 0;
//
//        @Override
//        public boolean handleMessage(Message msg) {
//            if (imageView == null)
//                return false;
//
//            if (cnt++ % 4 == 0) {
//                if (isShow) {
//                    imageView.setAlpha(0.8f);
//                } else {
//                    imageView.setAlpha(1.0f);
//                }
//                isShow = !isShow;
//            }
//            float alpha = imageView.getAlpha();
//            if (cnt % 2 == 0 && alpha < 1.0f) {
//                imageView.setAlpha(alpha + 0.01f);
//            } else {
//                imageView.setAlpha(alpha - 0.01f);
//            }
//            handler.sendEmptyMessageDelayed(0, 200);
//            return false;
//        }
//    });
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        L.e("ScrcpyService->onDestroy start");
//        isRecord = false;
//        stopForeground(true);
//        L.i("zxq_j:: wait encodeThread [0]");
//        try {
//            if (videoEncoder != null) {
//                videoEncoder.release();
//                videoEncoder = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            L.i("zxq_j:: wait encodeThread [2]");
//            if (display != null) {
//                SurfaceControl.destroyDisplay(display);
//                display = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        L.i("zxq_j:: wait encodeThread [3]");
//        if (windowManager != null && displayView != null) {
//            windowManager.removeView(displayView);
//        }
//        L.e("ScrcpyService->onDestroy end");
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}
