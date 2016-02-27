package com.usda.fmsc.android.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.CheckBox;

import java.util.Set;

public class DontAskAgainDialog {
    SharedPreferences sharedPreferences;
    private String title, message, askKey, valueKey;
    private String posBtnText, negBtnText, neuBtnText;
    private CheckBox checkBox;
    private Object val1 = 0, val2 = 1, val3 = 2;
    private Context context;

    public DontAskAgainDialog(Context context, String askKey, String valueKey, SharedPreferences sharedPreferences) {
        this.context = context;
        this.askKey = askKey;
        this.valueKey = valueKey;
        this.sharedPreferences = sharedPreferences;
    }

    OnClickListener onPosClick, onNegClick, onNeuClick;

    @NonNull
    public AlertDialog create() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(title);
        dialog.setMessage(message);

        checkBox = new CheckBox(context);
        checkBox.setText("Don't ask again.");

        dialog.setView(checkBox);

        if (posBtnText != null && posBtnText.length() > 0) {
            dialog.setPositiveButton(posBtnText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setValue(val1);

                    if (onPosClick != null) {
                        onPosClick.onClick(dialogInterface, i, val1);
                    }
                }
            });
        }


        if(neuBtnText != null && neuBtnText.length() > 0) {
            dialog.setNeutralButton(neuBtnText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setValue(val2);

                    if (onNeuClick != null) {
                        onNeuClick.onClick(dialogInterface, i, val2);
                    }
                }
            });
        }


        if(negBtnText != null && negBtnText.length() > 0){
            dialog.setNegativeButton(negBtnText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setValue(val3);

                    if (onNegClick != null) {
                        onNegClick.onClick(dialogInterface, i, val3);
                    }
                }
            });
        }

        return dialog.create();
    }

    public void show() {
        create().show();
    }


    private void setValue(Object value) {
        if (value != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (checkBox.isChecked()) {
                editor.putBoolean(askKey, false);
            }

            Class c = value.getClass();

            if (c.equals(Integer.class)) {
                editor.putInt(valueKey, (Integer)value);
            } else if (c.equals(String.class)) {
                editor.putString(valueKey, (String)value);
            } else if (c.equals(Boolean.class)) {
                editor.putBoolean(valueKey, (Boolean)value);
            } else if (c.equals(Float.class)) {
                editor.putFloat(valueKey, (Float)value);
            } else if (c.equals(Long.class)) {
                editor.putLong(valueKey, (Long)value);
            } else if (c.equals(Set.class)) {
                editor.putStringSet(valueKey, (Set<String>)value);
            } else if (c.equals(Double.class)) {
                editor.putLong(valueKey, Double.doubleToRawLongBits((Double)value));
            }

            editor.apply();
        }
    }

    public void setPositiveButton(String text,OnClickListener listener) {
        onPosClick = listener;
        posBtnText = text;
    }

    public void setPositiveButton(String text, OnClickListener listener, Object value) {
        onPosClick = listener;
        posBtnText = text;
        val1 = value;
    }

    public void setNeutralButton(String text, OnClickListener listener) {
        onNeuClick = listener;
        neuBtnText = text;
    }

    public void setNeutralButton(String text, OnClickListener listener, Object value) {
        onNeuClick = listener;
        neuBtnText = text;
        val2 = value;
    }

    public void setNegativeButton(String text, OnClickListener listener) {
        onNegClick = listener;
        negBtnText = text;

    }

    public void setNegativeButton(String text, OnClickListener listener, Object value) {
        onNegClick = listener;
        negBtnText = text;
        val3 = value;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public interface OnClickListener {
        void onClick(DialogInterface dialogInterface, int i, Object value);
    }
}
