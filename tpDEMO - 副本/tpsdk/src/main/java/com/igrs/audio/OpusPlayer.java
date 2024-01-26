package com.igrs.audio;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.igrs.sml.util.FileUtils;
import com.igrs.sml.util.L;

import java.util.concurrent.LinkedBlockingQueue;

public class OpusPlayer implements Runnable {
    private int sessionId;
    private AudioTrack audioTrack;
    private AudioManager audioManager;
    boolean isExit;
    private LinkedBlockingQueue<byte[]> mOpusDataQueue = new LinkedBlockingQueue<>();
    private long decoderHandler = 0;

    public OpusPlayer(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public void start(){
        int channel = OpusUtils.DEFAULT_OPUS_CHANNEL==1?AudioFormat.CHANNEL_OUT_MONO:AudioFormat.CHANNEL_OUT_STEREO;

        decoderHandler = OpusUtils.getInstance().createDecoder(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, OpusUtils.DEFAULT_OPUS_CHANNEL);

        int bufferSize = AudioTrack.getMinBufferSize(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE, channel, AudioFormat.ENCODING_PCM_16BIT);

        AudioAttributes audioAttributes = new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build();
        AudioFormat audioFormat = new AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(OpusUtils.DEFAULT_AUDIO_SAMPLE_RATE)
                .setChannelMask(channel)
                .build();
        sessionId = audioManager.generateAudioSessionId();
        audioTrack = new AudioTrack(audioAttributes, audioFormat, bufferSize, AudioTrack.MODE_STREAM, sessionId);
        new Thread(this).start();
    }
    public void putData(byte[] data) {
        try{
            mOpusDataQueue.put(data);
        }catch (Exception e){
            L.i(" putData ->mOpusDataQueue:"+mOpusDataQueue.size()+"\n e:"+e.toString());
        }
        if(mOpusDataQueue.size()>30 && System.currentTimeMillis()%3_000==0){
            L.i(" putData ->mOpusDataQueue:"+mOpusDataQueue.size());
            mOpusDataQueue.clear();
        }
    }

    public void stop() {
        if (!isExit) {
            isExit = true;
            audioTrack.stop();
            audioTrack.release();
        }
    }

    @Override
    public void run() {
        try {
            audioTrack.play();
            while (!isExit) {
                byte[] data = mOpusDataQueue.take();
                //FileUtils.writeFileToSDCard(data,"h264","test.opus",true,true);
                short[] decodeBufferArray = new short[960];
                int size = OpusUtils.getInstance().decode(decoderHandler, data, decodeBufferArray);
                if (size > 0) {
                    short[] decodeArray = new short[size*OpusUtils.DEFAULT_OPUS_CHANNEL];
                    System.arraycopy(decodeBufferArray, 0, decodeArray, 0, size*OpusUtils.DEFAULT_OPUS_CHANNEL);
                    audioTrack.write(decodeArray, 0, decodeArray.length);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.e("OpusPlayTask ->run e :" + e.toString());
        }
        stop();
    }
}
