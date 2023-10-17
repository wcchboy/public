package com.igrs.sml.util;

import android.os.Looper;

public class ANRException extends RuntimeException {
    public ANRException() {
        super("应用程序无响应！！");
        Thread mainThread = Looper.getMainLooper().getThread();
        setStackTrace(mainThread.getStackTrace());
    }
}