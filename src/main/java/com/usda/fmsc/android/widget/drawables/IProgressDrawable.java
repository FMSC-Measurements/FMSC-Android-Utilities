package com.usda.fmsc.android.widget.drawables;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface IProgressDrawable {
    void onDraw(Canvas canvas, Paint completePaint, float progress);
    void onSizeChanged(int w, int h, int oldw, int oldh, int size,
                       int paddingLeft, int paddingTop, int paddingRight, int paddingBottom);
}
