package com.usda.fmsc.android.widget.drawables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BitmapFadingProgressDrawable extends BitmapProgressDrawable {

    public BitmapFadingProgressDrawable(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public Bitmap onDrawBitmap(Bitmap bitmap, float progress) {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        // create a canvas where we can draw on
        Canvas canvas = new Canvas(newBitmap);
        // create a paint instance with alpha
        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha((int)(255 * progress));
        // now lets draw using alphaPaint instance
        canvas.drawBitmap(bitmap, 0, 0, alphaPaint);

        return newBitmap;
    }
}
