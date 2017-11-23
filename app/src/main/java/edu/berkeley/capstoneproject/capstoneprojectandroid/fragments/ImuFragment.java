package edu.berkeley.capstoneproject.capstoneprojectandroid.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.HashMap;
import java.util.Map;

import edu.berkeley.capstoneproject.capstoneprojectandroid.CapstoneProjectAndroidApplication;
import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Feather52;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.Encoder;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.sensors.IMU;
import edu.berkeley.capstoneproject.capstoneprojectandroid.services.Feather52Service;

import static edu.berkeley.capstoneproject.capstoneprojectandroid.services.Feather52Service.EXTRA_LABEL;
import static edu.berkeley.capstoneproject.capstoneprojectandroid.services.Feather52Service.EXTRA_TOOK_AT;
import static edu.berkeley.capstoneproject.capstoneprojectandroid.services.Feather52Service.EXTRA_VALUE;

/**
 * Created by Alex on 25/10/2017.
 */

public class ImuFragment extends Feather52Fragment {

    private static final String TAG = ImuFragment.class.getSimpleName();

    public static final String EXTRA_SENSOR_ID = "EXTRA_SENSOR_ID";

    private Feather52Service mFeather52Service;

    private final Feather52 mFeather52 = CapstoneProjectAndroidApplication.getInstance().getFeather52();
    private IMU mImu;

    private float gyr1;
    private float gyr2;

    private View mView;
    private LineChart mAccView;
    private LineChart mGyrView;
    private LineChart mEncoderView;
    private TextView mDegText;

    private int[] mColors = new int[] {
            ColorTemplate.COLORFUL_COLORS[0],
            ColorTemplate.COLORFUL_COLORS[1],
            ColorTemplate.COLORFUL_COLORS[2],
            ColorTemplate.LIBERTY_COLORS[0],
            ColorTemplate.LIBERTY_COLORS[1],
            ColorTemplate.LIBERTY_COLORS[2],
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_imu, container, false);

        mAccView = (LineChart) mView.findViewById(R.id.fragment_imu_linechart_acc);
        mGyrView = (LineChart) mView.findViewById(R.id.fragment_imu_linechart_gyr);
        mDegText = (TextView) mView.findViewById(R.id.fragment_degree);
//        mEncoderView = (LineChart) mView.findViewById(R.id.fragment_imu_linechart_encoder);

        initLineChart(mAccView);
        initLineChart(mGyrView);
//        initLineChart(mEncoderView);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBroadcastReceiver, getIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private LineDataSet createSet(String label, int index) {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        int color = mColors[index % mColors.length];

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(color);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        set.setLabel(label);
        return set;
    }


    @Override
    public void clearUi() {
        clearLineChart(mAccView);
        clearLineChart(mGyrView);
//        clearLineChart(mEncoderView);
    }




    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Feather52Service.ACTION_DATA_AVAILABLE.equals(action)) {
                String label = intent.getStringExtra(EXTRA_LABEL);
                long tookAt = intent.getLongExtra(EXTRA_TOOK_AT, 0);
                float value = intent.getFloatExtra(EXTRA_VALUE, 0);
                Log.d("Receive Data", label + " " + value);
                addDataEntry(label, new Entry(tookAt, value));

                if (label.equals(IMU.LABEL_IMU_ACC_Z))
                    gyr1 = value;
                if (label.equals(IMU.LABEL_IMU_GYR_Z))
                    gyr2 = value;

                mDegText.setText(String.valueOf(calBendDegree(gyr1, gyr2)));
            }

        }
    };

    private void addDataEntry(String label, Entry e) {

        LineChart lineChart = null;
        if (label.equals(IMU.LABEL_IMU_ACC_X) || label.equals(IMU.LABEL_IMU_ACC_Y) || label.equals(IMU.LABEL_IMU_ACC_Z)) {
            lineChart = mAccView;
        }
        else if (label.equals(IMU.LABEL_IMU_GYR_X) || label.equals(IMU.LABEL_IMU_GYR_Y) || label.equals(IMU.LABEL_IMU_GYR_Z)) {
            lineChart = mGyrView;
        }
        else if (label.equals(Encoder.LABEL_ENCODER_ANGLE)) {
//            lineChart = mEncoderView;
        }

        if (lineChart == null) {
            return;
        }

        LineData data = lineChart.getLineData();
        if (data == null) {
            return;
        }

        ILineDataSet set = data.getDataSetByLabel(label, true);

        if(set == null) {
            set = createSet(label, data.getDataSetCount());
            data.addDataSet(set);
        }
        set.addEntry(e);
        //data.addEntry(e, 0);
        data.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.setVisibleXRangeMaximum(10000);
        lineChart.moveViewTo(e.getX(), e.getY(), YAxis.AxisDependency.LEFT);

        // TODO Autoscale

    }


    private void initLineChart(LineChart lineChart) {
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        lineChart.setData(data);

        XAxis xl = lineChart.getXAxis();
        //xl.setTypeface(mTfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void clearLineChart(LineChart lineChart) {
        LineData data = lineChart.getLineData();
        if (data != null) { data.clearValues(); }
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    protected IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Feather52Service.ACTION_DATA_AVAILABLE);

        return filter;
    }

    private float calBendDegree(float gyr1, float gyr2) {
        float angle1 = gyr1 > 0 ? gyr1 : 360 + gyr1;
//        float angle2 = 180 - gyr2;
        float angle2 = gyr2 > 0 ? gyr2 : 360 + gyr2;

        float angle =  (angle1 > 180) ? (angle1 - angle2) : (angle1 - angle2 + 360);
        Log.d("Degree", String.valueOf(angle));
        return angle%360;
    }

}
