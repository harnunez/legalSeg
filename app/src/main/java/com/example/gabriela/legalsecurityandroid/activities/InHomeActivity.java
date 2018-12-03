package com.example.gabriela.legalsecurityandroid.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;
import com.example.gabriela.legalsecurityandroid.models.NewsModel;
import com.example.gabriela.legalsecurityandroid.services.VolleyImplementation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class InHomeActivity extends AppCompatActivity {

    private ImageButton shutDown;
    private View viewTimer;
    private ProgressBar progressBar;
    private TextView title_header_event;
    private TextView titleHeader;
    private TextView timerBack;
    private TextView timerMessage;
    private ImageButton cancel;
    private CountDownTimer downTimer;
    private MediaPlayer alarm;
    private LocationManager locationManager;
    private ProgressBar loadingService;

    // Location properties
    private String provider;
    private String latitud;
    private String longitud;
    private String event;
    private String useNameSelect;
    private String cliente;
    private Timer timeService;

    private static final int COUNTDOWN_MINUTES = 2;
    private static final int ACCESS_FINE_LOCATION_CODE = 100;

    // Model News
    private NewsModel newsModel;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_home);
        initProperties();

        getLocation();
        timer(COUNTDOWN_MINUTES);
        executeEventCancel();
    }


    // Properties
    private  void initProperties() {
        title_header_event = findViewById(R.id.access_title_header);
        shutDown = findViewById(R.id.icon_shut_down);
        viewTimer = findViewById(R.id.view_timer);
        progressBar = findViewById(R.id.progressBarCircle);
        titleHeader = findViewById(R.id.description_access_strong);
        timerBack = findViewById(R.id.timer_back);
        timerMessage = findViewById(R.id.description_access);
        cancel = findViewById(R.id.cancel_button_image);
        loadingService = findViewById(R.id.loadingService);

        // getPut Extras
        int eventSelected = getIntent().getExtras().getInt("event");
        event = Integer.toString(eventSelected);
        useNameSelect = getIntent().getExtras().getString("userName");
        cliente = getIntent().getExtras().getString("idCliente");


        if (event.equals(2)) {
            title_header_event.setText(R.string.enter_title_header);
        } else {
            title_header_event.setText(R.string.leave_title_header);
        }

        executeShutDown();
    }

    // Timer
    private  void timer(int minutes) {
        downTimer = new CountDownTimer(minutes * 60 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Long timer = millisUntilFinished / 1000;
                timerBack.setText("" + timer);
                int progress = (int) (millisUntilFinished / 1000);
                progressBar.setProgress(progress);

                timerServiceCall();
            }

            @Override
            public void onFinish() {
                loadingService.setVisibility(View.GONE);
                timeService.cancel();
                downTimer.cancel();
                changeViewForLevelAlert();
            }
        };
        activeAlarm();
        downTimer.start();
    }

    // Timer Service call
    private void timerServiceCall() {
        timeService = new Timer();
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                executeService();
            }
        };
        timeService.schedule(myTask, 5000);
    }

    // Add sound alarm
    private void activeAlarm() {
        alarm = MediaPlayer.create(InHomeActivity.this,R.raw.alarm);
        alarm.start();
        alarm.setLooping(true);
    }

    // Get Current Location
    private void getLocation() {
        if( checkCurrentAndroidVersion()){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE );
            // requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_CODE );
        }
        else {
            executeLocationManager();
        }
    }

    private void executeLocationManager(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        // CheckPermissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            onLocationChanged(location);
        } else {
            // alertError("No es posible acceder a tu ubicación, por favor verificá los permisos y reintenta nuevamente");
        }

        // timer(COUNTDOWN_MINUTES);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case ACCESS_FINE_LOCATION_CODE:
                String permission = permissions[0];
                int result = grantResults[0];

                if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                    if(result == PackageManager.PERMISSION_GRANTED){
                        executeLocationManager();

                        //Toast.makeText(this, "Permiso concedido", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private boolean checkCurrentAndroidVersion(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public void onLocationChanged(Location location) {
        latitud = String.valueOf(((location.getLatitude())));
        longitud = String.valueOf((location.getLongitude()));
    }

    // Execute service
    private void executeService() {
        VolleyImplementation vimp = new VolleyImplementation(this, new doConnectionEvent() {
            @Override
            public void onOk(JSONObject response) {
                Gson gson = new GsonBuilder().create();
                newsModel = gson.fromJson(response.toString(), NewsModel.class);
                if (newsModel.codeResponse == 0) {
                    alarm.stop();
                    loadingService.setVisibility(View.INVISIBLE);
                    timeService.cancel();
                    timerBack.setVisibility(View.INVISIBLE);
                    downTimer.onFinish();
                } else {
                    alertError(String.valueOf(R.string.error_default));
                }
            }

            @Override
            public void onError(VolleyError error) {

                alertError("Error, Algo salió mal por favor reintente más tarde");
            }
        });
        if (latitud != "" && latitud != null && longitud != "" && longitud != null) {
            vimp.buildJsonNews(event, latitud, longitud, useNameSelect, cliente);
        } else {
            timeService.cancel();
            downTimer.cancel();
            backRootActivity();
            executeAccessLocation();
            // getLocation();
        }
        vimp.doConnectionNovedades();
    }



    // Permissions Location
    private void executeAccessLocation() {
        if (ActivityCompat.checkSelfPermission(InHomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(InHomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(InHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            executeLocationManager();
            // Write you code here if permission already given.
        }
    }


    // Get parselable response
    private void changeViewForLevelAlert() {
        if (newsModel.alertLevel == 0) {
            // TODO: MODIFICAR
            if (event.equals("Entrando")) {
                setViewLevel(R.drawable.prueba_ok, R.string.message_strong_succes, R.string.message_succes);
            } else {
                setViewLevel(R.drawable.prueba_ok, R.string.message_strong_succes, R.string.message_succes);
            }
        } else if (newsModel.alertLevel == 1) {
            if (event.equals("Entrando")) {
                setViewLevel(R.drawable.prueba_ok, R.string.message_strong_succes, R.string.message_succes);
            } else {
                setViewLevel(R.drawable.prueba_ok, R.string.message_strong_succes, R.string.message_succes);
            }
        } else if (newsModel.alertLevel == 2) {
            if (event.equals("Entrando")) {
                setViewLevel(R.drawable.prueba_aguada, R.string.message_strong_aguarda_icon, R.string.message_aguarda_icon);
            } else {
                setViewLevel(R.drawable.prueba_aguada, R.string.message_strong_aguarda_icon, R.string.message_aguarda_icon);
            }
        } else if (newsModel.alertLevel == 3) {
            if (event.equals("Entrando")) {
                setViewLevel(R.drawable.prueba_peligro, R.string.message_strong_peligro, R.string.message_peligro);
            } else {
                setViewLevel(R.drawable.prueba_peligro, R.string.message_strong_peligro, R.string.message_peligro);
            }
        } else {
            alertError(String.valueOf(R.string.error_default));
        }
    }

    // Data for view Level
    private void setViewLevel(Integer image, Integer titleHeaderDescription, Integer description) {
        viewTimer.setBackgroundResource(image);
        titleHeader.setText(titleHeaderDescription);
        timerMessage.setText(description);
        timerBack.setText("");
        progressBar.setVisibility(View.INVISIBLE);
    }


    // Action button cancel
    private void executeEventCancel() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.stop();
                timeService.cancel();
                downTimer.cancel();
                InHomeActivity.this.finish();
            }
        });
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


    // Action shut Down
    private void executeShutDown() {
        // shutDown
        shutDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.stop();
                timeService.cancel();
                downTimer.cancel();
                cleanPreferencesUserLogued();
                backRootActivity();
            }
        });
    }

    // root Activity
    private void backRootActivity() {
        Intent myIntent = new Intent(InHomeActivity.this, LoginActivity.class);
        startActivity(myIntent);
    }

    // Clean preferences session
    private void cleanPreferencesUserLogued() {
        SharedPreferences preferences =getSharedPreferences("CredentialsUserLogued",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }



    @Override
    protected void onPause() {
        super.onPause();
        executeEventCancel();
    }
}
