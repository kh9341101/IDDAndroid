package edu.berkeley.capstoneproject.capstoneprojectandroid.services;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Alex on 26/10/2017.
 */

public class GattOperationQueue extends Thread {

    private static final String TAG = GattOperationQueue.class.getSimpleName();

    private static final int THREAD_SLEEP = 100;

    private final Queue<Runnable> mQueue = new LinkedBlockingQueue<>();
    private AtomicBoolean mCancel = new AtomicBoolean(false);

    @Override
    public void run() {
        try {
            while(true) {
                while (mQueue.size() == 0) {
                    synchronized (this) {
                        wait();
                    }
                    if (mCancel.get()) {
                        return;
                    }
                }

                Log.d(TAG, "Executing new runnable");
                Runnable r = mQueue.remove();
                r.run();

                Thread.sleep(THREAD_SLEEP);
            }
        }
        catch (InterruptedException e) {
            Log.w(TAG, "Received interrupt");
        }
    }

    public synchronized void enqueue(final Runnable runnable) {
        Log.d(TAG, "Adding new runnable");
        mQueue.add(runnable);
        notify();
    }

    public synchronized void cancel() {
        Log.d(TAG, "Canceling...");
        mCancel.set(true);
        notify();
    }
}
