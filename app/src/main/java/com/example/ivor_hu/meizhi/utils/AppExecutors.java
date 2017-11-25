package com.example.ivor_hu.meizhi.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by ivor on 2017/11/24.
 */

public class AppExecutors {
    private static final int DEFAULT_THREAD_NUM = 3;

    private static AppExecutors sInstance;

    private final Executor normal;
    private final Executor mainThread;

    private AppExecutors(Executor normal, Executor mainThread) {
        this.normal = normal;
        this.mainThread = mainThread;
    }

    private AppExecutors() {
        this(Executors.newFixedThreadPool(DEFAULT_THREAD_NUM),
                new MainThreadExecutor());
    }

    public static AppExecutors getInstance() {
        if (sInstance != null) {
            return sInstance;
        }

        synchronized (AppExecutors.class) {
            if (sInstance == null) {
                sInstance = new AppExecutors();
            }
        }
        return sInstance;
    }

    public Executor normal() {
        return normal;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
