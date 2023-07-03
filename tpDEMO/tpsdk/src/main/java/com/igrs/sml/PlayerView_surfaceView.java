package com.igrs.sml;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.RequiresApi;

import com.igrs.sml.callback.DecoderCallback;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;

import java.nio.ByteBuffer;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PlayerView_surfaceView extends SurfaceView {
    private final Context context;
    private final Object LOCK_LOGIN = new Object();
    private float width_scale = 1;
    private float height_scale = 1;
    private int width = 1920;
    private int height = 1080;

    private final float r_width = 1920.0f;
    private final float r_height = 1080.0f;
    public String uuid = UUID.randomUUID().toString();

    private H264Decoder h264Decoder;

    public PlayerView_surfaceView(Context context) {
        super(context);
        this.context = context;
    }

    public PlayerView_surfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    private TaskModel taskModel;
    DecoderCallback decoderCallback;

    public void init(TaskModel taskModel, DecoderCallback decoderCallback) {
        this.taskModel = taskModel;
        this.decoderCallback = decoderCallback;
        if (taskModel == null) {
            return;
        }

        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        L.i("PlayerView_surfaceView::-------------init->dev_id:" + taskModel.dev_id + " uuid:" + uuid + "\n " + Build.PRODUCT);//+ Log.getStackTraceString(new Throwable()));
        getHolder().addCallback(new SurfaceHolder.Callback() {
                    private Surface surface;
                    final int count = 0;

                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        L.i("PlayerView_surfaceView onSurfaceTextureAvailable dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId + " uuid:" + uuid + " width:" + width + " height:" + height);
                        setPause(true);
                        if (width != 0 && height != 0) {
                            PlayerView_surfaceView.this.width = width;
                            PlayerView_surfaceView.this.height = height;
                            width_scale = r_width / width;
                            height_scale = r_height / height;
                        }

                        if (surface != null) {
                            surface.release();
                            surface = null;
                        }
                        surface = holder.getSurface();
                        if (surface.isValid()) {
                            L.i("PlayerView_surfaceView onSurfaceTextureAvailable isValid  ->dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId);
                            try {
                                h264Decoder = new H264Decoder(VideoManage.getInstance().getH264Queue(taskModel.dev_id), surface, taskModel.dev_id, new DecoderCallback() {
                                    @Override
                                    public void decoderCallback(int result) {
                                        setPause(false);
                                        if (decoderCallback != null) {
                                            decoderCallback.decoderCallback(0);
                                        }
                                    }

                                    @Override
                                    public void decoderSizeChage(int width, int height, MediaFormat mediaFormat) {
                                        if (decoderCallback != null) {
                                            decoderCallback.decoderSizeChage(width, height, mediaFormat);
                                        }
//                                        post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                L.i("PlayerView_surfaceView setFixedSize :" +width + "  " + height);
//                                                PlayerView_surfaceView.this.getHolder().setFixedSize(width,height);
//                                                //surfaceChanged(holder,1,width,height);
//                                            }
//                                        });
                                    }

                                    @Override
                                    public void setOnFrameAvailableListener(int index, Bitmap bitmap) {
                                        if (decoderCallback != null) {
                                            decoderCallback.setOnFrameAvailableListener(index, bitmap);
                                        }
                                    }
                                });
                                h264Decoder.start();
                            } catch (Exception e) {
                                L.e("PlayerView_surfaceView decoder er:" + e.toString());
                                if (decoderCallback != null) {
                                    decoderCallback.decoderCallback(-1);
                                }
                            }
                        } else {
                            if (decoderCallback != null) {
                                decoderCallback.decoderCallback(-1);
                            }
                            L.e("PlayerView_surfaceView  onSurfaceTextureAvailable -----surface.isValid is false->dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId);
                        }
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                        L.i("PlayerView_surfaceView-----------onSurfaceTextureSizeChanged->width:" + width + " height:" + height + " width_scale:" + width_scale + " height_scale:" + height_scale);
                        if (width != 0 && height != 0) {
                            PlayerView_surfaceView.this.width = width;
                            PlayerView_surfaceView.this.height = height;
                            width_scale = r_width / width;
                            height_scale = r_height / height;
                        }
                       // PlayerView_surfaceView.this.setMeasure(mVideoWidth,screenHeight);
                       // PlayerView_surfaceView.this.requestLayout();
                        //PlayerView_surfaceView.this.sets(mVideoWidth,screenHeight);
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        L.i("PlayerView_surfaceView-----------onSurfaceTextureDestroyed->dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId);
                        if (surface != null) {
                            surface.release();
                            surface = null;
                        }
                        setPause(true);
                        if (h264Decoder != null) {
                            h264Decoder.onDestroy();
                            h264Decoder = null;
                        }
                    }
                });
    }


    public Bitmap getBitmap() {
        return getBitmap();
    }

    private long downTime = 0;

    public boolean onTouchEvent(MotionEvent event) {
        if (taskModel == null) {
            return super.onTouchEvent(event);
        }
        ByteBuffer buffer = ByteBuffer.allocate(10000);
        int action = event.getAction();
        int pointerCount = event.getPointerCount();
        if (action == MotionEvent.ACTION_DOWN) {
            downTime = event.getDownTime();
            buffer.putInt(0);
        } else {
            buffer.putInt((int) (event.getEventTime() - downTime));
        }

        final int historySize = event.getHistorySize();

        buffer.putInt(action);
        buffer.putInt(pointerCount+historySize);
        L.i("historySize:"+historySize+" pointerCount:"+pointerCount);

        for (int h = 0; h < historySize; h++) {
            //L.i("getHistoricalEventTime:"+event.getHistoricalEventTime(h));
            for (int p = 0; p < pointerCount; p++) {
                buffer.putInt(event.getPointerId(p));//id
                int x = (int) (event.getHistoricalX(p, h) * width_scale);//x
                int y = (int) (event.getHistoricalY(p, h) * height_scale);//y
                buffer.putInt(x);//x
                buffer.putInt(y);//y
                //L.i("onLoginEventBus--h-a:" + action + "--ws:" + width_scale + " hs:" + height_scale + "----->w:" + width + " h:" + height + " x:" + x + " y:" + y + " uuid:" + uuid);
            }
        }

        for (int i = 0; i < pointerCount; i++) {
            buffer.putInt(event.getPointerId(i));//id
            int x = (int) (event.getX(i) * width_scale);//x
            int y = (int) (event.getY(i) * height_scale);//y
            buffer.putInt(x);//x
            buffer.putInt(y);//y
            //L.i("onLoginEventBus---a:" + action + "--ws:" + width_scale + " hs:" + height_scale + "----->w:" + width + " h:" + height + " x:" + x + " y:" + y + " uuid:" + uuid);
        }
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.get();
        }
        ProjectionSDK.getInstance().sendTouchMsg(taskModel.dev_id, bytes);
        buffer.clear();


        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //L.i("onKeyDown-->Action:" + event.getAction()+" keyCode:"+keyCode);
        sendKeEvent(event.getAction(), keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //L.i("onKeyUp-->Action:" + event.getAction()+" keyCode:"+keyCode);
        sendKeEvent(event.getAction(), keyCode);
        setPause(false);
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 后台和前台切换  压后台后服务器会停止发送数据;到前台后服务器恢复发送数据
     *
     * @param isPause true:切换至后台; false:切换至前台
     * @return true调用成功；false调用失败（可能是未初始化成功）
     */
    public boolean setPause(boolean isPause) {
        //-100 切换至后台 ；-101 切换至前台
        int pasu = isPause ? -100 : -101;
        sendMotionEvent(pasu, 0, 0);
        L.i("fps setPause-->isPause:" + isPause + " time:" + System.currentTimeMillis());
        return true;
    }


    private void sendKeEvent(int action, int key) {
        if (taskModel == null) {
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.putInt(action);
        buffer.putInt(key);
        L.i("sendKeEvent-----action:" + action + " key:" + key + " uuid:" + uuid);
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.get();
        }
        ProjectionSDK.getInstance().sendTouchMsg(taskModel.dev_id, bytes);
    }

    private void sendMotionEvent(int action, float x, float y) {
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.putInt(0);
        buffer.putInt(action);
        buffer.putInt(1);//point
        buffer.putInt(0);//id
        buffer.putInt((int) (x * width_scale));//x
        buffer.putInt((int) (y * height_scale));//y
        L.i("sendMotionEvent-----" + taskModel.dev_id + " action:" + action + " width_scale:" + width_scale + " height_scale:" + height_scale + "----->w:" + width + " h:" + height + " x:" + x + " y:" + y + " uuid:" + uuid);
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.get();
        }
        ProjectionSDK.getInstance().sendTouchMsg(taskModel.dev_id, bytes);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    public void onDestroy() {
        setVisibility(GONE);
        L.i("PlayerView-----------onDestroy->dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId);
        setPause(true);
        if (h264Decoder != null) {
            h264Decoder.onDestroy();
            h264Decoder = null;
        }
    }

//    @Override
//    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
//
//    }
//
//    @Override
//    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
//
//    }
//
//    @Override
//    public void onDrawFrame(GL10 gl10) {
//
//    }
}
