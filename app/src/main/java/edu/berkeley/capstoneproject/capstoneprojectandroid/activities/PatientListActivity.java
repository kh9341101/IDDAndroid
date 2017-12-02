package edu.berkeley.capstoneproject.capstoneprojectandroid.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import java.util.ArrayList;

import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.listeners.RecyclerItemClickListener;
import edu.berkeley.capstoneproject.capstoneprojectandroid.adapters.PatientAdapter;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.AppDatabase;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Patient;
import static edu.berkeley.capstoneproject.capstoneprojectandroid.activities.ProfileActivity.patientListActivity;
import static edu.berkeley.capstoneproject.capstoneprojectandroid.activities.ProfileActivity.previousActivity;

public class PatientListActivity extends AppCompatActivity {

    private ShimmerRecyclerView shimmerRecycler;
    private PatientAdapter mAdapter;
    private AppDatabase mdb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView.LayoutManager layoutManager;
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_patient_list);
        layoutManager = new LinearLayoutManager(this);
        setTitle(R.string.ab_list_title1);

        shimmerRecycler = (ShimmerRecyclerView) findViewById(R.id.shimmer_recycler_view);

//        if (demoConfiguration.getItemDecoration() != null) {
//            shimmerRecycler.addItemDecoration(demoConfiguration.getItemDecoration());
//        }

        mAdapter = new PatientAdapter();

        shimmerRecycler.setLayoutManager(layoutManager);
        shimmerRecycler.setAdapter(mAdapter);
        shimmerRecycler.showShimmerAdapter();
        shimmerRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(this, shimmerRecycler ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        Patient p = mAdapter.getItemAtPostition(position);
                        Log.i("Patient Selected", p.getTitle() + " " + p.getUid() + " " + String.valueOf(position));
                        Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                        intent.putExtra(previousActivity, patientListActivity);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
//        mAdapter.setOnPatientSelectedListener(new PatientAdapter.onPatientSelectedListener() {
//            @Override
//            public void onPatientSelected(View v, int position) {
//                Log.i("Patient Selected", String.valueOf(position));
//                Intent intent = new Intent(this, ProfileActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        mdb = AppDatabase.getAppDatabase(getApplicationContext());

        shimmerRecycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayPatient();
            }
        }, 1000);
    }

    private void displayPatient() {
//        Patient[] plist = getPatient(getResources());
//        mdb.userDao().insertAll(plist);
//        Patient p = new Patient("Alan", "fuck", getResources().getString(R.string.ndtv_image_url), "2018/1/1");
//        mdb.userDao().insert(p);
        ArrayList<Patient> tmp = new ArrayList<Patient>(mdb.userDao().getAll());
        mAdapter.setCards(tmp);
        shimmerRecycler.hideShimmerAdapter();
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    public static ArrayList<Patient> getPatient(Resources resources) {

        String title = "Annie\t";
        String image = resources.getString(R.string.ndtv_image_url);
        String desc = "1234567";
        String summary = "2017/5/5/";

        final Patient patient1 = new Patient(title, desc, image, summary);

        title = "Annie";
        image = resources.getString(R.string.op_image_url);
        desc = "1234567";
        summary = "2017/5/5/";

        final Patient patient2 = new Patient(title, desc, image, summary);


        title = "Annie";
        image = resources.getString(R.string.got_image_url);
        desc = "1234567";
        summary = "2017/5/5/";

        final Patient patient3 = new Patient(title, desc, image, summary);

        title = "Annie";
        image = resources.getString(R.string.jet_image_url);
        desc = "1234567";
        summary = "2017/5/5/";

        final Patient patient4 = new Patient(title, desc, image, summary);

        title = "Annie";
        image = resources.getString(R.string.jet_image_url);
        desc = "1234567";
        summary = "2017/5/5/";

        final Patient patient5 = new Patient(title, desc, image, summary);

        title = "Annie";
        image = resources.getString(R.string.jet_image_url);
        desc = "1234567";
        summary = "2017/5/5/";

        final Patient patient6 = new Patient(title, desc, image, summary);

        ArrayList<Patient> ret = new ArrayList<Patient>() {{
            add(patient1);
            add(patient2);
            add(patient3);
            add(patient4);
            add(patient5);
            add(patient6);

        }};
        return ret;
//        return new Patient[]{patient1, patient2, patient3, patient4, patient5, patient6};
    }
}
