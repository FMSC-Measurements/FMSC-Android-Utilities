package com.usda.fmsc.android.Transitions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewTreeObserver;

public class TransitionHelper {

    public static void fixSharedElementTransitionForStatusAndNavigationBar(final Activity activity) {
        final View decor = activity.getWindow().getDecorView();
        activity.postponeEnterTransition();
        decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onPreDraw() {
                decor.getViewTreeObserver().removeOnPreDrawListener(this);
                activity.startPostponedEnterTransition();
                return true;
            }
        });
    }

    public static void setSharedElementEnterTransition(final Activity activity, int transition) {
        activity.getWindow().setSharedElementEnterTransition(TransitionInflater.from(activity).inflateTransition(transition));
    }
}
