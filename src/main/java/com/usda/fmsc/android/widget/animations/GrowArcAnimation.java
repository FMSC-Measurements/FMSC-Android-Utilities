package com.usda.fmsc.android.widget.animations;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;

import com.usda.fmsc.android.widget.drawables.FABProgressArcDrawable;

public class GrowArcAnimation implements FABProgressArcDrawable.ArcAnimation {
    private ValueAnimator growAnim;

    GrowArcAnimation(ValueAnimator.AnimatorUpdateListener updateListener, Animator.AnimatorListener listener) {
        growAnim = ValueAnimator.ofFloat(ArcAnimationFactory.MINIMUM_SWEEP_ANGLE, ArcAnimationFactory.MAXIMUM_SWEEP_ANGLE);
        growAnim.setInterpolator(new DecelerateInterpolator());
        growAnim.setDuration(ArcAnimationFactory.SWEEP_ANIM_DURATION);
        growAnim.addUpdateListener(updateListener);
        growAnim.addListener(listener);
    }

    @Override
    public ValueAnimator getAnimator() {
        return growAnim;
    }
}
