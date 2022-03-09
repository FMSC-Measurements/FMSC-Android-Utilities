package com.usda.fmsc.android.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.usda.fmsc.android.R;

public class NumberPickerDialog extends DialogFragment {
    private static final String DEFAULT_COLOR = "default_color";
    private Listener _Listener;
    private NumberPicker _NumberPicker;
    private @ColorInt int _Color = -1;

    public static NumberPickerDialog newInstance(@ColorInt int defaultColor) {

        Bundle args = new Bundle();

        NumberPickerDialog fragment = new NumberPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null && args.containsKey(DEFAULT_COLOR)) {
            _Color = args.getInt(DEFAULT_COLOR);
        }

        _NumberPicker = new NumberPicker(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder db = new AlertDialog.Builder(getActivity());

        db.setView(_NumberPicker);
        db.setPositiveButton(R.string.str_ok, (dialog, which) -> {
            if (_Listener != null) {
                _Listener.onColorSelected(_NumberPicker.getValue());
            }
        }).setNeutralButton(R.string.str_cancel, null);

        if (_Color != -1) {
            _NumberPicker.setValue(_Color);
        }

        return db.create();
    }

    public NumberPickerDialog setListener(Listener listener) {
        _Listener = listener;
        return this;
    }

    public interface Listener {
        void onColorSelected(@ColorInt int color);
    }
}
