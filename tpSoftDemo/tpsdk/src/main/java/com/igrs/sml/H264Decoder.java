package com.igrs.sml;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import com.igrs.sml.callback.DecoderCallback;
import com.igrs.sml.callback.OnDecoderAvailableListener;
import com.igrs.sml.tcp.TcpConst;
import com.igrs.sml.util.BaseUtil;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

public class H264Decoder extends Thread {

    private final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    private MediaCodec mMediaCodec;
    private MediaFormat mMediaFormat;
    private Surface mSurface;

    private LinkedBlockingQueue<byte[]> mH264DataQueue;

    private DecodeOutputThread mDecodeOutputThread;
    private DecoderCallback decoderCallback;

    private String dev_id;
    private boolean findFirstKey = false;

    private boolean isExit = false;

    private long timeoutUs = 100;
    private int width = 3840;
    private int height = 2160;

    private Object lock_code = new Object();

    private H264Decoder() {
    }

    public H264Decoder(LinkedBlockingQueue<byte[]> mH264DataQueue, Surface surface, String dev_id, DecoderCallback decoderCallback) {
        this.mH264DataQueue = mH264DataQueue;
        this.mSurface = surface;
        this.dev_id = dev_id;
        this.decoderCallback = decoderCallback;
        if (ProjectionSDK.getInstance().isM2) {
            width = 64;
            height = 64;
        } else {
            width = 3840;
            height = 2160;
        }
    }

    protected OnDecoderAvailableListener onDecoderAvailableListener;
    public void setOnDecoderAvailableListener(OnDecoderAvailableListener onDecoderAvailableListener) {
        this.onDecoderAvailableListener = onDecoderAvailableListener;
    }

    private int retry = 5;

    @Override
    public void start() {
        super.start();
        L.e("H264Decoder Decoder->start " + dev_id);
        if (mSurface == null) {
            L.e("H264Decoder->start " + dev_id + " startDecoder failed, please check the MediaCodec is init correct");
            throw new IllegalArgumentException("H264Decoder startDecoder failed, please check the Surface");
        }
        reset();
    }

