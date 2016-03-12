package com.example.ivor_hu.meizhi.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

/**
 * Created by Ivor on 2016/2/6.
 */
public class VolleyUtil {
    private static final String TAG = "VolleyUtil";
    private static VolleyUtil mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    public interface OnJSONResponse {
        void onResponse(JSONObject jsonObject);
    }

    private VolleyUtil(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyUtil(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // Instantiate the cache
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 20 * 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);

            // Start the queue
            mRequestQueue.start();
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
//            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public void getJSON(final String url, final OnJSONResponse onJSONResponse) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        onJSONResponse.onResponse(jsonObject);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "onErrorResponse: " + volleyError.getMessage(), volleyError);
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    public RequestFuture<JSONObject> getJSONSync(String url, String tag) {
        RequestFuture<JSONObject> stuffFuture = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(url, null, stuffFuture, stuffFuture);
        request.setTag(tag);
        mRequestQueue.add(request);
        return stuffFuture;
    }
}
