package com.igrs.tpsdk.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.igrs.sml.event.SystemEvent;
import com.igrs.sml.tcp.TcpConst;
import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;
import com.igrs.sml.util.SurfaceControl;
import com.igrs.sml.util.TouchUtil;
import com.igrs.tpsdk.ProjectionSDK;
import com.igrs.tpsdk.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;


public class ScreenRecordService_scrcpy extends Service {

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View displayView;
    private DisplayManager displayManager = null;
    private VirtualDisplay virtualDisplay;
    private IBinder display;

    private int width;
    private int height;

    private int mScreenWidth = 1920;
    private int mScreenHeight = 1080;

    private float width_scale = 1;
    private float height_scale = 1;

    View imageView = null;

    private boolean isRecord = false;
    private boolean isExit = false;


    @Override
    public void onCreate() {
        super.onCreate();
        L.e("ScreenRecordService_sys->onCreate this:" + this.hashCode());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        if (width < height) {
            int temp = width;
            width = height;
            height = temp;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showFloatingWindow();
        }
        TouchUtil.getInstance().init(this, width, height, new TouchUtil.EncoderSetCallBack() {
            @Override
            public void requestKeyCallback(String ip) {
                L.e("ScreenRecordService_sys->requestKeyCallback->ip:"+ip);
                requestKey();
            }

            @Override
            public void setFps(int fps) {

            }

            @Override
            public void setBit(int bit) {

            }
        });
        try {
            Surface surface = reset(mScreenWidth, mScreenHeight);
            virtualDisplay = displayManager.createVirtualDisplay("mediaProjection", mScreenWidth, mScreenHeight, 1, surface, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC | DisplayManager.VIRTUAL_DISPLAY_FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        isExit = false;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showFloatingWindow() {
        L.i("ScreenRecordService_sys->showFloatingWindow  width:" + width + " height:" + height + " width_scale:" + width_scale);
        if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this)) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            displayView = layoutInflater.inflate(R.layout.float_image, null);
            layoutParams.width = 1;
            layoutParams.height = 1;
            layoutParams.x = 0;
            layoutParams.y = 0;
            windowManager.addView(displayView, layoutParams);
            imageView = displayView.findViewById(R.id.img_dot);
            handler.sendEmptyMessage(0);
        }
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onSystemEventBus(final SystemEvent event) {
        if (event != null) {
            EventBus.getDefault().removeStickyEvent(event);
            switch (event.type) {
                case SystemEvent.TYPE_VIDEO_FPS: {
                    try {
                        seelp_fps = 1000 / event.taskModel.fps;
                        L.i("ScreenRecord->adjustFps->vbv: fps=" + event.taskModel.fps + "  seelp_fps:" + seelp_fps);
                    } catch (Exception e) {
                        L.e("ScreenRecord TYPE_VIDEO_FPS e->" + e.toString());
                    }
                }
                break;
                case SystemEvent.TYPE_VIDEO_REQ_KEY: {
                    requestKey();
                }
                break;
                case SystemEvent.TYPE_VIDEO_SIZE: {
                    mScreenWidth = event.taskModel.resolution_x;
                    mScreenHeight = event.taskModel.resolution_y;
                    try {
                        width_scale = 1.f * width / mScreenWidth;
                        height_scale = 1.f * height / mScreenHeight;
                        TouchUtil.getInstance().reSize(width_scale, height_scale);
                        L.i("ScreenRecordService_sys->startScy  width:" + width + " height:" + height + " width_scale:" + width_scale + " height_scale:" + height_scale);
                        if (virtualDisplay != null) {
                            virtualDisplay.resize(mScreenWidth, mScreenWidth, 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        L.e("ScreenRecordService_sys->startScy e->" + e.toString());
                    }
                }
                break;
            }
        }
    }


    private final Object onStart_lock = new Object();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.e("ScreenRecordService_sys->onStartCommand->isRecord:" + isRecord);
        synchronized (onStart_lock) {
            if (isRecord) {
                requestKey();
            } else {
                isRecord = true;
                encodeThread.start();
            }
        }
        return Service.START_NOT_STICKY;
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        int cnt = 0;
        boolean isShow = false;
        final int count = 0;

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
            handler.sendEmptyMessageDelayed(0, 200);
            return false;
        }
    });

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.e("ScreenRecordService_sys->onDestroy start");
        isRecord = false;
        stopForeground(true);
        L.i("ScreenRecord:: wait encodeThread [0]");

