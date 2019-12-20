package com.usda.fmsc.android.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.preference.ListPreference;

public abstract class EnumPreference extends ListPreference {
    private int[] itemValues;
    private String[] itemValuesStr;
    private CharSequence[] itemNames;
    private int value;

    private DialogInterface.OnClickListener listener;

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
        setEntries(itemNames);

        itemValuesStr = new String[itemValues.length];
        for (int i = 0; i < itemValues.length; i++) {
            itemValuesStr[i] = itemValues[i] + Integer.toHexString(i);
        }

        setEntryValues(itemValuesStr);
    }

    protected void parseEnums() { }


    protected abstract CharSequence[] getItemNames();

    protected abstract int[] getItemValues();



//    @Override
//    protected void showDialog(Bundle state) {
//        int selected = getSharedPreferences().getInt(getKey(), -1);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
//                .setTitle(getDialogTitle())
//                .setIcon(getDialogIcon())
//                .setNegativeButton(getNegativeButtonText(), this)
//                .setSingleChoiceItems(itemNames, selected, (dialogInterface, i) -> {
//                    setValue(itemValues[i]);
//
//                    if (listener != null) {
//                        listener.onClick(dialogInterface, i);
//                    }
//
//                    dialogInterface.dismiss();
//                });
//
//        AndroidUtils.Internal.registerOnActivityDestroyListener(this, getPreferenceManager());
//
//        mDialog = builder.create();
//        if (state != null) {
//            mDialog.onRestoreInstanceState(state);
//        }
//        mDialog.show();
//    }

    @Override
    public void setValue(String value) {
        int index = findIndexOfValue(value);
        getSharedPreferences().edit().putInt(getKey(), itemValues[index]).apply();
        setSummary(itemNames[index]);
        notifyChanged();
        //super.setValue(value);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        if (itemValues != null)
            return itemValues[index];
        return null;
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        value = (defaultValue == null) ? getSharedPreferences().getInt(getKey(), 0) : 0;

        for (int i = 0; i < itemValues.length; i++) {
            if (itemValues[i] == value) {
                setSummary(itemNames[i]);
                setValueIndex(i);
                return;
            }
        }

    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }
}
