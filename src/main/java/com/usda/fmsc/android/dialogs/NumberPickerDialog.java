package com.usda.fmsc.android.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.usda.fmsc.android.R;

public class NumberPickerDialog extends DialogFragment {
    private static final String DEFAULT_NUMBER = "default_number";
    private Listener _Listener;
    private NumberPicker _NumberPicker;
    private int _Number = 0;

    public static NumberPickerDialog newInstance(int defaultNumber) {

        Bundle args = new Bundle();

        NumberPickerDialog fragment = new NumberPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null && args.containsKey(DEFAULT_NUMBER)) {
            _Number = args.getInt(DEFAULT_NUMBER);
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
                _Listener.onNumberSelected(_NumberPicker.getValue());
            }
        }).setNeutralButton(R.string.str_cancel, null);

        _NumberPicker.setValue(_Number);

        return db.create();
    }

    public NumberPickerDialog setListener(Listener listener) {
        _Listener = listener;
        return this;
    }

    public interface Listener {
        void onNumberSelected(int number);
    }
}
