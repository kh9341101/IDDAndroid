package edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 27/10/2017.
 */

public class EncoderData extends MeasurementData {

    private static final String TAG = EncoderData.class.getSimpleName();

    public static final String LABEL_ANGLE = "Angle";

    private final int mAngle;

    public EncoderData(int angle) {
        mAngle = angle;
    }


    public int getAngle() {
        return mAngle;
    }

    @Override
    public Map<String, Float> getValues() {
        Map<String, Float> values = new HashMap<>(1);
        values.put(LABEL_ANGLE, new Float(mAngle));
        return values;
    }
}
