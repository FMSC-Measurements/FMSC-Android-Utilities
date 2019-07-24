package com.usda.fmsc.android.dialogs;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class EnumSelectionDialog<T extends Enum<T>> extends AlertDialog.Builder {
    private Enum[] items;
    private String[] itemNames;
    private Enum selectedItem;
    private DialogInterface.OnClickListener listener;

    public EnumSelectionDialog(Context context, Class<? extends Enum> enumType) {
        this(context, enumType.getEnumConstants());
    }

    public EnumSelectionDialog(Context context, Enum[] enums) {
        super(context);

        this.items = enums;

        if (items.length > 0) {

            itemNames = new String[items.length];
            for (int i = 0; i < items.length; i++) {
                itemNames[i] = items[i].toString();
            }

            this.setItems(itemNames, (dialog, which) -> {
                selectedItem = items[which];

                if (listener != null) {
                    listener.onClick(dialog, which);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    public T getSelectedItem() {
        return ((T) selectedItem);
    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }
}
