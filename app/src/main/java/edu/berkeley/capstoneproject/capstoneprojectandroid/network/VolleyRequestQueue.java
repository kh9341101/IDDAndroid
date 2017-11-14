package edu.berkeley.capstoneproject.capstoneprojectandroid.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Alex on 27/10/2017.
 */

public class VolleyRequestQueue {

    private static final String TAG = VolleyRequestQueue.class.getSimpleName();

    private static VolleyRequestQueue mInstance = null;

    private static Context mContext;
    private RequestQueue mRequestQueue;

    private VolleyRequestQueue(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyRequestQueue getInstance() {
        return mInstance;
    }

    public static synchronized void init(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyRequestQueue(context);
        }
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addRequest(Request<T> request) {
        mRequestQueue.add(request);
    }

}
