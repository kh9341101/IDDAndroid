package edu.berkeley.capstoneproject.capstoneprojectandroid.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.berkeley.capstoneproject.capstoneprojectandroid.CapstoneProjectAndroidApplication;
import edu.berkeley.capstoneproject.capstoneprojectandroid.SampleGattAttributes;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Feather52;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.exercises.Exercise;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.Measurement;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.data.EncoderData;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.Encoder;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.IMU;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.data.ImuData;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.Metric;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.Sensor;
import edu.berkeley.capstoneproject.capstoneprojectandroid.network.helpers.MeasurementHelper;

/**
 * Created by Alex on 21/10/2017.
 */

public class Feather52Service extends Service {

    private static final String TAG = Feather52Service.class.getSimpleName();

    public static final UUID UUID_SERVICE_SENSOR = UUID.fromString("7f951693-1a34-e0b6-ab48-3887a9a5cb72");
    public static final UUID UUID_CHARACTERISTIC_ENCODER = UUID.fromString("86e15c11-6636-a3a3-6946-cbae8325d627");
    public static final UUID UUID_CHARACTERISTIC_IMU = UUID.fromString("e0822222-6316-2baf-fa46-96c693d870de");
    public static final UUID UUID_CHARACTERISTIC_STARTSTOP = UUID.fromString("4117d83c-d5d9-51b0-cd48-1615c62e5a65");

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;


    public static final String ACTION_GATT_CONNECTED = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.ACTION_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.ACTION_DATA_AVAILABLE";

    public static final String EXTRA_DATA = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.EXTRA_DATA";
    public static final String EXTRA_SERVICE_UUID = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.EXTRA_SERVICE_UUID";
    public static final String EXTRA_CHARACTERISTIC_UUID = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.EXTRA_CHARACTERISTIC_UUID";
    public static final String EXTRA_SENSOR_ID = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.EXTRA_SENSOR_ID";
    public static final String EXTRA_LABEL = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.EXTRA_LABEL";
    public static final String EXTRA_TOOK_AT = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.EXTRA_TOOK_AT";
    public static final String EXTRA_VALUE = "edu.berkeley.capstoneproject.capstoneprojectandroid.services.EXTRA_VALUE";


    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattService mSensorService;

    private int mConnectionState = STATE_DISCONNECTED;

    private final Feather52 mFeather52 = CapstoneProjectAndroidApplication.getInstance().getFeather52();


