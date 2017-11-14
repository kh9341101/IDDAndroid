package edu.berkeley.capstoneproject.capstoneprojectandroid.adapters;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.SampleGattAttributes;

/**
 * Created by Alex on 22/10/2017.
 */

public class BluetoothGattServiceAdapter extends ArrayAdapter<BluetoothGattService> {

    private static final String TAG = "BluetoothDeviceAdapter";

    private LayoutInflater mInflater;

    public BluetoothGattServiceAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        mInflater = LayoutInflater.from(context);
    }

    public BluetoothGattServiceAdapter(@NonNull Context context, @LayoutRes int resource, List<BluetoothGattService> services) {
        super(context, resource, services);
        mInflater = LayoutInflater.from(context);
    }


    public class Holder {
        TextView mTextName;
        TextView mTextUuid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_2, null);

            holder = new Holder();
            holder.mTextName = convertView.findViewById(android.R.id.text1);
            holder.mTextUuid = convertView.findViewById(android.R.id.text2);

            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }

        BluetoothGattService service = getItem(position);
        if (service != null) {
            holder.mTextName.setText(SampleGattAttributes.lookup(service.getUuid().toString(), "Unknown service"));
            holder.mTextUuid.setText(service.getUuid().toString());
        }

        return convertView;
    }
}
