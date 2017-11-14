package edu.berkeley.capstoneproject.capstoneprojectandroid.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.berkeley.capstoneproject.capstoneprojectandroid.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Alex on 17/10/2017.
 */

public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    private static final String TAG = "BluetoothDeviceAdapter";

    private LayoutInflater mInflater;
    private List<BluetoothDevice> mDevices;

    public BluetoothDeviceAdapter(@NonNull Context context, @LayoutRes int resource, List<BluetoothDevice> bluetoothDevices) {
        super(context, resource, bluetoothDevices);
        mDevices = bluetoothDevices;
        mInflater = LayoutInflater.from(context);
    }


    public class Holder {
        TextView mTextName;
        TextView mTextAddress;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder = new Holder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.bluetooth_device, null);
            holder.mTextName = (TextView) convertView.findViewById(R.id.text_bluetooth_name);
            holder.mTextAddress = (TextView) convertView.findViewById(R.id.text_bluetooth_address);

            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }

        BluetoothDevice bluetoothDevice = getItem(position);
        if (bluetoothDevice != null) {
            holder.mTextName.setText(bluetoothDevice.getName());
            holder.mTextAddress.setText(bluetoothDevice.getAddress());
        }

        return convertView;
    }


    public boolean contains(BluetoothDevice device) {
        return mDevices.contains(device);
    }
}
