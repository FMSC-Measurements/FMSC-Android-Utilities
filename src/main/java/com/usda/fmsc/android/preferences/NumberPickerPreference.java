package com.usda.fmsc.android.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.usda.fmsc.android.AndroidUtils;
import com.usda.fmsc.android.R;

public class NumberPickerPreference extends DialogPreference {

    public static final int DEFAULT_MAX_VALUE = 100;
    public static final int DEFAULT_MIN_VALUE = 0;

    private NumberPicker picker;
    private int value, minValue = DEFAULT_MIN_VALUE, maxValue = DEFAULT_MAX_VALUE;
    private boolean useValueInSummary = false;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.NumberPickerPreference);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);

            if (attr == R.styleable.NumberPickerPreference_valueInSummary) {
                useValueInSummary = a.getBoolean(attr, false);
            } else if (attr == R.styleable.NumberPickerPreference_minimumValue) {
                minValue = a.getInt(attr, DEFAULT_MIN_VALUE);
            } else if (attr == R.styleable.NumberPickerPreference_maximumValue) {
                maxValue = a.getInt(attr, DEFAULT_MAX_VALUE);
            }
        }
        a.recycle();
    }

    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        picker = new NumberPicker(getContext());
        picker.setLayoutParams(layoutParams);
        picker.setBackgroundColor(AndroidUtils.UI.getColor(getContext(), android.R.color.transparent));

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setValue(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, minValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
//        int value = 0;
//
//        if (restorePersistedValue) {
//            value = defaultValue != null ? (int)defaultValue : getSharedPreferences().getInt(getKey(), 0);
//            setValue(value);
//        }
//
//        setSummary(Integer.toString(value));

        setValue(restorePersistedValue ? getPersistedInt(minValue) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);

        if (useValueInSummary) {
            setSummary(Integer.toString(this.value));
        }
    }

    public int getValue() {
        return this.value;
    }
}