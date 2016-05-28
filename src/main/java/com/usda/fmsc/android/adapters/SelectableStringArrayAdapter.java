package com.usda.fmsc.android.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectableStringArrayAdapter extends SelectableArrayAdapter<String> {
    private LayoutInflater mInflater;
    private int viewResourceId;

    public SelectableStringArrayAdapter(Activity activity, int resourceId, ArrayList<String> list) {
        super(activity, resourceId, list);

        mInflater = LayoutInflater.from(activity);
        viewResourceId = resourceId;
    }

    @Override
    public View getViewEx(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) convertView;

        if (tv == null) {
            tv = (TextView) mInflater.inflate(viewResourceId, null);
        }

        tv.setText(getItem(position));

        return tv;
    }
}