    @Override
    public void run() {
        super.run();

        findFirstKey = false;
        long startTime = System.currentTimeMillis();

        byte[] data;
        byte[] bytes_time;
        long lastTimestampNs = 0;
        int inputBufferIndex;

        int count = 0;
        long temp = 0;

        long lastTime = 0;

        long lastInputTime = 0;
         int err_count = 0;
        while (!isExit) {
            synchronized (lock_code) {
                if (mMediaCodec == null) {
                    try {
                        lock_code.wait();
                    } catch (Exception e) {
                    }
                    continue;
                }
            }
            try {
                inputBufferIndex = mMediaCodec.dequeueInputBuffer(timeoutUs);
                if (inputBufferIndex >= 0) {
                    lastInputTime = System.currentTimeMillis();
                    retry = 10;
                    err_count = 0;
                    try {
                        data = mH264DataQueue.take();
                        if (!findFirstKey) {
                            L.e("H264Decoder need  first key " + dev_id + " name:" + mMediaCodec.getName());
                            while (!isExit) {
                                if (!BaseUtil.checkIsIFrame(data)) {
                                    data = mH264DataQueue.take();
                                } else {
                                    L.e("H264Decoder  find " + dev_id + "  first key dif:" + (System.currentTimeMillis() - startTime));
                                    findFirstKey = true;
                                    break;
                                }
                            }
                        }
                        if (data == null || data.length == 0) {
                            continue;
                        }
                    } catch (Exception e) {
                        continue;
                    }

                    ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufferIndex);
                    if (TcpConst.h264_has_time) {
                        bytes_time = new byte[8];
                        //System.arraycopy(data, 0, bytes_time, 0, 8);

//                        long time = Common.BytesToLong(bytes_time);
//                        if (lastTimestampNs == 0) {
//                            presentationTimeUs = System.nanoTime() / 1000;
//                        } else {
//                            long dif = time - lastTimestampNs;
//                            presentationTimeUs = (System.nanoTime() - dif) / 1000;
//                            //L.i("time","dif:"+(dif)+" t:"+time+" r:"+presentationTimeUs+" n:"+System.nanoTime()+" ["+data[8]+" "+data[9]+" "+data[10]+" "+data[11]+" "+data[12]+" "+data[13]+"] "+data.length+"<>");
//                        }
//                        lastTimestampNs = time;

                        byte[] datas = new byte[data.length - bytes_time.length];
                        System.arraycopy(data, bytes_time.length, datas, 0, datas.length);
                        inputBuffer.put(datas);
                        //FileUtils.writeFileToSDCard(datas,"h264",startTime+".h264",true,true);

                        //mMediaCodec.queueInputBuffer(inputBufferIndex, 0, datas.length, presentationTimeUs, 0);
                        mMediaCodec.queueInputBuffer(inputBufferIndex, 0, datas.length, System.nanoTime() / 1000, 0);

                        //mMediaCodec.queueInputBuffer(inputBufferIndex, 0, datas.length, presentationTimeUs, 0);
                        //FileUtils.writeFileToSDCard(datas, "h264", "ios1.h264", true, true);
                        //mMediaCodec.queueInputBuffer(inputBufferIndex, 8, data.length, presentationTimeUs, 0);
                        //mMediaCodec.queueInputBuffer(inputBufferIndex, 0, data.length, System.nanoTime()/ 1000 , 0);
                    } else {
                        inputBuffer.put(data);
                        mMediaCodec.queueInputBuffer(inputBufferIndex, 0, data.length, System.nanoTime() / 1000, 0);
                    }
                    count++;
                    if (System.currentTimeMillis() - temp >= 3000) {
                        temp = System.currentTimeMillis();
                        RuntimeInfo.info_fps = count / 3;
                        L.i("fps", "-->H264Decoder decoder-----" + dev_id + "------>count:" + (count / 3) + " size:" + mH264DataQueue.size() + " name:" + mMediaCodec.getName());
                        count = 0;
                    }
                    try {
                        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, timeoutUs);

                        while (outputBufferIndex >= 0) {
                            mMediaCodec.releaseOutputBuffer(outputBufferIndex, bufferInfo.size != 0);
                            outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, timeoutUs);
                        }
                        if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                            MediaFormat newFormat = mMediaCodec.getOutputFormat();
                            width = newFormat.getInteger(MediaFormat.KEY_WIDTH);
                            if (newFormat.containsKey("crop-left") && newFormat.containsKey("crop-right")) {
                                width = newFormat.getInteger("crop-right") + 1 - newFormat.getInteger("crop-left");
                            }
                            height = newFormat.getInteger(MediaFormat.KEY_HEIGHT);
                            if (newFormat.containsKey("crop-top") && newFormat.containsKey("crop-bottom")) {
                                height = newFormat.getInteger("crop-bottom") + 1 - newFormat.getInteger("crop-top");
                            }
                            L.i("H264Decoder " + dev_id + " size changed: " + width + "x" + height + "\nMediaCodec.INFO_OUTPUT_FORMAT_CHANGED encoder output format changed: " + newFormat);
                            if (decoderCallback != null) {
                                decoderCallback.decoderSizeChage(width, height, newFormat);
                            }
                        }
                    } catch (Exception e) {
                    }
                } else {
                    if(mH264DataQueue.size()<6){
                        continue;
                    }
                    try{
                        Thread.sleep(16);
                    }catch (Exception e){
                    }
                    L.i("DecodeInputThread " + dev_id + " dequeueInputBuffer:" + inputBufferIndex + " size:" + mH264DataQueue.size());
                    err_count++;
                    if(err_count>20){
                        L.e("H264Decoder -1 DecodeThread  -1-1-1-1-1");
                        reset();
                        continue;
                    }
                }
            } catch (MediaCodec.CodecException codecException) {
                try {
                    L.e("H264Decoder DecodeThread " + dev_id + " CodecException isRecoverable:" + codecException.isRecoverable() + " isTransient:" + codecException.isTransient() + " getDiagnosticInfo:" + codecException.getDiagnosticInfo());
                    if (codecException != null && !codecException.isRecoverable() && !codecException.isTransient()) {
                        reset();
                    }
                    if (codecException != null && codecException.isRecoverable() && mH264DataQueue.size() >= 200) {
                        findFirstKey = false;
                        if (mMediaCodec != null) {
                            mMediaCodec.stop();
                            //mMediaCodec.reset();
                            mMediaCodec.configure(mMediaFormat, mSurface, null, 0);
                            mMediaCodec.start();
                        }
                    }
                } catch (Exception e1) {
                    L.e("H264Decoder DecodeThread  " + dev_id + "  CodecException=" + e1.toString());
                    reset();
                }
            } catch (Exception e) {
                try {
                    err_count++;
                    L.e("H264Decoder DecodeThread " + dev_id + "  err_count=" + err_count + " size:" + mH264DataQueue.size() + " e=" + e.toString());
                    e.printStackTrace();
                    if (err_count % 60 == 0 && mH264DataQueue.size() >= 20) {
                        reset();
                    } else {
                        Thread.sleep(30);
                    }
                } catch (Exception e1) {
                    L.e("H264Decoder DecodeThread  " + dev_id + "  e1=" + e1.toString());
                }
            }
        }
        L.e("H264Decoder " + dev_id + " DecodeThread(" + getName() + ")  \n\n ===stop output DecodeThread===");
    }


    public synchronized void reset() {
        L.e("H264Decoder Decoder->reset isExit:" + isExit + " " + dev_id);
        if (isExit) {
            return;
        }
        L.e("H264Decoder ->reset " + dev_id + " retry:" + retry + " size=" + mH264DataQueue.size() + " mSurface sValid:" + mSurface.isValid());
        if (retry <= 0) {
            L.e("H264Decoder->reset " + dev_id + " 解码失败 \n" + Log.getStackTraceString(new Exception("test")));
            if (decoderCallback != null) {
                decoderCallback.decoderCallback(-1);
            }
            onDestroy();
        } else {
            try {
                retry--;
                if (mMediaCodec != null) {
                    try {
                        mMediaCodec.stop();
                    } catch (Exception e) {
                    }
                    try {
                        mMediaCodec.release();
                    } catch (Exception e) {
                    }
                    mMediaCodec = null;
                    synchronized (lock_code) {
                        lock_code.notifyAll();
                    }
                    Thread.sleep(200);
                }
                if (mDecodeOutputThread != null) {
                    try {
                        mDecodeOutputThread.join(50);
                    } catch (Exception e) {
                    }
                    mDecodeOutputThread = null;
                }
                findFirstKey = false;
                try {
                    mMediaFormat = MediaFormat.createVideoFormat(TcpConst.mime_type, width, height);
                    mMediaFormat.setInteger(MediaFormat.KEY_LOW_LATENCY, 1);//启用低延迟解码时为 1，否则为 0。
                    mMediaCodec = MediaCodec.createDecoderByType(TcpConst.mime_type);
                    mMediaCodec.configure(mMediaFormat, mSurface, null, 0);
                    mMediaCodec.start();
                    L.i("H264Decoder->reset " + dev_id + " 硬解成功");
                    if (decoderCallback != null) {
                        decoderCallback.decoderCallback(1);
                    }
                    mDecodeOutputThread = new DecodeOutputThread();
                    mDecodeOutputThread.start();
                } catch (Exception e) {
                    L.e("H264Decoder->reset " + dev_id + " 硬解失败  e:" + e.toString());
                    if (mMediaCodec != null) {
                        try {
                            mMediaCodec.stop();
                        } catch (Exception e1) {
                        }
                        try {
                            mMediaCodec.release();
                        } catch (Exception e1) {
                        }
                        mMediaCodec = null;
                        Thread.sleep(100);
                    }
                    try {
                        if (TcpConst.mime_type.equals(MediaFormat.MIMETYPE_VIDEO_AVC)) {
                            mMediaCodec = MediaCodec.createByCodecName("OMX.google.h264.decoder");
                        } else {
                            mMediaCodec = MediaCodec.createByCodecName("OMX.google.hevc.decoder");
                        }
                        if (mMediaCodec != null && mSurface != null) {
                            mMediaCodec.configure(mMediaFormat, mSurface, null, 0);
                            mMediaCodec.start();
                        }
                        L.i("H264Decoder->reset " + dev_id + " 软解成功");
                        if (decoderCallback != null) {
                            decoderCallback.decoderCallback(2);
                        }
                        mDecodeOutputThread = new DecodeOutputThread();
                        mDecodeOutputThread.start();
                    } catch (Exception e1) {
                        Thread.sleep(200);
                        reset();
                    }
                }
            } catch (Exception e) {
                try {
                    L.e("H264Decoder Decoder->reset err  " + dev_id + " retry:" + retry + " rest e:" + e.toString());
                    Thread.sleep(200);
                    reset();
                } catch (Exception e2) {
                }
            }
        }
    }


    /**
     * release all resource that used in Encoder
     */
    public void onDestroy() {
        L.e("H264Decoder->destroy " + dev_id);// + " \n" + Log.getStackTraceString(new Exception("Trace log")));
        findFirstKey = false;
        isExit = true;
        mH264DataQueue.clear();

        if (mDecodeOutputThread != null) {
            try {
                mDecodeOutputThread.join(50);
            } catch (Exception e) {
                L.e("H264Decoder->mDecodeOutputThread->destroy e:" + e.toString());
                e.printStackTrace();
            }
            mDecodeOutputThread = null;
        }
        if (mMediaCodec != null) {
            if (mMediaCodec != null) {
                try {
                    mMediaCodec.stop();
                } catch (Exception e) {
                }
                try {
                    mMediaCodec.release();
                } catch (Exception e) {
                }
            }
            mMediaCodec = null;
        }
        L.e("H264Decoder->destroy end");
    }

    private class DecodeOutputThread extends Thread {
        private final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex;
        long lastTime = 0;
        private int count = 0;
        private long temp = 0;
        @Override
        public void run() {
            synchronized (lock_code) {
                lock_code.notifyAll();
            }
            while (!isExit) {
                synchronized (lock_code) {
                    if (mMediaCodec == null) {
                        break;
                    }
                }
                try {
                    outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, timeoutUs);
                    if (outputBufferIndex >= 0) {
//                        long dif = System.currentTimeMillis() - lastTime;
//                        if (bufferInfo.size == 0 || dif < 16) {
//                            mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
//                        } else {
//
//                        }
                        count++;
                        if (System.currentTimeMillis() - temp >= 3000) {
                            temp = System.currentTimeMillis();
                            L.i("fps", "-->H264Decoder out decoder-----" + dev_id + "------>count:" + (count / 3) + " size:" + mH264DataQueue.size() + " name:" + mMediaCodec.getName());
                            count = 0;
                        }
                        mMediaCodec.releaseOutputBuffer(outputBufferIndex, bufferInfo.size != 0);
                        lastTime = System.currentTimeMillis();
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newFormat = mMediaCodec.getOutputFormat();
                        width = newFormat.getInteger(MediaFormat.KEY_WIDTH);
                        if (newFormat.containsKey("crop-left") && newFormat.containsKey("crop-right")) {
                            width = newFormat.getInteger("crop-right") + 1 - newFormat.getInteger("crop-left");
                        }
                        height = newFormat.getInteger(MediaFormat.KEY_HEIGHT);
                        if (newFormat.containsKey("crop-top") && newFormat.containsKey("crop-bottom")) {
                            height = newFormat.getInteger("crop-bottom") + 1 - newFormat.getInteger("crop-top");
                        }
                        //
                        L.i("H264Decoder " + dev_id + " size changed: " + width + "x" + height + "\nMediaCodec.INFO_OUTPUT_FORMAT_CHANGED encoder output format changed: " + newFormat);
                        if (decoderCallback != null) {
                            decoderCallback.decoderSizeChage(width, height, newFormat);
                        }
                    }
                } catch (Exception e) {
                    if (mMediaCodec == null) {
                        break;
                    }
                }
            }
            L.e("H264Decoder " + dev_id + " DecodeOutputThread(" + getName() + ")  \n\n ===stop output DecodeOutputThread===");
        }
    }
}
