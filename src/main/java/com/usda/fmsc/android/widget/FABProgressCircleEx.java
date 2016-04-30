package com.usda.fmsc.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.*;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.usda.fmsc.android.AndroidUtils;
import com.usda.fmsc.android.R;
import com.usda.fmsc.android.widget.drawables.FABProgressArcDrawable;
import com.usda.fmsc.android.widget.drawables.FABProgressArcView;


@CoordinatorLayout.DefaultBehavior(FABProgressCircleEx.Behavior.class)
public class FABProgressCircleEx extends FrameLayout implements FABProgressArcDrawable.ArcListener {
    private final int SIZE_NORMAL = 1;
    private final int SIZE_MINI = 2;

    private int arcColor;
    private int arcWidth;
    private int circleSize;
    private boolean roundedStroke;

    private boolean viewsAdded;
    private FABProgressArcView progressArc;
    private FABProgressListener listener;

    public FABProgressCircleEx(Context context) {
        super(context);
        init(null);
    }

    public FABProgressCircleEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FABProgressCircleEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FABProgressCircleEx(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setupInitialAttributes(attrs);
    }

    private void setupInitialAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attrArray = getAttributes(attrs);
            try {
                arcColor = attrArray.getColor(R.styleable.FABProgressCircleEx_arcColor, AndroidUtils.UI.getColor(getContext(), R.color.primaryLighter));
                arcWidth = attrArray.getDimensionPixelSize(R.styleable.FABProgressCircleEx_arcWidth, getResources().getDimensionPixelSize(R.dimen.progress_arc_stroke_width));
                circleSize = attrArray.getInt(R.styleable.FABProgressCircleEx_circleSize, 1);
                roundedStroke = attrArray.getBoolean(R.styleable.FABProgressCircleEx_roundedStroke, false);
            } finally {
                attrArray.recycle();
            }
        }
    }

    private TypedArray getAttributes(AttributeSet attrs) {
        return getContext().obtainStyledAttributes(attrs, R.styleable.FABProgressCircleEx, 0, 0);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkChildCount();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!viewsAdded) {
            addArcView();
            viewsAdded = true;
        }
    }

    /**
     * We need to draw a new view with the arc over the FAB, to be able to hide the fab shadow
     * (if it exists).
     */
    private void addArcView() {
        setClipChildren(false);
        progressArc = new FABProgressArcView(getContext(), arcColor, arcWidth, roundedStroke);
        progressArc.setInternalListener(this);
        addView(progressArc,
                new FrameLayout.LayoutParams(getFabDimension() + arcWidth, getFabDimension() + arcWidth,
                        Gravity.CENTER));
    }

    /**
     * FABProgressCircle will get its dimensions depending on its child dimensions. It will be easier
     * to force proper graphic standards for the button if we can get sure that only one child is
     * present. Every FAB library around has a single root layout, so it should not be an issue.
     */
    private void checkChildCount() {
        if (getChildCount() != 1) {
            throw new IllegalStateException("Invalid child count");
        }
    }

    public void attachListener(FABProgressListener listener) {
        this.listener = listener;
    }

    public void show() {
        progressArc.show();
    }

    /**
     * Method exposed to allow the user to hide the animation if something went wrong (like an error
     * in the async task running.
     */
    public void hide() {
        progressArc.stop();
    }

    public void beginFinalAnimation() {
        progressArc.requestCompleteAnimation();
    }

    @Override
    public void onArcAnimationComplete() {
        hide();

        if (listener != null) {
            listener.onFABProgressAnimationEnd();
        }
    }

    private int getFabDimension() {
        if (circleSize == SIZE_NORMAL) {
            return getResources().getDimensionPixelSize(R.dimen.fab_size_normal);
        } else {
            return getResources().getDimensionPixelSize(R.dimen.fab_size_mini);
        }
    }

    public interface FABProgressListener {
        void onFABProgressAnimationEnd();
    }


    private FloatingActionButton button;

    public FloatingActionButton getButton() {
        if (button == null) {
            button = (FloatingActionButton) getChildAt(0);
        }

        return button;
    }


    public static class Behavior extends CoordinatorLayout.Behavior<FABProgressCircleEx> {
        private final boolean SNACKBAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, FABProgressCircleEx child, View dependency) {
            return SNACKBAR_BEHAVIOR_ENABLED && dependency instanceof Snackbar.SnackbarLayout;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, FABProgressCircleEx child, View dependency) {
            float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
            child.setTranslationY(translationY);
            return true;
        }
    }
}