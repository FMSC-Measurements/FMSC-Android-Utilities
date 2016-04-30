package com.usda.fmsc.android.utilities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class WebRequest {
    private static WebRequest mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private WebRequest(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized WebRequest getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new WebRequest(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static void addRequest(Request request, Context context) {
        getInstance(context).addToRequestQueue(request);
    }

    public static void getJson(String url, Context context, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        addRequest(new JsonObjectRequest(url, listener, errorListener), context);
    }
}
