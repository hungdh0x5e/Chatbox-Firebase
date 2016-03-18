package com.hungdh.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // UI references.
    private EditText mEmailView, mPasswordView;
    private Button mBtnLogin, mBtnRegister;
    private ProgressDialog mAuthProgressDialog;

    // Firebase
    public Firebase firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Firebase
        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://hungdh-demo.firebaseio.com");
        // Check logged in?
        if (firebase.getAuth() != null) {
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
            finish();
            return;
        }

        getWidgetForm();


    }

    private void getWidgetForm() {
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);

        mBtnLogin = (Button) findViewById(R.id.btnLogin);
        mBtnRegister = (Button) findViewById(R.id.btnRegister);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebase.getAuth() != null) {
                    Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
                    return;
                }
                attempt(true);
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebase.getAuth() != null) {
                    Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
                    return;
                }
                attempt(false);
            }
        });


    }

    private void attempt(boolean isLogin) {
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Util.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Util.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (isLogin)
                checkLogin(email, password);
            else checkRegister(email, password);
        }
    }


    private void checkLogin(final String email, String password) {
        mAuthProgressDialog.show();
        firebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                mAuthProgressDialog.hide();
                Toast.makeText(getApplicationContext(), "Login successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                mAuthProgressDialog.hide();
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkRegister(String email, String password) {
        mAuthProgressDialog.show();
        firebase.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                mAuthProgressDialog.hide();
                Toast.makeText(getApplicationContext(), "Register successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
                mAuthProgressDialog.hide();
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void about(View v){
        new MaterialDialog.Builder(this)
                .title(R.string.about_title)
                .content(R.string.about_content)
                .positiveText(R.string.about_ok)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
