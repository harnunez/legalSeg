package com.example.gabriela.legalsecurityandroid.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.example.gabriela.legalsecurityandroid.Constants.Constants;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.Utils.NetworkUtil;
import com.example.gabriela.legalsecurityandroid.Utils.Util;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;
import com.example.gabriela.legalsecurityandroid.models.LoginUserModel;
import com.example.gabriela.legalsecurityandroid.services.VolleyImplementation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private static final long splashTimeOut=2000;
    //private final static String WIFI_STATE = "android.net.wifi.WIFI_STATE_CHANGED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initProperties();
        //startAppSplashActivity();
    }

    private void initProperties() {
        progressBar = findViewById( R.id.splash_progress_bar );
    }

    private void startAppSplashActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkExistCredentialUserLogued();
            }
        },splashTimeOut);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setRegisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStatus);
    }

    private void checkExistCredentialUserLogued() {
        SharedPreferences prefs = getSharedPreferences("CredentialsUserLogued",Context.MODE_PRIVATE);
        if (prefs.contains("username") && prefs.contains("password")) {
            String user = prefs.getString("username", "");
            String password = prefs.getString("password", "");

            //check connection
            if (NetworkUtil.isNetworkEnable(this)){
                executeService(user, password);
            }
            else {
                showWarningConnectionMessage();
            }
        } else {
            startLoginActivity();
        }
    }

    private void executeService(String user, String password) {

        VolleyImplementation vimp = new VolleyImplementation(this, new doConnectionEvent() {

            @Override
            public void onOk(JSONObject response) {
               Gson gson = new GsonBuilder().create();
                LoginUserModel mLogin = gson.fromJson(response.toString(), LoginUserModel.class);
                if (mLogin.codeResponse == 0) {
                    startNewActivity(mLogin);
                    finish();
                } else {
                    if (mLogin.message != "" && mLogin.message != null) {
                        startLoginActivity();
                        //Util.alertError(mLogin.message, SplashActivity.this);
                    } else {
                        Util.alertError(getResources().getString(R.string.error_login), SplashActivity.this);

                    }
                   //  alertError(messageError);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Util.alertError(getResources().getString(R.string.error_connection), SplashActivity.this);
            }
        });
        vimp.buildJsonLogin(user, password);
        vimp.doConnectionLogin();
    }

    private void showWarningConnectionMessage() {
        Util.warningDialog(getResources().getString(R.string.warning_connection), SplashActivity.this);
    }

    private void startLoginActivity(){
        Intent myIntent = new Intent(SplashActivity.this,  LoginActivity.class);
        startActivity(myIntent);
        finish();
    }

    private void startNewActivity(LoginUserModel mLogin) {
        Intent myIntent = new Intent(SplashActivity.this, SelectUserActivity.class);
        myIntent.putExtra("useLogued", mLogin);
        startActivity(myIntent);
    }

    private void setRegisterReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.CONNECTIVITY_ACTION);
        registerReceiver(networkStatus, intentFilter);
    }

    private BroadcastReceiver networkStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(NetworkUtil.isNetworkEnable(context)){
                startAppSplashActivity();
                progressBar.setVisibility( View.INVISIBLE );
            }else{
                showWarningConnectionMessage();
                progressBar.setVisibility( View.VISIBLE );

            }
        }
    };
}

