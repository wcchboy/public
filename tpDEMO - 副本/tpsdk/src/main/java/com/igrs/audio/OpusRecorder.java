package com.igrs.audio;


import android.media.AudioRecord;

import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class OpusRecorder implements Runnable {

    private AudioRecord audioRecord;
    private boolean isRecorder = false;
    private short[] audioBuffer;

    private static OpusRecorder instance = null;
    public static OpusRecorder getInstance() {
        if (instance == null) {
            synchronized (OpusRecorder.class) {
                if (instance == null) {
                    instance = new OpusRecorder();
                }
            }
        }
        return instance;
    }
    private OpusRecorder(){
        L.i("OpusRecorder->OpusRecorder()");
    }

    public void start(AudioRecord audioRecord) {
        //audioBuffer = new short[OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE*OpusUtils.DEFAULT_OPUS_CHANNEL/100];//4800*16*2/8每次编码的数据也就是10ms

        //frame_size 120(2.5), 240(5), 480(10), 960(20), 1920(40), 2880(60) -->10 ms (480 samples at 48 kHz)
        audioBuffer = new short[OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE*OpusUtils.DEFAULT_OPUS_CHANNEL/100/4];//4800*16*2/8每次编码的数据也就是10ms

        L.i("OpusRecorder->start 1 audioBuffer:"+audioBuffer.length);
        L.i("OpusRecorder->start 1 audioRecord:"+audioRecord);
        this.audioRecord = audioRecord;
        new Thread(this).start();
        L.i("OpusRecorder->start 2 audioRecord:"+audioRecord);
        //audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, DecodeOpusPresenter.DEFAULT_AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
    }

    public void stop() {
        isRecorder = false;
        if(audioRecord!=null){
            try{
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }catch (Exception e){
            }
        }
    }

    @Override
    public void run() {
        isRecorder = true;
        try{
            L.i("OpusRecorder->run start ");
            audioRecord.startRecording();
            OpusUtils opusUtils = OpusUtils.getInstance();
            long createEncoder = opusUtils.createEncoder(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, OpusUtils.DEFAULT_OPUS_CHANNEL, 8);

//            File filePcm = new File("/sdcard/test.pcm");
//            if (filePcm.exists()) {
//                filePcm.delete();
//            }
//            filePcm.createNewFile();
//
//            FileOutputStream filePcmOutputStream = new FileOutputStream(filePcm, true);
//            BufferedOutputStream filePcmBufferedOutputStream = new BufferedOutputStream(filePcmOutputStream);

            L.i("OpusRecorder->run start isRecorder:"+isRecorder);
            while (isRecorder) {
                int curShortSize = audioRecord.read(audioBuffer, 0, audioBuffer.length);
                //L.i("OpusRecorder->run curShortSize:"+curShortSize+" audioBuffer.length:"+audioBuffer.length);
                try {
                    if (curShortSize > 0 && curShortSize <= audioBuffer.length) {

//                        StringBuffer stringBuffer  = new StringBuffer();
//                        for (int i=0;i<audioBuffer.length;i++){
//                            stringBuffer.append(audioBuffer[i]+" ");
//                        }
//                        L.i("OpusRecorder->run curShortSize:"+curShortSize+" audioBuffer:"+stringBuffer.toString());
                        byte[] byteArray = new byte[audioBuffer.length / 4];//编码后大小减小8倍
                        int encodeSize = opusUtils.encode(createEncoder, audioBuffer, 0, byteArray);

                        if (encodeSize > 0) {
                            byte[] decodeArray = new byte[encodeSize];
                            System.arraycopy(byteArray, 0, decodeArray, 0, encodeSize);
                            if(encodeSize==3){
                                //L.e("OpusRecorder pcm encodeSize:" + encodeSize + " [" + decodeArray[0]+" "+decodeArray[1]+" "+decodeArray[2]+"]");
                                //11110 1 00
                                //String bs = String.format("%8s", Integer.toBinaryString(decodeArray[0]&0xFF)).replace(" ", "0");
                                //L.i(""+bs);
                            }else{
                                //L.i("OpusRecorder pcm encodeSize:" + encodeSize + " [" + decodeArray[0]+" "+decodeArray[1]+" "+decodeArray[2]+"]");
                                ProjectionSDK.getInstance().sendAudioMessage("",decodeArray);//写入OPUS
                            }
                        } else {
                            L.i("OpusRecorder pcm encodeSize:" + encodeSize);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e("OpusRecorder->run---e:"+e.toString());
                }
            }
            opusUtils.destroyEncoder(createEncoder);
            audioRecord.stop();
            audioRecord.release();
        }catch (Exception e){
            L.e("OpusRecorder->run---e:"+e.toString());
        }
        L.i("OpusRecorder->run end ");
    }

}
