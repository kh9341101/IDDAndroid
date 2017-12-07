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
import java.util.Arrays;

import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.listeners.RecyclerItemClickListener;
import edu.berkeley.capstoneproject.capstoneprojectandroid.adapters.PatientAdapter;
import edu.berkeley.capstoneproject.capstoneprojectandroid.database.AppDatabase;
import edu.berkeley.capstoneproject.capstoneprojectandroid.database.Patient;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.PatientHolder;
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

        shimmerRecycler = findViewById(R.id.shimmer_recycler_view);

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
                        PatientHolder.setUid(p.getUid());
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
        mdb.userDao().deleteAll();
        mdb.userDao().insertAll(getPatient(getResources()));
        ArrayList<Patient> tmp = new ArrayList<>(mdb.userDao().getAll());
        mAdapter.setCards(tmp);
        shimmerRecycler.hideShimmerAdapter();
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }


    public static Patient[] getPatient(Resources resources) {

        Float[] tmpcount = new Float[] {0f, 0f, 0f, 0f, 0f, 0f, 0f};
        Float[] tmpmax = new Float[] {0f, 0f, 0f, 0f, 0f, 0f, 0f};

        Float[] tmpavg = new Float[] {27.5f, 33f, 38f, 47f, 58f, 70f, 82.5f};
        ArrayList<Float> avgdegree = new ArrayList<>(Arrays.asList(tmpavg));
        ArrayList<Float> bendcount = new ArrayList<>(Arrays.asList(tmpcount));
        ArrayList<Float> maxdegree = new ArrayList<>(Arrays.asList(tmpmax));

        String title = "Annie";
        String image = resources.getString(R.string.ndtv_image_url);
        String desc = "Age: 23\n" +
                "Condition: Anterior Cruciate Ligament Tear\n" +
                "Rehabilitation Period : 2 months";
        String summary = "2017/12/2/";
        Patient patient1 = new Patient(title, desc, image, summary, avgdegree, bendcount, maxdegree);

        title = "Oski ";
        image = resources.getString(R.string.op_image_url);
        desc = "Age: 75\n" +
                "Condition: Fracture of the cruciate ligament\n" +
                "Rehabilitation Period : 3 years";
        summary = "2017/12/2/";
//        avgdegree = {27.5f, 33f, 38f, 47f, 58f, 70f, 82.5f};
        Patient patient2 = new Patient(title, desc, image, summary, avgdegree, bendcount, maxdegree);


        title = "Luke";
        image = resources.getString(R.string.got_image_url);
        desc = "Age: 36\n" +
                "Condition: Meniscus tear\n" +
                "Rehabilitation Period : 3 weeks";
        summary = "2017/12/2/";
//        avgdegree = {27.5f, 33f, 38f, 47f, 58f, 70f, 82.5f};
        Patient patient3 = new Patient(title, desc, image, summary, avgdegree, bendcount, maxdegree);

        title = "Song Yu";
        image = resources.getString(R.string.jet_image_url);
        desc = "Age: 58\n" +
                "Condition: Thigh fracture\n" +
                "Rehabilitation Period : 6 months";
        summary = "2017/12/2/";
//        avgdegree = {27.5f, 33f, 38f, 47f, 58f, 70f, 82.5f};
        Patient patient4 = new Patient(title, desc, image, summary, avgdegree, bendcount, maxdegree);

        title = "Kobe";
        image = resources.getString(R.string.kobe_image_url);
        desc = "Age: 39\n" +
                "Condition:  Torn Achilles Tendon\n" +
                "Rehabilitation Period : 3 years";
        summary = "2017/12/2/";
//        avgdegree = {27.5f, 33f, 38f, 47f, 58f, 70f, 82.5f};
        Patient patient5 = new Patient(title, desc, image, summary, avgdegree, bendcount, maxdegree);

        title = "Rose";
        image = resources.getString(R.string.rose_image_url);
        desc = "Age: 29\n" +
                "Condition:  Anterior cruciate ligament injury\n" +
                "Rehabilitation Period : 5 years";
        summary = "2017/12/2/";
//        avgdegree = {27.5f, 33f, 38f, 47f, 58f, 70f, 82.5f};
        Patient patient6 = new Patient(title, desc, image, summary, avgdegree, bendcount, maxdegree);

        return new Patient[]{patient1, patient2, patient3, patient4, patient5, patient6};
    }

//    public static Patient[] getPatient(Resources resources) {
//
//        String title = "Annie\t";
//        String image = resources.getString(R.string.ndtv_image_url);
//        String desc = "1234567";
//        String summary = "2017/5/5/";
//
//        final Patient patient1 = new Patient(title, desc, image, summary);
//
//        title = "Annie";
//        image = resources.getString(R.string.op_image_url);
//        desc = "1234567";
//        summary = "2017/5/5/";
//
//        final Patient patient2 = new Patient(title, desc, image, summary);
//
//
//        title = "Annie";
//        image = resources.getString(R.string.got_image_url);
//        desc = "1234567";
//        summary = "2017/5/5/";
//
//        final Patient patient3 = new Patient(title, desc, image, summary);
//
//        title = "Annie";
//        image = resources.getString(R.string.jet_image_url);
//        desc = "1234567";
//        summary = "2017/5/5/";
//
//        final Patient patient4 = new Patient(title, desc, image, summary);
//
//        title = "Annie";
//        image = resources.getString(R.string.jet_image_url);
//        desc = "1234567";
//        summary = "2017/5/5/";
//
//        final Patient patient5 = new Patient(title, desc, image, summary);
//
//        title = "Annie";
//        image = resources.getString(R.string.jet_image_url);
//        desc = "1234567";
//        summary = "2017/5/5/";
//
//        final Patient patient6 = new Patient(title, desc, image, summary);
//
////        ArrayList<Patient> ret = new ArrayList<Patient>() {{
////            add(patient1);
////            add(patient2);
////            add(patient3);
////            add(patient4);
////            add(patient5);
////            add(patient6);
////
////        }};
////        return ret;
//        return new Patient[]{patient1, patient2, patient3, patient4, patient5, patient6};
//    }
}
