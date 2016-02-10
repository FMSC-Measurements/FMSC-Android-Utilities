package com.usda.fmsc.android.adapters;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdapterUtils {

    @SuppressWarnings("unchecked")
    public static <T> List<T> getItemsAtPositions(List<Integer> positions, BaseAdapter adapter) {
        ArrayList<T> items = new ArrayList<>();

        for (int i = 0; i < adapter.getCount(); i++) {
            items.add((T)adapter.getItem(i));
        }

        return items;
    }
}
