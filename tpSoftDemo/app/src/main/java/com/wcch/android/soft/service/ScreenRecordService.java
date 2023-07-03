package com.wcch.android.soft.service;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Config;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.igrs.audio.OpusRecorder;
import com.igrs.audio.OpusUtils;
import com.igrs.sml.EncodeThread;
import com.igrs.sml.RuntimeInfo;
import com.igrs.sml.event.SystemEvent;
import com.igrs.sml.event.TcpEvent;
import com.igrs.sml.tcp.TcpConst;
import com.igrs.sml.util.BaseUtil;
import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;
import com.igrs.sml.util.TouchUtil;
import com.igrs.tpsdk.ProjectionSDK;
import com.wcch.android.App;
import com.wcch.android.R;
import com.wcch.android.soft.util.BluetoothUtil;
import com.wcch.android.soft.util.ConfigUtil;
import com.wcch.android.soft.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;


public class ScreenRecordService extends Service {

    private String packageName = ".ScreenCaptureActivity";

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View displayView;


    private EncodeThread encodeThread;


    private int width;
    private int height;
    private ImageView imageView = null;

    private VirtualDisplay virtualDisplay;
    private MediaProjection mediaProjection;
    private Surface surface;
    private int fps=22;
    private  String TAG="ScreenRecordService";
    private ProjectionSDK projectionSDK;
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG,"onCreate getPackageName:"+getPackageName());
        packageName = getPackageName() + ".soft.ScreenCaptureActivity";

        int angle = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        L.e("ScreenRecordService->onCreate :" + this.hashCode() + " angle:" + angle + " packageName:" + packageName + " " + this.hashCode());
        if (angle == 0) {
            ORIENTATION_PORTRAIT = true;
        }

        if (TcpConst.mime_type.equals(MediaFormat.MIMETYPE_VIDEO_AVC)) {
            fps = 22;
        } else {
            fps = 60;
        }


        TouchUtil.getInstance().init(this, width, height, new TouchUtil.EncoderSetCallBack() {
            @Override
            public void requestKeyCallback(String ip) {
                L.e("ScreenRecordService->requestKeyCallback->ip:" + ip);
                if(encodeThread!=null)encodeThread.requestKey();
            }

            @Override
            public void setFps(int fps) {
                L.e("ScreenRecordService->setFps->fps:" + fps);
                if(encodeThread!=null)encodeThread.setMaxFps(fps);
            }

            @Override
            public void setBit(int bit) {
                L.e("ScreenRecordService->setBit->bit:" + bit);
                if(encodeThread!=null)encodeThread.setBit(bit);
            }
        });

        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point outSize = new Point();
        display.getRealSize(outSize);

        width = outSize.x;
        height = outSize.y;

        if (width > height) {
            int temp = width;
            width = height;
            height = temp;
        }


        L.i("ScreenRecordService----------onCreate  dev:" + width + "x" + height + " scr:" + RuntimeInfo.mScreenWidth + "x" + RuntimeInfo.mScreenHeight);

        OrientationEventListener mOrientationListener = new OrientationEventListener(ScreenRecordService.this) {
            int oldangle;
            int angle;

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return; // 手机平放时，检测不到有效的角度
                }
                // 只检测是否有四个角度的改变
                if (orientation > 350 || orientation < 10) {
                    // 0度：手机默认竖屏状态（home键在正下方）
                    angle = 0;
                } else if (orientation > 80 && orientation < 100) {
                    // 90度：手机顺时针旋转90度横屏（home建在左侧）
                    angle = 3;
                } else if (orientation > 170 && orientation < 190) {
                    // 180度：手机顺时针旋转180度竖屏（home键在上方）
                    angle = 2;
                } else if (orientation > 260 && orientation < 280) {
                    // 270度：手机顺时针旋转270度横屏，（home键在右侧）
                    angle = 1;
                }
                if (oldangle != angle) {
                    oldangle = angle;
                    if (oldangle != 0) {
                        synchronized (Lock_orientationChanged) {
                            if (!ORIENTATION_PORTRAIT && angle != 2) {
                                L.i("ScreenRecordService>----------angle:" + oldangle + " canDetectOrientation:" + canDetectOrientation());
                                projectionSDK.sendCMDMessage(ConfigUtil.getInstance().getIdentification(), ("{\"cmd\":15,\"ori\":" + angle + "}").getBytes());
                            }
                        }
                    }
                }
            }
        };
        if (mOrientationListener.canDetectOrientation()) {
            L.i("ScreenRecordService_gl>----------mOrientationListener 1");
            mOrientationListener.enable();
        } else {
            L.i("ScreenRecordService_gl>----------mOrientationListener 0");
            mOrientationListener.disable();
        }

        registerNetworkConnectChangeReceiver();
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true,
                mBrightnessObserver);

        showFloatingWindow(true);

        EventBus.getDefault().register(this);
        if(Config.DEBUG){
            startService(new Intent(this, RuntimeInfoService.class));
        }
    }

    protected Handler exitHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            L.i("ScreenRecordService exitHandler---->" + hashCode());
            exitHandler.removeMessages(0);
            try {
                Intent nfIntent = new Intent(ScreenRecordService.this, Class.forName(packageName)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //PendingIntent pendingIntent = PendingIntent.getActivity(ScreenRecordService.this, 0, nfIntent, 0);
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getActivity(ScreenRecordService.this, 0, nfIntent, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getActivity(ScreenRecordService.this, 0, nfIntent, PendingIntent.FLAG_ONE_SHOT);
                }

                pendingIntent.send();
//                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                activityManager.moveTaskToFront(MyApplication.getInstance().currentActivity().getTaskId(), 0);
            } catch (Exception e) {
                e.printStackTrace();
                L.e("bak app e->e:" + e.toString());
            }
            if (msg.what == 1) {
                stopSelf();
                EventBus.getDefault().postSticky(new SystemEvent(SystemEvent.TYPE_REC_EXIT));
            }
            return false;
        }
    });

    private void registerNetworkConnectChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenBroadcastReceiver, filter);
    }


    private ContentObserver mBrightnessObserver = new ContentObserver(new Handler()) {
        private int old_brightness=0;
        @Override
        public void onChange(boolean selfChange) {
            try {
                int screen_brightness = (int)(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)/ 2.55f);
                if(old_brightness!=screen_brightness){
                    old_brightness = screen_brightness;
                    projectionSDK.sendCMDMessage(ConfigUtil.getInstance().getIdentification(), ("{\"cmd\":14,\"brightness\":" + screen_brightness + "}").getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private BroadcastReceiver screenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    L.i("BaseService->broadcastReceiver->wifi断开");
                    exitHandler.sendEmptyMessage(0);
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
//                    if (imageView != null) {
//                        imageView.setImageResource(R.drawable.ic_desk);
//                    }
                    L.e("BaseService->broadcastReceiver->wifi连接 " + info);
                }
            }
            ////////////////////////以下是处理亮屏、熄屏、解锁功能////////////////////////
            // 1、app在前台， 2、app在后台， 3、屏幕关屏， 4、屏幕点亮
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                projectionSDK.sendCMDMessage(ConfigUtil.getInstance().getIdentification(), ("{\"cmd\":13,\"type\":" + 3 + "}").getBytes());
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                projectionSDK.sendCMDMessage(ConfigUtil.getInstance().getIdentification(), ("{\"cmd\":13,\"type\":" + 4 + "}").getBytes());
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                projectionSDK.sendCMDMessage(ConfigUtil.getInstance().getIdentification(), ("{\"cmd\":13,\"type\":" + 5 + "}").getBytes());
            }
        }
    };


    private void showFloatingWindow(boolean isShowTag) {
        L.i("ScreenRecordService->showFloatingWindow  isShowTag:" + isShowTag);
        if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this)) {
            if(imageView==null){
                Display display = windowManager.getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight();
                L.i("ScreenRecordService->showFloatingWindow init width:" + width + " height:" + height);
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                displayView = layoutInflater.inflate(R.layout.float_image, null);
                displayView.setOnTouchListener(new FloatingOnTouchListener());
                imageView = (ImageView) displayView;
                layoutParams.width = Common.diptopx(this, 40);
                layoutParams.height = layoutParams.width;
                layoutParams.x = width - layoutParams.width - Common.diptopx(this, 16);
                layoutParams.y = height - layoutParams.width - Common.diptopx(this, 140);
                windowManager.addView(displayView, layoutParams);
                handler.sendEmptyMessage(0);
            }
            if(isShowTag){
                displayView.setVisibility(View.VISIBLE);
            }else{
                displayView.setVisibility(View.GONE);
            }
        }
    }
    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        private long clickTime = 0;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    clickTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    if (System.currentTimeMillis() - clickTime < 200) {
                        //Intent nfIntent = getPackageManager().getLaunchIntentForPackage(packageName).setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        L.i("ScreenRecordService exitHandler---->ACTION_UP " + hashCode());
                        exitHandler.sendEmptyMessage(0);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public synchronized void onTcpEventBus(TcpEvent event) {
        L.i("ScreenRecordService->onTcpEventBus->" + event.msg);
        EventBus.getDefault().removeStickyEvent(event);
        if (event.code == TcpEvent.TYPE_TCP_CONNECT) {
            L.e("ScreenRecordService TYPE_TCP_CONNECT");
            exitHandler.removeMessages(0);
            if (encodeThread != null) {
                encodeThread.setPause(false);
            }
            return;
        } else if (event.code == TcpEvent.TYPE_TCP_DISCONNECT) {
            L.e("ScreenRecordService TYPE_TCP_DISCONNECT");
            exitHandler.sendEmptyMessage(0);
            if (encodeThread != null) {
                encodeThread.setPause(true);
            }
            return;
        }

        if (event != null && !TextUtils.isEmpty(event.msg)) {
            try {
                JSONObject json = new JSONObject(event.msg.toLowerCase());
                int cmd = json.getInt("cmd");
                if (cmd == 10) {
                    L.i("ScreenRecordService exitHandler---->cmd =10 " + hashCode());
                    exitHandler.sendEmptyMessage(0);
                    return;
                }
            } catch (Exception e) {
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public synchronized void onSystemEventBus(SystemEvent event) {

        EventBus.getDefault().removeStickyEvent(event);
        switch (event.type) {
            case SystemEvent.TYPE_SIZE_CHANGE:
                try {
                    int weight=720;
                    int wwidth = event.taskModel.resolution_x;
                    int wheight = event.taskModel.resolution_y;


//                    int wwidth = 2048;
//                    int wheight = 1152;


                    int fps = event.taskModel.fps;


                    if (wheight == 0 && wheight == 0) {
                        L.i("ScreenRecordService->size change->need pause");
                        if (encodeThread != null) {
                            encodeThread.onStop();
                        }
                        if (virtualDisplay != null) {
                            virtualDisplay.release();
                            virtualDisplay = null;
                        }
                        RuntimeInfo.mScreenWidth = 0;
                        RuntimeInfo.mScreenHeight = 0;
                        RuntimeInfo.info_fps = 0;
                        RuntimeInfo.info_bit = 0;
                        return;
                    }

                    if (wwidth > wheight) {
                        weight = wwidth;
                    } else {//竖屏
                        weight = wheight;
                    }

                    //不能超过手机屏幕尺寸 和4k
                    if (weight > 3840) {
                        if (height > 3840) {
                            weight = 3840;
                        } else {
                            weight = height;
                        }
                    }


//                    if (weight > height) {
//                        weight = height;
//                    }

                    //2048*1536
                    RuntimeInfo.screenRecordSize = weight;


                    L.i("ScreenRecordService->size change->" + wwidth + "x" + wheight + " weight:" + weight + " old:" + RuntimeInfo.mScreenWidth + "x" + RuntimeInfo.mScreenHeight + " dev:" + width + "x" + height);
                    if (wheight != RuntimeInfo.mScreenHeight || ScreenRecordService.this.fps != fps) {

                        if(fps>0){
                            ScreenRecordService.this.fps = fps;
                        }


                        if (event.taskModel.fps != null && event.taskModel.fps > 0 && encodeThread != null) {
                            encodeThread.setMaxFps(event.taskModel.fps);
                        }

                        int temp_w =  (int) (RuntimeInfo.screenRecordSize * width / height);
                        int temp_h =  (int) RuntimeInfo.screenRecordSize;

                        L.i("ScreenRecordService->size change->" + wwidth + "x" + wheight + " weight:" + weight + " temp_w:" + temp_w + "x" + temp_h + " dev:" + width + "x" + height);

                        if (wwidth > wheight) {
                            if(temp_w>wheight){
                                temp_w =  wheight;
                                temp_h =  (int) (wheight * height/width);

                            }
                        } else {
                            if(temp_w>wwidth){
                                temp_w =  wwidth;
                                temp_h =  (int) (wwidth * height/width);
                            }
                        }

                        RuntimeInfo.mScreenWidth = temp_w;
                        RuntimeInfo.mScreenHeight = temp_h;

                        if (((int) RuntimeInfo.mScreenWidth & 1) == 1) {
                            RuntimeInfo.mScreenWidth--;
                        }
                        if (((int)RuntimeInfo.mScreenHeight & 1) == 1) {
                            RuntimeInfo.mScreenHeight--;
                        }

                        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
                        int ori = mConfiguration.orientation; //获取屏幕方向
                        L.e("ScreenRecordService->size change->ori:" + ori + " " + RuntimeInfo.mScreenWidth + "x" + RuntimeInfo.mScreenHeight);
                        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
                            //横屏
                            startRec(RuntimeInfo.mScreenHeight, RuntimeInfo.mScreenWidth);
                        } else {
                            //竖屏
                            if (RuntimeInfo.mScreenWidth == 886 && "Lenovo L79031".equals(Build.MODEL)) {
                                //拯救者在886宽度竖屏无法录制
                                RuntimeInfo.mScreenWidth = 888;
                            }
                            startRec(RuntimeInfo.mScreenWidth, RuntimeInfo.mScreenHeight);
                            //startRec(1280, 2176);
                        }

                        //startRec(RuntimeInfo.mScreenWidth, RuntimeInfo.mScreenWidth);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e("ScreenRecordService->size change->e:" + e.toString());
                }

                break;
            case SystemEvent.TYPE_REC_STOP:
                //needEnCoder = false;
                //TODO 蓝牙sockect 连接断开 停止采集
                stopSelf();
                break;
        }
    }

    private void startRec(float width, float height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        L.i("ScreenRecordService->startRec->width:" + width + " height:" + height + " mediaProjection is null=" + (mediaProjection == null));
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        if (mediaProjection != null) {
            try {
                surface = encodeThread.prepareEncoder((int) width, (int) height,fps);
                mediaProjection.registerCallback(callback, null);
                virtualDisplay = mediaProjection.createVirtualDisplay("mediaProjection", (int) width, (int) height, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC|DisplayManager.MATCH_CONTENT_FRAMERATE_SEAMLESSS_ONLY, surface, null, null);
                int angle = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
                projectionSDK.sendCMDMessage(ConfigUtil.getInstance().getIdentification(), ("{\"cmd\":15,\"ori\":" + angle + "}").getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                L.e("ScreenRecordService->startRec->e:" + e.toString());
                EventBus.getDefault().postSticky(new SystemEvent(SystemEvent.TYPE_REC_FAILED));
                exitHandler.sendEmptyMessageDelayed(1, 200);
            }
        } else {
            L.e("ScreenRecordService->startRec->e: mediaProjection is null break");
        }

    }

    private void stopRec() {

    }

    private AudioRecord audioRecord;

    @SuppressLint("MissingPermission")
    @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
        /*if (projectionSDK==null){
            projectionSDK = new ProjectionSDK();
        }*/
        if (!App.getInstance().isScreenRecord.get()) {
            L.e("ScreenRecordService->onStartCommand->isScreenRecord:" + false + " stopSelf");
            stopSelf();
            return Service.START_NOT_STICKY;
        }
        if (intent.hasExtra("showTag")) {
            L.i("ScreenRecordService->onStartCommand->showTag");
            if (intent.getBooleanExtra("showTag", true)) {
                showFloatingWindow(true);
            } else {
                showFloatingWindow(false);
            }
            return Service.START_NOT_STICKY;
        } else if (intent.hasExtra("resultData")) {
            Intent resultData = intent.getParcelableExtra("resultData");
            L.i("ScreenRecordService->onStartCommand->resultData:" + resultData);
            try {
                BaseUtil.createNotificationChannel(this, getString(R.string.screen_capture_ing));
                try {
                    MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                    mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, resultData);//必须在通知显示之后调用
                    LogUtil.d(TAG, "ScreenRecordService->onStartCommand->mediaProjection:"+mediaProjection);
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e("ScreenRecordService->onStartCommand->e:" + e.toString());
                    EventBus.getDefault().postSticky(new SystemEvent(SystemEvent.TYPE_REC_FAILED));
                    exitHandler.sendEmptyMessageDelayed(1, 200);
                    return Service.START_NOT_STICKY;
                }


                L.i("ScreenRecordService->onStartCommand->1");
                encodeThread = new EncodeThread(ConfigUtil.getInstance().getIdentification());
                L.i("ScreenRecordService->onStartCommand->2");
                encodeThread.setMaxFps(fps);
                encodeThread.start();
                L.i("ScreenRecordService->onStartCommand->3");

                Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
                int ori = mConfiguration.orientation; //获取屏幕方向
                if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
                    //横屏
                    startRec(RuntimeInfo.mScreenHeight, RuntimeInfo.mScreenWidth);
                } else {
                    //竖屏
                    startRec(RuntimeInfo.mScreenWidth, RuntimeInfo.mScreenHeight);
                }
                L.i("ScreenRecordService->onStartCommand->4");

                try{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && audioRecord == null) {
                        AudioPlaybackCaptureConfiguration.Builder builder = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection);
                        //builder.addMatchingUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION);//多媒体
                        builder.addMatchingUsage(AudioAttributes.USAGE_MEDIA);//多媒体
                        builder.addMatchingUsage(AudioAttributes.USAGE_ALARM);//闹铃
                        builder.addMatchingUsage(AudioAttributes.USAGE_GAME);//游戏
                        AudioPlaybackCaptureConfiguration config = builder.build();

                        int channel = OpusUtils.DEFAULT_OPUS_CHANNEL == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
                        int bufferSize = AudioRecord.getMinBufferSize(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, channel, AudioFormat.ENCODING_PCM_16BIT);

                        AudioFormat format = new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE)
                                .setChannelMask(channel)
                                .build();

                        audioRecord = new AudioRecord.Builder()
                                .setAudioFormat(format)
                                .setBufferSizeInBytes(bufferSize)
                                .setAudioPlaybackCaptureConfig(config)
                                .build();
                        OpusRecorder.getInstance().start(audioRecord);
                    }else{
//                    int channel = OpusUtils.DEFAULT_OPUS_CHANNEL == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
//                    int bufferSize = AudioRecord.getMinBufferSize(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, channel, AudioFormat.ENCODING_PCM_16BIT);
//                    AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, channel, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
//                    OpusRecorder.getInstance().start(audioRecord);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                Intent home_intent = new Intent(Intent.ACTION_MAIN);
                home_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                home_intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(home_intent);
            }
        }
        return Service.START_NOT_STICKY;
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        int cnt = 0;
        boolean isShow = false;

        @Override
        public boolean handleMessage(Message msg) {
            if (imageView == null)
                return false;
            if (cnt++ % 4 == 0) {
                if (isShow) {
                    imageView.setAlpha(0.8f);
                } else {
                    imageView.setAlpha(1.0f);
                }
                isShow = !isShow;
            }
            float alpha = imageView.getAlpha();
            if (cnt % 2 == 0 && alpha < 1.0f) {
                imageView.setAlpha(alpha + 0.01f);
            } else {
                imageView.setAlpha(alpha - 0.01f);
            }
            if (App.getInstance().isScreenRecord.get()) {
                handler.sendEmptyMessageDelayed(0, 300);
            }
            return false;
        }
    });


    private Object Lock_orientationChanged = new Object();
    private boolean ORIENTATION_PORTRAIT = false;

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        L.e("ScreenRecordService->onConfigurationChanged-->" + newConfig.orientation);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            L.e("ORIENTATION_LANDSCAPE");
            synchronized (Lock_orientationChanged) {
                ORIENTATION_PORTRAIT = false;
            }
            if (mediaProjection != null) {
                startRec(RuntimeInfo.mScreenHeight, RuntimeInfo.mScreenWidth);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            L.e("ORIENTATION_PORTRAIT");
            synchronized (Lock_orientationChanged) {
                ORIENTATION_PORTRAIT = true;
            }
            if (mediaProjection != null) {
                startRec(RuntimeInfo.mScreenWidth, RuntimeInfo.mScreenHeight);
            }
        }

    }

    /**
     * cmd:0
     * action:
     * -100 切换至后台
     * -101 切换至前台
     * <p>
     * cmd:1
     * action:
     * 0 方向（0）home键在正下方
     * 1 方向（270）home建在右侧
     * 2 方向（180）home建在上方 //android 没有此方向
     * 3 方向（90）home建在左侧
     * <p>
     * cmd:2
     * action:
     * 0 锁屏
     * 1 开屏
     * 2 解锁
     * <p>
     * cmd:3
     * action:0~100 亮度
     */

    MediaProjection.Callback callback = new MediaProjection.Callback() {
        @Override
        public void onStop() {
            super.onStop();
            L.e("ScreenRecordService->Callback->onStop->");
            exitHandler.sendEmptyMessageDelayed(1, 1200);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        L.e("ScreenRecordService->onDestroy start " + this.hashCode());
        App.getInstance().isScreenRecord.set(false);
        exitHandler.removeMessages(0);
        try {
            if (windowManager != null && displayView != null) {
                L.i("ScreenRecordService->onDestroy  windowManager_displayView");
                windowManager.removeView(displayView);
                windowManager = null;
                displayView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            try{
                BluetoothDevice mBluetoothDevice = BluetoothUtil.getInstance().getConnectDevice();
                if (mBluetoothDevice != null && mBluetoothDevice.getAddress().equals( App.getInstance().current_device.device_mac)) {
                    BluetoothUtil.getInstance().disconnect(ScreenRecordService.this, mBluetoothDevice);
                }
            }catch (Exception e){
            }
        }else{
            try{
                OpusRecorder.getInstance().stop();
            }catch (Exception e){
            }
        }
        stopForeground(true);
        //EventBus.getDefault().post(new SystemEvent(SystemEvent.TYPE_REC_EXIT));
        RuntimeInfo.ser_mac = null;
        EventBus.getDefault().unregister(this);
        RuntimeInfo.info_fps = 0;
        if (encodeThread != null) {
            encodeThread.onDestroy();
            encodeThread = null;
        }
        try {
            if (surface != null) {
                surface.release();
                surface = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mediaProjection != null) {
                mediaProjection.unregisterCallback(callback);
                mediaProjection.stop();
                mediaProjection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (virtualDisplay != null) {
                virtualDisplay.release();
                virtualDisplay = null;
            }
        } catch (Exception e) {
            L.e("ScreenRecordService->onDestroy  virtualDisplay e:" + e.toString());
            e.printStackTrace();
        }

        try {
            if (screenBroadcastReceiver != null) {
                unregisterReceiver(screenBroadcastReceiver);
                screenBroadcastReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mBrightnessObserver != null) {
                getContentResolver().unregisterContentObserver(mBrightnessObserver);
                mBrightnessObserver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        L.e("ScreenRecordService->onDestroy end\n");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
