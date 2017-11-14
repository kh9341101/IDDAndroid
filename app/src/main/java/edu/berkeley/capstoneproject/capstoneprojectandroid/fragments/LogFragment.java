package edu.berkeley.capstoneproject.capstoneprojectandroid.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;

import java.util.Date;

import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.services.Feather52Service;

import static edu.berkeley.capstoneproject.capstoneprojectandroid.services.Feather52Service.EXTRA_LABEL;
import static edu.berkeley.capstoneproject.capstoneprojectandroid.services.Feather52Service.EXTRA_TOOK_AT;
import static edu.berkeley.capstoneproject.capstoneprojectandroid.services.Feather52Service.EXTRA_VALUE;

/**
 * Created by Alex on 31/10/2017.
 */

public class LogFragment extends Feather52Fragment {

    private static final String TAG = LogFragment.class.getSimpleName();

    private View mView;
    private TextView mLogView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_log, container, false);
        mLogView = (TextView) mView.findViewById(R.id.feather52_log_textview);
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


    private void appendLog(String content) {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm:ss");
        mLogView.setText(
                dateFormat.format(new Date()) + " $ " + content + "\n" + mLogView.getText().toString()
        );
    }

    private void appendLogValue(String label, long tookAt, float value) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(label + ": ");
        stringBuilder.append("time=" + tookAt + " ");
        stringBuilder.append("value=" + value);
        appendLog(stringBuilder.toString());
    }


    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Feather52Service.ACTION_DATA_AVAILABLE.equals(action)) {
                String label = intent.getStringExtra(EXTRA_LABEL);
                long tookAt = intent.getLongExtra(EXTRA_TOOK_AT, 0);
                float value = intent.getFloatExtra(EXTRA_VALUE, 0);

                appendLogValue(label, tookAt, value);
            }
        }
    };

    protected IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Feather52Service.ACTION_DATA_AVAILABLE);

        return filter;
    }

    @Override
    public void clearUi() {
        mLogView.setText("");
    }
}
