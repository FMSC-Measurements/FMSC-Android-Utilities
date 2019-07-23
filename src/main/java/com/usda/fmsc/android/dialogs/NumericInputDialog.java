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

    public Integer getInt() {
        return Integer.parseInt(input.getText().toString());
    }

    public Double getDouble() {
        return Double.parseDouble(input.getText().toString());
    }


    public void setFlags(int flags) {
        this.flags = flags;
    }
}
