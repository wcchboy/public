package com.igrs.audio;

import com.igrs.sml.util.L;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class OpusUtils {

    public static int DEFAULT_AUDIO_SAMPLE_RATE = 48000;
    public static int DEFAULT_OPUS_CHANNEL = 2;//默认采用单声道

//    public static int DEFAULT_AUDIO_SAMPLE_RATE = 16000;
//    public static int DEFAULT_OPUS_CHANNEL = 1;//默认采用单声道

    static  {
        System.loadLibrary("opusJni");
    }
    private static OpusUtils instance = null;
    private OpusUtils() {
        L.e("OpusUtils->new->this:  " + hashCode());
    }
    public static OpusUtils getInstance() {
        if (instance == null) {
            synchronized (OpusUtils.class) {
                if (instance == null) {
                    instance = new OpusUtils();
                }
            }
        }
        return instance;
    }


    public native long  createEncoder(int sampleRateInHz, int channelConfig, int complexity);
    public native long  createDecoder(int sampleRateInHz, int channelConfig);
    public native int  encode(long handle, short[] lin,int offset,byte[] encoded);
    public native int decode(long handle, byte[] encoded, short[] lin);

    public native void  destroyEncoder(long handle);
    public native void  destroyDecoder(long handle);


    public static  short[] byteArrayToShortArray(byte[] byteArray) {
        short[] shortArray = new short[byteArray.length / 2];
        ByteBuffer.wrap(byteArray).order(ByteOrder.nativeOrder()).asShortBuffer().get(shortArray);
        return shortArray;
    }

    public byte[] toByteArray(short[] src) {
        int count = src.length;
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2] = (byte) (src[i] >> 8);
            dest[i * 2 + 1] = (byte) (src[i] >> 0);
        }
        return dest;
    }
}
