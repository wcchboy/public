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
import android.view.TextureView;

import androidx.annotation.RequiresApi;

import com.igrs.sml.callback.DecoderCallback;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;

import java.nio.ByteBuffer;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PlayerView_texture extends TextureView {
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

    public PlayerView_texture(Context context) {
        super(context);
        this.context = context;
    }

    public PlayerView_texture(Context context, AttributeSet attrs) {
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
       // setScaleX(1.00001f);
        setSurfaceTextureListener(new SurfaceTextureListener() {
            private Surface surface;
            final int count = 0;
            @Override
            public void onSurfaceTextureAvailable(final SurfaceTexture surfaceTexture, final int width, final int height) {
                L.i("PlayerView onSurfaceTextureAvailable dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId + " uuid:" + uuid + " width:" + width + " height:" + height);
                setPause(true);
                if (width != 0 && height != 0) {
                    PlayerView_texture.this.width = width;
                    PlayerView_texture.this.height = height;
                    width_scale = r_width / width;
                    height_scale = r_height / height;
                }

                if (surface != null) {
                    surface.release();
                    surface = null;
                }
                surface = new Surface(surfaceTexture);
                if (surface.isValid()) {
                    L.i("PlayerView onSurfaceTextureAvailable isValid  ->dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId);
                    try {
                        h264Decoder = new H264Decoder(VideoManage.getInstance().getH264Queue(taskModel.dev_id), surface, taskModel.dev_id, new DecoderCallback() {
                            @Override
                            public void decoderCallback(int result) {
                                if(result!=-1){
                                    setPause(false);
                                }
                                if (decoderCallback != null) {
                                    decoderCallback.decoderCallback(result);
                                }
                                if(result==-1){
                                    setPause(true);
                                }
                            }

                            @Override
                            public void decoderSizeChage(int width, int height, MediaFormat outFormat) {
                                if (decoderCallback != null) {
                                    decoderCallback.decoderSizeChage(width, height,outFormat);
                                }
                            }
                        });
                        h264Decoder.start();
                    } catch (Exception e) {
                        L.e("PlayerView decoder er:" + e.toString());
                        if (decoderCallback != null) {
                            decoderCallback.decoderCallback(-1);
                        }
                    }
                } else {
                    if (decoderCallback != null) {
                        decoderCallback.decoderCallback(-1);
                    }
                    L.e("PlayerView  onSurfaceTextureAvailable -----surface.isValid is false->dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                L.i("PlayerView-----------onSurfaceTextureSizeChanged->width:" + width + " height:" + height + " width_scale:" + width_scale + " height_scale:" + height_scale);
                if (width != 0 && height != 0) {
                    PlayerView_texture.this.width = width;
                    PlayerView_texture.this.height = height;
                    width_scale = r_width / width;
                    height_scale = r_height / height;
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                L.i("PlayerView-----------onSurfaceTextureDestroyed->dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId);
                if (surface != null) {
                    surface.release();
                    surface = null;
                }
                setPause(true);
                if (h264Decoder != null) {
                    h264Decoder.onDestroy();
                    h264Decoder = null;
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
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
                L.i("onLoginEventBus--h-a:" + action + "--ws:" + width_scale + " hs:" + height_scale + "----->w:" + width + " h:" + height + " x:" + x + " y:" + y + " uuid:" + uuid);
            }
        }


        for (int i = 0; i < pointerCount; i++) {
            buffer.putInt(i);//id
            int x = (int) (event.getX(i) * width_scale);//x
            int y = (int) (event.getY(i) * height_scale);//y
            buffer.putInt(x);//x
            buffer.putInt(y);//y
            L.i("onLoginEventBus---a:" + action + "--ws:" + width_scale + " hs:" + height_scale + "----->w:" + width + " h:" + height + " x:" + x + " y:" + y + " uuid:" + uuid);
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
