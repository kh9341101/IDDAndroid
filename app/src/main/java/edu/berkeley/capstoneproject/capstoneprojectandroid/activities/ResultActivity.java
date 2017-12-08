package edu.berkeley.capstoneproject.capstoneprojectandroid.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.berkeley.capstoneproject.capstoneprojectandroid.database.Patient;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.PatientHolder;
import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.database.AppDatabase;

public class ResultActivity extends AppCompatActivity {
    int pStatus = 0;

    private AppDatabase mdb;
    private Handler handler = new Handler();

    @BindView(R.id.tv)                  TextView tv;
    @BindView(R.id.circularProgressbar) ProgressBar mProgress;
    @BindView(R.id.degreechart)         LineChart mChart;
    @BindView(R.id.dailyavg)            TextView mAvg;
    @BindView(R.id.dailymax)            TextView mMax;
    @BindView(R.id.dailybendcount)      TextView mBendCount;


    private Patient mpatient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_new);
        ButterKnife.bind(this);
//        Resources res = getResources();
//        final ProgressBar mProgress = findViewById(R.id.circularProgressbar);

        mdb = AppDatabase.getAppDatabase(getApplicationContext());
        mpatient = mdb.userDao().findByUid(PatientHolder.getUid());
        Drawable drawable = getResources().getDrawable(R.drawable.circular);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

      /*  ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
        animation.setDuration(50000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();*/
//        addData2Chart();
        String t = "Today's Avg: " + String.valueOf(Math.round(mpatient.getDailyAvgDegree().get(mpatient.getDailyAvgDegree().size() - 1))) + "°";
        mAvg.setText(t);
        t = "Today's Max: " + String.valueOf(Math.round(mpatient.getDailyMaxDegree().get(mpatient.getDailyMaxDegree().size() - 1))) + "°";
        mMax.setText(t);
        t = "Bend Count: " + String.valueOf(Math.round(mpatient.getDailyBendCount().get(mpatient.getDailyBendCount().size() - 1)));
        mBendCount.setText(t);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus < 79.46) {
                    pStatus += 1;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgress.setProgress(pStatus);
                            tv.setText(pStatus + "%");

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(8); //thread will take approx 1.5 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        initChart();
        setData(7, 100);
        mChart.animateX(1000);
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResultActivity.this, ProfileActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    private LineDataSet createSet(String label) {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setColor(color);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
//        set.setFillColor(color);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        set.setLabel(label);
        return set;
    }


    private void initChart() {
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        Description description = new Description();
        description.setText("");
        mChart.setDescription(description);

    }

    private void setData(int count, float range) {

        ArrayList<Entry> recommendValues = new ArrayList<>();
        recommendValues.add(new Entry(0, 30));
        recommendValues.add(new Entry(1, 38));
        recommendValues.add(new Entry(2, 46));
        recommendValues.add(new Entry(3, 55));
        recommendValues.add(new Entry(4, 65));
        recommendValues.add(new Entry(5, 80));
        recommendValues.add(new Entry(6, 95));
        LineDataSet defaultSet = new LineDataSet(recommendValues, "Target Degree");
        defaultSet.setDrawIcons(false);
        // set the line to be drawn like this "- - - - - -"
        defaultSet.enableDashedLine(10f, 5f, 0f);
        defaultSet.enableDashedHighlightLine(10f, 5f, 0f);
        defaultSet.setColor(Color.YELLOW);
        defaultSet.setCircleColor(Color.YELLOW);
        defaultSet.setLineWidth(1f);
        defaultSet.setCircleRadius(3f);
        defaultSet.setDrawCircleHole(false);
        defaultSet.setValueTextSize(9f);
//        defaultSet.setDrawFilled(true);
        defaultSet.setFormLineWidth(1f);
        defaultSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        defaultSet.setFormSize(15.f);





        ArrayList<Entry> values = new ArrayList<Entry>();
//        ArrayList<Float> t = mdb.userDao().findByUid(PatientHolder.getUid()).getDailyAvgDegree();
        ArrayList<Float> t = mpatient.getDailyAvgDegree();
        for (int i = 0; i < t.size(); i++) {

//            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, t.get(i)));
        }


        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Daily Avg Degree");

            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
//            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

//            if (Utils.getSDKInt() >= 18) {
//                // fill drawable only supported on api level 18 and above
//                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
//                set1.setFillDrawable(drawable);
//            }
//            set1.setFillColor(Color.BLACK);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets
            dataSets.add(defaultSet);

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            XAxis xAxis = mChart.getXAxis();
//            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return getnPreviousDateString((int) value - 6);
                }
            });

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return String.valueOf(value) + '°';

                }
            });

            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setEnabled(false);
//            mChart.setDraw(false);
            // set data
            mChart.setData(data);
        }
    }
    private Date previousDate(int n) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, n);
        return cal.getTime();
    }

    private String getnPreviousDateString(int n) {
        DateFormat dateFormat = new SimpleDateFormat("EEE");
        return dateFormat.format(previousDate(n));
    }
}