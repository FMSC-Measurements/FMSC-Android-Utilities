package com.usda.fmsc.android.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.usda.fmsc.android.AndroidUtils;
import com.usda.fmsc.android.R;
import com.usda.fmsc.android.listeners.DeclaredOnClickListener;
import com.usda.fmsc.android.widget.drawables.CheckMarkProgressDrawable;
import com.usda.fmsc.android.widget.drawables.IProgressDrawable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;

public class MultiStateTouchCheckBox extends View {
    private static final int DEFAULT_CHECKED_COLOR = Color.RED;
    private static final int DEFAULT_UNCHECKED_COLOR = Color.GRAY;
    private static final int DEFAULT_PARTIAL_CHECKED_COLOR = Color.BLUE;
    private static final int DEFAULT_CHECKMARK_COLOR = Color.WHITE;


    private Paint mCirclePaint, mCorrectPaint;
    private int radius, size, cx, cy;
    private float checkMarkProgress, progressValue = 1;
    private CheckedState checkedState = CheckedState.NotChecked;
    private boolean isChecked, isMultiState = false, ignoreEvent;
    private int animDurtion = 150;

    private int checkedColor = DEFAULT_CHECKED_COLOR;
    private int unCheckColor = DEFAULT_UNCHECKED_COLOR;
    private int partialColor = DEFAULT_PARTIAL_CHECKED_COLOR;
    private int checkMarkColor = DEFAULT_CHECKMARK_COLOR;
    private int drawToColor, drawFromColor;

    private IProgressDrawable drawable;

    private OnCheckedStateChangeListener listener;
    private OnClickListener onClickListener;


