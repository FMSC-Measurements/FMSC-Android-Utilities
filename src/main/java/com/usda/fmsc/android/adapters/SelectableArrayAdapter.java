package com.usda.fmsc.android.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public abstract class SelectableArrayAdapter<T> extends ArrayAdapter<T> {
    protected int NON_SELECTED_COLOR = 0xFF191919;
    protected int SELECTED_COLOR = 0xFF3366CC;
    private int selectedPosition = -1;

    public SelectableArrayAdapter(Activity activity, int resourceId, ArrayList<T> list) {
        super(activity, resourceId, list);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        View view = getViewEx(position, convertView, parent);

        // Change the background color
        if (position == selectedPosition)
            view.setBackgroundColor(SELECTED_COLOR);
        else
            view.setBackgroundColor(NON_SELECTED_COLOR);

        return view;
    }

    public abstract View getViewEx(int position, View convertView, ViewGroup parent);

    public void setSelected(int position) {
        selectedPosition = position;
    }

    public void setSelectedColor(int selectedColor) {
        SELECTED_COLOR = selectedColor;
    }

    public void setNonSelectedColor(int nonSelectedColor) {
        NON_SELECTED_COLOR = nonSelectedColor;
    }
}