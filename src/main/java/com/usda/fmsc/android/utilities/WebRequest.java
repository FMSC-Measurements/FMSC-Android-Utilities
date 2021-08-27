package com.usda.fmsc.android.utilities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class WebRequest {
    private final RequestQueue mRequestQueue;

    public WebRequest(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public <T> void addRequest(Request<T> request) {
        addToRequestQueue(request);
    }

    public void getJson(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        addRequest(new JsonObjectRequest(url, listener, errorListener));
    }
}
