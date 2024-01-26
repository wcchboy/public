package com.igrs.sml.util;

import android.os.Environment;
import android.util.Log;

import com.igrs.tpsdk.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log统一管理类
 * 
 * @author qf
 * 
 */
public class L {
	private L() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}
	// 是否需要打印bug，可以在application的onCreate函数里面初始化
	//public static boolean isDebug = BuildConfig.DEBUG;
	public static boolean isDebug = true;
	private static final String TAG = "qf";
	private static final String fileName = "tp.txt";


	public static void setIsDebug(boolean isDebug){
		 L.isDebug = isDebug;
	}

	public static void del() {
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					fileName);
			if (file.isFile() && file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
		}
	}

	static public String getMethodName() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		StackTraceElement e = stacktrace[4];
		String methodName = e.getMethodName();
		try{
			return new Exception().getStackTrace()[2].getClassName()+"->"+methodName;
		}catch (Exception e1){
			return methodName;
		}
	}

	// 下面四个是默认tag的函数
	public static void i(String msg) {
		if (isDebug)
			Log.i(TAG, msg);
	}

	public static void d(String msg) {
		if (isDebug)
			Log.d(TAG, msg);
	}

	public static void e(String msg) {
		if (isDebug)
			Log.e(TAG, msg);
	}

	public static void v(String msg) {
		if (isDebug)
			Log.v(TAG, msg);
	}

	public static void w(String msg) {
		if (isDebug) {
			Log.w(TAG, msg);

			try {
				try {
					File crash = new File(Environment.getExternalStorageDirectory(),"crash");
					if (!crash.exists()) {
						crash.mkdir();
					}
				} catch (Exception e) {
					Log.i("error:", e+"");
				}

				File file = new File(Environment.getExternalStorageDirectory()+"/crash",fileName);
				@SuppressWarnings("resource")
				BufferedWriter bw = new BufferedWriter(new FileWriter(file,
						true));
				String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
				bw.write(date+" "+msg + "\n");
				bw.flush();
			} catch (Exception e) {
			}

		}

	}

	public static void http(String msg) {
		if (isDebug) {
			Log.d(TAG, msg);
			try {

				File file = new File(Environment.getExternalStorageDirectory() + "/crash",
						"igrs_http.txt");
				@SuppressWarnings("resource")
				BufferedWriter bw = new BufferedWriter(new FileWriter(file,
						true));
				String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
				bw.write(date + " " + msg + "\n");
				bw.flush();
				bw.close();
			} catch (Exception e) {
			}
		}

	}
	public static void touch(String msg) {
		if (isDebug) {
			Log.d(TAG, msg);
			try {

				File file = new File(Environment.getExternalStorageDirectory() + "/crash",
						"igrs_touch.txt");
				@SuppressWarnings("resource")
				BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
				String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
				bw.write(date + " " + msg + "\n");
				bw.flush();
				bw.close();
			} catch (Exception e) {
			}
		}

	}

	// 下面是传入自定义tag的函数
	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.e(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (isDebug)
			Log.v(tag, msg);
	}

}