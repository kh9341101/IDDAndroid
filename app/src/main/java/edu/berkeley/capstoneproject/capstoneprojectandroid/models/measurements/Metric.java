package edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.Sensor;

/**
 * Created by Alex on 27/10/2017.
 */

public class Metric<T extends Number> {

    private static final String TAG = Metric.class.getSimpleName();

    private final Sensor mSensor;
    private final String mName;
    private final List<Measurement<T>> mMeasurements = new ArrayList<>();

    private int mID;

    public Metric(String name, Sensor sensor) {
        mName = name;
        mSensor = sensor;
    }

    public String getName() {
        return mName;
    }

    public Sensor getSensor() {
        return mSensor;
    }

    public synchronized void addMeasurement(long tookAt, T value) {
        addMeasurement(new Measurement<T>(tookAt, value));
    }

    public synchronized void addMeasurement(Measurement<T> measurement) {
        mMeasurements.add(measurement);
    }

    public synchronized List<Measurement<T>> getMeasurements() {
        return mMeasurements;
    }

    public synchronized int getID() {
        return mID;
    }

    public synchronized void setID(int id) {
        mID = id;
    }

    @Override
    public String toString() {
        return mName;
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put("metric", new JSONObject()
                        .put("name", mName)
                        .put("sensor_id", mSensor.getId()));
    }
}
