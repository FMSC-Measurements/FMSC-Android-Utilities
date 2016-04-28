package com.usda.fmsc.android.Transitions;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.usda.fmsc.android.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ElevationTransition extends Transition {

    private static final String PROPNAME_ELEVATION = "trans.elevation";

    public ElevationTransition() {
    }

    public ElevationTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        Float elevation = transitionValues.view.getElevation();
        transitionValues.values.put(PROPNAME_ELEVATION, elevation);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }

        Float startVal = (Float) startValues.values.get(PROPNAME_ELEVATION);
        Float endVal = (Float) endValues.values.get(PROPNAME_ELEVATION);
        if (startVal == null || endVal == null || startVal.floatValue() == endVal.floatValue()) {
            return null;
        }

        final View view = endValues.view;
        ValueAnimator a = ValueAnimator.ofFloat(startVal, endVal);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setElevation((float)animation.getAnimatedValue());
            }
        });

        return a;
    }


    public static void finishTransition(Activity activity) {
        // if we transition the status and navigation bar we have to wait till everything is available
        TransitionHelper.fixSharedElementTransitionForStatusAndNavigationBar(activity);
        // set a custom shared element enter transition
        TransitionHelper.setSharedElementEnterTransition(activity, R.transition.detail_activity_shared_element_enter_transition);
    }

    @SafeVarargs
    public static void startTransition(Activity activity, Intent intent,Pair<View, String>... pairs) {
        startTransition(activity, intent, 0, pairs);
    }

    @SafeVarargs
    public static void startTransition(Activity activity, Intent intent, int requestCode, Pair<View, String>... pairs) {
        ArrayList<Pair<View, String>> transitionPairs = new ArrayList<>();

        transitionPairs.addAll(Arrays.asList(pairs));

        // We also want to transition the status and navigation bar barckground. Otherwise they will flicker
        transitionPairs.add(Pair.create(activity.findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
        transitionPairs.add(Pair.create(activity.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));


        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                transitionPairs.toArray(new Pair[transitionPairs.size()])).toBundle();

        if (requestCode == 0)
            activity.startActivity(intent, bundle);
        else
            activity.startActivityForResult(intent, requestCode, bundle);
    }
}