package edu.berkeley.capstoneproject.capstoneprojectandroid.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.services.BluetoothLeService;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;

public class MyServiceActivity extends AppCompatActivity {

    private static final String TAG = "MyServiceActivity";

    public static final String EXTRAS_SERVICE_UUID = "EXTRAS_SERVICE_UUID";


    public static final UUID UUID_CHAR_READ = UUID.fromString("02e2315a-8b79-1c87-dc43-29d0bacab9b8");
    public static final UUID UUID_CHAR_SEND = UUID.fromString("4117d83c-d5d9-51b0-cd48-1615c62e5a65");
    public static final UUID UUID_CHAR_NOTIFY = UUID.fromString("e0822222-6316-2baf-fa46-96c693d870de");


    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattService mGattService;
    private BluetoothGattCharacteristic mReadCharacteristic;
    private BluetoothGattCharacteristic mSendCharacteristic;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private boolean mNotifyEnabled = false;
    private String mServiceUuid;


    private EditText mEditSend;
    private Button mButtonSend;

    private TextView mTextRead;
    private Button mButtonRead;

    private TextView mTextNotify;
    private Button mButtonNotify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_service);

        mServiceUuid = getIntent().getStringExtra(EXTRAS_SERVICE_UUID);

        mEditSend     = (EditText) findViewById(R.id.my_service_edit_send);
        mButtonSend   = (Button)   findViewById(R.id.my_service_btn_send);
        mTextRead     = (TextView) findViewById(R.id.my_service_text_read);
        mButtonRead   = (Button)   findViewById(R.id.my_service_btn_read);
        mTextNotify   = (TextView) findViewById(R.id.my_service_text_notify);
        mButtonNotify = (Button)   findViewById(R.id.my_service_button_notify);

        mButtonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Read characteristic");
                mBluetoothLeService.readCharacteristic(mReadCharacteristic);
            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Send characteristic");

                String text = mEditSend.getText().toString();
                byte[] bytes = new byte[1];
                bytes[0] = (byte)text.charAt(0);
                mSendCharacteristic.setValue(bytes);
                mSendCharacteristic.setWriteType(WRITE_TYPE_NO_RESPONSE);
                mBluetoothLeService.writeCharacteristic(mSendCharacteristic);
            }
        });

        mButtonNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, !mNotifyEnabled);
                mNotifyEnabled = !mNotifyEnabled;
            }
        });

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "Service connected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) iBinder).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            for (BluetoothGattService s : mBluetoothLeService.getSupportedGattServices()) {
                if (s.getUuid().toString().equals(mServiceUuid)) {
                    mGattService = s;
                    break;
                }
            }

            if (mGattService == null) {
                Log.w(TAG, "Service not found");
                Toast.makeText(MyServiceActivity.this, "Service not found", Toast.LENGTH_SHORT).show();
                return;
            }

            mReadCharacteristic = mGattService.getCharacteristic(UUID_CHAR_READ);
            if (mReadCharacteristic != null) { Log.d(TAG, "Read characteristic found"); }
            mSendCharacteristic = mGattService.getCharacteristic(UUID_CHAR_SEND);
            if (mSendCharacteristic != null) { Log.d(TAG, "Send characteristic found"); }
            mNotifyCharacteristic = mGattService.getCharacteristic(UUID_CHAR_NOTIFY);
            if (mNotifyCharacteristic != null) { Log.d(TAG, "Notify characteristic found"); }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Service disconnected");
            mBluetoothLeService = null;
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "Disconnected: ending...");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, "New data available");

                String uuid = intent.getStringExtra(BluetoothLeService.EXTRA_CHARACTERISTIC_UUID);
                final BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(uuid));
                if (characteristic == null) {
                    Log.w(TAG, "Data available but characteristic not found");
                    return;
                }

                final String data = byteArrayToHex(characteristic.getValue());
                Log.d(TAG, "Data=" + data);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Data: " + data);

                        if (characteristic.getUuid().equals(UUID_CHAR_READ)) {
                            mTextRead.setText(String.valueOf(data));
                        }
                        else if (characteristic.getUuid().equals(UUID_CHAR_NOTIFY)) {
                            mTextNotify.setText(String.valueOf(data));
                        }
                    }
                });
            }
        }
    };


    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
