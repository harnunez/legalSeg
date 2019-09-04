package com.example.gabriela.legalsecurityandroid.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gabriela.legalsecurityandroid.Constants.Constants;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.Utils.Util;
import com.example.gabriela.legalsecurityandroid.Utils.UtilDialog;
import com.example.gabriela.legalsecurityandroid.Utils.UtilNetwork;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class HomeActivity extends AppCompatActivity {

    private TextView userName;
    private ImageButton inHome;
    private ImageButton outHome;
    private ImageButton shutDown;
    private String useNameSelect;
    private String idCliente;
    private String eventSelected;
    private boolean runningServiceCall = false;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        initProperties();
        initGoogleAPIClient();
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

    private void checkAppLocationPermisson() {
        if (Util.checkCurrentAndroidVersion()) {
            requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.ACCESS_FINE_LOCATION_CODE );
        } else {
            checkGpsSettings();
        }
    }

    private void checkAppProviders() {
        if (!UtilNetwork.isNetworkEnable( HomeActivity.this )) {
            endRunningServiceCall();
            UtilDialog.warningDialog( getResources().getString( R.string.warning_connection ), HomeActivity.this );
        }
        else {
            endRunningServiceCall();
            showNewActivity(eventSelected);

           // executeService(eventSelected);
        }
    }

    private void executeEventEnterHome() {
        inHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!runningServiceCall){
                    startRunningServiceCall();
                    eventSelected = Constants.EVENT_ENTER_HOME;
                    checkAppLocationPermisson();
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
        String permission = permissions[0];
        int result = grantResults[0];
        switch (requestCode) {
            case (Constants.ACCESS_FINE_LOCATION_CODE ):
                if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                    if(result == PackageManager.PERMISSION_GRANTED) {
                        checkGpsSettings();
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

    private void executeEventLeaveHome() {
        outHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!runningServiceCall){
                    startRunningServiceCall();
                    eventSelected = Constants.EVENT_LEAVE_HOME;
                    checkAppLocationPermisson();
                }
            }
        });
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
    }

    private void popupShoutDown(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                .setTitle( getResources().getString( R.string.warning_title ) )
                .setMessage( getResources().getString( R.string.popup_logout ))
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

    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(HomeActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        switch (requestCode) {
            case Constants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        checkAppProviders();
                        break;
                    case RESULT_CANCELED:
                        endRunningServiceCall();
                        UtilDialog.warningDialog( getResources().getString( R.string.warning_gps ), HomeActivity.this );
                        break;
                }
                break;
        }
    }

    private void checkGpsSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(Util.getUserLocation());

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener( new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    checkAppProviders();

                }catch (ApiException e){
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            checkAppProviders();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Util.showGpsDialog(e,HomeActivity.this);
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        } );
    }

}
