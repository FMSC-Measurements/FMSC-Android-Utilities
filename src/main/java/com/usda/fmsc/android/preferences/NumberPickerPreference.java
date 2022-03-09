package com.usda.fmsc.android.preferences;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.preference.DialogPreference;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceViewHolder;

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
    private boolean useValueInSummary = false, dialogShown;

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
    protected void onClick() {
        if (!dialogShown) {
            showDialog(null);
        }
    }


    protected void showDialog(Bundle state) {
        Context context = getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(getDialogTitle())
                .setIcon(getDialogIcon())
                .setPositiveButton(getPositiveButtonText(), onDialogClick)
                .setNegativeButton(getNegativeButtonText(), onDialogClick);

        View contentView = onCreateDialogView();
        if (contentView != null) {
            //onBindDialogView(contentView);
            builder.setView(contentView);
        } else {
            builder.setMessage(getDialogMessage());
        }

        //AndroidUtils.Internal.registerOnActivityDestroyListener(this, getPreferenceManager());

        // Create the dialog
        final Dialog dialog = builder.create();
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }

        dialog.setOnDismissListener(onDialogDismissed);
        dialog.show();

        dialogShown = true;
    }

    //@Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        picker.setLayoutParams(layoutParams);
        picker.setBackgroundColor(AndroidUtils.UI.getColor(getContext(), android.R.color.transparent));

        AndroidUtils.UI.setNumberPickerColor(picker, R.color.accent);

        picker.setValue(getValue());

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker);

        return dialogView;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        picker = new NumberPicker(getContext());
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setValue(getValue());
    }

    private final Dialog.OnDismissListener onDialogDismissed = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            dialogShown = false;
        }
    };
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, minValue);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        if (defaultValue == null) {
            try {
                value = getPersistedInt(minValue);
            } catch (Exception e) {
                try {
                    value = (int)getPersistedLong(minValue);
                } catch (Exception e2) {
                    value = minValue;
                }
            }
        } else {
            try {
                value = (Integer)defaultValue;
            } catch (Exception e) {
                value = minValue;
            }
        }

        setValueInSummary();
    }

    private final Dialog.OnClickListener onDialogClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == Dialog.BUTTON_POSITIVE)  {
                setValue(picker.getValue());
            }
        }
    };


    public void setValue(int value) {
        this.value = value;

        try {
            persistInt(this.value);
        } catch (Exception e) {
            persistLong(this.value);
        }

        setValueInSummary();
    }

    private void setValueInSummary() {
        if (useValueInSummary) {
            setSummary(Integer.toString(this.value));
        }
    }

    public int getValue() {
        return this.value;
    }
}