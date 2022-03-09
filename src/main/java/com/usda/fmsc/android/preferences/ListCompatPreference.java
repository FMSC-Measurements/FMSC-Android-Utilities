package com.usda.fmsc.android.preferences;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import androidx.preference.ListPreference;
import android.util.AttributeSet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;

public class ListCompatPreference extends ListPreference {
    protected AppCompatDialog mDialog;
    private String negativeButtonText, positiveButtonText;

    private final DialogInterface.OnClickListener defaultListener = (dialog, which) -> {

    };
    private DialogInterface.OnClickListener positiveClickListener, negativeClickListener, singleChoiceClickListener;

    public ListCompatPreference(Context context) {
        super(context);
        init();
    }

    public ListCompatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(21)
    public ListCompatPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public ListCompatPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        positiveClickListener = negativeClickListener = singleChoiceClickListener = defaultListener;
    }



    @Override
    protected void onClick() {
        if (getEntries() == null || getEntryValues() == null) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array.");
        }

        int selected = findIndexOfValue(getValue());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(getDialogTitle())
                .setIcon(getDialogIcon());

        if (getNegativeButtonText() != null)
                builder.setNegativeButton(getNegativeButtonText(), positiveClickListener);

        if (getPositiveButtonText() != null)
                builder.setPositiveButton(getPositiveButtonText(), negativeClickListener);

        builder.setSingleChoiceItems(getEntries(), selected, singleChoiceClickListener)
            .setOnDismissListener(this::onDialogDismissed);

        onPrepareDialogBuilder(builder);

        mDialog = builder.create();
        mDialog.show();
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {

    }

    protected void onDialogDismissed(DialogInterface dialog) {

    }

    @Override
    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    @Override
    public String getPositiveButtonText() {
        return positiveButtonText;
    }

    public void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public void setPositiveClickListener(DialogInterface.OnClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
    }

    public void setNegativeClickListener(DialogInterface.OnClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
    }

    public void setSingleChoiceClickListener(DialogInterface.OnClickListener singleChoiceClickListener) {
        this.singleChoiceClickListener = singleChoiceClickListener;
    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        positiveClickListener = negativeClickListener = singleChoiceClickListener = listener;
    }
}