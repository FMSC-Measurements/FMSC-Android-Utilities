package com.usda.fmsc.android.utilities;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.IntegerRes;

import java.util.HashMap;

public class ResourceBitmapProvider implements BitmapManager.IBitmapProvider {
    private final Context _Context;
    private final HashMap<String, Integer> _Resources = new HashMap<>();


    public ResourceBitmapProvider(Context context) {
        _Context = context;
    }


    @Override
    public String getProviderId() {
        return "ResourceBitmapProvider" + _Context.getPackageName();
    }

    @Override
    public Bitmap getBitmap(String key) {
        if (_Resources.containsKey(key)) {
                return BitmapFactory.decodeResource(_Context.getResources(), _Resources.get(key));
        } else {
            throw new RuntimeException("Key Not Found");
        }
    }

    @Override
    public boolean hasBitmap(String key) {
        return _Resources.containsKey(key);
    }


    public void addResource(String key, @IntegerRes Integer resourceId) {
        _Resources.put(key, resourceId);
    }
}
