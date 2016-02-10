package com.usda.fmsc.android.widget.drawables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class BitmapProgressDrawable implements ProgressDrawable {

    private Bitmap bitmap, originalBitmap;
    protected float padding = .15f, bitmapScale = .7f;

    public BitmapProgressDrawable(Bitmap bitmap) {
        this.bitmap = originalBitmap = bitmap;
    }

    @Override
    public final void onDraw(Canvas canvas, Paint completePaint, float progress) {
        canvas.drawBitmap(onDrawBitmap(bitmap, progress), (int)(bitmap.getWidth() * padding), (int)(bitmap.getHeight() * padding), null);
    }

    public abstract Bitmap onDrawBitmap(Bitmap bitmap, float progress);

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh, int size, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        double nW, nH;

        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            nW = (w * bitmapScale);

            double adj = nW / originalBitmap.getWidth();

            nH = (originalBitmap.getHeight() / adj);
        } else {
            nH = (h * bitmapScale);

            double adj = nH / originalBitmap.getHeight();

            nW = (originalBitmap.getWidth() / adj);
        }

        bitmap = Bitmap.createScaledBitmap(originalBitmap, (int)nW, (int)nH, false);
    }
}