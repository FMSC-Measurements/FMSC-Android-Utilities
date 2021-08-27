package com.usda.fmsc.android.widget.drawables;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

public final class FABProgressArcView extends ProgressBar {
    public static final String NAME_SPACE = "ns";
    public static final String ARC_COLOR = "arcColor";
    public static final String ARC_WIDTH = "arcWidth";
    public static final String ROUNDED_STROKE = "roundedStroke";

    public static final int SHOW_SCALE_ANIM_DELAY = 150;

    private FABProgressArcDrawable.ArcListener internalListener;
    private final int arcColor;
    private final int arcWidth;
    private final boolean roundedStroke;


    public FABProgressArcView(Context context, AttributeSet attributeSet) {
        super(context);
        this.arcColor = attributeSet.getAttributeIntValue(NAME_SPACE, ARC_COLOR, 2);
        this.arcWidth = attributeSet.getAttributeIntValue(NAME_SPACE, ARC_WIDTH, 1);
        this.roundedStroke = attributeSet.getAttributeBooleanValue(NAME_SPACE, ROUNDED_STROKE, false);
        init(arcColor, arcWidth, roundedStroke);
    }

    public FABProgressArcView(Context context, int arcColor, int arcWidth, boolean roundedStroke) {
        super(context);
        this.arcColor = arcColor;
        this.arcWidth = arcWidth;
        this.roundedStroke = roundedStroke;
        init(arcColor, arcWidth, roundedStroke);
    }

    public void init(int arcColor, int arcWidth, boolean roundedStroke) {
        setupInitialAlpha();
        FABProgressArcDrawable arcDrawable = new FABProgressArcDrawable(arcWidth, arcColor, roundedStroke);
        setIndeterminateDrawable(arcDrawable);
    }

    private void setupInitialAlpha() {
        setAlpha(0);
    }

    public void setInternalListener(FABProgressArcDrawable.ArcListener internalListener) {
        this.internalListener = internalListener;
    }

    public void show() {
        postDelayed(() -> {
            setAlpha(1);
            getDrawable().reset();
        }, SHOW_SCALE_ANIM_DELAY);
    }

    public void stop() {
        getDrawable().stop();

        //not smooth, fix
        ObjectAnimator.ofFloat(this, "alpha", 1, 0).setDuration(100).start();
    }

    public void reset() {
        getDrawable().reset();

        ValueAnimator arcScaleX = ObjectAnimator.ofFloat(this, "scaleX", 1);
        ValueAnimator arcScaleY = ObjectAnimator.ofFloat(this, "scaleY", 1);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(0).setInterpolator(new DecelerateInterpolator());
        set.playTogether(arcScaleX, arcScaleY);
        set.start();
    }

    public void requestCompleteAnimation() {
        postDelayed(() -> getDrawable().requestCompleteAnimation(internalListener), SHOW_SCALE_ANIM_DELAY);
    }

    private FABProgressArcDrawable getDrawable() {
        Drawable ret = getIndeterminateDrawable();
        return (FABProgressArcDrawable) ret;
    }

    public AnimatorSet getScaleDownAnimator() {
        float scalePercent = (float) getWidth() / (getWidth() + arcWidth + 5);

        ValueAnimator arcScaleX = ObjectAnimator.ofFloat(this, "scaleX", scalePercent);
        ValueAnimator arcScaleY = ObjectAnimator.ofFloat(this, "scaleY", scalePercent);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(150).setInterpolator(new DecelerateInterpolator());
        set.playTogether(arcScaleX, arcScaleY);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setupInitialAlpha();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        return set;
    }
}
