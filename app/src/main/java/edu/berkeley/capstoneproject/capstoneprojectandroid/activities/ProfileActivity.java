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

    @BindView(R.id.viewexercise) Button _mexercise;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_patient_profile);
        ButterKnife.bind(this);



        _mexercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
