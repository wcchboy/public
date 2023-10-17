package com.igrs.sml;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.RequiresApi;

import com.igrs.sml.callback.DecoderCallback;
import com.igrs.sml.tcp.TcpConst;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;

import java.nio.ByteBuffer;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PlayerView extends PlayerView_texture {

    public PlayerView(Context context) {
        super(context);
    }
    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
