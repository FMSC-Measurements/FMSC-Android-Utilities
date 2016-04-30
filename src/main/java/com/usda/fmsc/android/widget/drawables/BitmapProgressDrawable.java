package com.usda.fmsc.android.widget.drawables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.usda.fmsc.android.AndroidUtils;

public abstract class BitmapProgressDrawable implements IProgressDrawable {
    private Bitmap bitmap;
    protected float padding = .15f, bitmapScale = .5f;

    public BitmapProgressDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public final void onDraw(Canvas canvas, Paint completePaint, float progress) {
        if (progress > 0) {
            int left = (canvas.getWidth() - bitmap.getWidth()) / 2;
            int top = (canvas.getHeight() - bitmap.getHeight()) / 2;

            canvas.drawBitmap(onDrawBitmap(bitmap, progress), left, top, null);
        }
    }

    public abstract Bitmap onDrawBitmap(Bitmap bitmap, float progress);

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh, int size, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        bitmap = AndroidUtils.UI.scaleBitmap(bitmap, w * bitmapScale, true);
    }
}