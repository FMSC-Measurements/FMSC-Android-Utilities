package com.usda.fmsc.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.usda.fmsc.android.widget.listeners.SyncedScrollListener;

import java.util.ArrayList;

public class SyncedScrollView extends ScrollView implements SyncedScrollListener {
    private final ArrayList<SyncedScrollListener> listeners;

    public SyncedScrollView(Context context) {
        this(context, null, 0);
    }

    public SyncedScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SyncedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        listeners = new ArrayList<>();
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        for (SyncedScrollListener listener : listeners) {
            listener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    @Override
    public void onScrollChanged(SyncedScrollView view, int l, int t, int oldl, int oldt) {

        if (view != this)
            super.scrollTo(l, t);
    }

    public void addScrollListener(SyncedScrollListener listener) {
        listeners.add(listener);
    }

    public void removeScrollListener(SyncedScrollListener listener) {
        listeners.remove(listener);
    }

}
