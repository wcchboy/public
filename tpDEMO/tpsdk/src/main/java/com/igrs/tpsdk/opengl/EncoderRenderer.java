/*
 * Copyright 2013 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.igrs.tpsdk.opengl;

import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import com.igrs.sml.util.L;
import com.igrs.tpsdk.opengl.filter.AFilter;
import com.igrs.tpsdk.opengl.filter.NoFilter;
import com.igrs.tpsdk.opengl.gles.EglCore;
import com.igrs.tpsdk.opengl.gles.WindowSurface;
import com.igrs.tpsdk.opengl.util.MatrixUtils;
import com.igrs.tpsdk.service.EncoderH264CallBack;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.microedition.khronos.opengles.GL10;

@RequiresApi(api = Build.VERSION_CODES.M)
public class EncoderRenderer implements Runnable {

    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_FRAME_AVAILABLE = 2;
    private static final int MSG_UPDATE_SIZE = 3;
    private static final int MSG_UPDATE_SHARED_CONTEXT = 4;
    private static final int MSG_QUIT = 5;

    private final EGLContext eglContext;
    private WindowSurface mInputEncoderSurface;

    private int mTextureId;
    private VideoEncoder mVideoEncoder;
    private volatile EncoderHandler mHandler;

    private final Object mReadyFence = new Object();      // guards ready/running
    private boolean mReady;
    private boolean mRunning;

    private int encoderW;
    private int encoderH;

    private EglCore eglCore;

    private boolean needEncode = false;
    protected Object LOCK_needEnCoder = new Object();

    public EncoderRenderer(int encoderW, int encoderH) {
        this.encoderW = encoderW;
        this.encoderH = encoderH;
        L.i("EncoderRenderer->构造:->encoderW:" + encoderW + " encoderH:" + encoderH);
        eglContext = EGL14.eglGetCurrentContext();
        eglCore = new EglCore(eglContext, EglCore.FLAG_RECORDABLE);
        mShowFilter.create();
        mShowFilter.setSize(encoderW, encoderH);
        MatrixUtils.flip(mShowFilter.getMatrix(), false, true);
    }

    public void startRecording() {
        L.i("Encoder: startRecording()");
        synchronized (mReadyFence) {
            if (mRunning) {
                L.w("Encoder thread already running  mReady:" + mReady);
                return;
            }
            mRunning = true;
            new Thread(this, "TextureMovieEncoder").start();
            while (!mReady) {
                try {
                    mReadyFence.wait();
                } catch (InterruptedException ie) {
                    // ignore
                }
            }
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_START_RECORDING));
    }

    public void setPause(boolean isPause) {
        synchronized (LOCK_needEnCoder) {
            needEncode = !isPause;
            LOCK_needEnCoder.notify();
            if(!isPause){
                requestKey();
            }
        }
    }

    public void stopRecording() {
        if (mHandler != null) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_RECORDING));
        }
    }

    public void destroy() {
        if (mHandler != null) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_QUIT));
        }

    }

    public boolean isRecording() {
        synchronized (mReadyFence) {
            return mRunning;
        }
    }

    public void frameAvailable(long timestamp) {
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE, (int) (timestamp >> 32), (int) timestamp));
    }

    public void setTextureId(int id) {
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }
        mTextureId = id;
        mShowFilter.setTextureId(mTextureId);
    }

    /**
     * Encoder thread entry point.  Establishes Looper/Handler and waits for messages.
     * <p>
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        Looper.prepare();
        synchronized (mReadyFence) {
            mHandler = new EncoderHandler(this);
            mReady = true;
            mReadyFence.notify();
        }
        Looper.loop();
        L.i("Encoder thread exiting");
        synchronized (mReadyFence) {
            mReady = mRunning = false;
            mHandler = null;
        }
    }


    private static class EncoderHandler extends Handler {
        private final WeakReference<EncoderRenderer> mWeakEncoder;

        public EncoderHandler(EncoderRenderer encoder) {
            mWeakEncoder = new WeakReference<EncoderRenderer>(encoder);
        }

        @Override  // runs on encoder thread
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;
            EncoderRenderer encoder = mWeakEncoder.get();
            //L.i("EncoderHandler->handleMessage->what:"+what+" encoder:"+encoder);
            if (encoder == null) {
                L.w("EncoderHandler.handleMessage: encoder is null");
                return;
            }
            switch (what) {
                case MSG_START_RECORDING:
                    encoder.handleStartRecording();
                    break;
                case MSG_STOP_RECORDING:
                    encoder.handleStopRecording();
                    break;
                case MSG_FRAME_AVAILABLE://2
                    long timestamp = (((long) inputMessage.arg1) << 32) | (((long) inputMessage.arg2) & 0xffffffffL);
                    encoder.handleFrameAvailable(timestamp);
                    break;
                case MSG_UPDATE_SIZE:
                    encoder.handleUpdateSize(inputMessage.arg1, inputMessage.arg2);
                    break;
                case MSG_UPDATE_SHARED_CONTEXT:
                    encoder.handleUpdateSharedContext((EGLContext) inputMessage.obj);
                    break;
                case MSG_QUIT:
                    encoder.handleDestroy();
                    break;
                default:
                    throw new RuntimeException("Unhandled msg what=" + what);
            }
        }
    }


    private void handleStartRecording() {
        L.i("EncoderRenderer->handleStartRecording onEnCodeAvailableCallBack is null = " + " hashCode:" + this.hashCode());
        L.i("EncoderRenderer->handleStartRecording  w=" + encoderW + "; h=" + encoderH);
        try {
            mVideoEncoder = new VideoEncoder(encoderW, encoderH);
        } catch (Exception e) {
            L.i("EncoderRenderer->handleStartRecording  e=" +e.toString()) ;
            throw new RuntimeException(e);
        }
        L.i("EncoderRenderer->handleStartRecording  surface=" + mVideoEncoder.getInputSurface());

        mInputEncoderSurface = new WindowSurface(eglCore, mVideoEncoder.getInputSurface(), true);
        mInputEncoderSurface.makeCurrent();
    }

    long temp = 0;
    int count = 0;


    int mFrameNum = 0;


    int errorCount = 0;


    long temp_eh = 0;
    int count_eh = 0;

    long lastTime;
    private void handleFrameAvailable(long timestampNanos) {
        //L.i( "-->handleFrameAvailable----------->time:" +timestampNanos+" dif:"+(timestampNanos-lastTime));
        lastTime = timestampNanos;
        GLES20.glViewport(0, 0, encoderW, encoderH);
        GLES20.glEnable(GL10.GL_MULTISAMPLE);

        mShowFilter.setTextureId(mTextureId);
        mShowFilter.draw();
        //drawBox(mFrameNum++);

        mInputEncoderSurface.setPresentationTime(timestampNanos);
        mInputEncoderSurface.swapBuffers();

        count_eh++;
        if (System.currentTimeMillis() - temp_eh >= 3000) {
            temp_eh = System.currentTimeMillis();
            L.i("fps", "ScreenRecord_gl->handleFrameAvailable----------->count:" + count_eh / 3);
            count_eh = 0;
        }


        //2020.09.17 add
        try {
            mVideoEncoder.drainEncoder(timestampNanos);
        } catch (Exception e) {
            if (errorCount++ % 100 == 0) {
                L.e("EncoderRenderer->mVideoEncoder.drainEncoder- " + encoderW + "x" + encoderH + " -e:" + e.toString());
            }
        }
        // end
    }

    private void drawBox(int posn) {
        final int width = mInputEncoderSurface.getWidth();
        int xpos = (posn * 4) % (width - 50);
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glScissor(xpos, 0, 100, 100);
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
    }

    private void handleStopRecording() {
        L.i("handleStopRecording");
        Looper.myLooper().quit();
//        synchronized (mReadyFence) {
//            mReady = mRunning = false;
//            mHandler = null;
//        }
        if (mVideoEncoder != null) {
            mVideoEncoder.release();
            mVideoEncoder = null;
        }
        if (mInputEncoderSurface != null) {
            mInputEncoderSurface.release();
            mInputEncoderSurface = null;
        }
    }

    private void handleDestroy() {
        Looper.myLooper().quit();
        if (mVideoEncoder != null) {
            mVideoEncoder.release();
            mVideoEncoder = null;
        }
        if (mInputEncoderSurface != null) {
            mInputEncoderSurface.release();
            mInputEncoderSurface = null;
        }
        if (eglCore != null) {
            eglCore.release();
            eglCore = null;
        }
    }

    private void handleUpdateSharedContext(EGLContext newSharedContext) {
        L.i("EncoderRenderer->handleUpdatedSharedContext " + newSharedContext);
        mInputEncoderSurface.releaseEglSurface();
        mInputEncoderSurface.recreate(eglCore);
        mInputEncoderSurface.makeCurrent();
    }

    private void handleUpdateSize(int width, int height) {
        encoderW = width;
        encoderH = height;

        L.i("EncoderRenderer->handleUpdateSize->encoderW:" + encoderW + " encoderH:" + encoderH);
        mInputEncoderSurface.releaseEglSurface();
        Surface surface = mVideoEncoder.reset(width, height);
        if (surface == null) {
            L.e("handleUpdateSize width:" + width + " height:" + height + " surface is null ");
            return;
        }
        mInputEncoderSurface.recreate(eglCore, surface);
        mInputEncoderSurface.makeCurrent();

        mShowFilter.setSize(width, height);
    }

    public void adjustCodecSize(int encoderW, int encoderH) {
        synchronized (mReadyFence) {
            L.i("EncoderRenderer->adjustCodecSize:" + encoderW + " encoderH:" + encoderH + " title:" + encoderH + " mReady:" + mReady);
            if (!mReady) {
                return;
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SIZE, encoderW, encoderH));
        }
    }

    public void adjustCodecParameter(int bitRate) {
        if (mVideoEncoder != null) {
            mVideoEncoder.adjustCodecParameter(bitRate);
        }
    }

    public synchronized void requestKey() {
        L.i("EncoderRenderer->requestKey:" + encoderW + " encoderH:" + encoderH +" mReady:"+mReady);
        if (mVideoEncoder != null) {
            mVideoEncoder.requestKey();
            //mInputEncoderSurface = new WindowSurface(eglCore, mVideoEncoder.reset(encoderW,encoderH), true);
            //mInputEncoderSurface.makeCurrent();
        }
    }

    private final AFilter mShowFilter = new NoFilter();




}
