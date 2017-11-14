package edu.berkeley.capstoneproject.capstoneprojectandroid.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.services.BluetoothService;

import static edu.berkeley.capstoneproject.capstoneprojectandroid.services.BluetoothService.BLUETOOTH_SERVICE_TOAST;

public class DeviceActivity extends Activity {

    private static final String TAG = "DeviceActivity";

    public static final String EXTRA_DEVICE_ADDRESS = "device_address";

    public static final int HANDLER_MSG_STATE = 0;
    public static final int HANDLER_MSG_WRITE = 1;
    public static final int HANDLER_MSG_READ = 2;
    public static final int HANDLER_MSG_TOAST = 3;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;

    private EditText mEditBluetoothSend;
    private TextView mTextBluetoothRead;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "Handler message: " + msg.what);
            switch(msg.what) {
                case HANDLER_MSG_STATE:
                    switch(mService.getState()) {
                        case NONE:
                            Toast.makeText(DeviceActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                            break;
                        case CONNECTING:
                            Toast.makeText(DeviceActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
                            break;
                        case CONNECTED:
                            Toast.makeText(DeviceActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case HANDLER_MSG_WRITE:
                    mEditBluetoothSend.setText("");
                    break;
                case HANDLER_MSG_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mTextBluetoothRead.setText(
                            readMessage + "\n" + mTextBluetoothRead.getText().toString()
                    );
                    break;
                case HANDLER_MSG_TOAST:
                    Toast.makeText(DeviceActivity.this, msg.getData().getString(BLUETOOTH_SERVICE_TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private BluetoothService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        String address = getIntent().getStringExtra(EXTRA_DEVICE_ADDRESS);
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);

        if (mBluetoothDevice == null) {
            Log.e(TAG, "No bluetooth device found");
        }

        mService = new BluetoothService(mHandler, mBluetoothDevice);

        setTitle(mBluetoothDevice.getName());

        Button buttonSend = (Button) findViewById(R.id.button_bluetooth_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mEditBluetoothSend.getText().toString();
                mService.write(msg.getBytes());
            }
        });

        mEditBluetoothSend = (EditText) findViewById(R.id.edit_bluetooth_send);
        mTextBluetoothRead = (TextView) findViewById(R.id.text_bluetooth_received);

        mService.connect();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(DeviceActivity.this, MainActivity.class);

            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mService != null) {
            mService.stop();
        }
        super.onDestroy();
    }
}
