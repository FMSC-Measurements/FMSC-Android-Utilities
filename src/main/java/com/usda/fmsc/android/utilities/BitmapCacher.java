package com.usda.fmsc.android.utilities;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.util.ArrayList;

public class BitmapCacher {
    private LruCache<String, Bitmap> mMemoryCache;
    private ArrayList<String> keys;

    public BitmapCacher() {
        if (mMemoryCache == null) {
            keys = new ArrayList<>();

            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }


            };
        }
    }

    public boolean containKey(String key) {
        return keys.contains(key);
    }

    public void put(String key, Bitmap bitmap) {
        if (!containKey(key)) {
            mMemoryCache.put(key, Bitmap.createBitmap(bitmap));
            keys.add(key);
        }
    }

    public Bitmap get(String key) {
        Bitmap bmp = mMemoryCache.get(key);

        return bmp == null || bmp.isRecycled() ? null : Bitmap.createBitmap(mMemoryCache.get(key));
    }

    public Bitmap remove(String key) {
        keys.remove(key);
        return mMemoryCache.remove(key);
    }

    public void recycle() {
        for (String k : keys) {
            Bitmap b = remove(k);
            if (b != null) {
                b.recycle();
            }
        }
    }
}
