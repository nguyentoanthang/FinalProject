package com.example.mac.finalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.Bind;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;

    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        new NetCheck().execute();
        _signupButton.setEnabled(false);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        progressDialog.dismiss();
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        _signupButton.setEnabled(true);
    }

    public void onErrorInternet() {
        Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
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

    private class NetCheck extends AsyncTask<String, String, Boolean> {



        @Override
        protected void onPostExecute(Boolean th) {
            if (th == true) {
                new ProcessRegister().execute();
            } else {
                onErrorInternet();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if(netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    private class ProcessRegister extends AsyncTask<String, String, Boolean> {

        String name, email, password;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            name = _nameText.getText().toString();
            email = _emailText.getText().toString();
            password = _passwordText.getText().toString();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean == true) {
                onSignupSuccess();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            ParseObject newUser = new ParseObject("User");
            newUser.put("Name", name);
            newUser.put("Email", email);
            newUser.put("Pass", password);
            newUser.saveInBackground();

            return true;
        }
    }
}
