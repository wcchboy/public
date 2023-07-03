package com.igrs.sml;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.igrs.sml.callback.DecoderCallback;
import com.igrs.sml.tcp.TcpConst;
import com.igrs.sml.util.BaseUtil;
import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class H264Decoder_ {

    private MediaCodec mMediaCodec;
    private MediaFormat mMediaFormat;
    private Surface mSurface;

    private LinkedBlockingQueue<byte[]> mH264DataQueue;


    private boolean isRunning = false;
    private DecoderCallback decoderCallback;

    private String dev_id;
    private boolean findFirstKey = false;

    private H264Decoder_() {

    }

    public H264Decoder_(LinkedBlockingQueue<byte[]> mH264DataQueue, Surface surface, String dev_id, DecoderCallback decoderCallback) {
        this.mH264DataQueue = mH264DataQueue;
        this.mSurface = surface;
        this.dev_id = dev_id;
        this.decoderCallback = decoderCallback;
    }

    private int d_count = 0;

    public void start() throws Exception {
        d_count = 0;
        isRunning = false;
        findFirstKey = false;
        try {
            if (mSurface == null) {
                L.e("H264Decoder_->start " + dev_id + " startDecoder failed, please check the MediaCodec is init correct");
                throw new IllegalArgumentException("H264Decoder_ startDecoder failed, please check the Surface");
            }
            mMediaFormat = MediaFormat.createVideoFormat(TcpConst.mime_type, 3840, 2160);
           // mMediaFormat.setInteger(MediaFormat.KEY_LOW_LATENCY,1);//启用低延迟解码时为 1，否则为 0。
            L.e("H264Decoder_->start " + dev_id);
            isRunning = true;
           // mMediaCodec = MediaCodec.createDecoderByType(mime_type);
            mMediaCodec = MediaCodec.createByCodecName("OMX.google.hevc.decoder");
            mMediaCodec.setCallback(callback);
            mMediaCodec.configure(mMediaFormat, mSurface, null, 0);
            mMediaCodec.start();
            L.e("H264Decoder_->start " + dev_id + " 硬解成功");
            if (decoderCallback != null) {
                decoderCallback.decoderCallback(1);
            }
        } catch (Exception e) {
            L.e("H264Decoder_->start " + dev_id + " 硬解失败  ");
            try {
                mMediaCodec = MediaCodec.createByCodecName("OMX.google.h264.decoder");
                if (mMediaCodec != null && mSurface != null) {
                    isRunning = true;
                    mMediaCodec.setCallback(callback);
                    mMediaCodec.configure(mMediaFormat, mSurface, null, 0);
                    mMediaCodec.start();
                }
                L.e("H264Decoder_->start " + dev_id + " 软解成功");
                if (decoderCallback != null) {
                    decoderCallback.decoderCallback(2);
                }
            } catch (Exception e1) {
                if (decoderCallback != null) {
                    decoderCallback.decoderCallback(-1);
                }
            }
        }

    }

    public synchronized void reset() {
        d_count = 0;
        findFirstKey = false;
        L.e("H264Decoder_ ->reset() " + dev_id + " size=" + mH264DataQueue.size() + " isValid:" + mSurface.isValid());
        try {
            if (mMediaCodec != null) {
                mMediaCodec.stop();
                mMediaCodec.reset();
                mMediaCodec.setCallback(callback);
                mMediaCodec.configure(mMediaFormat, mSurface, null, 0);
                mMediaCodec.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.e("H264Decoder_ Decoder->reset " + dev_id + " try drest e:" + e.toString());
            dreset();
        }
    }

    public void dreset() {
        d_count = 0;
        findFirstKey = false;
        L.e("H264Decoder_ ->dreset() " + dev_id + " size=" + mH264DataQueue.size() + " isValid:" + mSurface.isValid());
        onDestroy();
        L.e("H264Decoder_->dreset ");
        try {
            Thread.sleep(500);
            L.e("H264Decoder_->dreset star " + dev_id);
            mMediaCodec = MediaCodec.createDecoderByType(TcpConst.mime_type);
            mMediaFormat = MediaFormat.createVideoFormat(TcpConst.mime_type, 3840, 2160);
            start();
            L.e("H264Decoder_->dreset end " + dev_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * release all resource that used in Encoder
     */
    public void onDestroy() {
        isRunning = false;
        L.e("H264Decoder_->destroy " + dev_id);// + " \n" + Log.getStackTraceString(new Exception("Trace log")));
        if (mMediaCodec != null) {
            try {
                //L.e("H264Decoder_->mMediaCodec->destroy 3-1");
                mMediaCodec.stop();
                //L.e("H264Decoder_->mMediaCodec->destroy 3-2");
                mMediaCodec.release();
                //L.e("H264Decoder_->mMediaCodec->destroy 3-3 end");
            } catch (Exception e) {
                L.e("H264Decoder_->mMediaCodec->destroy e:"+e.toString());
                e.printStackTrace();
            }
            mMediaCodec = null;
        }
        L.e("H264Decoder_->destroy end");
    }

    MediaCodec.Callback callback = new MediaCodec.Callback() {
        private long lastShowTime=0;
        private byte[] data;
        private byte[] bytes_time;
        private long renderTimestampNs = 0;
        private long startTime = 0;
        private int count = 0;
        private long temp = 0;

        private int count_out = 0;
        private long temp_out = 0;

        private int fps_in=0,fps_out=0;
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec mediaCodec, int inputBufferIndex) {
            if (inputBufferIndex >= 0) {
                try {
                    data = mH264DataQueue.take();
                    if (!findFirstKey) {
                        boolean isKey = false;
                        long time = System.currentTimeMillis();
                        L.e("DecodeInputThread need  first key ");
                        while (isRunning && !isKey) {
                            isKey = BaseUtil.checkIsIFrame(data);
                            if (!isKey) {
                                data = mH264DataQueue.take();
                            } else {
                                findFirstKey = true;
                                if(TcpConst.h264_has_time){
                                    bytes_time = new byte[8];
                                    System.arraycopy(data, 0, bytes_time, 0, 8);
                                    startTime = Common.BytesToLong(bytes_time);
                                }
                                L.e("DecodeInputThread  find  first key dif:" + (System.currentTimeMillis() - time));
                            }
                        }
                    }
                } catch (Exception e) {
                    L.e("H264Decoder_-> get data<0 e=" + e.toString());
                }
                count++;
                if (System.currentTimeMillis() - temp >= 3000) {
                    temp = System.currentTimeMillis();
                    //RuntimeInfo.info_fps = count / 3;
                    fps_in = count / 3;
                    L.i("fps", "-->decoder----in------->in:" + fps_in+" out:"+fps_out);
                    count = 0;
                }
                try{
                    ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufferIndex);
                    inputBuffer.put(data);
                    if(TcpConst.h264_has_time){
                        bytes_time = new byte[8];
                        System.arraycopy(data, 0, bytes_time, 0, 8);
                        renderTimestampNs = Common.BytesToLong(bytes_time);
                        mMediaCodec.queueInputBuffer(inputBufferIndex, 8, data.length, System.nanoTime() / 1000, 0);
                    }else{
                        mMediaCodec.queueInputBuffer(inputBufferIndex, 0, data.length, System.nanoTime() / 1000, 0);
                    }
                }catch (Exception e){
                   L.e("onInputBufferAvailable->inputBufferIndex:"+inputBufferIndex+" e->"+e.toString());
                }
            }
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec mediaCodec, int outputBufferIndex, @NonNull MediaCodec.BufferInfo bufferInfo) {
            //L.i("H264Decoder onOutputBufferAvailable: " +outputBufferIndex);
            if (outputBufferIndex >= 0) {
                count_out++;
                if (System.currentTimeMillis() - temp_out >= 3000) {
                    temp_out = System.currentTimeMillis();
                    //RuntimeInfo.info_fps = count / 3;
                    fps_out = count_out / 3;
                    L.i("fps", "-->decoder-------out---->in:" + fps_in+" out:"+fps_out);
                    count_out = 0;
                }

                try{
                    if(bufferInfo.size != 0){
                        mMediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                    }else{
                        L.e("fps", "-->decoder-------bufferInfo.size:" +bufferInfo.size);
                        mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                    }
                }catch (Exception e){
                    L.e("fps", "onOutputBufferAvailable-->decoder-------e:" +e.toString());
                }


            }
        }

        @Override
        public void onError(@NonNull MediaCodec mediaCodec, @NonNull MediaCodec.CodecException e) {
            L.e("H264Decoder_->onError  "+e.toString());
            e.printStackTrace();
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
            MediaFormat newFormat = mMediaCodec.getOutputFormat();
            int width = newFormat.getInteger(MediaFormat.KEY_WIDTH);
            if (newFormat.containsKey("crop-left") && newFormat.containsKey("crop-right")) {
                width = newFormat.getInteger("crop-right") + 1 - newFormat.getInteger("crop-left");
            }
            int height = newFormat.getInteger(MediaFormat.KEY_HEIGHT);
            if (newFormat.containsKey("crop-top") && newFormat.containsKey("crop-bottom")) {
                height = newFormat.getInteger("crop-bottom") + 1 - newFormat.getInteger("crop-top");
            }
            L.i("H264Decoder_ "+dev_id+" size changed: " + width + "x" + height+"\nMediaCodec.INFO_OUTPUT_FORMAT_CHANGED encoder output format changed: " + newFormat);
            if (decoderCallback != null) {
                decoderCallback.decoderSizeChage(width, height,newFormat);
            }
        }
    };

}