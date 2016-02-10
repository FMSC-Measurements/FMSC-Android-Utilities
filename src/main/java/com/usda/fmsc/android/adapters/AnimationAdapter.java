package com.usda.fmsc.android.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.usda.fmsc.android.R;

import java.util.ArrayList;

public class AnimationAdapter<T> extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<T> items;
    private DisplayMetrics metrics;

    private class Holder {
        TextView textview;
    }

    public enum AnimationType {

    }

    public AnimationAdapter(Context context, ArrayList<T> items, DisplayMetrics metrics) {
        super(context, 0, items);

        this.context = context;
        this.inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.metrics = metrics;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final T item = this.items.get(position);
        final Holder holder;

        boolean animate = false;

        if (convertView == null) {
            convertView = inflater.inflate(
                    android.R.layout.simple_list_item_1, null);

            holder = new Holder();
            holder.textview = (TextView) convertView
                    .findViewById(android.R.id.text1);

            convertView.setTag(holder);
            animate = true;
        } else {
            holder = (Holder) convertView.getTag();
        }

        //holder.textview.setText(item);

        Animation animation = getAnimation(position, convertView);

        if (animation != null) {
            convertView.startAnimation(animation);
        }

        return convertView;
    }

    public Animation getAnimation(int position, View view) {
        Animation animation = null;

        int mode = 0;

        switch (mode) {
            case 1:
                //translate 1
                animation = new TranslateAnimation(metrics.widthPixels / 2, 0, 0, 0);
                break;
            case 2:
                //translate 2
                animation = new TranslateAnimation(0, 0, metrics.heightPixels, 0);
                break;
            case 3:
                //scale
                animation = new ScaleAnimation(1.0f, 1.0f, 0f, 1.0f);
                break;
            case 4:
                animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                break;
            case 5:
                animation = AnimationUtils.loadAnimation(context, R.anim.hyperspace_in);
                break;
            case 6:
                animation = AnimationUtils.loadAnimation(context, R.anim.hyperspace_out);
                break;
            case 7:
                animation = AnimationUtils.loadAnimation(context, R.anim.wave_scale);
                break;
            case 8:
                animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
                break;
            case 9:
                animation = AnimationUtils.loadAnimation(context, R.anim.push_left_out);
                break;
            case 10:
                animation = AnimationUtils.loadAnimation(context, R.anim.push_up_in);
                break;
            case 11:
                animation = AnimationUtils.loadAnimation(context, R.anim.push_up_out);
                break;
            case 12:
                animation = AnimationUtils.loadAnimation(context, R.anim.shake);
                break;
            case 13:
                animation = AnimationUtils.loadAnimation(context, R.anim.push_down_in);
                break;
            case 14:
                animation = AnimationUtils.loadAnimation(context, R.anim.push_down_out);
                break;
        }

        animation.setDuration(500);

        return animation;
    }
}
