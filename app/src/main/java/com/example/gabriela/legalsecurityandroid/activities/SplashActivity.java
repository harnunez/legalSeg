package com.example.gabriela.legalsecurityandroid.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.VolleyError;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;
import com.example.gabriela.legalsecurityandroid.models.LoginUserModel;
import com.example.gabriela.legalsecurityandroid.services.VolleyImplementation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {


    private static int splashTimeOut=5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkExistCredentialUserLogued();
                finish();
            }
        },splashTimeOut);

    }


    private void checkExistCredentialUserLogued() {
        SharedPreferences prefs = getSharedPreferences("CredentialsUserLogued",Context.MODE_PRIVATE);
        if (prefs.contains("username") && prefs.contains("password")) {
            String user = prefs.getString("username", "");
            String password = prefs.getString("password", "");
            executeService(user, password);
        } else {
            Intent myIntent = new Intent(SplashActivity.this,  LoginActivity.class);
            startActivity(myIntent);
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
                } else {
                    if (mLogin.message != "" && mLogin.message != null) {
                        alertError(mLogin.message);
                    } else {
                        alertError("No se puede acceder a la información por favor reintente más tarde");
                    }
                   //  alertError(messageError);
                }
            }

            @Override
            public void onError(VolleyError error) {
                alertError("Error, Algo salió mal por favor reintente más tarde");
            }
        });
        vimp.buildJsonLogin(user, password);
        vimp.doConnectionLogin();
    }


    // start new Activity
    private void startNewActivity(LoginUserModel mLogin) {
        Intent myIntent = new Intent(SplashActivity.this, SelectUserActivity.class);
        myIntent.putExtra("useLogued", mLogin);
        startActivity(myIntent);
    }


    // Alert error
    private void alertError(String message) {
        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Error");
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}

