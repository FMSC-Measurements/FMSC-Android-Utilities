package com.usda.fmsc.android.widget.drawables;

import android.graphics.drawable.AnimationDrawable;

//somehow fixes the AnimationDrawable random stop issue
public class AnimationDrawableEx extends AnimationDrawable {
    public AnimationDrawableEx() { }

    public AnimationDrawableEx(AnimationDrawable drawable) {
        for (int i = 0; i < drawable.getNumberOfFrames(); i++) {
            addFrame(drawable.getFrame(i), drawable.getDuration(i));
        }

        setOneShot(drawable.isOneShot());
    }
}
