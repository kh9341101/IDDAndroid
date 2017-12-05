package edu.berkeley.capstoneproject.capstoneprojectandroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Patient;
import edu.berkeley.capstoneproject.capstoneprojectandroid.PatientHolder;

import butterknife.ButterKnife;
import butterknife.BindView;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.AppDatabase;

public class ProfileActivity extends AppCompatActivity {

    public static final String previousActivity = "previousActivity";
    public static final int loginActivity = 1;
    public static final int patientListActivity = 2;


    public static int patientUid;

    private final String dtag = "Description: \n";
    private final String stag = "Summary: ";

    private AppDatabase mdb;
    private int previousActivityType;

    @BindView(R.id.viewexercise) Button _mexercise;
    @BindView(R.id.viewresult) Button _mresult;
    @BindView(R.id.patient_name) TextView _mname;
    @BindView(R.id.patient_description) TextView _mdescription;
    @BindView(R.id.patient_summary) TextView _msummary;
    @BindView(R.id.profile_image) ImageView _mimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_patient_profile);
        ButterKnife.bind(this);

        previousActivityType = getIntent().getIntExtra(previousActivity, -1);
//        patientUid = getIntent().getIntExtra(patientKey, -1);


        _mexercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        _mresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ResultActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mdb = AppDatabase.getAppDatabase(getApplicationContext());
        patientUid = PatientHolder.getUid();
        displayViewbyUid();

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (previousActivityType == patientListActivity) {
            Intent intent = new Intent(ProfileActivity.this, PatientListActivity.class);
            startActivity(intent);
        } else if (previousActivityType == loginActivity) {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    private void displayViewbyUid() {
        Patient p = mdb.userDao().findByUid(patientUid);
        _mname.setText(p.getTitle());
        String tmp = dtag + p.getDescription();
        _mdescription.setText(tmp);
        tmp = stag + p.getSummaryText();
        _msummary.setText(tmp);
        Picasso.with(ProfileActivity.this).load(p.getThumbnailUrl()).placeholder(R.drawable.man).error(R.drawable.man).into(_mimage);
        Log.i("DisplayView", "Image: " + p.getThumbnailUrl() + "\nDescription: " + p.getDescription());
    }

}
