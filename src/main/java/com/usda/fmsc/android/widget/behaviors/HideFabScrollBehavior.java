package com.usda.fmsc.android.widget.behaviors;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public class HideFabScrollBehavior extends FloatingActionButton.Behavior {

    public HideFabScrollBehavior(Context context, AttributeSet attributeSet) {
        super();
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if(dyConsumed > 0 && child.getVisibility() == View.VISIBLE){
            child.hide();
        } else if(dyConsumed < 0 && child.getVisibility() == View.GONE){
            child.show();
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}
