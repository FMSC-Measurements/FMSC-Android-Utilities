package com.usda.fmsc.android.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.usda.fmsc.android.R;

import java.text.NumberFormat;

public class ProgressDialogEx extends AlertDialog {


    private final Dialog dialog;
    private final TextView mMessageView;

    private CharSequence mMessage = "";

    public ProgressDialogEx(Context context) {
        super(context);

        dialog = new Dialog(context);
        dialog.setCancelable(false);

        int llPadding = 30;
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setPadding(llPadding, llPadding, llPadding, llPadding);
        root.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        root.setLayoutParams(llParam);
        root.setBackgroundColor(getContext().getColor(R.color.design_default_color_background));

        ProgressBar mProgress = new ProgressBar(getContext());
        mProgress.setIndeterminate(true);
        mProgress.setPadding(0, 0, llPadding, 0);
        mProgress.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        mMessageView = new TextView(getContext());
        mMessageView.setText(mMessage);
        mMessageView.setTextSize(16);
        mMessageView.setLayoutParams(llParam);

        root.addView(mProgress);
        root.addView(mMessageView);

        root.setOnClickListener(v -> dismiss());

        setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setContentView(root, llParam);
            window.setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
    }


    public void setCanceledOnTouchOutside(boolean flag) {
        dialog.setCanceledOnTouchOutside(flag);
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void setMessage(CharSequence message) {
        mMessage = message;

        if (mMessageView != null) {
            mMessageView.setText(mMessage);
        }
    }
}