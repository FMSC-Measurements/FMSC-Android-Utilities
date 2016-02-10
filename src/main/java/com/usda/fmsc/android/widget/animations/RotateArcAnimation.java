package com.usda.fmsc.android.widget.animations;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import com.usda.fmsc.android.widget.drawables.FABProgressArcDrawable;

final class RotateArcAnimation implements FABProgressArcDrawable.ArcAnimation {

  private ValueAnimator rotateAnim;

  RotateArcAnimation(ValueAnimator.AnimatorUpdateListener updateListener) {
    rotateAnim = ValueAnimator.ofFloat(0f, 360f);
    rotateAnim.setInterpolator(new LinearInterpolator());
    rotateAnim.setDuration(ArcAnimationFactory.ROTATE_ANIMATOR_DURATION);
    rotateAnim.addUpdateListener(updateListener);
    rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
    rotateAnim.setRepeatMode(ValueAnimator.RESTART);
  }

  @Override public ValueAnimator getAnimator() {
    return rotateAnim;
  }
}
