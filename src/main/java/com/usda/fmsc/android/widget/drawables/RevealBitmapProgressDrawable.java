package com.usda.fmsc.android.widget.drawables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class RevealBitmapProgressDrawable extends BitmapProgressDrawable {

    public RevealBitmapProgressDrawable(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public Bitmap onDrawBitmap(Bitmap bitmap, float progress) {
        return revealBitmap(bitmap, progress);
    }

    private Bitmap revealBitmap(Bitmap foreground, float progress) {
        Bitmap bitmap = Bitmap.createBitmap(foreground.getWidth(), foreground.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Color.RED);
        canvas.drawBitmap(foreground, 0, 0, paint);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight() - (int)(bitmap.getHeight() * progress), paint);

        return bitmap;
    }

    private Bitmap combineTwoBitmaps(Bitmap background, Bitmap foreground) {
        Bitmap combinedBitmap = Bitmap.createBitmap(background.getWidth(), background.getHeight(), background.getConfig());
        Canvas canvas = new Canvas(combinedBitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(background, 0, 0, paint);
        canvas.drawBitmap(foreground, 0, 0, paint);
        return combinedBitmap;
    }
}
