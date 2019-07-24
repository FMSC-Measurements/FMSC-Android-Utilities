package com.usda.fmsc.android.preferences;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class EnumSelectionPreference extends ListCompatPreference {
    private String[] itemNames;
    private Class<? extends Enum> enumType;
    private DialogInterface.OnClickListener listener;

    public EnumSelectionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EnumSelectionPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public EnumSelectionPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        Enum[] items = enumType.getEnumConstants();

        if (items.length > 0) {

            itemNames = new String[items.length];
            for (int i = 0; i < items.length; i++) {
                itemNames[i] = items[i].toString();
            }

            builder.setItems(itemNames, (dialogInterface, i) -> {
                setValue(i);

                if (listener != null) {
                    listener.onClick(dialogInterface, i);
                }
            });
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int newValue = 0;
            String value = getValue();

            for (int i = 0; i < itemNames.length; i++) {
                if (itemNames[i].equals(value)) {
                    if (callChangeListener(newValue)) {
                        setValue(i);
                    }
                }
            }
        }
    }


    public void setValue(int value) {
        getSharedPreferences().edit().putInt(getKey(), value).apply();

        if (itemNames != null) {
            setSummary(itemNames[value]);
        }
    }

    public int getEnumValue() {
        return getSharedPreferences().getInt(getKey(), 0);
    }


    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return itemNames[index];
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public void setEnumType(Class<? extends Enum> enumType) {
        this.enumType = enumType;
    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }
}
