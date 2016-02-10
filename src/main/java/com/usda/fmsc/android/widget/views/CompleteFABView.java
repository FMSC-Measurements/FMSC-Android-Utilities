package com.usda.fmsc.android.widget.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.usda.fmsc.android.R;

public class CompleteFABView extends FrameLayout {

    private final int RESET_DELAY = 3000;

    private Drawable iconDrawable;
    private int arcColor;
    private Listener listener;
    private boolean viewsAdded;

    public CompleteFABView(Context context, Drawable iconDrawable, int arcColor) {
        super(context);
        this.iconDrawable = iconDrawable;
        this.arcColor = arcColor;
        init();
    }

    public void attachListener(Listener listener) {
        this.listener = listener;
    }

    private void init() {
        inflate(getContext(), R.layout.progress_fab_complete_layout, this);
    }

    private void tintCompleteFabWithArcColor() {
        Drawable background = getResources().getDrawable(R.drawable.oval_complete);
        background.setColorFilter(arcColor, PorterDuff.Mode.SRC_ATOP);
        findViewById(R.id.completeFabRoot).setBackgroundDrawable(background);
    }

    private void setIcon() {
        ImageView iconView = (ImageView) findViewById(R.id.completeFabIcon);
        iconView.setImageDrawable(
                iconDrawable != null ? iconDrawable : getResources().getDrawable(R.drawable.ic_done_white_36dp));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!viewsAdded) {
            setupContentSize();
            tintCompleteFabWithArcColor();
            setIcon();
            viewsAdded = true;
        }
    }

    private void setupContentSize() {
        int contentSize = (int) getResources().getDimension(R.dimen.fab_content_size);
        int mContentPadding = (getChildAt(0).getMeasuredWidth() - contentSize) / 2;
        getChildAt(0).setPadding(mContentPadding, mContentPadding, mContentPadding, mContentPadding);
    }

    public void animate(AnimatorSet progressArcAnimator) {
        animate(progressArcAnimator, false);
    }

    private void animate(AnimatorSet progressArcAnimator, boolean inverse) {
        ValueAnimator completeFabAnim = ObjectAnimator.ofFloat(getChildAt(0), "alpha", inverse ? 0 : 1);
        completeFabAnim.setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator());

        View icon = findViewById(R.id.completeFabIcon);

        ValueAnimator iconScaleAnimX = ObjectAnimator.ofFloat(icon, "scaleX", 0, 1);
        ValueAnimator iconScaleAnimY = ObjectAnimator.ofFloat(icon, "scaleY", 0, 1);

        Interpolator iconAnimInterpolator = new LinearInterpolator();
        iconScaleAnimX.setDuration(250).setInterpolator(iconAnimInterpolator);
        iconScaleAnimY.setDuration(250).setInterpolator(iconAnimInterpolator);

        AnimatorSet animatorSet = new AnimatorSet();
        if (inverse) {
            animatorSet.playTogether(completeFabAnim);
        } else {
            animatorSet.playTogether(completeFabAnim, progressArcAnimator, iconScaleAnimX,
                    iconScaleAnimY);
        }

        animatorSet.addListener(inverse ? getInverseAnimatorListener() : getAnimatorListener());
        if (inverse) {
            animatorSet.setStartDelay(RESET_DELAY);
        }
        animatorSet.start();
    }

    private Animator.AnimatorListener getAnimatorListener() {
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (listener != null) {
                    listener.onCompleteFABAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        };
    }

    private Animator.AnimatorListener getInverseAnimatorListener() {
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        };
    }

    public void reset() {
        animate(null, true);
    }

    /**
     * This view must block every touch event so the user cannot click on fab anymore if this view
     * is visible.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public interface Listener {
        void onCompleteFABAnimationEnd();
    }

}
