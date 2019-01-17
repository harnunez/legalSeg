package com.example.gabriela.legalsecurityandroid.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.android.volley.VolleyError;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.Utils.UtilDialog;
import com.example.gabriela.legalsecurityandroid.Utils.UtilNetwork;
import com.example.gabriela.legalsecurityandroid.Utils.Util;
import com.example.gabriela.legalsecurityandroid.models.LoginUserModel;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;
import com.example.gabriela.legalsecurityandroid.services.VolleyImplementation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private EditText user;
    private EditText password;
    private ProgressBar loading;
    private boolean serviceInstanceBeenCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.initProperties();
        this.executeAccessLocation();
        this.initializeEvents();
    }

    // Init properties
    private void initProperties() {
        user = findViewById(R.id.user);
        password = findViewById(R.id.password);
        login = findViewById(R.id.button_login);
        loading = findViewById(R.id.loadingService);
    }

    // Permissions Location
    private void executeAccessLocation() {
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            // Write you code here if permission already given.
        }
    }

    private void initializeEvents() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.fieldIsEmpty(user)){
                    UtilDialog.warningDialog( getResources().getString( R.string.complete_user_field ), LoginActivity.this );
                }
                else  if(Util.fieldIsEmpty( password )){
                    UtilDialog.warningDialog( getResources().getString( R.string.complete_password_field ), LoginActivity.this );
                }
                else {
                    loading.setVisibility(View.GONE);
                    if(!serviceInstanceBeenCalled){
                        serviceInstanceBeenCalled = true;
                        executeService();
                    }
                }
            }
        });
    }

    private void executeService() {
        VolleyImplementation vimp = new VolleyImplementation(this, new doConnectionEvent() {
            @Override
            public void onOk(JSONObject response) {
                Gson gson = new GsonBuilder().create();
                LoginUserModel mLogin = gson.fromJson(response.toString(), LoginUserModel.class);
                loading.setVisibility(View.INVISIBLE);
                if (mLogin.codeResponse == 0) {
                    sharedPreferenceLogin();
                    startNewActivity(mLogin);
                } else {
                    UtilDialog.warningDialog( mLogin.message,LoginActivity.this );
                }
                serviceInstanceBeenCalled = false;
            }

            @Override
            public void onError(VolleyError error) {
                UtilDialog.warningDialog( getResources().getString(R.string.error_connection), LoginActivity.this );
                serviceInstanceBeenCalled = false;
            }
        });

        if (validateLoginFields() ) {
            vimp.buildJsonLogin(user.getText().toString(), password.getText().toString());
        } else {
            UtilDialog.warningDialog( getResources().getString(R.string.invalid_data), LoginActivity.this );
        }

        if(UtilNetwork.isNetworkEnable( LoginActivity.this )){
            vimp.doConnectionLogin();
        }
        else {
            UtilDialog.warningDialog( getResources().getString( R.string.warning_connection ), LoginActivity.this );
        }
    }

    private boolean validateLoginFields(){
        return user.getText().toString() != "" && user.getText().toString() != null && password.getText().toString() != "" && password.getText().toString() != null;
    }

    private void cleanText() {
        user.getText().clear();
        password.getText().clear();
    }

    // Save credential login
    private void sharedPreferenceLogin() {
        SharedPreferences prefs =
                getSharedPreferences("CredentialsUserLogued",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username",user.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.commit();
    }

    // start new Activity
    private void startNewActivity(LoginUserModel mLogin) {
        Intent myIntent = new Intent(LoginActivity.this, SelectUserActivity.class);
        myIntent.putExtra("useLogued", mLogin);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
