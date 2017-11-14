package edu.berkeley.capstoneproject.capstoneprojectandroid.helpers;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

/**
 * Created by Alex on 26/10/2017.
 */

public class Feather52Helper {

    private static final String TAG = Feather52Helper.class.getSimpleName();


    public static int decodeImu(BluetoothGattCharacteristic characteristic) {
        int flag = characteristic.getProperties();
        int format = -1;
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
            Log.d(TAG, "IMU format UINT16.");
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            Log.d(TAG, "IMU format UINT8.");
        }
        final int imu = characteristic.getIntValue(format, 1);
        Log.d(TAG, String.format("Received imu: %d", imu));

        return imu;
    }


    public static int decodeEncoder(BluetoothGattCharacteristic characteristic) {
        int flag = characteristic.getProperties();
        int format = -1;
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
            Log.d(TAG, "Encoder format UINT16.");
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            Log.d(TAG, "Encoder format UINT8.");
        }
        final int imu = characteristic.getIntValue(format, 1);
        Log.d(TAG, String.format("Received encoder: %d", imu));

        return imu;
    }

}
