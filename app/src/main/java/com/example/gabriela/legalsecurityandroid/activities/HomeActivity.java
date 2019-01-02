package com.example.gabriela.legalsecurityandroid.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.Utils.NetworkUtil;
import com.example.gabriela.legalsecurityandroid.Utils.Util;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;
import com.example.gabriela.legalsecurityandroid.models.EventModel;
import com.example.gabriela.legalsecurityandroid.services.VolleyImplementation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;


public class HomeActivity extends AppCompatActivity {

    private TextView userName;
    private ImageButton inHome;
    private ImageButton outHome;
    private ImageButton shutDown;
    private String useNameSelect;
    private String idCliente;
    private String eventSelected;
    private boolean runningServiceCall = false;
    private static final String EVENT_ENTER_HOME = "3";
    private static final String EVENT_LEAVE_HOME = "2";
    private final int ACCESS_FINE_LOCATION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initProperties();
        executeActionButtons();
    }

    private void initProperties() {
        useNameSelect = getIntent().getExtras().getString("UserName");
        idCliente = getIntent().getExtras().getString("idCliente");
        userName = findViewById(R.id.TextUserNameSelected);
        inHome = findViewById(R.id.ingresar_btn);
        outHome = findViewById(R.id.salir_btn);
        shutDown = findViewById(R.id.icon_shut_down);
        userName.setText("Hola " + useNameSelect);
    }

    private void executeActionButtons() {
        executeEventEnterHome();
        executeEventLeaveHome();
        executeShutDown();
    }

    private void checkAppLocationPermisson(final String eventSelected) {
        if( Util.checkCurrentAndroidVersion()){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE );
        }
        else {
            checkAppProviders();
        }
    }

    private void checkAppProviders() {
        if(! Util.isGPSEnable(HomeActivity.this)){
            endRunningServiceCall();
            Util.warningDialog( getResources().getString( R.string.warning_gps ), HomeActivity.this);
        }
        else if(! NetworkUtil.isNetworkEnable( HomeActivity.this )){
            endRunningServiceCall();
            Util.warningDialog( getResources().getString( R.string.warning_connection ), HomeActivity.this);
        }
        else{
            executeService(eventSelected);
        }
    }

    private void executeEventEnterHome() {
        inHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!runningServiceCall){
                    startRunningServiceCall();
                    eventSelected = EVENT_ENTER_HOME;
                    checkAppLocationPermisson(eventSelected);
                }

            }
        });
    }

    private void startRunningServiceCall(){
        runningServiceCall = true;
    }

    private void endRunningServiceCall(){
        runningServiceCall = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_CODE:
                String permission = permissions[0];
                int result = grantResults[0];

                if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                    if(result == PackageManager.PERMISSION_GRANTED) {
                        checkAppProviders();
                    }
                    else{
                        Toast.makeText(this, "Es necesario habilitar este permiso para que funcione la aplicación", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    // Event leave
    private void executeEventLeaveHome() {
        outHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!runningServiceCall){
                    startRunningServiceCall();
                    eventSelected = EVENT_LEAVE_HOME;
                    checkAppLocationPermisson(eventSelected);
                }
            }
        });
    }

    // EventsWS
    public void executeService(final String eventSelected) {
        VolleyImplementation vimp = new VolleyImplementation(this, new doConnectionEvent() {
            @Override
            public void onOk(JSONObject response) {
                Log.d("Response", response.toString());
                // TODO:
                Gson gson = new GsonBuilder().create();
                EventModel event = gson.fromJson(response.toString(), EventModel.class);
                Log.d("EventModel", event.toString());
                if (event.availableEvents > 0) {
                    showNewActivity(eventSelected);
                } else {
                    Util.alertError(event.message, HomeActivity.this);
                }
                endRunningServiceCall();
            }

            @Override
            public void onError(VolleyError error) {
                Log.d("Event", "Error Respuesta en JSON: " + error.getMessage());
                endRunningServiceCall();
            }
        });
        vimp.buildJsonEvents(idCliente);
        vimp.doConnectionEvents();
    }

    private void executeShutDown() {
        shutDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupShoutDown();
            }
        });
    }

    private void backRootActivity() {
        Intent myIntent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(myIntent);
    }

    private void cleanPreferencesUserLogued() {
        SharedPreferences preferences =getSharedPreferences("CredentialsUserLogued",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private  void showNewActivity(String event) {

        Intent myIntent = new Intent(HomeActivity.this, InHomeActivity.class);
        myIntent.putExtra("event", event);
        myIntent.putExtra("userName", useNameSelect);
        myIntent.putExtra("idCliente", idCliente);
        startActivity(myIntent);
        finish();
    }

    private void popupShoutDown(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                .setTitle( getResources().getString( R.string.warning_title ) )
                .setMessage( getResources().getString( R.string.popup_shutdown ))
                .setPositiveButton( getResources().getString( R.string.yes_message ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cleanPreferencesUserLogued();
                        backRootActivity();
                    }
                } )
                .setNegativeButton( getResources().getString( R.string.no_message ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                } );

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
