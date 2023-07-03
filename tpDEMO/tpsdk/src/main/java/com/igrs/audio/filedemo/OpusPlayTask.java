package com.igrs.audio.filedemo;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.text.TextUtils;

import com.igrs.audio.OpusUtils;
import com.igrs.sml.util.L;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OpusPlayTask extends DecodeOpusPresenter implements Runnable {

    private AudioTrack audioTrack;
    public OnOpusPlayListener onOpusPlayListener = null;
    boolean isPlay = false;
    private FileOutputStream filePcmOutputStream = null;
    private BufferedOutputStream filePcmBufferedOutputStream = null;
    AudioManager audioManager;

    private String recorderDecodedPcmFilePath;
    private String recorderFilePath;

    private boolean isPCM;

    public OpusPlayTask(AudioManager audioManager, String recorderPcmFilePath, String recorderDecodedPcmFilePath, boolean isPCM) {
        this.audioManager = audioManager;
        this.recorderFilePath = recorderPcmFilePath;
        this.recorderDecodedPcmFilePath = recorderDecodedPcmFilePath;
        this.isPCM = isPCM;

        int channel = OpusUtils.DEFAULT_OPUS_CHANNEL==1?AudioFormat.CHANNEL_OUT_MONO:AudioFormat.CHANNEL_OUT_STEREO;

        int bufferSize = AudioTrack.getMinBufferSize(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, channel, AudioFormat.ENCODING_PCM_16BIT);

         AudioAttributes audioAttributes = new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build();
         AudioFormat audioFormat = new AudioFormat.Builder()
                 .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                 .setSampleRate(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE)
                 .setChannelMask(channel)
                 .build();

        L.i("OpusPlayTask channel:"+channel+" bufferSize:"+bufferSize);

        int sessionId = audioManager.generateAudioSessionId();
        audioTrack = new AudioTrack(audioAttributes, audioFormat, bufferSize, AudioTrack.MODE_STREAM, sessionId);
        if (!TextUtils.isEmpty(recorderDecodedPcmFilePath) && !isPCM) {//保存解码后的PCM文件以检查问题
            File filePcm = new File(recorderDecodedPcmFilePath);
            File fileDir = new File(filePcm.getParent());
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            if (filePcm.exists()) {
                filePcm.delete();
            }
            try {
                filePcm.createNewFile();
                filePcmOutputStream = new FileOutputStream(filePcm, true);
                filePcmBufferedOutputStream = new BufferedOutputStream(filePcmOutputStream);
            } catch (Exception e) {
            }
        }
    }


    public void stop() {
        if (isPlay) {
            isPlay = false;
            cancelDecode();
            audioTrack.stop();
            audioTrack.release();
            if (filePcmBufferedOutputStream != null) {
                try {
                    filePcmBufferedOutputStream.close();
                } catch (Exception e) {
                }

                filePcmBufferedOutputStream = null;
            }
            if (filePcmOutputStream != null) {
                try {
                    filePcmOutputStream.close();
                } catch (Exception e) {
                }

                filePcmOutputStream = null;
            }
        }
    }


    @Override
    public void opusDecode(short[] formatShortArray) {
        audioTrack.write(formatShortArray, 0, formatShortArray.length);
        if (filePcmBufferedOutputStream != null) {//保存解码后的PCM文件以检查问题
            try {
                filePcmBufferedOutputStream.write(OpusUtils.getInstance().toByteArray(formatShortArray));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void opusDecodeFinish() {

    }

    @Override
    public void run() {
        try{
            audioTrack.play();
            isPlay = true;
            L.e("OpusPlayTask ->run->isPCM:"+isPCM+" recorderFilePath:"+recorderFilePath);
            if (!isPCM) {
                decodeOpusFile(recorderFilePath, false);
            } else {
                readFile(recorderFilePath, false);
            }
            if (isPlay) {
                stop();
                if (onOpusPlayListener != null) {
                    onOpusPlayListener.onCompere();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            L.e("OpusPlayTask ->run e :"+e.toString());
        }

    }

    public interface OnOpusPlayListener {
        public void onCompere();
    }
}
