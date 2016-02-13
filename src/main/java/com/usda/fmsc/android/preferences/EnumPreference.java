package com.usda.fmsc.android.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;

import com.usda.fmsc.android.AndroidUtils;

public abstract class EnumPreference extends ListCompatPreference {
    private int[] itemValues;
    private CharSequence[] itemNames;

    DialogInterface.OnClickListener listener;

    public EnumPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(21)
    public EnumPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public EnumPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        parseEnums();
        itemNames = getItemNames();
        itemValues = getItemValues();
    }

    protected void parseEnums() { }


    protected abstract CharSequence[] getItemNames();
    protected abstract int[] getItemValues();

    @Override
    protected void showDialog(Bundle state) {
        int selected = getSharedPreferences().getInt(getKey(), -1);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(getDialogTitle())
                .setIcon(getDialogIcon())
                .setNegativeButton(getNegativeButtonText(), this)
                .setSingleChoiceItems(itemNames, selected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setValue(itemValues[i]);

                        if (listener != null) {
                            listener.onClick(dialogInterface, i);
                        }

                        dialogInterface.dismiss();
                    }
                });

        AndroidUtils.Interal.registerOnActivityDestroyListener(this, getPreferenceManager());

        mDialog = builder.create();
        if (state != null) {
            mDialog.onRestoreInstanceState(state);
        }
        mDialog.show();
    }

    public void setValue(int value) {
        getSharedPreferences().edit().putInt(getKey(), value).commit();
        setSummary(itemNames[value]);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        if (itemValues != null)
            return itemValues[index];
        return null;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        int value = 0;

        if (restorePersistedValue) {
            value = defaultValue != null ? (int)defaultValue : getSharedPreferences().getInt(getKey(), 0);
            setValue(value);
        }

        setSummary(itemNames[value]);
    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }
}
