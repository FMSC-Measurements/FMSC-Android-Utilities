package com.usda.fmsc.android.widget.layoutmanagers;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;

public class LinearLayoutManagerWithSmoothScroller extends LinearLayoutManager {
    private boolean scrollingEnabled = true;

    public LinearLayoutManagerWithSmoothScroller(Context context) {
        super(context, VERTICAL, false);
    }

    public LinearLayoutManagerWithSmoothScroller(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        RecyclerView.SmoothScroller smoothScroller = new SnappedSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    @Override
    public void setRecycleChildrenOnDetach(boolean recycleChildrenOnDetach) {
        super.setRecycleChildrenOnDetach(recycleChildrenOnDetach);
    }

    @Override
    public boolean canScrollHorizontally() {
        return this.scrollingEnabled && super.canScrollHorizontally();
    }

    @Override
    public boolean canScrollVertically() {
        return this.scrollingEnabled && super.canScrollVertically();
    }

    public void setScrollingEnabled(boolean enabled) {
        this.scrollingEnabled = enabled;
    }

    private class SnappedSmoothScroller extends LinearSmoothScroller {

        public SnappedSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return LinearLayoutManagerWithSmoothScroller.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return SNAP_TO_START;
        }
    }
}