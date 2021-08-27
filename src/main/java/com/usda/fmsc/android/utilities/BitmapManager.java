package com.usda.fmsc.android.utilities;

import android.graphics.Bitmap;

import com.usda.fmsc.android.AndroidUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BitmapManager {
    private final BitmapCacher cacher;
    private final HashMap<String, IBitmapProvider> bitmapProviders;
    private final HashMap<String, ScaleOptions> scaleOptions;

    private int imageLimitSize;


    public BitmapManager(IBitmapProvider... providers) {
        this(providers, 1000);
    }

    public BitmapManager(IBitmapProvider[] providers, int maxImageSize) {
        this.imageLimitSize = maxImageSize;

        cacher = new BitmapCacher();
        scaleOptions = new HashMap<>();
        bitmapProviders = new HashMap<>();

        for (IBitmapProvider provider : providers) {
            bitmapProviders.put(provider.getProviderId(), provider);
        }
    }


    public Bitmap get(String key) {
        return get(null, key, null);
    }

    public Bitmap get(String providerId, String key) {
        return get(providerId, key, null);
    }

    public Bitmap get(String key, ScaleOptions options) {
        return get(null, key, null);
    }

    public Bitmap get(String providerId, String key, ScaleOptions options) {
        for (Map.Entry<String, IBitmapProvider> kvp: bitmapProviders.entrySet()) {
            if (kvp.getValue().hasBitmap(key)) {
                providerId = kvp.getKey();
                break;
            }
        }

        if (providerId == null) {
            throw new RuntimeException("Invalid Provider ID");
        }

        String cKey = providerId + key;
        IBitmapProvider brp = bitmapProviders.get(providerId);

        if (options != null) {
            if (scaleOptions.containsKey(cKey) && !options.equals(scaleOptions.get(cKey))) {
                cacher.remove(cKey);
            } else {
                options = new ScaleOptions();
            }
        }

        if (cacher.containKey(cKey)) {
            Bitmap bmp = cacher.get(key);

            if (bmp == null || bmp.isRecycled()) {
                bmp = getBitmapFromResource(brp, key, options);
            }

            return bmp;
        } else {
            return getBitmapFromResource(brp, key, options);
        }
    }

    private Bitmap getBitmapFromResource(IBitmapProvider brp, String key, ScaleOptions options) {
        if (brp != null) {
            String cKey = brp.getProviderId() + key;
            Bitmap bmp;

            bmp = brp.getBitmap(key);

            if (bmp != null) {
                if (options == null) {
                    if (scaleOptions.containsKey(cKey)) {
                        options = scaleOptions.get(cKey);
                    } else {
                        options = new ScaleOptions();
                    }
                }

                int size = options.getSize() == 0 || options.getSize() > imageLimitSize ? imageLimitSize : options.getSize();

                if (bmp.getHeight() > size || bmp.getWidth() > size || options.isUpScale()) {
                    if (options.getScaleMode() == ScaleMode.Max) {
                        bmp = AndroidUtils.UI.scaleBitmap(bmp, size, false);
                    } else {
                        bmp = AndroidUtils.UI.scaleMinBitmap(bmp, size, false);
                    }
                }

                cacher.put(key, bmp);
            } else {
                throw new RuntimeException("Invalid Bitmap Resource");
            }

            return bmp;
        } else {
            throw new RuntimeException("Bitmap Provider not found");
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        recycle();
    }




    public void setScaleOptionsForResource(String providerId, String key, ScaleOptions options) {
        String cKey = providerId + key;
        IBitmapProvider brp = bitmapProviders.get(providerId);

        if (brp != null) {
            scaleOptions.put(cKey, options);
        }  else {
            throw new RuntimeException("Bitmap Provider not found");
        }
    }


    public boolean containResource(String key) {
        if (cacher.containKey(key)) {
            return true;
        } else {
            for (Map.Entry<String, IBitmapProvider> kvp: bitmapProviders.entrySet()) {
                if (kvp.getValue().hasBitmap(key)) {
                    return true;
                }
            }
        }

        return false;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScaleOptions that = (ScaleOptions) o;
            return size == that.size &&
                    upScale == that.upScale &&
                    scaleMode == that.scaleMode;
        }

        @Override
        public int hashCode() {
            return Objects.hash(size, scaleMode, upScale);
        }
    }

    public enum ScaleMode { Min, Max }


    public interface IBitmapProvider {
        String getProviderId();
        Bitmap getBitmap(String key);
        boolean hasBitmap(String key);
    }
}
