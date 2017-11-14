package edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.Measurement;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.Metric;
import edu.berkeley.capstoneproject.capstoneprojectandroid.utils.BytesUtils;

/**
 * Created by Alex on 25/10/2017.
 */

public abstract class Sensor {

    private static final String TAG = Sensor.class.getSimpleName();

    public enum SensorType {
        IMU, ENCODER
    }


    private final int mId;
    private final String mName;
    private final SensorType mType;
    private final Map<String, Class> mMetrics = new HashMap<>();

    public Sensor (int id, SensorType type, String name) {
        mId = id;
        mType = type;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public SensorType getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }

    public static int decodeSensorId(byte[] bytes) {
        return BytesUtils.bytesToInt16(bytes);
    }
}