        if (display != null) {
            SurfaceControl.destroyDisplay(display);
            display = null;
        }

        if (encodeThread != null) {
            L.i("ScreenRecord:: encodeThread");
            try {
                encodeThread.interrupt();
                encodeThread.join(0);
            } catch (InterruptedException e) {
                L.e("ScreenRecord:: encodeThread e:" + e.toString());
                e.printStackTrace();
            }
            encodeThread = null;
        }

        if (mVideoEncoder != null) {
            L.i("ScreenRecord:: mVideoEncoder");
            try {
                mVideoEncoder.stop();
                mVideoEncoder.release();
                mVideoEncoder = null;
            } catch (Exception e) {
                L.e("ScreenRecord:: mVideoEncoder e:" + e.toString());
                e.printStackTrace();
            }
        }
        try {
            L.i("ScreenRecord:: virtualDisplay");
            if (virtualDisplay != null) {
                virtualDisplay.release();
                virtualDisplay = null;
            }
        } catch (Exception e) {
            L.e("ScreenRecord:: virtualDisplay e:" + e.toString());
            e.printStackTrace();
        }
        L.i("ScreenRecord:: windowManager_displayView");
        if (windowManager != null && displayView != null) {
            windowManager.removeView(displayView);
        }
        L.e("ScreenRecordService_sys->onDestroy end");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private final int maxFps = 25;               // 30fps
    private static final int IFRAME_INTERVAL = 10;           // 1 seconds between I-frames
    private static final int REPEAT_FRAME_DELAY_US = 100_000; // repeat after 100ms
    private static final String KEY_MAX_FPS_TO_ENCODER = "max-fps-to-encoder";
    private MediaCodec mVideoEncoder;
    private MediaFormat format;
    /////////////////////////////////
    private int seelp_fps = 1000 / maxFps;


