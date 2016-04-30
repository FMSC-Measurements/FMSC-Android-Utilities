package com.usda.fmsc.android.widget.drawables;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class PolygonProgressDrawable implements IProgressDrawable {
    protected PointF[] points;
    protected int sides, startingAngle;
    protected boolean clockwise = true;


    public PolygonProgressDrawable() {
        this(3, 90, true);
    }

    public PolygonProgressDrawable(int sides) {
        this(sides, 0, true);
    }

    public PolygonProgressDrawable(int sides, int startingAngle) {
        this(sides, startingAngle, true);
    }

    public PolygonProgressDrawable(int sides, int startingAngle, boolean clockwise) {
        this.sides = sides;
        this.points = new PointF[sides];
        this.startingAngle = startingAngle;
        this.clockwise = clockwise;
    }


    @Override
    public void onDraw(Canvas canvas, Paint paint, float progress) {
        if (progress > 0) {
            for (int i = 0; i < (progress * sides) && i < sides; i++) {
                if (progress < (i + 1f) / sides) {
                    float x, y;

                    if (i < sides - 1) {
                        x = points[i].x + (points[i + 1].x - points[i].x) * progress;
                        y = points[i].y + (points[i + 1].y - points[i].y) * progress;
                    } else {
                        x = points[i].x + (points[0].x - points[i].x) * progress;
                        y = points[i].y + (points[0].y - points[i].y) * progress;
                    }

                    canvas.drawLine(points[i].x, points[i].y, x, y, paint);
                } else {
                    if (i < sides - 1) {
                        canvas.drawLine(points[i].x, points[i].y, points[i + 1].x, points[i + 1].y, paint);
                    } else {
                        canvas.drawLine(points[i].x, points[i].y, points[0].x, points[0].y, paint);
                    }
                }
            }
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh, int size, int pL, int pT, int pR, int pB) {
        float radius = size / 2f * 0.7f;

        float step = 360f / sides;

        PointF center = new PointF(w / 2f, h / 2f);

        float angle = startingAngle;
        for (int i = 0; i < sides; i++) {
            points[i] = DegreesToXY(angle, radius, center);

            if (clockwise) {
                angle -= step;
            } else {
                angle += step;
            }
        }
    }

    protected PointF DegreesToXY(float degrees, float radius, PointF origin) {
        PointF xy = new PointF(0, 0);
        float radians = (float) (degrees * Math.PI / 180f);

        xy.x = (float) Math.cos(radians) * radius + origin.x;
        xy.y = (float) Math.sin(-radians) * radius + origin.y;

        return xy;
    }

    protected double xyToDegrees(PointF xy, PointF origin) {
        double deltax = origin.x - xy.x;
        double deltay = origin.y - xy.y;

        double radAngle = Math.atan2(deltay, deltax);
        double degreeAngle = radAngle * 180.0 / Math.PI;

        return 180.0 - degreeAngle;
    }
}