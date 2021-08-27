package com.usda.fmsc.android.widget.animations;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;

import com.usda.fmsc.android.widget.drawables.FABProgressArcDrawable;

public class ShrinkArcAnimation implements FABProgressArcDrawable.ArcAnimation {
    private final ValueAnimator shrinkAnim;

    ShrinkArcAnimation(ValueAnimator.AnimatorUpdateListener updateListener, Animator.AnimatorListener listener) {
        shrinkAnim = ValueAnimator.ofFloat(ArcAnimationFactory.MAXIMUM_SWEEP_ANGLE, ArcAnimationFactory.MINIMUM_SWEEP_ANGLE);
        shrinkAnim.setInterpolator(new DecelerateInterpolator());
        shrinkAnim.setDuration(ArcAnimationFactory.SWEEP_ANIM_DURATION);
        shrinkAnim.addUpdateListener(updateListener);
        shrinkAnim.addListener(listener);
    }

    @Override
    public ValueAnimator getAnimator() {
        return shrinkAnim;
    }
}
