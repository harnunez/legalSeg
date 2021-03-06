package com.example.gabriela.legalsecurityandroid.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.gabriela.legalsecurityandroid.Constants.Constants;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.Utils.UtilDialog;
import com.example.gabriela.legalsecurityandroid.Utils.UtilNetwork;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;
import com.example.gabriela.legalsecurityandroid.models.LoginUserModel;
import com.example.gabriela.legalsecurityandroid.services.LoginService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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
            if (UtilNetwork.isNetworkEnable(this)){
                executeService(user, password);
            }
            else {
                showWarningConnectionMessage();
            }
        } else {
            startLoginActivity();
        }
    }

    private void executeService(String user, String password){
        LoginService loginService = new LoginService( this, new doConnectionEvent() {
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
                        //UtilDialog.alertError(mLogin.message, SplashActivity.this);
                    } else {
                        UtilDialog.alertError(getResources().getString(R.string.error_login), SplashActivity.this);

                    }
                    //  alertError(messageError);
                }
            }

            @Override
            public void onError(VolleyError error) {
                UtilDialog.alertError(getResources().getString(R.string.error_connection), SplashActivity.this);
            }
        } );
        loginService.buildJsonLogin( user,password );
        loginService.doConnection();
    }

    private void showWarningConnectionMessage() {
        UtilDialog.warningDialog(getResources().getString(R.string.warning_connection), SplashActivity.this);
    }

    private void startLoginActivity(){
        Intent myIntent = new Intent(SplashActivity.this,  LoginActivity.class);
        startActivity(myIntent);
        finish();
    }

    private void startNewActivity(LoginUserModel mLogin) {
        Intent myIntent = new Intent(SplashActivity.this, SelectUserActivity.class);
        myIntent.putExtra("useLogued", mLogin);
        myIntent.putExtra("init", true);
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
            if(UtilNetwork.isNetworkEnable(context)){
                startAppSplashActivity();
                progressBar.setVisibility( View.INVISIBLE );
            }else{
                showWarningConnectionMessage();
                progressBar.setVisibility( View.VISIBLE );

            }
        }
    };

}

