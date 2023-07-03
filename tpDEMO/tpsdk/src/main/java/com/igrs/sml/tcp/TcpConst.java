package com.igrs.sml.tcp;

import android.media.MediaFormat;

public class TcpConst {
    public static final String TAG = "igrs_tcp";
    public static final byte TYPY_HEARTBEAT = 0;//心跳
    public static final byte TYPY_SYS = 0x10;//16 sys命令
    public static final byte TYPY_CMD = 0x11;//17 cmd命令
    public static final byte TYPY_AUDIO = 0x22;//34 音频数据
    public static final byte TYPY_VIDEO = 0x33;//51 视频数据
    public static final byte TYPY_TOUCH = 0x44;//68 touch
    public static final byte TYPY_KEYBOARD = 0x55;//68 keyboard
    public static final byte TYPY_MOUSE = 0x66;//mouse

    public static final boolean h264_has_time = true;//


    public static final int tcp_time_out = 20_000;//

   public static final String mime_type = MediaFormat.MIMETYPE_VIDEO_AVC;
    //public static final String mime_type = MediaFormat.MIMETYPE_VIDEO_HEVC;
}
