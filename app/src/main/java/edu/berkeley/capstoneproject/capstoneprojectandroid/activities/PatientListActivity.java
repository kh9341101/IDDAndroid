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

import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.listeners.RecyclerItemClickListener;
import edu.berkeley.capstoneproject.capstoneprojectandroid.adapters.PatientAdapter;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Patient;

public class PatientListActivity extends AppCompatActivity {

    private ShimmerRecyclerView shimmerRecycler;
    private PatientAdapter mAdapter;


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
                        Log.i("Patient Selected", String.valueOf(position));
                        Intent intent = new Intent(view.getContext(), ProfileActivity.class);
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

        shimmerRecycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayPatient();
            }
        }, 1000);
    }

    private void displayPatient() {

        mAdapter.setCards(getPatient(getResources()));
        shimmerRecycler.hideShimmerAdapter();
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    public static Patient[] getPatient(Resources resources) {

        String title = "Annie";
        String image = resources.getString(R.string.ndtv_image_url);
        String desc = "1234567";
        String summary = "2017/5/5/";

        Patient patient1 = new Patient(title, desc, image, summary);

        title = "Annie";
        image = resources.getString(R.string.op_image_url);
        desc = "1234567";
        summary = "2017/5/5/";

        Patient patient2 = new Patient(title, desc, image, summary);


        title = "Annie";
        image = resources.getString(R.string.got_image_url);
        desc = "1234567";
        summary = "2017/5/5/";

        Patient patient3 = new Patient(title, desc, image, summary);

        title = "Annie";
        image = resources.getString(R.string.jet_image_url);
        desc = "1234567";
        summary = "2017/5/5/";

        Patient patient4 = new Patient(title, desc, image, summary);

        title = "Annie";
        image = resources.getString(R.string.jet_image_url);
        desc = "1234567";
        summary = "2017/5/5/";

        Patient patient5 = new Patient(title, desc, image, summary);

        title = "Annie";
        image = resources.getString(R.string.jet_image_url);
        desc = "1234567";
        summary = "2017/5/5/";

        Patient patient6 = new Patient(title, desc, image, summary);

        return new Patient[]{patient1, patient2, patient3, patient4, patient5, patient6};
    }
}
