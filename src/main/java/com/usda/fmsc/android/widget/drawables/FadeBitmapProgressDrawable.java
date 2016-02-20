package com.usda.fmsc.android.widget.drawables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class FadeBitmapProgressDrawable extends BitmapProgressDrawable {

    public FadeBitmapProgressDrawable(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public Bitmap onDrawBitmap(Bitmap bitmap, float progress) {

        Bitmap b = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint(Color.RED);
        paint.setAlpha((int) (255 * progress));
        paint.setAntiAlias(true);

        canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth() * progress), (int)(bitmap.getHeight() * progress), false), 0, 0, paint);

        return bitmap;
    }
}
