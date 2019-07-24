package com.usda.fmsc.android.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class ViewAnimator {

    public static void expandView(final View view) {
        expandView(view, null);
    }

    public static void expandView(final View view, final Animator.AnimatorListener listener) {
        //set Visible
        view.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(view, 0, view.getMeasuredHeight());

        if (listener != null) {
            mAnimator.addListener(listener);
        }

        mAnimator.start();
    }

    public static  void collapseView(final View view) {
        collapseView(view, null);
    }

    public static  void collapseView(final View view, final Animator.AnimatorListener listener) {
        int finalHeight = view.getHeight();

        ValueAnimator mAnimator = slideAnimator(view, finalHeight, 0);

        mAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                view.setVisibility(View.GONE);
            }
        });

        if (listener != null) {
            mAnimator.addListener(listener);
        }

        mAnimator.start();
    }

    private static ValueAnimator slideAnimator(final View view, int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(valueAnimator -> {
            //Update Height
            int value = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = value;
            view.setLayoutParams(layoutParams);
        });

        return animator;
    }

    public static class SimpleAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }

        @Override
        public void onAnimationStart(Animator animator) {

        }
    }
}
