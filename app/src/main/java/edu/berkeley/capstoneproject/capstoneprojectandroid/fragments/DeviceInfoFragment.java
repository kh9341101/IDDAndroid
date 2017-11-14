package edu.berkeley.capstoneproject.capstoneprojectandroid.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.berkeley.capstoneproject.capstoneprojectandroid.CapstoneProjectAndroidApplication;
import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.activities.Feather52Activity;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Feather52;
import edu.berkeley.capstoneproject.capstoneprojectandroid.services.Feather52Service;

/**
 * Created by Alex on 01/11/2017.
 */

public class DeviceInfoFragment extends Feather52Fragment {

    private static final String TAG = DeviceInfoFragment.class.getSimpleName();

    private final Feather52 mFeather52 = CapstoneProjectAndroidApplication.getInstance().getFeather52();

    private View mView;
    private TextView mMacView;
    private TextView mBondedView;
    private TextView mConnectionView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_device_info, container, false);

        mMacView = (TextView) mView.findViewById(R.id.fragment_device_info_textview_mac);
        mBondedView = (TextView) mView.findViewById(R.id.fragment_device_info_textview_bonded);
        mConnectionView = (TextView) mView.findViewById(R.id.fragment_device_info_textview_state);

        final BluetoothDevice device = mFeather52.getBluetoothDevice();
        if (device != null) {
            mMacView.setText(device.getAddress());
            switch(device.getBondState()) {
                case BluetoothDevice.BOND_BONDED:
                    mBondedView.setText("Yes");
                    break;
            }
        }

        Feather52Service service = ((Feather52Activity) getActivity()).getFeather52Service();
        if (service != null) {
            setConnectionState(service.getConnectionState());
        }

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBroadcastReceiver, getIntentFilter());
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void clearUi() {

    }



    private void setConnectionState(int state) {
        switch (state) {
            case Feather52Service.STATE_DISCONNECTED:
                mConnectionView.setText("Disconnected");
                mConnectionView.setTextColor(Color.RED);
                break;
            case Feather52Service.STATE_CONNECTING:
                mConnectionView.setText("Connecting");
                mConnectionView.setTextColor(Color.YELLOW);
                break;
            case Feather52Service.STATE_CONNECTED:
                mConnectionView.setText("Connected");
                mConnectionView.setTextColor(Color.GREEN);
                break;
        }
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Feather52Service.ACTION_GATT_CONNECTED)) {
                setConnectionState(Feather52Service.STATE_CONNECTED);
            }
            else if (action.equals(Feather52Service.ACTION_GATT_DISCONNECTED)) {
                setConnectionState(Feather52Service.STATE_CONNECTING);
            }
        }
    };

    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Feather52Service.ACTION_GATT_CONNECTED);
        filter.addAction(Feather52Service.ACTION_GATT_DISCONNECTED);
        return filter;
    }

}
