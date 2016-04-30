package com.usda.fmsc.android.widget.drawables;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CheckMarkProgressDrawable implements IProgressDrawable {
    private float[] points = new float[6];

    public CheckMarkProgressDrawable() { }

    @Override
    public void onDraw(Canvas canvas, Paint correctPaint, float progress) {
        if(progress > 0) {
            if (progress < 1/3f) {
                float x = points[0] + (points[2] - points[0]) * progress;
                float y = points[1] + (points[3] - points[1]) * progress;
                canvas.drawLine(points[0], points[1], x, y, correctPaint);
            } else {
                float x = points[2] + (points[4] - points[2]) * progress;
                float y = points[3] + (points[5] - points[3]) * progress;
                canvas.drawLine(points[0], points[1], points[2], points[3], correctPaint);
                canvas.drawLine(points[2], points[3], x,y, correctPaint);
            }
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh, int size, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        float r = size / 2f;
        points[0] = r / 2f + paddingLeft;
        points[1] = r + paddingTop;

        points[2] = r * 5f / 6f + paddingLeft;
        points[3] = r + r / 3f + paddingTop;

        points[4] = r * 1.5f + paddingLeft;
        points[5] = r - r / 3f + paddingTop;
    }
}