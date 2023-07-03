package com.igrs.sml;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import com.igrs.sml.callback.DecoderCallback;
import com.igrs.sml.tcp.TcpConst;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;
import com.igrs.tpsdk.opengl.filter.AFilter;
import com.igrs.tpsdk.opengl.filter.OesFilter;
import com.igrs.tpsdk.opengl.util.GlesUtil;
import com.igrs.tpsdk.opengl.util.MatrixUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.UUID;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PlayerView_gl extends GLSurfaceView implements GLSurfaceView.Renderer {
    private final Context context;
    private float width_scale = 1;
    private float height_scale = 1;
    private int width = 1920;
    private int height = 1080;



    private final float r_width = 1920.0f;
    private final float r_height = 1080.0f;
    public String uuid = UUID.randomUUID().toString();

    private H264Decoder h264Decoder;
    private boolean sizeChanged = false;

    public PlayerView_gl(Context context) {
        super(context);
        this.context = context;
    }

    public PlayerView_gl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    private TaskModel taskModel;
    DecoderCallback decoderCallback;

    private Object LOCK_Surface = new Object();

    public void init(TaskModel taskModel, DecoderCallback decoderCallback) {
        this.taskModel = taskModel;
        this.decoderCallback = decoderCallback;
        if (taskModel == null) {
            return;
        }

        setEGLContextClientVersion(3);
        this.setRenderer(this);
        this.setRenderMode(RENDERMODE_CONTINUOUSLY);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.hht.action.FULLSCREEN_SOURCE");
        context.registerReceiver(broadcastReceiver, filter);
        L.i("PlayerView_gl::-------------init->dev_id:" + taskModel.dev_id + " uuid:" + uuid + "\n " + Build.PRODUCT);//+ Log.getStackTraceString(new Throwable()));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            L.i("action:" + action + " " + intent);
            if ("com.hht.action.FULLSCREEN_SOURCE".equals(action)) {
                String sourceKey = intent.getStringExtra("sourceKey");
                L.e("sourceKey:" + sourceKey);
                if ("OPS".equals(sourceKey)) {
                    if (h264Decoder != null) {
                        h264Decoder.reset();
                    }
                    setPause(false);
                } else if ("ANDROID".equals(sourceKey)) {

                }
            }
        }
    };


    private long downTime = 0;
    private ByteBuffer mBuffer;
    private PlayerImgCallback playerImgCallback;

    public void setPlayerImgCallback(PlayerImgCallback playerImgCallback) {
        mBuffer = ByteBuffer.allocate(1920 * 1080 * 4);
        this.playerImgCallback = playerImgCallback;
    }

    public interface PlayerImgCallback {
        public void getBitmap(Bitmap bitmap);
    }

    boolean needBitmap = false;


    public boolean onTouchEvent(MotionEvent event) {
        if (taskModel == null) {
            return super.onTouchEvent(event);
        }



        ByteBuffer buffer = ByteBuffer.allocate(1000);
        int action = event.getAction();
        int pointerCount = event.getPointerCount();
        if (action == MotionEvent.ACTION_DOWN) {
            downTime = event.getDownTime();
            buffer.putInt(0);
        } else {
            buffer.putInt((int) (event.getEventTime() - downTime));
        }
        buffer.putInt(action);
        buffer.putInt(pointerCount);
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

    public void getBitmap(){
        if (playerImgCallback != null) {
            needBitmap = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //L.i("onKeyDown-->Action:" + event.getAction()+" keyCode:"+keyCode);
        sendKeEvent(event.getAction(), keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        L.i("onKeyUp-->Action:" + event.getAction() + " keyCode:" + keyCode);
        sendKeEvent(event.getAction(), keyCode);
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
        L.i("PlayerView_gl-----------onSurfaceTextureDestroyed->dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId);
        setPause(true);
        if (h264Decoder != null) {
            h264Decoder.onDestroy();
            h264Decoder = null;
        }
    }

    private AFilter mShowFilter;

    private int surfaceTextureId;
    private SurfaceTexture surfaceTexture;
    private Surface surface;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        L.i("PlayerView_gl onSurfaceTextureAvailable dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId + " uuid:" + uuid + " width:" + width + " height:" + height);
        setPause(true);

        surfaceTextureId = GlesUtil.createCameraTexture();

        mShowFilter = new OesFilter();
        mShowFilter.create();

        surfaceTexture = new SurfaceTexture(surfaceTextureId);
        surfaceTexture.setDefaultBufferSize(3840, 2160);

        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            int count;
            long temp;
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                //queueEvent(runnable);
                count++;
                if (System.currentTimeMillis() - temp >= 3000) {
                    temp = System.currentTimeMillis();
                    L.i("fps", "PlayerView_gl-->onFrameAvailable-1---------->count:" + (count / 3));
                    count = 0;
                }
                //requestRender();
            }
        });
        surface = new Surface(surfaceTexture);

        L.i("PlayerView_gl onSurfaceTextureAvailable surface:" + surface);
        try {
            L.i("H264Decoder_ 2 onSurfaceTextureAvailable isValid  ->dev_id:" + taskModel.dev_id + " tid:" + taskModel.taskId);
            h264Decoder = new H264Decoder(VideoManage.getInstance().getH264Queue(taskModel.dev_id), surface, taskModel.dev_id, new DecoderCallback() {
                @Override
                public void decoderCallback(int result) {
                    setPause(false);
                    if (decoderCallback != null) {
                        decoderCallback.decoderCallback(0);
                    }
                }

                @Override
                public void decoderSizeChage(int width, int height, MediaFormat outFormat) {
                    L.e("PlayerView_gl_gl decoderSizeChage onSurfaceChanged:" + width + "x" + height  + " t:" + surfaceTexture.getTimestamp());
                    r_count = 0;
                    if (decoderCallback != null) {
                        decoderCallback.decoderSizeChage(width, height, outFormat);
                    }

//                    if (TcpConst.mime_type.equals(MediaFormat.MIMETYPE_VIDEO_HEVC)){
//
//                    }
                    int w = outFormat.getInteger(MediaFormat.KEY_WIDTH);
                    int h = outFormat.getInteger(MediaFormat.KEY_HEIGHT);

                    int d_w = width;
                    int d_h = height;

                    if (outFormat.containsKey("crop-right")) {
                        d_w = outFormat.getInteger("crop-right");
                    }
                    if (outFormat.containsKey("crop-bottom")) {
                        d_h = outFormat.getInteger("crop-bottom");
                    }
//                    fragmentBuffer.clear();
//                    fragmentBuffer.put(new float[]{
//                            0f, 0f,
//                            0f, 1.0f * d_h / h,
//                            1.0f * d_w / w, 0f,
//                            1.0f * d_w / w, 1.0f * d_h / h});
//                    fragmentBuffer.position(0);
//                    mShowFilter.setCoordMatrix(fragmentBuffer);

                }
            });
            h264Decoder.start();
        } catch (Exception e) {
            L.e("PlayerView_gl_gl H264Decoder_ decoder er:" + e.toString());
            e.printStackTrace();
            if (decoderCallback != null) {
                decoderCallback.decoderCallback(-1);
            }
        }

        synchronized (LOCK_Surface) {
            LOCK_Surface.notify();
        }
        fragmentBuffer = ByteBuffer.allocateDirect(8 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    FloatBuffer fragmentBuffer;

    int r_count = 0;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        L.i("PlayerView_gl-----------onSurfaceChanged->width:" + width + " height:" + height + " width_scale:" + width_scale + " height_scale:" + height_scale + " t:" + surfaceTexture.getTimestamp());
        sizeChanged = true;
        if (width != 0 && height != 0) {
            width_scale = r_width / width;
            height_scale = r_height / height;
        }
        PlayerView_gl.this.width = width;
        PlayerView_gl.this.height = height;
        mShowFilter.setSize(width, height);


    }


    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0.0f, 0.0f, 0f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


        try {
            surfaceTexture.updateTexImage();
        } catch (Exception e) {
        }

        try {
            mShowFilter.setTextureId(surfaceTextureId);
            mShowFilter.draw();

            if (needBitmap) {
                GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBuffer);
                ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
                buf.order(ByteOrder.LITTLE_ENDIAN);
                GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);
                buf.rewind();
                Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bm.copyPixelsFromBuffer(buf);
                playerImgCallback.getBitmap(MatrixUtils.horverImage(bm, false, true));
                try {
                    buf.clear();
                    buf = null;
                    bm.recycle();
                    bm = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                needBitmap = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
