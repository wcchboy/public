package com.wcch.android.utils;

import android.content.Context;

import com.wcch.android.App;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyException implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler exception = Thread.getDefaultUncaughtExceptionHandler();
    private File t;

    public MyException(File file) {
        File file2 = new File(RouteInfo.ROOT_BUG_PATH);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        this.t = new File(file2, file.getName());
    }

    public void uncaughtException(Thread thread, Throwable th) {
            String obj = System.err.toString();
            String str = th.getCause() + "";
            String message = th.getMessage();
            String localizedMessage = th.getLocalizedMessage();
            StackTraceElement[] stackTrace = th.getStackTrace();
            String str2 = null;
            for (int i = 0; i < stackTrace.length; i++) {
                str2 = str2 + "\r\n-----------------------  the " + i + " element  ----\r\ntoString: " + stackTrace[i].toString() + "\r\nClassName: " + stackTrace[i].getClassName() + "\r\nFileName: " + stackTrace[i].getFileName() + "\r\nLineNumber: " + stackTrace[i].getLineNumber() + "\r\nMethodName: " + stackTrace[i].getMethodName();
            }
        try
        {
            FileHelper.bugFileOutput(App.Companion.getInstance(), this.t, "@uncaughtException, thread: " + thread + " @name: " + thread.getName() + " @id: " + thread.getId() + "@exception:" + th.toString() + "@error:" + obj + "@cause:" + str + "@message:" + message + "@Localizemessage:" + localizedMessage + "------@trace:" + str2 + "\r\n-----@suppress:");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.exception;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th);
        }
    }



    public static void outputException(Context context, Exception exc) throws IOException
    {
            StackTraceElement[] stackTrace = exc.getStackTrace();
            String exc2 = exc.toString();
            for (int i = 0; i < stackTrace.length; i++) {
                exc2 = exc2 + "\r\n-----------------------  the " + i + " element  ----\r\ntoString: " + stackTrace[i].toString() + "\r\nClassName: " + stackTrace[i].getClassName() + "\r\nFileName: " + stackTrace[i].getFileName() + "\r\nLineNumber: " + stackTrace[i].getLineNumber() + "\r\nMethodName: " + stackTrace[i].getMethodName();
            }
            File file = new File("Bug" + new SimpleDateFormat(DateUtils.ymd).format(new Date()) + ".txt");
            File file2 = new File(RouteInfo.ROOT_BUG_PATH);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            FileHelper.bugFileOutput(context, new File(file2, file.getName()), "------@trace:" + exc2 + "\r\n-----");
    }
}
