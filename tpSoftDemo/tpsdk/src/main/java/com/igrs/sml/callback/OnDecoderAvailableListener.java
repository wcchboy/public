package com.igrs.sml.callback;

import android.graphics.Bitmap;

public interface OnDecoderAvailableListener {
    void setOnFrameAvailableListener(int index, Bitmap bitmap);
}
