package edu.berkeley.capstoneproject.capstoneprojectandroid.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.berkeley.capstoneproject.capstoneprojectandroid.CapstoneProjectAndroidApplication;
import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.adapters.BluetoothDeviceAdapter;

@TargetApi(21)
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 2;
    private static final int SCAN_PERIOD = 10000;

    private BluetoothManager mBtManager;
    private BluetoothAdapter mBtAdapter;
    private BluetoothLeScanner mLeScanner;

    private ScanSettings mSettings;
    private List<ScanFilter> mFilters;

    private Button mScanButton;
    private BluetoothDeviceAdapter mPairedDevicesAdapter, mScannedDevicesAdapter;
    private ListView mPairedListView, mScannedListView;

    private boolean mScanning = false;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth LE required !", Toast.LENGTH_SHORT).show();
            finish();
        }

        mBtManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = mBtManager.getAdapter();

        initViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestBluetoothPermissions();
        }

        //registerBtReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mSettings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                mFilters = new ArrayList<ScanFilter>();
            }
            setupScanner();
            populatePairedDevices();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                mScanButton.setEnabled(true);
            }
            else {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);


                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                        ) {
                    // All Permissions Granted

                    // Permission Denied
                    Toast.makeText(MainActivity.this, "All Permission GRANTED !! Thank You :)", Toast.LENGTH_SHORT)
                            .show();


                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "One or More Permissions are DENIED Exiting App :(", Toast.LENGTH_SHORT)
                            .show();

                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void requestBluetoothPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Show Location");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {

                // Need Rationale
                String message = "App need access to " + permissionsNeeded.get(0);

                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        Toast.makeText(MainActivity.this, "No new Permission Required- Launching App .You are Awesome!!", Toast.LENGTH_SHORT)
                .show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {

        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }


    private void initViews() {
        mPairedDevicesAdapter = new BluetoothDeviceAdapter(this, R.layout.bluetooth_device, new ArrayList<BluetoothDevice>());
        mScannedDevicesAdapter = new BluetoothDeviceAdapter(this, R.layout.bluetooth_device, new ArrayList<BluetoothDevice>());

        mPairedListView = (ListView) findViewById(R.id.list_bluetooth_paired_devices);
        mPairedListView.setAdapter(mPairedDevicesAdapter);
        mPairedListView.setOnItemClickListener(mDeviceClickListener);

        mScannedListView = (ListView) findViewById(R.id.list_bluetooth_scanned_devices);
        mScannedListView.setAdapter(mScannedDevicesAdapter);
        mScannedListView.setOnItemClickListener(mDeviceClickListener);

        mScanButton = (Button) findViewById(R.id.button_bluetooth_scan);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Start Discovery");

                mScannedDevicesAdapter.clear();
                mScannedDevicesAdapter.notifyDataSetChanged();

                scanLeDevices(true);
                view.setVisibility(View.GONE);
            }
        });

        enableScanButton();
    }


    private void enableScanButton() {
        mScanButton.setEnabled(mBtAdapter != null && mBtAdapter.isEnabled());
    }


    private void registerBtReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
    }



    private void setupScanner() {
        if (mBtAdapter != null) {
            mLeScanner = mBtAdapter.getBluetoothLeScanner();
        }
    }


    private void populatePairedDevices() {
        Log.d(TAG, "Populating paired devices");

        mPairedDevicesAdapter.clear();

        if (mBtAdapter != null && mBtAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
            mPairedDevicesAdapter.addAll(pairedDevices);

            if (pairedDevices.size() == 0) {
                Toast.makeText(this, "No paired device", Toast.LENGTH_SHORT).show();
            }
        }

        mPairedDevicesAdapter.notifyDataSetChanged();
    }

    private void addScannedDevice(BluetoothDevice device) {
        if (!mBtAdapter.getBondedDevices().contains(device)) {
            if (!mScannedDevicesAdapter.contains(device)) {
                mScannedDevicesAdapter.add(device);
                mScannedDevicesAdapter.notifyDataSetChanged();
            }
        }
    }

    private void scanLeDevices(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    if (Build.VERSION.SDK_INT < 21) {
                        mBtAdapter.stopLeScan(mScanCallback18);
                    }
                    else {
                        mLeScanner.stopScan(mScanCallback21);
                    }
                }
            }, SCAN_PERIOD);

            Log.d(TAG, "Start scanning");
            mScanning = true;
            if (Build.VERSION.SDK_INT < 21) {
                mBtAdapter.startLeScan(mScanCallback18);
            }
            else {
                mLeScanner.startScan(mFilters, mSettings, mScanCallback21);
            }
        }
        else {
            mScanning = false;
            if (Build.VERSION.SDK_INT < 21) {
                mBtAdapter.stopLeScan(mScanCallback18);
            }
            else {
                mLeScanner.stopScan(mScanCallback21);
            }
        }
    }


    private BluetoothAdapter.LeScanCallback mScanCallback18 = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "New LE device scanned (API 18)");
                    addScannedDevice(bluetoothDevice);
                }
            });
        }
    };


    private ScanCallback mScanCallback21 = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, "New LE device scanned (API 21)");
            addScannedDevice(result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d(TAG, "Scan finished");
            if (results.size() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "No device found", Toast.LENGTH_SHORT).show();
                        mScanButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Scan failed (API 21)");
        }
    };


    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            CapstoneProjectAndroidApplication.getInstance().getFeather52().setBluetoothDevice((BluetoothDevice) adapterView.getItemAtPosition(i));

            if (mScanning) {
                scanLeDevices(false);
                mScanning = false;
            }

            Intent intent = new Intent(adapterView.getContext(), Feather52Activity.class);
            startActivity(intent);
            finish();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                Log.d(TAG, "Bluetooth state changed");
                setupScanner();
                populatePairedDevices();
                enableScanButton();
            }
            // When discovery finds a device
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d(TAG, "New device found");
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                addScannedDevice(device);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "Discovery finished");
                if (mScannedDevicesAdapter.getCount() == 0) {
                    Log.d(TAG, "No device found");
                    Toast.makeText(context, "No device found", Toast.LENGTH_SHORT).show();
                    mScanButton.setVisibility(View.VISIBLE);
                }
            }


        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBtAdapter != null) {
                mBtAdapter.cancelDiscovery();
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        //this.unregisterReceiver(mReceiver);

        super.onDestroy();
    }
}
