package edu.berkeley.capstoneproject.capstoneprojectandroid.models.exercises;

import edu.berkeley.capstoneproject.capstoneprojectandroid.CapstoneProjectAndroidApplication;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Feather52;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.Metric;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.Encoder;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.IMU;

/**
 * Created by Alex on 27/10/2017.
 */

public class TestExercise extends Exercise {

    private static final String TAG = TestExercise.class.getSimpleName();

    private static final String TEST_EXERCISE_NAME = "TEST_EXERCISE";

    public final Feather52 mFeather52 = CapstoneProjectAndroidApplication.getInstance().getFeather52();

    public TestExercise() {
        super(TEST_EXERCISE_NAME);
        mMetrics.put(Encoder.LABEL_ENCODER_ANGLE, new Metric(Encoder.LABEL_ENCODER_ANGLE, mFeather52.getEncoder()));
        mMetrics.put(IMU.LABEL_IMU_ACC_X, new Metric(IMU.LABEL_IMU_ACC_X, mFeather52.getArmIMU()));
        mMetrics.put(IMU.LABEL_IMU_ACC_Y, new Metric(IMU.LABEL_IMU_ACC_Y, mFeather52.getArmIMU()));
        mMetrics.put(IMU.LABEL_IMU_ACC_Z, new Metric(IMU.LABEL_IMU_ACC_Z, mFeather52.getArmIMU()));
    }

}
