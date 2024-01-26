package com.igrs.audio.filedemo;

import android.text.TextUtils;

import com.igrs.audio.OpusUtils;
import com.igrs.sml.util.L;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public abstract class DecodeOpusPresenter {

    public String TAG = "qf";

    private boolean isCancel = false;
    private String decodeOpusFilePath = null;
    //public  int BUFFER_LENGTH = 120;
    //public  int BUFFER_LENGTH = 80;
    public  int BUFFER_LENGTH = 128;
    //public  int BUFFER_LENGTH = 60;
   // public  int BUFFER_LENGTH = 240;
    //public  int BUFFER_LENGTH = 128;
    public void decodeOpusFile(String path, boolean newThreadRun) {
        L.i( "DecodeOpusPresenter->decodeOpusFile: "+path+", newThreadRun:"+newThreadRun);
        decodeOpusFilePath = path;
        isCancel = false;
        if (!newThreadRun) {
            opusFileDecoder(true);
        } else {
            new Thread(new Runnable (){
                @Override
                public void run() {
                    opusFileDecoder(true);
                }

            }).start();
        }
    }

    public void readFile(String path, boolean newThreadRun) {
        L.i( "readFile: "+path+", newThreadRun:"+newThreadRun);
        decodeOpusFilePath = path;
        isCancel = false;
        if (!newThreadRun) {
            opusFileDecoder(false);
        } else {
            new Thread(new Runnable(){
                @Override
                public void run() {
                    opusFileDecoder(false);
                }
            }).start();
        }
    }

    public void cancelDecode() {
        isCancel = true;
    }

    private void opusFileDecoder(boolean needDecoder) {
        if (TextUtils.isEmpty(decodeOpusFilePath)) {
            opusDecodeFinish();
            return;
        }

        OpusUtils tntOpusUtils = OpusUtils.getInstance();
        long decoderHandler = tntOpusUtils.createDecoder(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, OpusUtils.DEFAULT_OPUS_CHANNEL);
        FileInputStream fis;
        try {
            fis = new FileInputStream(decodeOpusFilePath);
        } catch (Exception e){
            opusDecodeFinish();
            return;
        }
        BufferedInputStream bis = new BufferedInputStream(fis);
        L.i( "DecodeOpusPresenter opusFileDecoder-->");
        while (!isCancel) {
            byte[] data = new byte[BUFFER_LENGTH];
            int read = -1;
            try {
                read = bis.read(data, 0, data.length);
            } catch (Exception e){
            }
            if (read < 0) {//已经读完了
                L.i( "OpusFileDecoder compare 已经读完了");
                break;
            } else {
                if (needDecoder) {

                    short[] decodeBufferArray = new short[960];
                    int size = OpusUtils.getInstance().decode(decoderHandler, data, decodeBufferArray);
                    if (size > 0) {
                        short[] decodeArray = new short[size*OpusUtils.DEFAULT_OPUS_CHANNEL];
                        System.arraycopy(decodeBufferArray, 0, decodeArray, 0, size*OpusUtils.DEFAULT_OPUS_CHANNEL);
                       // audioTrack.write(decodeArray, 0, decodeArray.length);
                        opusDecode(decodeArray);//输出数据到接口
                    } else {
                        L.i("opusDecode length :"+data.length+" size:"+size+" error:\n");
                        switch (size){
                            case 0:
                                L.i("No error");
                                break;
                            case -1:
                                L.i("One or more invalid/out of range arguments");
                                break;
                            case -2:
                                L.i("Not enough bytes allocated in the buffer");
                                break;
                            case -3:
                                L.i("An internal error was detected");
                                break;
                            case -4:
                                L.i("The compressed data passed is corrupted");
                                break;
                            case -5:
                                L.i("Invalid/unsupported request number");
                                break;
                            case -6:
                                L.i("An encoder or decoder structure is invalid or already freed");
                                break;
                            case -7:
                                L.i("Memory allocation has failed");
                                break;
                        }
                    }
                } else {
                    opusDecode(OpusUtils.byteArrayToShortArray(data));
                }
            }
        }
        tntOpusUtils.destroyDecoder(decoderHandler);
        try{
            bis.close();
            fis.close();
        }catch (Exception e){
        }
        opusDecodeFinish();
    }

    public abstract void opusDecode(short[] formatShortArray);

    public abstract void opusDecodeFinish();
}
