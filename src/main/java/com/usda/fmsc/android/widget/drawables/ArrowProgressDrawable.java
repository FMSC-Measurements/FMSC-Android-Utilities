package com.usda.fmsc.android.widget.drawables;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class ArrowProgressDrawable implements ProgressDrawable {
    protected PointF[] points;
    protected double rotate;

    public ArrowProgressDrawable() {
        this(0);
    }

    public ArrowProgressDrawable(double rotate) {
        rotate = (rotate % 360) / 180d * Math.PI;

        this.rotate = rotate;
        this.points = new PointF[4];
    }


    @Override
    public void onDraw(Canvas canvas, Paint correctPaint, float progress) {
        if(progress > 0) {
            if (progress < 2/3f) {
                float x = points[0].x + (points[1].x - points[0].x) * progress;
                float y = points[0].y + (points[1].y - points[0].y) * progress;
                canvas.drawLine(points[0].x, points[0].y, x, y, correctPaint);
            } else {
                float x1 = points[1].x + (points[2].x - points[1].x) * progress;
                float y1 = points[1].y + (points[2].y - points[1].y) * progress;

                float x2 = points[1].x + (points[3].x - points[1].x) * progress;
                float y2 = points[1].y + (points[3].y - points[1].y) * progress;


                canvas.drawLine(points[0].x, points[0].y, points[1].x, points[1].y, correctPaint);
                canvas.drawLine(points[1].x, points[1].y, x1, y1, correctPaint);
                canvas.drawLine(points[1].x, points[1].y, x2, y2, correctPaint);
            }
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh, int size, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        float len = h / 3f * 0.8f;

        PointF center = new PointF(w / 2f, h/ 2f);

        points[0] = new PointF(center.x, center.y + len);
        points[1] = new PointF(center.x, center.y - len);
        points[2] = DegreesToXY(-45, len, points[1]);
        points[3] = DegreesToXY(225, len, points[1]);

        if (rotate != 0) {
            for (PointF p : points) {
                double x = p.x - center.x;
                double y = p.y - center.y;

                double x1 = x * Math.cos(rotate) - y * Math.sin(rotate);
                double y1 = x * Math.sin(rotate) + y * Math.cos(rotate);

                p.x = (float)(x1 + center.x);
                p.y = (float)(y1 + center.y);
            }
        }
    }

    protected PointF DegreesToXY(float degrees, float radius, PointF origin) {
        PointF xy = new PointF(0,0);
        float radians = (float)(degrees * Math.PI / 180f);

        xy.x = (float)Math.cos(radians) * radius + origin.x;
        xy.y = (float)Math.sin(-radians) * radius + origin.y;

        return xy;
    }
}