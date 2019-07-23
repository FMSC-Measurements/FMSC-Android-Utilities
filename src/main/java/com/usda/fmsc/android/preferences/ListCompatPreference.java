package com.usda.fmsc.android.preferences;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import android.util.AttributeSet;

import com.usda.fmsc.android.AndroidUtils;

public class ListCompatPreference extends ListPreference {
    protected AppCompatDialog mDialog;

    public ListCompatPreference(Context context) {
        super(context);
    }

    public ListCompatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(21)
    public ListCompatPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public ListCompatPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public CharSequence getSummary() {
        final CharSequence summary = super.getSummary();
        if (summary == null || Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return summary;
        }
        CharSequence entry = getEntry();
        return String.format(summary.toString(), entry == null ? "" : entry);
    }

    @Override
    protected void showDialog(Bundle state) {
        if (getEntries() == null || getEntryValues() == null) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array.");
        }

        int selected = findIndexOfValue(getValue());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(getDialogTitle())
                .setIcon(getDialogIcon())
                .setNegativeButton(getNegativeButtonText(), this)
                .setPositiveButton(getPositiveButtonText(), this)
                .setSingleChoiceItems(getEntries(), selected, this);

        AndroidUtils.Internal.registerOnActivityDestroyListener(this, getPreferenceManager());

        mDialog = builder.create();
        if (state != null) {
            mDialog.onRestoreInstanceState(state);
        }
        mDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which >= 0 && getEntryValues() != null) {
            String value = getEntryValues()[which].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}