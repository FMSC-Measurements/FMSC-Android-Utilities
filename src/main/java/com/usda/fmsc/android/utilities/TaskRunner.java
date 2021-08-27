package com.usda.fmsc.android.utilities;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    private final Handler handler = new Handler(Looper.getMainLooper());

    public <R, P> void executeAsync(Task<P, R> task, P params) {
        executor.execute(() -> {
            final R result;

            try {
                result = task.run(params);

                handler.post(() -> {
                    task.complete(result);
                });
            } catch (Exception e) {
                handler.post(() -> {
                    task.error(e);
                });
            }
        });
    }

    public static abstract class Task<P, R> {
        private boolean _Cancel;
        private Status status = Status.CREATED;

        private R run(P params) {
            status = Status.RUNNING;
            return onBackgroundWork(params);
        }
        protected abstract R onBackgroundWork(P params);

        private void complete(R result) {
            status = Status.COMPLETED;
            onComplete(result);
        }
        protected abstract void onComplete(R result);

        private void error(Exception exception) {
            status = Status.FAILED;
            onError(exception);
        }
        protected abstract void onError(Exception exception);

        public boolean isCancelled() { return _Cancel; }
        public void cancel() { _Cancel = true; status = Status.CANCELLED; }

        public Status getStatus() {
            return status;
        }
    }

    public enum Status {
        CREATED,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}