    private final MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    Thread encodeThread = new Thread() {
        private final int TIMEOUT_USEC = 50;
        private int count = 0;
        private long temp = 0;
        private int errCount = 0;

        private byte[] information;

        private int index;

        byte[] bytes_time;

        @Override
        public void run() {
            super.run();
            L.i("VideoEncoder->encodeThread  run  isRecord:" + isRecord);
            while (isRecord) {
                try {
                    index = mVideoEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                    if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        try {
                            MediaFormat newFormat = mVideoEncoder.getOutputFormat();
                            L.e("ScreenRecord KEY_PROFILE:" + newFormat.getInteger(MediaFormat.KEY_PROFILE) + " KEY_LEVEL:" + newFormat.getInteger(MediaFormat.KEY_LEVEL));
                            L.i("ScreenRecord output format changed.\n new format: " + newFormat.toString());
                            byte[] sps = newFormat.getByteBuffer("csd-0").array();
                            StringBuffer s1 = new StringBuffer();
                            for (int i = 0; i < sps.length; i++) {
                                s1.append(sps[i] + " ");
                            }
                            L.e("sps:\n" + s1.toString());
                            // FileUtils.writeFileToSDCard(sps,"h264","sps.h264",true,false);
                            byte[]pps = newFormat.getByteBuffer("csd-1").array();

                            StringBuffer s2 = new StringBuffer();
                            for (int i = 0; i < pps.length; i++) {
                                s2.append(pps[i] + " ");
                            }
                            L.e("pps:\n" + s2.toString());
                            //FileUtils.writeFileToSDCard(sps,"h264","pps.h264",true,false);
                            L.i("ScreenRecord started media muxer, index=" + index);
                        } catch (Exception e) {
                        }
                    } else if (index >= 0) {
                        //L.i("index: " +index);
                        count++;
                        if (System.currentTimeMillis() - temp >= 3000) {
                            temp = System.currentTimeMillis();
                            //RuntimeInfo.info_fps = count / 3;
                            L.i("fps", "ScreenRecord-->onScreenInfo----------->count:" + (count / 3));
                            count = 0;
                        }
                        ByteBuffer encodeData = mVideoEncoder.getOutputBuffer(index);
                        if (encodeData != null) {
                            encodeData.position(mBufferInfo.offset);
                            encodeData.limit(mBufferInfo.offset + mBufferInfo.size);
                            byte[] bytes;
                            if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                                //sps pps信息
                                bytes = new byte[mBufferInfo.size];
                                encodeData.get(bytes);
                                information = bytes;

                                StringBuffer s2 = new StringBuffer();
                                for (int i = 0; i < information.length; i++) {
                                    s2.append(information[i] + " ");
                                }
                                L.e("ScreenRecord information->length:"+information.length+"\n" + s2.toString());
                            }else if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                                if (TcpConst.h264_has_time) {
                                    bytes = new byte[8 + mBufferInfo.size + information.length];
                                    bytes_time = Common.LongToBytes(mBufferInfo.presentationTimeUs*1000);
                                    System.arraycopy(bytes_time, 0, bytes, 0, bytes_time.length);
                                    System.arraycopy(information, 0, bytes, bytes_time.length, information.length);
                                    encodeData.get(bytes, bytes_time.length+information.length, mBufferInfo.size);
                                } else {
                                    bytes = new byte[mBufferInfo.size + information.length];
                                    System.arraycopy(information, 0, bytes, 0, information.length);
                                    encodeData.get(bytes, information.length, mBufferInfo.size);
                                }
                            } else {
                                if (TcpConst.h264_has_time) {
                                    bytes = new byte[8 + mBufferInfo.size];
                                    bytes_time = Common.LongToBytes(mBufferInfo.presentationTimeUs*1000);
                                    System.arraycopy(bytes_time, 0, bytes, 0, bytes_time.length);
                                    encodeData.get(bytes, bytes_time.length, mBufferInfo.size);
                                } else {
                                    bytes = new byte[mBufferInfo.size];
                                    encodeData.get(bytes, 0, mBufferInfo.size);
                                }
                            }
                            try {
                                ProjectionSDK.getInstance().screenH264Queue.put(bytes);
                            } catch (Exception e) {
                            }
                        }
                        mVideoEncoder.releaseOutputBuffer(index, false);
                    } else {
                        if (index != -1) L.e("ScreenRecord encodeThread  index: " + index);
                    }
                } catch (Exception e) {
                }
            }
            L.e("VideoEncoder->encodeThread  out-------------");
        }
        // }
    };

    public Surface reset(int width, int height) throws Exception {
        L.i("ScreenRecord reset-----------isCodecRun:" + isRecord + " width:" + width + " height:" + height);
        if (mVideoEncoder != null) {
            try {
                mVideoEncoder.stop();
                mVideoEncoder.reset();
            } catch (Exception e) {
                e.printStackTrace();
                L.e("ScreenRecord reset-----------e:" + e.toString());
            }
        } else {
            mVideoEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        }

        format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, maxFps);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 12000_000);
        format.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, REPEAT_FRAME_DELAY_US);
        format.setFloat(KEY_MAX_FPS_TO_ENCODER, maxFps);
//        L.i("reset->format: " + format);

        format.setFloat(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 1000.0f / maxFps);
        format.setInteger(MediaFormat.KEY_PRIORITY, 0);//时实编码 0 ；1

        mVideoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        Surface mInputSurface = mVideoEncoder.createInputSurface();
        mVideoEncoder.start();
        return mInputSurface;
    }

    public synchronized void requestKey() {
        try {
            if (isRecord) {
                L.i("ScreenRecord  requestKey--isRecord:" + isRecord + " mVideoEncoder:" + mVideoEncoder);
            } else {
                L.e("ScreenRecord  requestKey--isRecord:" + isRecord + " mScreenRecord:" + mVideoEncoder + "   error---------------");
                return;
            }
            if (Build.VERSION.SDK_INT >= 23 && mVideoEncoder != null) {
                Bundle params = new Bundle();
                params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                mVideoEncoder.setParameters(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.e("ScreenRecord  requestKey--e:" + e.toString());
        }
    }

}
