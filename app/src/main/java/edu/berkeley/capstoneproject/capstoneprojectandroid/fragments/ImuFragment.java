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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import edu.berkeley.capstoneproject.capstoneprojectandroid.CapstoneProjectAndroidApplication;
import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.database.AppDatabase;
import edu.berkeley.capstoneproject.capstoneprojectandroid.database.Patient;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Feather52;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.PatientHolder;
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

    private AppDatabase mdb;
    private final float tolerance = 0.5f;
    private float bendCount = 0;
    private float prevDegree = 0;
    private float curDegree = 0;
    private float avgDegreeSum = 0;
    private float maxDegree = 0;
    private ArrayList<Float> Degree;
    private float nextDegree;
    private Patient mpatient;
    private Timer timer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_imu, container, false);

        mAccView = mView.findViewById(R.id.fragment_imu_linechart_acc);
        mGyrView = mView.findViewById(R.id.fragment_imu_linechart_gyr);
        mDegText = mView.findViewById(R.id.fragment_degree);

        initLineChart(mAccView);
        initLineChart(mGyrView);
        mdb = AppDatabase.getAppDatabase(getActivity().getApplicationContext());
        mpatient = mdb.userDao().findByUid(PatientHolder.getUid());
        recordDegree();

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

//                prevDegree = curDegree;
//                curDegree = nextDegree;
                nextDegree = calBendDegree(gyr1, gyr2);

//                if (Degree[0] != prevDegree)
//                    Degree[0] = prevDegree;
//                if (Degree[1] != curDegree)
//                    Degree[1] = curDegree;
//                if (Degree[2] != nextDegree)
//                    Degree[2] = nextDegree;
                mDegText.setText("Bend Degree : "+String.valueOf(nextDegree));
            }

        }
    };

    @Override
    public void onDestroy() {
        ArrayList<Float> dailyBendCount = mpatient.getDailyBendCount();
        ArrayList<Float> dailyAvg = mpatient.getDailyAvgDegree();
        ArrayList<Float> dailyMax = mpatient.getDailyMaxDegree();
        float pCount = dailyBendCount.get(dailyBendCount.size() - 1);
        float pAvg = dailyAvg.get(dailyAvg.size() - 1);
        dailyAvg.set(dailyAvg.size() - 1, (pCount * pAvg + avgDegreeSum)/(pCount + bendCount));
        Log.i("Update Avg Degree", String.valueOf(pAvg) + " to " + dailyAvg.get(dailyAvg.size() - 1));
//                pAvg * (pCount / (pCount + bendCount)) + avgDegree * (bendCount / (bendCount + pCount));
        dailyBendCount.set(dailyBendCount.size() - 1, pCount + bendCount);
        if (maxDegree > dailyMax.get(dailyMax.size() - 1)) {
            dailyMax.set(dailyMax.size() - 1, maxDegree);
        }
        timer.cancel();
        mdb.userDao().insert(mpatient);
        super.onDestroy();
    }

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
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
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
        float angle1 = gyr1 > 0 ? 180-gyr1 : 180- gyr1;
//        float angle2 = 180 - gyr2;
        float angle2 = gyr2 > 0 ? 360-gyr2 : - gyr2;

        float angle =  (angle2 > 180) ? 180-(angle2 - angle1) : 180-(angle2 - angle1 + 360);
        Log.d("Degree", String.valueOf(angle));
        return angle%360;
    }

    private void recordDegree() {
        if (Degree == null) {
            Degree = new ArrayList<>();
        }

        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run() {
                Log.d("Thread", "Before add Degree");
                if (Degree.size() == 0 || Math.abs(Degree.get(Degree.size() - 1) - nextDegree) > tolerance) {
                    Degree.add(nextDegree);
                }
                float t = findLocalMax();
                if (t != -1) {
                    bendCount ++;
                    avgDegreeSum += t;
                    if (t > maxDegree) {
                        maxDegree = t;
                    }
                }
            }
        }, 0, 1000);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
    }

    private float findLocalMax() {

//        return (Degree[1] - Degree[0] > tolerance && Degree[1] - Degree[2] > tolerance);
        final int sz = Degree.size();

        if (sz > 3 && Degree.get(sz - 3) < Degree.get(sz - 2) && Degree.get(sz - 2) > Degree.get(sz - 1)) {
            Log.d("CheckMax", "1: " + String.valueOf(Degree.get(sz-3)) + " 2: " + String.valueOf(Degree.get(sz-2)) + " 3: " + String.valueOf(Degree.get(sz-1)));
            return Degree.get(sz - 2);
        }
        else
            return -1;
    }
}
