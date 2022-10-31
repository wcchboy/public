package com.wcch.serialport.base;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author RyanWang
 * 2022/10/30, RyanWang, Create file
 * 线程Helper
 */
public class ThreadHelper {

    //private static ThreadHelper sHelper = new ThreadHelper();
    private ExecutorService mExecutor;
    private Handler mHandler;
    private Map<Runnable, Runnable> idleCallback;


    private ThreadHelper() {
        mExecutor = new ThreadPoolExecutor(0,
                Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), ThreadHelper.threadFactory("Settings", true));
        ;
        mHandler = new Handler(Looper.getMainLooper());
        idleCallback = new HashMap<>();
    }


    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return runnable -> {
            Thread result = new Thread(runnable, name);
            result.setDaemon(daemon);
            return result;
        };
    }


    public static ThreadHelper getInstance() {
        return Holder.SINGLE_INSTANCE;
    }

    private static class Holder {
        private static final ThreadHelper SINGLE_INSTANCE = new ThreadHelper();
    }

    public void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return mExecutor.submit(callable);
    }

    public <T> T timeoutExecute(Callable<T> callable, long timeout) throws TimeoutException {
        try {
            return submit(callable).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ExecutorService getExecutor() {
        return mExecutor;
    }

    public void checkWorkThread() {
        if (isMainThread()) {
            throw new IllegalThreadStateException("Method is run in the Main Thread.");
        }
    }

    public boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public void executeInMain(Runnable runnable) {
        mHandler.post(runnable);
    }

    public void executeDelayedInMain(Runnable runnable, long delayMillis) {
        mHandler.postDelayed(runnable, delayMillis);
    }

    public void executeDelayed(Runnable runnable, long delayMillis) {
        Runnable callback = () -> mExecutor.execute(runnable);
        idleCallback.remove(runnable);
        idleCallback.put(runnable, callback);
        executeDelayedInMain(callback, delayMillis);
    }

    public void removeIdleRunnable(Runnable runnable) {
        Runnable callback = idleCallback.remove(runnable);
        if (callback != null) {
            mHandler.removeCallbacks(callback);
        } else {
            mHandler.removeCallbacks(runnable);
        }
    }
}
