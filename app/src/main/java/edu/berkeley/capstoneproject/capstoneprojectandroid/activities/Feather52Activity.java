package edu.berkeley.capstoneproject.capstoneprojectandroid.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import edu.berkeley.capstoneproject.capstoneprojectandroid.CapstoneProjectAndroidApplication;
import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.adapters.Feather52DrawerAdapter;
import edu.berkeley.capstoneproject.capstoneprojectandroid.fragments.DeviceInfoFragment;
import edu.berkeley.capstoneproject.capstoneprojectandroid.fragments.ImuFragment;
import edu.berkeley.capstoneproject.capstoneprojectandroid.fragments.LogFragment;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Feather52;
import edu.berkeley.capstoneproject.capstoneprojectandroid.services.BluetoothLeService;
import edu.berkeley.capstoneproject.capstoneprojectandroid.services.Feather52Service;

import edu.berkeley.capstoneproject.capstoneprojectandroid.models.exercises.Exercise;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.exercises.TestExercise;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.measurements.Metric;
import edu.berkeley.capstoneproject.capstoneprojectandroid.network.helpers.ExerciseHelper;
import edu.berkeley.capstoneproject.capstoneprojectandroid.network.helpers.MetricHelper;

/**
 * Created by Alex on 25/10/2017.
 */

public class Feather52Activity extends AppCompatActivity {

    private static final String TAG = Feather52Activity.class.getSimpleName();


    private final Feather52 mFeather52 = CapstoneProjectAndroidApplication.getInstance().getFeather52();

    private BluetoothDevice mBluetoothDevice;
    private String mDeviceName;
    private String mDeviceAddress;

    private Feather52Service mFeather52Service;
    private boolean mConnected;
    private boolean mStarted;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private List<Feather52DrawerAdapter.DrawerItem> mDrawerItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView mRecyclerView;
    private Feather52DrawerAdapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mRecyclerLayoutManager;

    protected void onStart() {
        super.onStart();
        Intent gattServiceIntent = new Intent(this, Feather52Service.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feather52);

        mToolbar = (Toolbar) findViewById(R.id.feather52_toolbar);
        setSupportActionBar(mToolbar);
        setTitle(mFeather52.getName());

        setDrawer();

        mBluetoothDevice = mFeather52.getBluetoothDevice();
        if (mBluetoothDevice == null) {
            Log.e(TAG, "No bluetooth device found");
            finish();
        }
        mDeviceName = mBluetoothDevice.getName();
        mDeviceAddress = mBluetoothDevice.getAddress();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mFeather52Receiver, getIntentFilter());
        if (mFeather52Service != null) {
            final boolean result = mFeather52Service.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mFeather52Receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
        mFeather52Service = null;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Feather52Activity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feather52, menu);
        if (mConnected) {
            menu.findItem(R.id.feather52_menu_start).setVisible(!mStarted);
            menu.findItem(R.id.feather52_menu_stop).setVisible(mStarted);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feather52_menu_start:
                startTestExercise();
                invalidateOptionsMenu();
                return true;
            case R.id.feather52_menu_stop:
                stopTestExercise();
                invalidateOptionsMenu();
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        mRecyclerView = (RecyclerView) findViewById(R.id.feather52_recycler_view);
        mRecyclerView.setHasFixedSize(true);


        mDrawerItems = new ArrayList<>();
        mDrawerItems.add(new Feather52DrawerAdapter.DrawerItem("Device info", android.R.drawable.ic_menu_info_details, DeviceInfoFragment.class));
        mDrawerItems.add(new Feather52DrawerAdapter.DrawerItem("Log", android.R.drawable.ic_btn_speak_now, LogFragment.class));
        mDrawerItems.add(new Feather52DrawerAdapter.DrawerItem("IMU", android.R.drawable.ic_delete, ImuFragment.class));

        mRecyclerAdapter = new Feather52DrawerAdapter(this, mDrawerItems);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mRecyclerLayoutManager);
        mRecyclerAdapter.setOnItemSelectedListener(new Feather52DrawerAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View v, int position) {
                if (position == 0) {

                }
                else {
                    Feather52DrawerAdapter.DrawerItem item = mDrawerItems.get(position - 1);
                    setFragment(item);
                }
            }
        });

        setFragment(mDrawerItems.get(0));

    }

    private void setFragment(Feather52DrawerAdapter.DrawerItem item) {
        setFragment(item.getFragmentClass());
        setTitle(item.getTitle());
    }

    private void setFragment(Class<? extends Fragment> fragmentClass) {
        Fragment fragment = null;

        try {
            fragment = fragmentClass.newInstance();
        } catch (InstantiationException e) {
            Log.e(TAG, "Error creating fragment", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Error creating fragment", e);
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.feather52_container, fragment)
                .commit();

        mDrawerLayout.closeDrawers();
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "Service connected");
            mFeather52Service = ((Feather52Service.LocalBinder) iBinder).getService();
            if (!mFeather52Service.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            mFeather52Service.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Service disconnected");
            mFeather52Service = null;
        }
    };



    private final BroadcastReceiver mFeather52Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        Log.d(TAG, "Receiving intent with action: " + action);
        if (Feather52Service.ACTION_GATT_CONNECTED.equals(action)) {
            mConnected = true;
            invalidateOptionsMenu();
        }
        else if (Feather52Service.ACTION_GATT_DISCONNECTED.equals(action)) {
            mConnected = false;
            invalidateOptionsMenu();
        }
        }
    };


    private void startTestExercise() {
        mStarted = true;
        final Exercise exercise = new TestExercise();
        mFeather52.addExercise(exercise);
        exercise.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ExerciseHelper.create(exercise);
                for (Metric m: exercise.getMetrics().values()) {
                    MetricHelper.create(exercise, m);
                }
                mFeather52Service.startRecording();
            }
        }).start();
    }


    private void stopTestExercise() {
        mStarted = false;
        mFeather52Service.stopRecording();
    }

    private static IntentFilter getIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(Feather52Service.ACTION_GATT_CONNECTED);
        intentFilter.addAction(Feather52Service.ACTION_GATT_DISCONNECTED);

        return intentFilter;
    }

    public Feather52Service getFeather52Service() {
        return mFeather52Service;
    }
}
