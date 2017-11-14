package edu.berkeley.capstoneproject.capstoneprojectandroid.models;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

import edu.berkeley.capstoneproject.capstoneprojectandroid.models.exercises.Exercise;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.Encoder;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.IMU;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.Sensor;

/**
 * Created by Alex on 25/10/2017.
 */

public class Feather52 {

    private static final String TAG = Feather52.class.getSimpleName();

    public static final int ENCODER_ELBOW_ID = 1;
    public static final int IMU_ARM_ID = 2;

    private final List<Sensor> mSensors = new ArrayList<>();
    private final IMU mArmIMU = new IMU(IMU_ARM_ID);
    private final Encoder mEncoder = new Encoder(ENCODER_ELBOW_ID);

    private final List<Exercise> mExercises = new ArrayList<>();

    private BluetoothDevice mBluetoothDevice;
    private boolean mConnected;

    public Feather52() {
        mSensors.add(mArmIMU);
        mSensors.add(mEncoder);
    }

    public IMU getArmIMU() {
        return mArmIMU;
    }

    public Encoder getEncoder() {
        return mEncoder;
    }

    public List<Sensor> getSensors() {
        return mSensors;
    }

    public Sensor getSensor(int id) {
        for (Sensor s: mSensors) {
            if (s.getId() == id) {
                return s;
            }
        }

        return null;
    }

    public synchronized void setConnected(boolean connected) {
        mConnected = connected;
    }

    public synchronized boolean isConnected() {
        return mConnected;
    }

    public synchronized List<Exercise> getExercises() {
        return mExercises;
    }

    public synchronized void addExercise(Exercise exercise) {
        mExercises.add(exercise);
    }

    public synchronized Exercise getCurrentExercise() {
        Exercise e = mExercises.get(mExercises.size() - 1);
        if (!e.isCompleted()) {
            return e;
        }

        return null;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice device) {
        mBluetoothDevice = device;
    }

    public String getName() {
        if (mBluetoothDevice != null) {
            return mBluetoothDevice.getName();
        }

        return "Feather52";
    }
}
