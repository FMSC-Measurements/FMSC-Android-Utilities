package com.usda.fmsc.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DialogFragmentEx extends DialogFragment {
    @IntDef({STYLE_NORMAL, STYLE_NO_TITLE, STYLE_NO_FRAME, STYLE_NO_INPUT})
    @Retention(RetentionPolicy.SOURCE)
    private @interface DialogStyle {
    }

    @DialogStyle
    private int style = -1;
    private int theme = -1;

    private OnClickListener onNegativeClickListener;
    private OnClickListener onNeutralClickListener;
    private OnClickListener onPositiveClickListener;
    private int negativeTitle;
    private int neutralTitle;
    private int positiveTitle;

    /**
     * Creates a new instance of a GenericDialogFragment
     *
     * @param titleId   Resource ID to a string representing the title of this GenericDialogFragment.
     * @param messageId Resource ID to a string representing the message of this GenericDialogFragment.
     * @return a new instance of a GenericDialogFragment.
     */
    public static DialogFragmentEx newInstance(int titleId, int messageId) {
        final Bundle args = new Bundle();
        final DialogFragmentEx dFrag = new DialogFragmentEx();

        // Add the arguments.
        args.putInt("title", titleId);
        args.putInt("message", messageId);

        dFrag.setArguments(args);
        dFrag.setRetainInstance(true);
        return dFrag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        dlg.setTitle(getArguments().getInt("title"));
        dlg.setMessage(getArguments().getInt("message"));

        // Optionally attempt to set a theme
        if (style != STYLE_NORMAL && theme != -1) {
            setStyle(style, theme);
        } else {
            // No custom theme, business as usual.
            setStyle(DialogFragment.STYLE_NORMAL, 0);
        }

        // Set the click handlers
        if (negativeTitle != 0)
            dlg.setNegativeButton(negativeTitle, onNegativeClickListener);
        if (neutralTitle != 0)
            dlg.setNeutralButton(neutralTitle, onNeutralClickListener);
        if (positiveTitle != 0)
            dlg.setPositiveButton(positiveTitle, onPositiveClickListener);

        return dlg.create();
    }

    public void setNegativeButton(int titleId, OnClickListener onNegativeClickListener) {
        this.onNegativeClickListener = onNegativeClickListener;
        this.negativeTitle = titleId;
    }

    public void setNeutralButton(int titleId, OnClickListener onNeutralClickListener) {
        this.onNeutralClickListener = onNeutralClickListener;
        this.neutralTitle = titleId;
    }

    public void setPositiveButton(int titleId, OnClickListener onPositiveClickListener) {
        this.onPositiveClickListener = onPositiveClickListener;
        this.positiveTitle = titleId;
    }

    public void setStyle(int newStyle) {
        this.style = newStyle;
    }

    public void setTheme(int newTheme) {
        this.theme = newTheme;
    }
}
