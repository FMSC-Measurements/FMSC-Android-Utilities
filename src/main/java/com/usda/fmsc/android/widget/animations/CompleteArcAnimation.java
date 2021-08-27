package com.usda.fmsc.android.widget.animations;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;

import com.usda.fmsc.android.widget.drawables.FABProgressArcDrawable;

public class CompleteArcAnimation implements FABProgressArcDrawable.ArcAnimation {
    private final ValueAnimator completeAnim;

    CompleteArcAnimation(ValueAnimator.AnimatorUpdateListener updateListener, Animator.AnimatorListener listener) {
        completeAnim = ValueAnimator.ofFloat(ArcAnimationFactory.MAXIMUM_SWEEP_ANGLE, ArcAnimationFactory.MINIMUM_SWEEP_ANGLE);
        completeAnim.setInterpolator(new DecelerateInterpolator());
        completeAnim.setDuration(ArcAnimationFactory.COMPLETE_ANIM_DURATION);
        completeAnim.addUpdateListener(updateListener);
        completeAnim.addListener(listener);
    }

    @Override
    public ValueAnimator getAnimator() {
        return completeAnim;
    }
}
