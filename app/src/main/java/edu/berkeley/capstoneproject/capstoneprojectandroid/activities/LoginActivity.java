package edu.berkeley.capstoneproject.capstoneprojectandroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.BindView;
import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.database.AppDatabase;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.PatientHolder;

import static edu.berkeley.capstoneproject.capstoneprojectandroid.activities.ProfileActivity.loginActivity;
import static edu.berkeley.capstoneproject.capstoneprojectandroid.activities.ProfileActivity.patientListActivity;
import static edu.berkeley.capstoneproject.capstoneprojectandroid.activities.ProfileActivity.previousActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    public static int userType = 0;
    private AppDatabase mdb;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.link_signup) TextView _signupLink;
    @BindView(R.id.btn_login) Button _loginButton;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Log.i("clickSignUp", "Here");
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        mdb = AppDatabase.getAppDatabase(getApplicationContext());
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }


        _loginButton.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
//                R.style.AppTheme_Dark_Dialog);
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.equals("admin") && password.equals("admin")) {
            userType = 0;
        }

        if (email.equals("padmin") && password.equals("padmin1")) {
            userType = 1;
            PatientHolder.setUid(mdb.userDao().findByName("Annie").getUid());
        }
        else if (email.equals("padmin") && password.equals("padmin2")) {
            userType = 1;
            PatientHolder.setUid(mdb.userDao().findByName("Oski").getUid());
        }
        else if (email.equals("padmin") && password.equals("padmin3")) {
            userType = 1;
            PatientHolder.setUid(mdb.userDao().findByName("Luke").getUid());
        }
        else if (email.equals("padmin") && password.equals("padmin4")) {
            userType = 1;
            PatientHolder.setUid(mdb.userDao().findByName("Song Yu").getUid());
        }
        else if (email.equals("padmin") && password.equals("padmin5")) {
            userType = 1;
            PatientHolder.setUid(mdb.userDao().findByName("Kobe").getUid());
        }
        else if (email.equals("padmin") && password.equals("padmin6")) {
            userType = 1;
            PatientHolder.setUid(mdb.userDao().findByName("Rose").getUid());
        }



        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        if (userType == 0) {
            Intent intent = new Intent(LoginActivity.this, PatientListActivity.class);
            intent.putExtra(previousActivity, loginActivity);
            startActivity(intent);
            finish();
        } else if (userType == 1) {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            intent.putExtra(previousActivity, loginActivity);
            startActivity(intent);
            finish();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.equals("admin") && password.equals("admin")) {
            return valid;
        }
        if (email.equals("padmin")) {
            return valid;
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

