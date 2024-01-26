/*
 * Copyright 2014 Google Inc. All rights reserved.
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

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.igrs.sml.RuntimeInfo;
import com.igrs.sml.tcp.TcpConst;
import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;
import com.igrs.tpsdk.service.EncoderH264CallBack;

import java.io.IOException;
import java.nio.ByteBuffer;

@RequiresApi(api = Build.VERSION_CODES.M)
public class VideoEncoder {
    private final String mime_type = MediaFormat.MIMETYPE_VIDEO_AVC;
    //private final String mime_type = MediaFormat.MIMETYPE_VIDEO_HEVC;
    private static final int FRAME_RATE = 22;               // 30fps
    private static final int IFRAME_INTERVAL = 10;           // 1 seconds between I-frames
    private static final int REPEAT_FRAME_DELAY_US = 100_000; // repeat after 100ms
    private static final String KEY_MAX_FPS_TO_ENCODER = "max-fps-to-encoder";

    private Surface mInputSurface;
    private MediaCodec mVideoEncoder;
    /////////////////////////////////
    private boolean isCodecRun = false;

    /////////////////////////////////
    MediaFormat format;

    private int encoderW;
    private int encoderH;

    String dev_id;

    @SuppressLint("HandlerLeak")
    public VideoEncoder(int width, int height)
            throws IOException {
        this.encoderW = width;
        this.encoderH = height;
        L.i("VideoEncoder-----init-----encoderW:" + encoderW + " encoderH:" + encoderH);
        mVideoEncoder = MediaCodec.createEncoderByType(mime_type);
        reset(width, height);
    }

    public Surface reset(int width, int height) {
        this.encoderW = width;
        this.encoderH = height;
        L.i("VideoEncoder reset------requestKey-----isCodecRun:" + isCodecRun + " width:" + width + " height:" + height);
        if (mVideoEncoder != null) {
            isCodecRun = false;
            try {
                mVideoEncoder.stop();
                mVideoEncoder.reset();
            } catch (Exception e) {
                e.printStackTrace();
                L.e("VideoEncoder reset-----------e:" + e.toString());
            }
        } else {
            L.i("VideoEncoder reset-----------mVideoEncoder is null");
            return null;
        }

        //int video_bitrate = 1600 * 1000;//width * height * FRAME_RATE * 1 / 5;

        //int video_bitrate = width * height * FRAME_RATE * 1 / 5;
        //int video_bitrate = width * height * 30 * 1 / 5;


        int video_bitrate = (int)(width * height * 30*0.7f * 1 / 5);
        RuntimeInfo.info_bit = video_bitrate;
        format = MediaFormat.createVideoFormat(mime_type, width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        format.setInteger(MediaFormat.KEY_BIT_RATE, video_bitrate);
//        format.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, REPEAT_FRAME_DELAY_US);
//        format.setFloat(KEY_MAX_FPS_TO_ENCODER, FRAME_RATE);
        L.i("reset->format: " + format);

        try {
            mVideoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (Exception e) {
            e.printStackTrace();
            L.e("reset----------->e:" + e.toString());
        }

        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        mInputSurface = mVideoEncoder.createInputSurface();
        startRecord();
        return mInputSurface;
    }

    public Surface getInputSurface() {
        return mInputSurface;
    }


    public void startRecord() {
        L.i("startRecord-----------encoderW:" + encoderW);
        req_time = System.currentTimeMillis();
        if (mVideoEncoder != null) {
            mVideoEncoder.start();
            isCodecRun = true;
        }
        L.w("startRecord-----end------encoderW:" + encoderW);
    }

    public void release() {
        isCodecRun = false;
        L.i("releasing encoder objects");
        if (mVideoEncoder != null) {
            try {
                mVideoEncoder.stop();
                mVideoEncoder.release();
                mVideoEncoder = null;
            } catch (Exception e) {
            }
        }
    }

    private int inuseBitRate;

    public void adjustCodecParameter(int bitRate) {
        if (isCodecRun && bitRate != inuseBitRate) {
            L.i("VideoEncoder->adjustCodecParameter->vbv: bitRate=" + bitRate + " inuseBitRate:" + inuseBitRate);
            Bundle param = new Bundle();
            param.putInt(MediaCodec.PARAMETER_KEY_VIDEO_BITRATE, bitRate);

            if (mVideoEncoder != null) {
                mVideoEncoder.setParameters(param);
                inuseBitRate = bitRate;
            }
        }
    }

    long req_time = 0;
    boolean isNeedKey = true;

    public synchronized void requestKey() {
        if (isCodecRun) {
            try {
                L.i("VideoEncoder  requestKey--encoderW:" + encoderW + " encoderH:" + encoderH);
                if (Build.VERSION.SDK_INT >= 23 && mVideoEncoder != null) {
                    isNeedKey = true;
                    req_time = System.currentTimeMillis();
                    Bundle params = new Bundle();
                    params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                    mVideoEncoder.setParameters(params);
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e("VideoEncoder  requestKey--encoderW:" + encoderW + " encoderH:" + encoderH + " e:" + e.toString());
            }
        }
    }


    private final MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private byte[] information;
    final int TIMEOUT_USEC = 100;

    byte[] bytes;
    byte[] bytes_time;

    long startTime = 0;

    int count = 0;
    long temp = 0;

    public int drainEncoder(long time) {
        int encoderStatus = mVideoEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
       // while (encoderStatus>=0){
            count++;
            if (System.currentTimeMillis() - temp >= 3000) {
                temp = System.currentTimeMillis();
                RuntimeInfo.info_fps = count / 3;
                L.i("fps", "-->drainEncoder----------->count:" + RuntimeInfo.info_fps);
                count = 0;
            }
            ByteBuffer encodeData = mVideoEncoder.getOutputBuffer(encoderStatus);
            if (encodeData == null) {
                throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null" + " encoderW:" + encoderW + " encoderH:" + encoderH);
            }
            if (startTime == 0) {
                startTime = time;
            }
            //long dif = time - startTime;
            //L.i( "-->drainEncoder----------->time:" +time+" dif:"+dif);

            if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                //sps pps信息
                bytes = new byte[mBufferInfo.size];
                encodeData.get(bytes, 0, mBufferInfo.size);
                information = bytes;
                StringBuffer s2 = new StringBuffer();
                for (int i = 0; i < information.length; i++) {
                    s2.append(information[i] + " ");
                }
                L.e("information->length:" + information.length + "\n" + s2.toString());
            } else if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {

//                dif = System.currentTimeMillis() - lastTime;
//                if (dif > 34) {
//                }
//                L.i("frame=>:: key, dif=" + dif);
//                lastTime = System.currentTimeMillis();

                if (TcpConst.h264_has_time) {
                    bytes = new byte[8 + mBufferInfo.size + information.length];
                    bytes_time = Common.LongToBytes(time);
                    System.arraycopy(bytes_time, 0, bytes, 0, bytes_time.length);
                    System.arraycopy(information, 0, bytes, bytes_time.length, information.length);
                    encodeData.get(bytes, bytes_time.length + information.length, mBufferInfo.size);
                } else {
                    bytes = new byte[mBufferInfo.size + information.length];
                    System.arraycopy(information, 0, bytes, 0, information.length);
                    encodeData.get(bytes, information.length, mBufferInfo.size);
                }
                ProjectionSDK.getInstance().sendVideoMessage(dev_id, bytes);
                //FileUtils.writeFileToSDCard(bytes,"h264","864.h264",true,true);
                //L.i("frame=>:: Key, size=" + mBufferInfo.size + " length:" + bytes.length);
            } else {
//                dif = System.currentTimeMillis() - lastTime;
//                if (dif > 34) {
//
//                }
//                L.i("frame=>:: unkown, dif=" + dif);
//                lastTime = System.currentTimeMillis();

                if (TcpConst.h264_has_time) {
                    bytes = new byte[8 + mBufferInfo.size];
                    bytes_time = Common.LongToBytes(time);
                    System.arraycopy(bytes_time, 0, bytes, 0, bytes_time.length);
                    encodeData.get(bytes, bytes_time.length, mBufferInfo.size);
                } else {
                    bytes = new byte[mBufferInfo.size];
                    encodeData.get(bytes, 0, mBufferInfo.size);
                }
                ProjectionSDK.getInstance().sendVideoMessage(dev_id, bytes);
                //FileUtils.writeFileToSDCard(bytes,"h264","864.h264",true,true);
                //L.i("frame=>:: unkown, size="+mBufferInfo.size+" length:"+bytes.length);
            }
            mVideoEncoder.releaseOutputBuffer(encoderStatus, false);
            //encoderStatus = mVideoEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
        //}
        return 1;
    }
    // end

}
