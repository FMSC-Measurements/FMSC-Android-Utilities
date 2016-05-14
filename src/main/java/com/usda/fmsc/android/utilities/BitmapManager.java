package com.usda.fmsc.android.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.usda.fmsc.android.AndroidUtils;

import java.util.HashMap;

public class BitmapManager {
    private BitmapCacher cacher;
    private int imageLimitSize;

    private HashMap<String, String> keyToUri;
    private HashMap<String, ScaleOptions> scaleOptions;

    public BitmapManager(Context context) {
        this(context, 1000);
    }

    public BitmapManager(Context context, int maxImageSize) {
        this.imageLimitSize = maxImageSize;

        cacher = new BitmapCacher();
        keyToUri = new HashMap<>();
        scaleOptions = new HashMap<>();
    }


    public Bitmap get(String key) {
        if (keyToUri.containsKey(key)) {
            Bitmap bmp = cacher.get(key);

            if (bmp == null || bmp.isRecycled()) {
                bmp = BitmapFactory.decodeFile(keyToUri.get(key));

                ScaleOptions options = scaleOptions.get(key);
                int size = options.getSize() == 0 ? imageLimitSize : options.getSize();

                if (options.getScaleMode() == ScaleMode.Max) {
                    bmp = AndroidUtils.UI.scaleBitmap(bmp, size, false);
                } else {
                    bmp = AndroidUtils.UI.scaleMinBitmap(bmp, size, false);
                }

                cacher.put(key, bmp);
            }

            return bmp;
        } else {
            throw new RuntimeException("Key Not Found");
        }
    }


    public void put(String key, String uri, Bitmap bitmap) {
        put(key, uri, bitmap, new ScaleOptions());
    }

    public void put(String key, String uri, Bitmap bitmap, ScaleOptions options) {
        cacher.put(key, bitmap);
        keyToUri.put(key, uri);

        scaleOptions.put(key, options);
    }


    public boolean containKey(String key) {
        return keyToUri.containsKey(key);
    }


    public void setImageLimitSize(int maxSize) {
        if (maxSize < 1) {
            imageLimitSize = 1000;
        } else {
            imageLimitSize = maxSize;
        }
    }


    public void recycle() {
        cacher.recycle();
        keyToUri.clear();
        scaleOptions.clear();
    }


    public static class ScaleOptions {
        private int size;
        private ScaleMode scaleMode;

        public ScaleOptions() {
            this(0, BitmapManager.ScaleMode.Max);
        }

        public ScaleOptions(int size, ScaleMode mode) {
            this.size = size;
            scaleMode = mode;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public ScaleMode getScaleMode() {
            return scaleMode;
        }

        public void setScaleMode(ScaleMode scaleMode) {
            this.scaleMode = scaleMode;
        }
    }

    public enum ScaleMode { Min, Max }
}
