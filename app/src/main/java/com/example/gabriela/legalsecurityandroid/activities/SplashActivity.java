package com.example.gabriela.legalsecurityandroid.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.VolleyError;
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

    private static final long splashTimeOut=2000;
    private final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    //private final static String WIFI_STATE = "android.net.wifi.WIFI_STATE_CHANGED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
            if (NetworkUtil.networkEnable(this)){
                executeService(user, password);
            }
            else {
                showWarningMessage();
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
                        Util.alertError(mLogin.message, SplashActivity.this);
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

    private void showWarningMessage() {
        Util.warningDialog(getResources().getString(R.string.warning_connection), SplashActivity.this);
    }

    private void startLoginActivity(){
        Intent myIntent = new Intent(SplashActivity.this,  LoginActivity.class);
        startActivity(myIntent);
        finish();
    }

    // start new Activity
    private void startNewActivity(LoginUserModel mLogin) {
        Intent myIntent = new Intent(SplashActivity.this, SelectUserActivity.class);
        myIntent.putExtra("useLogued", mLogin);
        startActivity(myIntent);
    }

    private void setRegisterReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        registerReceiver(networkStatus, intentFilter);
    }

    private BroadcastReceiver networkStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(NetworkUtil.networkEnable(context)){
                checkExistCredentialUserLogued();
            }
        }
    };
}

