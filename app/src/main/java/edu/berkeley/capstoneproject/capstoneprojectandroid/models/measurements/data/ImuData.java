package edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 26/10/2017.
 */

public class ImuData extends MeasurementData {

    private static final String TAG = ImuData.class.getSimpleName();

    private static final String LABEL_ACCX = "Acc X";
    private static final String LABEL_ACCY = "Acc Y";
    private static final String LABEL_ACCZ = "Acc Z";

    private final int mAccX;
    private final int mAccY;
    private final int mAccZ;


    public ImuData(int accX, int accY, int accZ) {
        mAccX = accX;
        mAccY = accY;
        mAccZ = accZ;
    }


    public int getAccX() {
        return mAccX;
    }

    public int getAccY() {
        return mAccY;
    }

    public int getAccZ() {
        return mAccZ;
    }

    @Override
    public Map<String, Float> getValues() {
        Map<String, Float> map = new HashMap<>(3);
        map.put(LABEL_ACCX, new Float(mAccX));
        map.put(LABEL_ACCY, new Float(mAccY));
        map.put(LABEL_ACCZ, new Float(mAccZ));
        return map;
    }
}
