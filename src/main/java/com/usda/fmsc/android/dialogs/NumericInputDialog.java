package com.usda.fmsc.android.dialogs;


import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.InputType;

public class NumericInputDialog extends InputDialog {
    protected int flags = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED;

    public NumericInputDialog(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public AlertDialog create() {
        AlertDialog dialog = super.create();

        input.setInputType(flags);

        return dialog;
    }

    public String getValue() {
        return input.getText().toString().trim();
    }

    public Integer getInt() {
        String value = input.getText().toString().trim();
        return value.length() > 0 ? Integer.parseInt(value) : 0;
    }

    public Double getDouble() {
        String value = input.getText().toString().trim();
        return value.length() > 0 ? Double.parseDouble(value) : 0;
    }


    public void setFlags(int flags) {
        this.flags = flags;
    }
}
