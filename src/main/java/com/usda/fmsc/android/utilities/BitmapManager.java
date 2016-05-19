package com.usda.fmsc.android.utilities;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.usda.fmsc.android.AndroidUtils;

import java.util.HashMap;

public class BitmapManager {
    private BitmapCacher cacher;
    private int imageLimitSize;

    private HashMap<String, String> keyToUri;
    private HashMap<String, ScaleOptions> scaleOptions;
    private HashMap<String, Boolean> isResources;

    private Resources resources;

    public BitmapManager(Resources resources) {
        this(resources, 1000);
    }

    public BitmapManager(Resources resources, int maxImageSize) {
        this.resources = resources;
        this.imageLimitSize = maxImageSize;

        cacher = new BitmapCacher();
        keyToUri = new HashMap<>();
        scaleOptions = new HashMap<>();
        isResources = new HashMap<>();
    }


    public Bitmap get(String key) {
        if (keyToUri.containsKey(key)) {
            Bitmap bmp = cacher.get(key);

            if (bmp == null || bmp.isRecycled()) {
                if (isResources.get(key)) {
                    bmp = BitmapFactory.decodeResource(resources, Integer.parseInt(keyToUri.get(key)));
                } else {
                    bmp = BitmapFactory.decodeFile(keyToUri.get(key));
                }

                ScaleOptions options = scaleOptions.get(key);
                int size = options.getSize() == 0 ? imageLimitSize : options.getSize();

                if (bmp.getHeight() > size || bmp.getWidth() > size || options.isUpScale()) {
                    if (options.getScaleMode() == ScaleMode.Max) {
                        bmp = AndroidUtils.UI.scaleBitmap(bmp, size, false);
                    } else {
                        bmp = AndroidUtils.UI.scaleMinBitmap(bmp, size, false);
                    }
                }

                cacher.put(key, bmp);
            }

            return bmp;
        } else {
            throw new RuntimeException("Key Not Found");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        recycle();
    }

    public void put(String key, String uri, Bitmap bitmap) {
        put(key, uri, bitmap, new ScaleOptions(), false);
    }

    public void put(String key, String uri, Bitmap bitmap, ScaleOptions options) {
        put(key, uri, bitmap, options, false);
    }

    public void put(String key, String uri, Bitmap bitmap, ScaleOptions options, boolean isResource) {
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
        private boolean upScale;

        public ScaleOptions() {
            this(0, BitmapManager.ScaleMode.Max, false);
        }

        public ScaleOptions(int size, ScaleMode scaleMode) {
            this(size, scaleMode, false);
        }

        public ScaleOptions(int size, ScaleMode scaleMode, boolean upScale) {
            this.size = size;
            this.scaleMode = scaleMode;
            this.upScale = upScale;
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

        public boolean isUpScale() {
            return upScale;
        }

        public void setUpScale(boolean upScale) {
            this.upScale = upScale;
        }
    }

    public enum ScaleMode { Min, Max }
}
