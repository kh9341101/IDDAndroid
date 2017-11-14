package edu.berkeley.capstoneproject.capstoneprojectandroid.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import edu.berkeley.capstoneproject.capstoneprojectandroid.models.exercises.Exercise;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.Measurement;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.Metric;

/**
 * Created by Alex on 27/10/2017.
 */

public class RailsServer {

    private static final String TAG = RailsServer.class.getSimpleName();

    private static RailsServer instance;

    private VolleyRequestQueue mRequestQueue;

    private String mHost = "http://192.168.1.2";
    private int mPort = 3000;

    private RailsServer() {
        mRequestQueue = VolleyRequestQueue.getInstance();
    }

    public static RailsServer getInstance() {
        if (instance == null) {
            instance = new RailsServer();
        }

        return instance;
    }

    public String getHost() {
        return mHost;
    }

    public void setHost(String host) {
        mHost = host;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int port) {
        mPort = port;
    }

    private StringBuilder getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(mHost + ":" + mPort);
        sb.append("/api/v1");
        return sb;
    }

    public StringBuilder getExercisesUrl() {
        return getUrl().append("/exercises");
    }

    public StringBuilder getExerciseUrl(Exercise exercise) {
        return getExercisesUrl().append("/" + exercise.getID());
    }

    public StringBuilder getMetricsUrl(Exercise exercise) {
        return getExerciseUrl(exercise).append("/metrics");
    }

    public StringBuilder getMetricUrl(Exercise exercise, Metric metric) {
        return getMetricsUrl(exercise).append("/" + metric.getID());
    }

    public StringBuilder getMeasurementsUrl(Exercise exercise, Metric metric) {
        return getMetricUrl(exercise, metric).append("/measurements");
    }
}