    private GattOperationQueue mOperationQueue;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                mFeather52.setConnected(true);
                broadcastNewState(intentAction);
                Log.i(TAG, "Connected to GATT server");
                Log.d(TAG, "Attempting to start service discovery: " + mBluetoothGatt.discoverServices());
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                mFeather52.setConnected(false);
                broadcastNewState(intentAction);
                Log.i(TAG, "Disconnected from GATT server");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "New GATT service discovered");
                for (BluetoothGattService s: gatt.getServices()) {
                    if (s.getUuid().equals(UUID_SERVICE_SENSOR)) {
                        Log.d(TAG, "Found sensor service!");
                        mSensorService = s;
                        broadcastNewService(ACTION_GATT_SERVICES_DISCOVERED, s);
                        break;
                    }
                }
            }
            else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead status=" + status + " char=" + characteristic.getUuid().toString());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                receivedData(gatt, characteristic);
            }
            else {
                Log.w(TAG, "onCharacteristicRead received: " + status);
                return;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged: " + characteristic.getUuid().toString());
            receivedData(gatt, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicWrite status=" + status);
        }
    };

    private void receivedData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Map<String, Measurement> measurements = decodeData(gatt, characteristic);
        for (String label: measurements.keySet()) {
            Measurement meas = measurements.get(label);
            long tookAt = meas.tookAt();
            Float value = meas.getValue().floatValue();
            broadcastData(ACTION_DATA_AVAILABLE, characteristic.getService(), characteristic, label, tookAt, value);
        }

        Exercise e = mFeather52.getCurrentExercise();
        for (String label: measurements.keySet()) {
            Metric metric = e.getMetric(label);
            Measurement meas = measurements.get(label);
            MeasurementHelper.create(e, metric, meas);
        }
    }

    private void broadcastNewState(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastNewService(final String action, final BluetoothGattService service) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_SERVICE_UUID, service.getUuid().toString());
        sendBroadcast(intent);
    }

    private void broadcastData(final String action, final BluetoothGattService service, final BluetoothGattCharacteristic characteristic, String label, long tookAt, float value) {
        final Intent intent = new Intent(action);

        intent.putExtra(EXTRA_SERVICE_UUID, service.getUuid().toString());
        intent.putExtra(EXTRA_CHARACTERISTIC_UUID, characteristic.getUuid().toString());
        intent.putExtra(EXTRA_LABEL, label);
        intent.putExtra(EXTRA_TOOK_AT, tookAt);
        intent.putExtra(EXTRA_VALUE, value);

        sendBroadcast(intent);
    }


    private void broadcastChange(String action, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        intent.putExtra(EXTRA_SERVICE_UUID, service.getUuid().toString());
        intent.putExtra(EXTRA_CHARACTERISTIC_UUID, characteristic.getUuid().toString());

        sendBroadcast(intent);
    }


    public class LocalBinder extends Binder {
        public Feather52Service getService() {
            return Feather52Service.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Starting service");
        mOperationQueue = new GattOperationQueue();
        mOperationQueue.start();
        //mHandlerThread = new HandlerThread("Feather52ServiceThread");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOperationQueue.cancel();
    }

    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to initialize BluetoothAdapter");
            return false;
        }

        return true;
    }



    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address");
            return false;
        }

        if (mDeviceAddress != null && address.equals(mDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an exisiting BluetoothGatt for connection");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            }
            else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found");
            return false;
        }

        Log.d(TAG, "Trying to create a new connection");
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }


    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        mBluetoothGatt.disconnect();
    }


    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }

        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    public void readCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        mOperationQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.readCharacteristic(characteristic);
            }
        });

    }

    public void writeCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if ((characteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0) {
            Log.d(TAG, "Write characteristic OK");
        }

        mOperationQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.writeCharacteristic(characteristic);
            }
        });
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if (characteristic != null) {
            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

            final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

            mOperationQueue.enqueue(new Runnable() {
                @Override
                public void run() {
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
            });
        }
    }


    public void startRecording() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if (mSensorService == null) {
            Log.w(TAG, "Can't record: no sensor service");
            return;
        }

        for (BluetoothGattCharacteristic c: mSensorService.getCharacteristics()) {
            if (c.getUuid().equals(UUID_CHARACTERISTIC_ENCODER) || c.getUuid().equals(UUID_CHARACTERISTIC_IMU)) {
                setCharacteristicNotification(c, true);
            }
        }
    }

    public void stopRecording() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if (mSensorService == null) {
            Log.w(TAG, "Can't record: no sensor service");
            return;
        }

        for (BluetoothGattCharacteristic c: mSensorService.getCharacteristics()) {
            if (c.getUuid().equals(UUID_CHARACTERISTIC_ENCODER) || c.getUuid().equals(UUID_CHARACTERISTIC_IMU)) {
                setCharacteristicNotification(c, false);
            }
        }
    }


    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }

        return mBluetoothGatt.getServices();
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothGatt.getDevice();
    }

    public Map<String, Measurement> decodeData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (characteristic.getService() == mSensorService) {
            byte[] bytes = characteristic.getValue();
            int id = Sensor.decodeSensorId(bytes);

            if (characteristic.getUuid().equals(UUID_CHARACTERISTIC_IMU)) {
                IMU imu = (IMU)mFeather52.getSensor(id);
                Map<String, Measurement> meas = IMU.decodeMeasurement(bytes);
                Exercise e = mFeather52.getCurrentExercise();
                e.addMeasurements(meas);
                return meas;
            }
//            else if (characteristic.getUuid().equals(UUID_CHARACTERISTIC_ENCODER)) {
//                Encoder encoder = mFeather52.getEncoder();
//                Map<String, Measurement> meas = Encoder.decodeMeasurement(bytes);
//                Exercise e = mFeather52.getCurrentExercise();
//                e.addMeasurements(meas);
//                return meas;
//            }
        }

        return null;
    }

    public int getConnectionState() {
        return mConnectionState;
    }
}
