package edu.berkeley.capstoneproject.capstoneprojectandroid.models.exercises;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.Measurement;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.Metric;
import edu.berkeley.capstoneproject.capstoneprojectandroid.network.RailsServer;

/**
 * Created by Alex on 25/10/2017.
 */

public abstract class Exercise {

    private static final String TAG = Exercise.class.getSimpleName();

    protected final String mName;
    protected final Map<String, Metric> mMetrics = new HashMap<>();

    protected Date mStartDate;
    protected Date mStopDate;

    protected boolean mCompleted = false;
    protected int mID;

    public Exercise(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public Map<String, Metric> getMetrics() {
        return mMetrics;
    }

    public Metric getMetric(String label) {
        return mMetrics.get(label);
    }

    public synchronized void addMetric(String label, Metric m) {
        mMetrics.put(label, m);
    }

    public synchronized void addMeasurements(Map<String, Measurement> measurements) {
        for (String label: measurements.keySet()) {
            if (mMetrics.containsKey(label)) {
                mMetrics.get(label).addMeasurement(measurements.get(label));
            }
        }
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public void setCompleted() {
        mCompleted = true;
    }

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        mID = id;
    }

    public void start() {
        mStartDate = new Date();
    }

    public void stop() {
        mStopDate = new Date();
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put("exercise", new JSONObject().put("name", mName));
    }
}
