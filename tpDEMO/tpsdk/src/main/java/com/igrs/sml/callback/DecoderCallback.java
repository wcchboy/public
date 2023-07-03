package com.igrs.sml.callback;

import android.graphics.Bitmap;
import android.media.MediaFormat;

import com.igrs.sml.util.L;

public abstract class DecoderCallback {
    public abstract void decoderCallback(int result);
    public abstract void decoderSizeChage(int width, int height, MediaFormat outFormat);
    public void setOnFrameAvailableListener(int index, Bitmap bitmap){
        L.i("DecoderCallback>----------setOnFrameAvailableListener-->index:" + index + " bitmap:" + bitmap );
    }
}
