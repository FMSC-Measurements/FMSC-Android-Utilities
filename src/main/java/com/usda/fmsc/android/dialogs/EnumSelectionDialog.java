package com.usda.fmsc.android.dialogs;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class EnumSelectionDialog<T extends Enum<T>> extends AlertDialog.Builder {
    private EnumSelectionDialog dialog;

    private Enum[] items;
    private String[] itemNames;
    private Enum selectedItem;
    private DialogInterface.OnClickListener listener;

    public EnumSelectionDialog(Context context, Class<? extends Enum> enumType) {
        super(context);

        dialog = this;

        items = enumType.getEnumConstants();

        if (items.length > 0) {

            itemNames = new String[items.length];
            for (int i = 0; i < items.length; i++) {
                itemNames[i] = items[i].toString();
            }

            this.setItems(itemNames, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedItem = items[which];

                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                }
            });
        }
    }

    public T getSelectedItem() {
        return ((T)selectedItem);
    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }
}
