package edu.berkeley.capstoneproject.capstoneprojectandroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.berkeley.capstoneproject.capstoneprojectandroid.R;

import butterknife.ButterKnife;
import butterknife.BindView;

public class ProfileActivity extends AppCompatActivity {

    public static final String previousActivity = "previousActivity";
    public static final int loginActivity = 1;
    public static final int patientListActivity = 2;

    private int previousActivityType;

    @BindView(R.id.viewexercise) Button _mexercise;
    @BindView(R.id.viewresult) Button _mresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_patient_profile);
        ButterKnife.bind(this);

        previousActivityType = getIntent().getIntExtra(previousActivity, -1);

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
}
