package com.usda.fmsc.android.dialogs;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class InputDialog extends AlertDialog.Builder {
    protected EditText input;
    private String inputText;
    private Context context;

    public InputDialog(Context context) {
        super(context);
        this.context = context;
    }

    @NonNull
    @Override
    public AlertDialog create() {
        AlertDialog dialog = super.create();

        input = new EditText(context);
        input.setText(inputText);

        dialog.setView(input);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                input.setSelection(input.getText().length());
            }
        });

        return dialog;
    }

    public String getText() {
        return input.getText().toString();
    }

    public InputDialog setInputText(String text) {
        inputText = text;
        return this;
    }

    public EditText getInput() {
        return input;
    }
}
