package com.usda.fmsc.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectableStringArrayAdapter extends ArrayAdapter<String> {
    protected int NON_SELECTED_COLOR = 0xFF191919;
    protected int SELECTED_COLOR = 0xFF3366CC;

    private ArrayList<String> items;
    private LayoutInflater mInflater;
    private int viewResourceId;
    private int selectedPosition = -1;

    public SelectableStringArrayAdapter(Activity activity, int resourceId, ArrayList<String> list) {
        super(activity, resourceId, list);

        mInflater = LayoutInflater.from(activity);
        viewResourceId = resourceId;
        items = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) convertView;
        if (tv == null) {
            tv = (TextView) mInflater.inflate(viewResourceId, null);
        }
        tv.setText(items.get(position));

        // Change the background color
        if (position == selectedPosition)
            tv.setBackgroundColor(SELECTED_COLOR);
        else
            tv.setBackgroundColor(NON_SELECTED_COLOR);

        return tv;
    }

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