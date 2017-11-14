package edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.data;

import java.util.Map;

/**
 * Created by Alex on 27/10/2017.
 */

public abstract class MeasurementData {

    private static final String TAG = MeasurementData.class.getSimpleName();

    public abstract Map<String, Float> getValues();
}
