package com.igrs.sml;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;

import com.igrs.sml.tcp.TcpConst;
import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;

import java.io.IOException;
import java.nio.ByteBuffer;

public class EncodeThread extends Thread {

    protected boolean isExit = false;
    private boolean needEncode = false;
    protected Object LOCK_needEnCoder = new Object();
    private byte[] information;

    private MediaCodec mEncoder;
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private int maxFps = 22;

    private String dev_id;

    public EncodeThread(String dev_id) {
        this.dev_id = dev_id;
        if (MediaFormat.MIMETYPE_VIDEO_HEVC.equals(TcpConst.mime_type)) {
            maxFps = 60;
        }
    }

    public void setMaxFps(int maxFps) {
        this.maxFps = maxFps;
        L.e("VideoEncoder  setMaxFps:" + maxFps);
    }

    public void setPause(boolean isPause) {
        synchronized (LOCK_needEnCoder) {
            needEncode = !isPause;
            LOCK_needEnCoder.notify();
            if (!isPause) {
                requestKey();
            }
        }
    }

    public void onStop() {
        synchronized (LOCK_needEnCoder) {
            needEncode = false;
            LOCK_needEnCoder.notify();
        }
        if (mEncoder != null) {
            try {
                mEncoder.stop();
                mEncoder.release();
                mEncoder = null;
            } catch (Exception e) {
                e.printStackTrace();
                L.e("VideoEncoder->prepareEncoder->e:" + e.toString());
            }
        }
    }

    public synchronized void requestKey() {
        try {
            if (Build.VERSION.SDK_INT >= 23 && mEncoder != null) {
                Bundle params = new Bundle();
                params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                mEncoder.setParameters(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.e("VideoEncoder  requestKey--e:" + e.toString());
        }
    }

    public void setBit(int bit) {

    }

    String h264Name = "";

    @Override
    public void run() {
        super.run();
        int count = 0;
        long temp = 0;
        int nul_count = 0;
        byte[] bytes;
        byte[] bytes_time;

        int c = 0;
        while (!isExit) {
            try {
                synchronized (LOCK_needEnCoder) {
                    if (!needEncode) {
                        LOCK_needEnCoder.wait();
                    }
                    if (mEncoder == null) {
                        Thread.sleep(100);
                        if (nul_count % 500 == 0) {
                            L.e("mEncoder is null ");
                        }
                        continue;
                    }
                    int index = mEncoder.dequeueOutputBuffer(mBufferInfo, 5);

                    if (index >= 0) {
                        //L.i("index: " +index+"  "+mBufferInfo.presentationTimeUs);
                        count++;
                        if (System.currentTimeMillis() - temp >= 3000) {
                            temp = System.currentTimeMillis();
                            RuntimeInfo.info_fps = count / 3;
                            L.i("fps", "-->encode----------->count:" + RuntimeInfo.info_fps);
                            count = 0;
                        }
                        ByteBuffer encodeData = mEncoder.getOutputBuffer(index);
                        if (encodeData != null) {
                            c++;
                            if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                                //sps pps信息
                                information = new byte[mBufferInfo.size];
                                encodeData.get(information);
                                StringBuffer s2 = new StringBuffer();
                                for (int i = 0; i < information.length; i++) {
                                    s2.append(information[i] + ",");
                                }
                                L.e("information->length:" + information.length + "\n" + s2.toString());
                                h264Name = System.currentTimeMillis() + "";
                            } else if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                                if (TcpConst.h264_has_time) {
                                    bytes = new byte[8 + information.length + mBufferInfo.size];
                                    bytes_time = Common.LongToBytes(mBufferInfo.presentationTimeUs * 1000);
                                    System.arraycopy(bytes_time, 0, bytes, 0, bytes_time.length);
                                    System.arraycopy(information, 0, bytes, bytes_time.length, information.length);
                                    encodeData.get(bytes, bytes_time.length + information.length, mBufferInfo.size);
                                } else {
                                    bytes = new byte[mBufferInfo.size + information.length];
                                    System.arraycopy(information, 0, bytes, 0, information.length);
                                    encodeData.get(bytes, information.length, mBufferInfo.size);
                                }
                                //FileUtils.writeFileToSDCard(bytes,"h264",h264Name+".h264",true,true);
                                // L.i("frame=>:: Key, size=" + mBufferInfo.size + " length:" + bytes.length);
                                ProjectionSDK.getInstance().sendVideoMessage(dev_id, bytes);
                            } else {
                                if (TcpConst.h264_has_time) {
                                    bytes = new byte[8 + mBufferInfo.size];
                                    bytes_time = Common.LongToBytes(mBufferInfo.presentationTimeUs * 1000);
                                    System.arraycopy(bytes_time, 0, bytes, 0, bytes_time.length);
                                    encodeData.get(bytes, bytes_time.length, mBufferInfo.size);
                                } else {
                                    bytes = new byte[mBufferInfo.size];
                                    encodeData.get(bytes);
                                }

                                //FileUtils.writeFileToSDCard(bytes,"h264",h264Name+".h264",true,true);

                                //L.i("frame=>:: unkown, size="+mBufferInfo.size+" length:"+bytes.length);
//                               c++;
//                               StringBuffer sb = new StringBuffer();
//                               for (int i = 0; i < 30; i++) {
//                                   sb.append(bytes[i] + " ");
//                               }
//                               L.i(c + " data->" + sb.toString());
                                ProjectionSDK.getInstance().sendVideoMessage(dev_id, bytes);
                            }

                        }
                        mEncoder.releaseOutputBuffer(index, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e("encodeThread  -e:" + e.toString());
            }
        }
        L.i("encodeThread  exit---end---------------------stopSelf---------------- isExit:" + isExit);
    }

    /**
     * 初始化编码器
     */
    public Surface prepareEncoder(int width, int height, int fps) throws IOException {
        RuntimeInfo.info_fps = 0;
        maxFps = fps;
        if (width <= 0 || height <= 0) {
            return null;
        }
        L.i("VideoEncoder->prepareEncoder->" + width + "x" + height);
        synchronized (LOCK_needEnCoder) {
            needEncode = false;
            LOCK_needEnCoder.notify();
        }
        if (mEncoder != null) {
            try {
                mEncoder.stop();
                mEncoder.release();
                mEncoder = null;
            } catch (Exception e) {
                e.printStackTrace();
                L.e("VideoEncoder->prepareEncoder->e:" + e.toString());
            }
        }

        int bit = (int) (RuntimeInfo.mScreenWidth * RuntimeInfo.mScreenHeight);
        //int bit = (int) (RuntimeInfo.mScreenWidth * RuntimeInfo.mScreenHeight * 1.5f);
        L.i("VideoEncoder->prepareEncoder->bit:" + bit + " maxFps:" + maxFps);

        String KEY_MAX_FPS_TO_ENCODER = "max-fps-to-encoder";
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(TcpConst.mime_type, width, height);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bit);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 300);

        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, maxFps);
        //mediaFormat.setInteger(MediaFormat.KEY_CAPTURE_RATE, maxFps);
        mediaFormat.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 1000000 / maxFps);


