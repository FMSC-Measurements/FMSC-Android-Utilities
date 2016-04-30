package com.usda.fmsc.android.utilities;

import android.os.Handler;

public class PostDelayHandler {
    private Runnable runnable;
    private int delay;
    private Handler handler;


    public PostDelayHandler(int delayMilliseconds) {
        this(delayMilliseconds, null);
    }

    public PostDelayHandler(int delayMilliseconds, Runnable runnable) {
        this.delay = delayMilliseconds;

        handler = new Handler();

        if (runnable != null) {
            post(runnable);
        }
    }

    public void post(Runnable runnable) {
        if (this.runnable != null) {
            handler.removeCallbacksAndMessages(null);
        }

        this.runnable = runnable;
        handler.postDelayed(this.runnable, delay);
    }

    public void setDelay(int delayMilliseconds) {
        this.delay = delayMilliseconds;
    }
}
