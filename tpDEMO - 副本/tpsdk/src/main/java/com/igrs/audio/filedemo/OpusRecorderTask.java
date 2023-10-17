package com.igrs.audio.filedemo;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.igrs.audio.OpusUtils;
import com.igrs.sml.util.L;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class OpusRecorderTask implements Runnable {

    private String opusAudioOpusPath;
    private String opusAudioPcmPath;

    private AudioRecord audioRecord;
    private boolean isRecorder = false;
    private byte[] audioBuffer;

    public OpusRecorderTask(String opusAudioOpusPath, String opusAudioPcmPath) {
        this.opusAudioOpusPath = opusAudioOpusPath;
        this.opusAudioPcmPath = opusAudioPcmPath;

        audioBuffer = new byte[OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE*16*OpusUtils.DEFAULT_OPUS_CHANNEL/8/100];//4800*16*2/8每次编码的数据也就是10ms
        //audioBuffer = new byte[640];//4800*16*2/8每次编码的数据也就是10ms

        int channel = OpusUtils.DEFAULT_OPUS_CHANNEL==1?AudioFormat.CHANNEL_IN_MONO:AudioFormat.CHANNEL_IN_STEREO;

        int bufferSize = AudioRecord.getMinBufferSize(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, channel, AudioFormat.ENCODING_PCM_16BIT);
        L.i("OpusRecorderTask opusAudioOpusPath:"+opusAudioOpusPath);
        L.i("OpusRecorderTask opusAudioPcmPath:"+opusAudioPcmPath);
        L.i("channel:"+channel+" "+audioBuffer.length);
        L.i("bufferSize:"+bufferSize);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, channel, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
    }

    public void stop() {
        isRecorder = false;
    }

    @Override
    public void run() {
        isRecorder = true;
        try{
            audioRecord.startRecording();
            File file = new File(opusAudioOpusPath);
            File filePcm = new File(opusAudioPcmPath);
            File fileDir = new File(file.getParent());
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            if (filePcm.exists()) {
                filePcm.delete();
            }
            file.createNewFile();
            filePcm.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            FileOutputStream filePcmOutputStream = new FileOutputStream(filePcm, true);

            BufferedOutputStream fileOpusBufferedOutputStream = new BufferedOutputStream(fileOutputStream);//默认buffer大小8192
            BufferedOutputStream filePcmBufferedOutputStream = new BufferedOutputStream(filePcmOutputStream);

            OpusUtils opusUtils = OpusUtils.getInstance();
            long createEncoder = opusUtils.createEncoder(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, OpusUtils.DEFAULT_OPUS_CHANNEL, 3);

            short[] pcm_in = new short[960];
            while (isRecorder) {
                //int curShortSize = audioRecord.read(audioBuffer, 0, audioBuffer.length);
                int curShortSize = audioRecord.read(pcm_in, 0, pcm_in.length);
                try {
                    L.e("pcm read length:"+curShortSize+" pcm_in:"+pcm_in.length);
                    if (curShortSize > 0 && curShortSize <= pcm_in.length) {
                        //filePcmBufferedOutputStream.write(pcm_in);//同时保存PCM以对比检查问题
                        byte[] byteArray = new byte[pcm_in.length / 4];//编码后大小减小8倍
                        int encodeSize = opusUtils.encode(createEncoder, pcm_in, 0, byteArray);
                        L.i("pcm encodeSize:" + encodeSize + " byteArray:" + byteArray.length+" pcm_in:"+pcm_in.length);
                        if (encodeSize > 0) {
                            byte[] decodeArray = new byte[encodeSize];
                            System.arraycopy(byteArray, 0, decodeArray, 0, encodeSize);
                            fileOpusBufferedOutputStream.write(decodeArray);//写入OPUS
                        } else {

                        }
                    }
                } catch (Exception e) {
                    L.e("---e:"+e.toString());
                }

//            while (isRecorder) {
//                int curShortSize = audioRecord.read(audioBuffer, 0, audioBuffer.length);
//                try {
//                    L.e("pcm read length:"+curShortSize+" audioBuffer:"+audioBuffer.length);
//                    if (curShortSize > 0 && curShortSize <= audioBuffer.length) {
//                        filePcmBufferedOutputStream.write(audioBuffer);//同时保存PCM以对比检查问题
//                        byte[] byteArray = new byte[audioBuffer.length / 8];//编码后大小减小8倍
//                        int encodeSize = opusUtils.encode(createEncoder, OpusUtils.byteArrayToShortArray(audioBuffer), 0, byteArray);
//                        L.i("pcm encodeSize:" + encodeSize + " byteArray:" + byteArray.length+" audioBuffer:"+audioBuffer.length);
//
//                        if (encodeSize > 0) {
//                            byte[] decodeArray = new byte[encodeSize];
//                            System.arraycopy(byteArray, 0, decodeArray, 0, encodeSize);
//                            fileOpusBufferedOutputStream.write(decodeArray);//写入OPUS
//                        } else {
//
//                        }
//                    }
//                } catch (Exception e) {
//                    L.e("---e:"+e.toString());
//                }


           }
            opusUtils.destroyEncoder(createEncoder);
            audioRecord.stop();
            audioRecord.release();
            filePcmBufferedOutputStream.close();
            filePcmOutputStream.close();
            fileOpusBufferedOutputStream.close();
            fileOutputStream.close();
        }catch (Exception e){
            L.e("---e:"+e.toString());
        }

    }
}