        mediaFormat.setFloat(KEY_MAX_FPS_TO_ENCODER, maxFps);

        mediaFormat.setInteger(MediaFormat.KEY_PRIORITY, 0);//时实编码 0 ；1


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /**
             * 可选配置，设置码率模式
             * BITRATE_MODE_CQ：恒定质量
             * BITRATE_MODE_VBR：可变码率
             * BITRATE_MODE_CBR：恒定码率
             */
            //mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
            //mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
            /**
             * 可选配置，设置H264 Profile
             * 需要做兼容性检查
             */
            //mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileMain);
            //mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
            /**
             * 可选配置，设置H264 Level
             * 需要做兼容性检查
             */
            //mediaFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCProfileMain);
        }

        /*将设置好的参数配置给编码器---有些手机不支持 KEY_BITRATE_MODE 参数*/

        mEncoder = MediaCodec.createEncoderByType(TcpConst.mime_type);
        // zxq add 2021.12.10




        // mMediaCodec.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                int selProfile = 0;
                int selLevel = 0;
                MediaCodecInfo cinfo = mEncoder.getCodecInfo();
                MediaCodecInfo.CodecCapabilities cc = cinfo.getCapabilitiesForType(TcpConst.mime_type);
                if (MediaFormat.MIMETYPE_VIDEO_HEVC.equals(TcpConst.mime_type)) {
                    for (MediaCodecInfo.CodecProfileLevel pf : cc.profileLevels) {
                        L.i("265 cprofile: " + " profile=" + pf.profile + "; level=" + pf.level);
                        // if (pf.profile == MediaCodecInfo.CodecProfileLevel.HEVCProfileMain) {
//                        if (pf.profile == MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10) {
//                            selProfile = pf.profile;
//                            selLevel = pf.level;
//                        }
                    }
                } else {
                    for (MediaCodecInfo.CodecProfileLevel pf : cc.profileLevels) {
                        //L.i("cprofile: " + " profile=" + pf.profile + "; level=" + pf.level);
                        //if (pf.profile == MediaCodecInfo.CodecProfileLevel.AVCProfileHigh) {
                        if (pf.profile == MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline) {
                            selProfile = pf.profile;
                            selLevel = pf.level;
                        }
                    }
                }
                if (selProfile > 0 && selLevel > 0) {
                    L.i("cprofile: " + "have found profile=" + selProfile + "; level=" + selLevel);
                    mediaFormat.setInteger(MediaFormat.KEY_PROFILE, selProfile);
                    mediaFormat.setInteger(MediaFormat.KEY_LEVEL, selLevel);
                }
            }
        } catch (Exception e) {
            L.i("cprofile: config exception ........");
        }
        // end

        L.e("mediaFormat:" + mediaFormat);
        mEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        Surface surface = mEncoder.createInputSurface();
        mEncoder.start();

//        try{
//            mEncoder.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
//        }catch (Exception e){
//        }


        RuntimeInfo.info_bit = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE);
        synchronized (LOCK_needEnCoder) {
            needEncode = true;
            LOCK_needEnCoder.notify();
        }
        return surface;
    }

    public void onDestroy() {
        isExit = true;
        RuntimeInfo.info_fps = 0;
        RuntimeInfo.info_bit = 0;
        synchronized (LOCK_needEnCoder) {
            needEncode = true;
            LOCK_needEnCoder.notify();
        }
        if (mEncoder != null) {
            try {
                mEncoder.stop();
                mEncoder.release();
                mEncoder = null;
            } catch (Exception e) {
                e.printStackTrace();
                L.e("VideoEncoder->prepareEncoder->e:" + e.toString());
            }
        }
    }

}