    public MultiStateTouchCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiStateTouchCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public MultiStateTouchCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        drawable = new CheckMarkProgressDrawable();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiStateTouchCheckBox);

        String handlerName;

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);


            if (attr == R.styleable.MultiStateTouchCheckBox_checkedColor) {
                checkedColor = a.getColor(attr, DEFAULT_CHECKED_COLOR);

            } else if (attr == R.styleable.MultiStateTouchCheckBox_partialCheckedColor) {
                partialColor = a.getColor(attr, DEFAULT_PARTIAL_CHECKED_COLOR);

            } else if (attr == R.styleable.MultiStateTouchCheckBox_uncheckedColor) {
                unCheckColor = a.getColor(attr, DEFAULT_UNCHECKED_COLOR);

            } else if (attr == R.styleable.MultiStateTouchCheckBox_checkMarkColor) {
                checkMarkColor = a.getColor(attr, DEFAULT_CHECKMARK_COLOR);

            } else if (attr == R.styleable.MultiStateTouchCheckBox_multiState) {
                isMultiState = a.getBoolean(attr, false);

            } else if (attr == R.styleable.MultiStateTouchCheckBox_duration) {
                animDurtion = a.getInteger(attr, 150);

            } else if (attr == R.styleable.MultiStateTouchCheckBox_onClick) {
                handlerName = a.getString(attr);
                if (handlerName != null) {
                    onClickListener = new DeclaredOnClickListener(this, handlerName);
                }

            } else if (attr == R.styleable.MultiStateTouchCheckBox_onCheckStateChange) {
                handlerName = a.getString(attr);
                if (handlerName != null) {
                    listener = new DeclaredOnCheckedStateChangeListener(this, handlerName);
                }

            }
        }
        a.recycle();

        drawToColor = drawFromColor = unCheckColor;

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(checkedColor);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mCorrectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCorrectPaint.setColor(checkMarkColor);
        mCorrectPaint.setDither(true);
        mCorrectPaint.setStyle(Paint.Style.STROKE);
        mCorrectPaint.setStrokeWidth(AndroidUtils.Convert.dpToPx(context, 2));
        mCorrectPaint.setStrokeJoin(Paint.Join.MITER);
        mCorrectPaint.setStrokeCap(Paint.Cap.SQUARE);
        mCorrectPaint.setPathEffect(new android.graphics.CornerPathEffect(2));
        mCorrectPaint.setAntiAlias(true);

        setOnClickListener(v -> {
            if (isMultiState) {
                switch (checkedState) {
                    case NotChecked:
                        showPartial();
                        break;
                    case PartialChecked:
                        leavePartial(CheckedState.Checked);
                        break;
                    case Checked:
                        hideCheckMark(CheckedState.NotChecked);
                        break;
                }
            } else {
                if (isChecked) {
                    if (checkedState == CheckedState.Checked) {
                        hideCheckMark(CheckedState.NotChecked);
                    } else {
                        leavePartial(CheckedState.Checked);
                    }
                } else {
                    if (checkedState == CheckedState.PartialChecked) {
                        leavePartial(checkedState);
                    } else {
                        showCircle();
                    }
                }
            }

            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        });

        setOnLongClickListener(v -> {
            CharSequence cs = getContentDescription();

            if (cs != null && cs.length() > 0) {
                Toast.makeText(getContext(), cs, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }


    public void setCheckedState(CheckedState checkedState) {
        if (this.checkedState != checkedState) {
            ignoreEvent = false;

            switch (checkedState) {
                case NotChecked:
                    if (this.checkedState == CheckedState.PartialChecked) {
                        leavePartial(checkedState);
                    } else {
                        hideCheckMark(checkedState);
                    }
                    break;
                case Checked:
                    if (this.checkedState == CheckedState.PartialChecked) {
                        leavePartial(checkedState);
                    } else {
                        showCircle();
                    }
                    break;
                case PartialChecked:
                    if (this.checkedState == CheckedState.Checked) {
                        hideCheckMark(CheckedState.PartialChecked);
                    } else {
                        showPartial();
                    }
                    break;
            }
        }
    }

    public void setCheckedStateNoEvent(CheckedState checkedState) {
        if (this.checkedState != checkedState) {
            ignoreEvent = true;

            switch (checkedState) {
                case NotChecked:
                    if (this.checkedState == CheckedState.PartialChecked) {
                        leavePartial(checkedState);
                    } else {
                        hideCheckMark(checkedState);
                    }
                    break;
                case Checked:
                    if (this.checkedState == CheckedState.PartialChecked) {
                        leavePartial(checkedState);
                    } else {
                        showCircle();
                    }
                    break;
                case PartialChecked:
                    if (this.checkedState == CheckedState.Checked) {
                        hideCheckMark(CheckedState.PartialChecked);
                    } else {
                        showPartial();
                    }
                    break;
            }
        }
    }

    public boolean isChecked(){
        return isChecked;
    }

    public CheckedState getCheckedState() {
        return checkedState;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        size = Math.min(w - getPaddingLeft() - getPaddingRight(), h - getPaddingBottom() - getPaddingTop());

        cx = w / 2;
        cy = h / 2;

        switch (checkedState) {
            case NotChecked:
                radius = (int) (size * 0.125f);
                break;
            case Checked:
                radius = (int) (size * 0.37f + size * 0.125f);
                break;
            case PartialChecked:
                radius = (int) (size * 0.185f + size * 0.125f);
                break;
        }


        if (drawable != null) {
            drawable.onSizeChanged(w, h, oldw, oldh, size, getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCirclePaint.setColor(evaluate(progressValue, drawFromColor, drawToColor));

        canvas.drawCircle(cx, cy, radius, mCirclePaint);

        if (drawable != null) {
            drawable.onDraw(canvas, mCorrectPaint, checkMarkProgress);
        }
    }


    public void setCheckedColor(int color){
        checkedColor = color;
    }

    public void setCheckMarkColor(int color){
        checkMarkColor = color;
        mCorrectPaint.setColor(checkMarkColor);
    }

    public void setUnCheckColor(int color){
        unCheckColor = color;
    }

    public void setPartialColor(int color){
        partialColor = color;
    }

    public void setCheckBoxDrawable(IProgressDrawable drawable) {
        this.drawable = drawable;
        ViewGroup.LayoutParams params = this.getLayoutParams();

        this.onSizeChanged(params.width, params.height, params.width, params.height);
    }

    public void setMultiState(boolean multiState) {
        this.isMultiState = multiState;
    }


    private int evaluate(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24)
                | ((startR + (int) (fraction * (endR - startR))) << 16)
                | ((startG + (int) (fraction * (endG - startG))) << 8)
                | ((startB + (int) (fraction * (endB - startB))));
    }


    private void hideCircle() {
        drawFromColor = drawToColor;
        drawToColor = unCheckColor;

        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
        va.setInterpolator(new LinearInterpolator());
        va.addUpdateListener(animation -> {
            progressValue = (float) animation.getAnimatedValue(); // 0f ~ 1f
            radius = (int) ((1 - progressValue) * size * 0.375f + size * 0.125f);
            if (progressValue >= 1) {
                isChecked = false;

                checkedState = CheckedState.NotChecked;

                if (!ignoreEvent && listener != null) {
                    listener.onCheckedStateChanged(MultiStateTouchCheckBox.this, false, checkedState);
                }

                ignoreEvent = false;
            }
            invalidate();
        });
        va.start();
    }

    private void showCircle() {
        drawToColor = checkedColor;

        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
        va.setInterpolator(new LinearInterpolator());
        va.addUpdateListener(animation -> {
            progressValue = (float) animation.getAnimatedValue(); // 0f ~ 1f
            radius = (int) (progressValue * size * 0.37f + size * 0.125f);
             if (progressValue >= 1) {
                isChecked = true;

                showCheckMark();
            }

            invalidate();
        });
        va.start();
    }


    private void showCheckMark() {
        if (checkedState == CheckedState.PartialChecked) {
            leavePartial(CheckedState.Checked);
        } else {
            ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
            va.setInterpolator(new LinearInterpolator());
            va.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue(); // 0f ~ 1f
                checkMarkProgress = value;
                invalidate();
                if (value >= 1) {
                    isChecked = true;

                    checkedState = CheckedState.Checked;

                    if (!ignoreEvent && listener != null) {
                        listener.onCheckedStateChanged(MultiStateTouchCheckBox.this, true, checkedState);
                    }

                    ignoreEvent = false;
                }
            });
            va.start();
        }
    }

    private void hideCheckMark(final CheckedState targetState) {
        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
        va.setInterpolator(new LinearInterpolator());
        va.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue(); // 0f ~ 1f
            checkMarkProgress = 1 - value;
            invalidate();

            if (value >= 1) {
                if (targetState == CheckedState.PartialChecked) {
                    drawFromColor = drawToColor;
                    drawToColor = partialColor;

                    final ValueAnimator va1 = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
                    va1.setInterpolator(new LinearInterpolator());
                    va1.addUpdateListener(animation1 -> {
                        progressValue = (float) animation1.getAnimatedValue(); // 0f ~ 1f
                        radius = (int) ((1 - (progressValue / 2)) * size * 0.375f + size * 0.125f);
                        if (progressValue >= 1) {
                            isChecked = true;

                            checkedState = CheckedState.PartialChecked;

                            if (!ignoreEvent && listener != null) {
                                listener.onCheckedStateChanged(MultiStateTouchCheckBox.this, true, checkedState);
                            }

                            ignoreEvent = false;
                        }
                        invalidate();
                    });

                    va1.start();
                } else {
                    hideCircle();
                }
            }
        });
        va.start();
    }


    private void showPartial() {
        if (checkedState == CheckedState.Checked) {
            hideCheckMark(CheckedState.PartialChecked);
        } else {
            drawFromColor = unCheckColor;
            drawToColor = partialColor;

            ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
            va.setInterpolator(new LinearInterpolator());
            va.start();
            va.addUpdateListener(animation -> {
                progressValue = (float) animation.getAnimatedValue(); // 0f ~ 1f
                radius = (int) (progressValue / 2 * size * 0.37f + size * 0.125f);
                if (progressValue >= 1) {
                    isChecked = true;

                    checkedState = CheckedState.PartialChecked;

                    if (!ignoreEvent && listener != null) {
                        listener.onCheckedStateChanged(MultiStateTouchCheckBox.this, true, checkedState);
                    }

                    ignoreEvent = false;
                }

                invalidate();
            });
        }
    }

    private void leavePartial(CheckedState targetState) {
        if (targetState == CheckedState.Checked) {
            drawFromColor = partialColor;
            drawToColor = checkedColor;

            ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
            va.setInterpolator(new LinearInterpolator());
            va.start();
            va.addUpdateListener(animation -> {
                progressValue = (float) animation.getAnimatedValue(); // 0f ~ 1f
                radius = (int) ((0.5 + (progressValue / 2)) * size * 0.37f + size * 0.125f);
                if (progressValue >= 1) {
                    isChecked = true;

                    checkedState = CheckedState.NotChecked;
                    showCheckMark();
                }

                invalidate();
            });
        } else {
            drawFromColor = partialColor;
            drawToColor = unCheckColor;

            ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(animDurtion);
            va.setInterpolator(new LinearInterpolator());
            va.addUpdateListener(animation -> {
                progressValue = (float) animation.getAnimatedValue(); // 0f ~ 1f
                radius = (int) ((0.5 - (progressValue / 2)) * size * 0.375f + size * 0.125f);
                if (progressValue >= 1) {
                    isChecked = false;

                    checkedState = CheckedState.NotChecked;

                    if (!ignoreEvent && listener != null) {
                        listener.onCheckedStateChanged(MultiStateTouchCheckBox.this, false, checkedState);
                    }

                    ignoreEvent = false;
                }
                invalidate();
            });
            va.start();
        }
    }



    public void setOnCheckedStateChangeListener(OnCheckedStateChangeListener listener){
        this.listener = listener;
    }


    public interface OnCheckedStateChangeListener {
        void onCheckedStateChanged(View view, boolean isChecked, CheckedState state);
    }

    /**
     * An implementation of OnCheckedStateChangeListener that attempts to lazily load a
     * named click handling method from a parent or ancestor context.
     */
    private static class DeclaredOnCheckedStateChangeListener implements OnCheckedStateChangeListener {
        private final View mHostView;
        private final String mMethodName;

        private Method mMethod;
        private Context mContext;

        public DeclaredOnCheckedStateChangeListener(@NonNull View hostView, @NonNull String methodName) {
            mHostView = hostView;
            mMethodName = methodName;
        }

        @Override
        public void onCheckedStateChanged(View v, boolean isChecked, CheckedState state) {
            if (mMethod == null) {
                resolveMethod(mHostView.getContext());
            }

            try {
                mMethod.invoke(mContext, v, isChecked, state);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not execute non-public method for onCheckedStateChanged", e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Could not execute method for onCheckedStateChanged", e);
            }
        }

        private void resolveMethod(Context context) {
            while (context != null) {
                try {
                    if (!context.isRestricted()) {
                        mMethod = context.getClass().getMethod(mMethodName, View.class, boolean.class, CheckedState.class);
                        mContext = context;
                        return;
                    }
                } catch (NoSuchMethodException e) {
                    // Failed to find method, keep searching up the hierarchy.
                }

                if (context instanceof ContextWrapper) {
                    context = ((ContextWrapper) context).getBaseContext();
                } else {
                    // Can't search up the hierarchy, null out and fail.
                    context = null;
                }
            }

            final int id = mHostView.getId();
            final String idText = id == NO_ID ? "" : " with id '"
                    + mHostView.getContext().getResources().getResourceEntryName(id) + "'";
            throw new IllegalStateException("Could not find method " + mMethodName
                    + "(View, boolean, CheckedState) in a parent or ancestor Context for app:onCheckStateChange "
                    + "attribute defined on view " + mHostView.getClass() + idText);
        }
    }

    public enum CheckedState {
        NotChecked,
        Checked,
        PartialChecked
    }
}
