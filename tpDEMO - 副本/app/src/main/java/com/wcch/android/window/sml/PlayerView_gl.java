package com.wcch.android.window.sml;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

import com.igrs.betotablet.utils.LogUtil;
import com.igrs.sml.H264Decoder;
import com.igrs.sml.callback.DecoderCallback;
import com.igrs.tpsdk.opengl.filter.AFilter;
import com.igrs.tpsdk.opengl.filter.OesFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class PlayerView_gl extends GLSurfaceView implements GLSurfaceView.Renderer {
    private final Context context;

    private final Object LOCK_LOGIN = new Object();

    private int v_w;
    private int v_h;

    public String uuid = UUID.randomUUID().toString();


    private int[] texture = new int[4];

    private List<Integer> textureList = new ArrayList<>();

    //dev_id,model
    private HashMap<String, GlTaskModel> surfaceTextureMap = new HashMap<>();

    private boolean isExit;
    private String[] urls = new String[]{
            "Thinkvision_Lenovo.mp4",
            "birds.mp4",
            "m4.mp4",
            "test.mp4",
            "test2.mp4",
            "alien-covenant.mp4",
            "i_am_you.mp4",
            "video1.mp4"
    };

    public PlayerView_gl(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PlayerView_gl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int width = 0;
    private int height = 0;

    private void init() {
        LogUtil.i("PlayerView_gl-------------init-> uuid:" + uuid + "\n size:" + getWidth() + "x" + getHeight());//+ Log.getStackTraceString(new Throwable()));
        setEGLContextClientVersion(3);  // 设置opengl版本;
        setEGLConfigChooser(new EGLConfigChooser() {
            @Override
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                int attribs[] = {
                        EGL10.EGL_LEVEL, 0,
                        EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
                        EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                        EGL10.EGL_RED_SIZE, 8,
                        EGL10.EGL_GREEN_SIZE, 8,
                        EGL10.EGL_BLUE_SIZE, 8,
                        EGL10.EGL_DEPTH_SIZE, 16,
                        EGL10.EGL_SAMPLE_BUFFERS, 1,
                        EGL10.EGL_SAMPLES, 4,  // 在这里修改MSAA的倍数，4就是4xMSAA，再往上开程序可能会崩
                        EGL10.EGL_NONE
                };
                EGLConfig[] configs = new EGLConfig[1];
                int[] configCounts = new int[1];
                egl.eglChooseConfig(display, attribs, configs, 1, configCounts);

                if (configCounts[0] == 0) {
                    // Failed! Error handling.
                    return null;
                } else {
                    return configs[0];
                }
            }
        });
        this.setRenderer(this);
        this.setRenderMode(RENDERMODE_WHEN_DIRTY);
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (!isExit) {
                    try {
                        requestRender();
                        Thread.sleep(1000 / 30);
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    public void onDestroy() {

    }

    private AFilter mShowFilter;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtil.i("PlayerView_gl-------------onSurfaceCreated");
        Log.d("11111111111111", "onSurfaceCreated");
        GLES30.glGenTextures(texture.length, texture, 0);
        for (int i = 0; i < texture.length; i++) {
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[i]);
            GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
            GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
            GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
            textureList.add(texture[i]);
        }
        mShowFilter = new OesFilter();
        mShowFilter.create();
    }

    public boolean addNode(LinkedBlockingQueue<byte[]> mH264DataQueue, String dev_id) {
        LogUtil.e("addNode........dev_id:" + dev_id + " textureList.size:" + textureList.size());
        if (textureList.size() > 0) {
            try {
                LogUtil.e("addNode........dev_id:" + dev_id);
                int tid = textureList.remove(0);
                SurfaceTexture surfaceTexture = new SurfaceTexture(tid);
                surfaceTexture.setDefaultBufferSize(width, height);
                final Runnable updataRunable = new Runnable() {
                    @Override
                    public void run() {
                        surfaceTexture.updateTexImage();
                    }
                };
                GlTaskModel glTaskModel = new GlTaskModel();
                H264Decoder h264Decoder = new H264Decoder(mH264DataQueue, new Surface(surfaceTexture), dev_id, new DecoderCallback() {
                    @Override
                    public void decoderCallback(int result) {

                    }

                    @Override
                    public void decoderSizeChage(int width, int height, MediaFormat format) {
                        LogUtil.e("decoderSizeChage........width:" + width + " height:" + height);
                    }
                });
                h264Decoder.start();
                glTaskModel.h264Decoder = h264Decoder;

                glTaskModel.dev_id = dev_id;
                glTaskModel.surfaceTexture = surfaceTexture;
                glTaskModel.texture = tid;
                surfaceTextureMap.put(dev_id, glTaskModel);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(e.toString());
            }
        } else {
            LogUtil.e("max........");
        }
        return false;
    }

    public void delNode(String taskId) {
        try {
            GlTaskModel glTaskModel = surfaceTextureMap.remove(taskId);
            textureList.add(glTaskModel.texture);
            if (glTaskModel.mediaPlayer != null) {
                glTaskModel.mediaPlayer.stop();
            }
            if (glTaskModel.h264Decoder != null) {
                glTaskModel.h264Decoder.onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("delNode error->e:" + e.toString());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        v_w = width / 2;
        v_h = height / 2;
        if (mShowFilter != null) mShowFilter.setSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        GLES20.glViewport(0, 0, width, height);
        try {
            int i = 0;
            for (Map.Entry<String, GlTaskModel> entry : surfaceTextureMap.entrySet()) {
                int row = i / 2;
                int col = i % 2;
                try {
                    GLES20.glViewport(row * v_w, col * v_h, v_w, v_h);
                    String taskId = entry.getKey();
                    GlTaskModel glTaskModel = entry.getValue();
                    //L.i("taskId:" + taskId + " tid:" + glTaskModel.texture + " x:" + row * v_w + " y:" + col * v_h+" w:"+v_w+" h:"+v_h);

                    glTaskModel.surfaceTexture.updateTexImage();

                    mShowFilter.setTextureId(glTaskModel.texture);
                    mShowFilter.draw();
                } catch (Exception e) {
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class GlTaskModel {
        String dev_id;
        int texture;
        SurfaceTexture surfaceTexture;
        MediaPlayer mediaPlayer;
        H264Decoder h264Decoder;
    }
}
