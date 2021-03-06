package com.example.gabriela.legalsecurityandroid.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.gabriela.legalsecurityandroid.Constants.Constants;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.Utils.Util;
import com.example.gabriela.legalsecurityandroid.Utils.UtilAlarm;
import com.example.gabriela.legalsecurityandroid.Utils.UtilDialog;
import com.example.gabriela.legalsecurityandroid.Utils.UtilNetwork;
import com.example.gabriela.legalsecurityandroid.Utils.UtilNotification;
import com.example.gabriela.legalsecurityandroid.fragments.SettingsFragmentFab;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;
import com.example.gabriela.legalsecurityandroid.models.NewsModel;
import com.example.gabriela.legalsecurityandroid.services.LocationUpdatesService;
import com.example.gabriela.legalsecurityandroid.services.NewsService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class InHomeActivity extends AppCompatActivity {
    private ImageButton shutDown;
    private View viewTimer;
    private ProgressBar progressBar;
    private TextView title_header_event;
    private TextView timerBack;
    private TextView timerMessage;
    private TextView titleMessageEvent;
    private Button buttonDefault;
    private MediaPlayer alarm;
    private LocationManager locationManager;
    private ProgressBar loadingService;

    // Location properties
    private String provider;
    private String latitud;
    private String longitud;
    private String event;
    private String eventInit;
    private String useNameSelect;
    private String cliente;
    private Boolean timerActive = false;
    private int secondsTillServiceCall = 5;

    private CountDownTimer timerCount;
    private long millisToFinish = STARTING_COUNTDOWN_TIME;
    private long millisOnHold = 0;
    private boolean isOperationEnd = false;
    private boolean isAppOnBackground = false;
    private boolean isOnCoverageArea = true;
    private boolean endActivity = false;
    private boolean endResponseApp = false;
    private boolean operatorRespond = false;

    private boolean myboolpreferences;

    private static final long STARTING_COUNTDOWN_TIME = 121000;
    private static final int SERVICE_INTERVAL_TIME = 5;

    // Model News
    private NewsModel newsModel;
    private boolean mAlreadyStartedService = false;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    LocationRequest mLocationRequest = new LocationRequest();
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_home);
        initProperties();
        initFusedLocationClient();
        setLocation();
        initTimer();
        executeEventCancel();
        executeEventShutDown();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishTimer();
        finishUpdatesLocation();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        unregisterReceiver(networkStatus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAppOnBackground = true;
        startServiceLocation();
        fusedLocationClient.removeLocationUpdates(locationCallback);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isAppOnBackground = false;
        finishUpdatesLocation();
        setRegisterReceiver();
        setUserLocation();
    }

    private  void initProperties() {
        title_header_event = findViewById(R.id.access_title_header);
        shutDown = findViewById(R.id.icon_shut_down);
        viewTimer = findViewById(R.id.view_timer);
        progressBar = findViewById(R.id.progressBarCircle);
        timerBack = findViewById(R.id.timer_back);
        timerMessage = findViewById(R.id.description_access);
        buttonDefault = findViewById(R.id.cancel_button_image);
        loadingService = findViewById(R.id.loadingService);
        titleMessageEvent = findViewById(R.id.description_access_strong);


        SharedPreferences sharedPref = getSharedPreferences("configList", MODE_PRIVATE);
        myboolpreferences = sharedPref.getBoolean("soundAlarm",false);

        event = getIntent().getExtras().getString("event");
        eventInit = getIntent().getExtras().getString("event");
        useNameSelect = getIntent().getExtras().getString("userName");
        cliente = getIntent().getExtras().getString("idCliente");

        /*//NOTE: This is the new service which should call every time to backend service when danger is received
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        locationServiceIntent.putExtra("idCliente", cliente);
        locationServiceIntent.putExtra("event", event);
        locationServiceIntent.putExtra("userName", useNameSelect);
        startService(locationServiceIntent);
        //NOTE: END NOTE*/

        if (eventInit.equals(Constants.EVENT_ENTER_HOME)) {
            title_header_event.setText(R.string.enter_title_header);
        }
        else {
            title_header_event.setText(R.string.leave_title_header);
        }
    }
    private void startTimer(long timeLeftInMillis){
        timerCount = new CountDownTimer( timeLeftInMillis, 1000 ){

            @Override
            public void onTick(long millisUntilFinished) {
                millisToFinish = millisUntilFinished;
                checkUpdateProgressBar(millisUntilFinished);
                callService();
            }

            @Override
            public void onFinish() {
                finishTimerTask();
                resetTimet();
            }
        }.start();
        timerActive = true;
    }

    private void resetTimet() {
        if(!endResponseApp){
            startTimer( STARTING_COUNTDOWN_TIME );
        }
    }

    private void finishTimerTask() {
        if(isOnCoverageArea){
            finishTimer();
            loadingService.setVisibility(View.GONE);
            if (!operatorRespond) {
                setSuccessViewLevel();
            }
            hideProgressBar();
        }
    }

    private void checkUpdateProgressBar(long millisUntilFinished) {
        if(isOnCoverageArea){
            updateProgressBar(millisUntilFinished);
        }
    }

    private void callService() {
        secondsTillServiceCall--;

        if(secondsTillServiceCall == 0){
            secondsTillServiceCall = SERVICE_INTERVAL_TIME;
            executeService();
        }
    }

    private void hideProgressBar(){
        progressBar.setVisibility( View.INVISIBLE );
        timerBack.setVisibility( View.INVISIBLE );
    }

    private void updateProgressBar(long millisUntilFinished) {
        Long timer = millisUntilFinished / 1000;
        timerBack.setText("" + timer);
        int progress = (int) (millisUntilFinished / 1000);
        progressBar.setProgress(progress);
    }

    private void finishTimer(){
        if(timerCount != null){
            timerCount.cancel();
            timerActive = false;
        }
    }

    void initFusedLocationClient(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient( this );
    }
    private void setLocation() {
        if (Util.checkCurrentAndroidVersion()) {
            requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.ACCESS_FINE_LOCATION_CODE );
        } else {
            setUserLocation();
        }
    }

    private void setUserLocation() {
        checkProvidersPermission();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    setLocationCoord(location);
                }
            };
        };
        fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
    }

    void startServiceLocation(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
        if (!mAlreadyStartedService) {
            Intent intent = new Intent(this, LocationUpdatesService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            }else {
                startService(intent);
            }
            LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver,
                    new IntentFilter(LocationUpdatesService.ACTION_LOCATION_BROADCAST));
            mAlreadyStartedService = true;
        }
    }


    private void checkProvidersPermission() {
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText( this,"permisos denegados",Toast.LENGTH_LONG ).show();
            return;
        }
    }

    private void executeErrorLocationEvent() {
        finishActivityComponents();
        UtilDialog.alertError( getResources().getString( R.string.error_login ), InHomeActivity.this );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Constants.ACCESS_FINE_LOCATION_CODE:
                String permission = permissions[0];
                int result = grantResults[0];

                if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                    if(result == PackageManager.PERMISSION_GRANTED){
                        setUserLocation();
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
    private void setLocationCoord(Location location) {
        try {
            latitud = String.valueOf( location.getLatitude() );
            longitud = String.valueOf( location.getLongitude() );
        }catch (Exception e){
            //TODO
        }
    }

    private void checkProvidersEnabled(){
        if (Util.isGPSEnable(InHomeActivity.this ) && UtilNetwork.isNetworkEnable( InHomeActivity.this )){
            initTimer();
        }
    }

    private void initTimer(){
        if(!timerActive){
            startTimer( millisToFinish );
        }
    }

    private void executeService() {
        if(checkCoordStatus()){
            NewsService newsService = new NewsService(this, new doConnectionEvent() {
                @Override
                public void onOk(JSONObject response) {
                    Gson gson = new GsonBuilder().create();
                    newsModel = gson.fromJson(response.toString(), NewsModel.class);
                    if (event.equals("2") || event.equals("3")) {
                        event = Constants.EVENT_ASK_FOR_EVENT;
                    }
                    if (newsModel.codeResponse == 0) {
                        changeViewForLevelAlert();
                    } else {
                        if (newsModel.codeResponse == 3) {
                            errorAbonoMessage(newsModel.message);
                        }else {
                            errorConnectionMessage();
                            finishTimer();
                        }
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    errorConnectionMessage();
                }
            });

            // String forceEventToNumberFour = "4";
            //NOTA: Cambio esto por pedido de Nicolás . Todo pooleo va con nro 4
            newsService.buildJsonNews(event, latitud, longitud, useNameSelect, cliente);//event

            newsService.doConnection();

        }else{
            setUserLocation();
        }
    }

    private boolean checkCoordStatus() {
        return !(coordIsEmpty(latitud) || coordIsEmpty(longitud));
    }

    private boolean coordIsEmpty(String coord){
        return TextUtils.isEmpty( coord );
    }

    private void errorConnectionMessage() {
        if (timerActive){
            UtilDialog.alertError(getResources().getString(R.string.error_connection), InHomeActivity.this);
        }
    }
    private void errorAbonoMessage(String message) {
        if (timerActive){
            endResponseApp = true;
            isOperationEnd = true;
            finishTimer();
            UtilDialog.errorDialog(message, InHomeActivity.this);
        }
    }

    private void changeViewForLevelAlert(){
        checkCoverageArea();

        if (newsModel.alertLevel != Constants.OPERATOR_NOT_RESPONDING){
            operatorRespond = true;
        }
        switch (newsModel.alertLevel){
            case Constants.OPERATOR_NOT_RESPONDING :
                handleTimerTask();
                break;
            case Constants.OPERATION_OK_RESPONSE :
                hideCountDownComponents();
                setSuccessViewLevel();
                finishAlarm();
                buttonDefault.setVisibility(View.VISIBLE);
                break;
            case Constants.WAIT_RESPONSE:
                hideCountDownComponents();
                buttonDefault.setVisibility(View.VISIBLE);
                setViewLevel(R.drawable.prueba_alerta, R.string.message_aguarda_icon, R.string.message_strong_aguarda_icon);
                showNotificationMessage( getResources().getString( R.string.notification_title ), getResources().getString( R.string.message_aguarda_icon ));
                break;
            case Constants.DANGER_RESPONSE :
                hideCountDownComponents();
                if(myboolpreferences){
                    UtilAlarm.startAlarm(InHomeActivity.this,R.raw.alarm);
                }
                buttonDefault.setVisibility(View.GONE);
                setViewLevel(R.drawable.prueba_peligro, R.string.message_peligro, R.string.message_strong_peligro);
                showNotificationMessage( getResources().getString( R.string.notification_title_alert ), getResources().getString( R.string.message_call911 ));
                event = Constants.EVENT_DANGER;
                break;
            case Constants.END_RESPONSE:
                hideCountDownComponents();
                finishActivityComponents();
                finishAlarm();
                endResponseApp = true;
                isOperationEnd = true;
                buttonDefault.setText(R.string.salir_btn);
                buttonDefault.setVisibility(View.GONE);
                setEndViewLevel();
                break;
            case Constants.OUTSIDE_COVERAGE_AREA_RESPONSE:
                if(!endActivity && !UtilDialog.showingDialogMessage){
                    finishTimer();
                    endResponseApp = true;
                    isOperationEnd = true;
                    millisOnHold =  millisToFinish;
                    UtilDialog.warningOutDialog(getResources().getString(R.string.warning_out_of_coverage), InHomeActivity.this);
                    showNotificationMessage( getResources().getString( R.string.notification_title ), "Te encontras fuera del área de cobertura" );
                }
                buttonDefault.setVisibility(View.VISIBLE);
                break;
            case Constants.CANCEL_BACKEND_CALL_RESPONSE:
                // response succesfull of backend's call
                break;
            default:
                UtilDialog.alertError( getResources().getString(R.string.error_default), InHomeActivity.this);
                break;
        }
    }

    private void handleTimerTask() {
        if(millisOnHold != 0){
            finishTimer();
            startTimer( millisOnHold );
        }else{
            initTimer();
        }
    }

    private void checkCoverageArea() {
        isOnCoverageArea = newsModel.alertLevel == Constants.OUTSIDE_COVERAGE_AREA_RESPONSE ? false : true;
    }

    private int setViewLevelOkOperationMessage() {
        return  eventInit.equals(Constants.EVENT_ENTER_HOME) ?  R.string.message_succes_entry  :
                R.string.message_succes ;
    }

    private void setSuccessViewLevel() {
        if (eventInit.equals(Constants.EVENT_ENTER_HOME)) {
            setViewLevel(R.drawable.prueba_ingresar, setViewLevelOkOperationMessage(),
                    R.string.message_strong_succes);
        }else{
            setViewLevel(R.drawable.prueba_salir, setViewLevelOkOperationMessage(),
                    R.string.message_strong_succes);
        }
        showNotificationMessage( getResources().getString( R.string.notification_title ), getResources().getString( setViewLevelOkOperationMessage() ));
    }
    private void setEndViewLevel() {
        setViewLevel(R.drawable.prueba_ok, R.string.message_end_title,R.string.message_end);
        showNotificationMessage( getResources().getString( R.string.notification_title ),
                getResources().getString( R.string.message_end));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finishApplicationTask();
            }
        },3000);
    }
    private void showNotificationMessage(String notificationTitle, String notificationMessage ) {
        if(isAppOnBackground){
            UtilNotification.sendNotification(InHomeActivity.this,notificationTitle, notificationMessage);
        }
    }

    private void hideCountDownComponents(){
        timerBack.setVisibility(View.INVISIBLE);
        loadingService.setVisibility(View.INVISIBLE);
        timerBack.setText("");
    }

    private void setViewLevel(Integer image, Integer description, Integer title) {
        title_header_event.setText("");
        titleMessageEvent.setText(title);
        viewTimer.setBackgroundResource(image);
        timerMessage.setText(description);
        timerBack.setText("");
        hideProgressBar();
    }

    private void executeEventCancel() {
        buttonDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOperationEnd){
                    popupAppRequest();
                }else{
                    finishActivityComponents();
                    finishApplicationTask();
                }
            }
        });
    }

    public void finishApplicationTask() {
        if(isOperationEnd ){
            finish();
            userRootActivity();
        } else {finish();}
    }

    private void finishUpdatesLocation(){
        stopService(new Intent(this, LocationUpdatesService.class));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
        mAlreadyStartedService = false;
    }

    private void executeEventShutDown() {
        shutDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOperationEnd){
                    popupShoutDown();
                }
                else{
                    finishActivityComponents();
                    cleanPreferencesUserLogued();
                    backRootActivity();
                }
            }
        });
    }

    private void finishActivityComponents(){
        UtilAlarm.stopAlarm();
        finishTimer();
    }

    private void finishAlarm(){
        UtilAlarm.stopAlarm();
    }
    private void backRootActivity() {
        Intent myIntent = new Intent(InHomeActivity.this, LoginActivity.class);
        startActivity(myIntent);
    }

    private void userRootActivity() {
        Intent myIntent = new Intent(InHomeActivity.this, SelectUserActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.putExtra("init", false);
        startActivity(myIntent);
    }
    private void cleanPreferencesUserLogued() {
        SharedPreferences preferences = getSharedPreferences("CredentialsUserLogued",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private void setRegisterReceiver() {
        registerReceiver(networkStatus, new IntentFilter( Constants.CONNECTIVITY_ACTION ));
    }

    private BroadcastReceiver networkStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(! UtilNetwork.isNetworkEnable( context )){
                finishTimer();
                UtilDialog.warningDialog(getResources().getString(R.string.warning_lost_connection), InHomeActivity.this);
            }else {
                initTimer();
            }
        }
    };

    private void cancelServiceCall(){
        event = Constants.EVENT_CANCEL_BACKEND_CALL;
        executeService();
    }

    private void popupAppRequest(){
        if(timerActive){ finishTimer(); }

        AlertDialog.Builder builder = new AlertDialog.Builder(InHomeActivity.this)
                .setTitle(R.string.warning_title)
                .setMessage( getResources().getString( R.string.popup_message ))
                .setPositiveButton( getResources().getString( R.string.yes_message ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executeEventsBeforeLeave();
                    }
                } )
                .setNegativeButton( getResources().getString( R.string.no_message ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!UtilNetwork.isNetworkEnable( InHomeActivity.this )) {
                            Toast.makeText( InHomeActivity.this, getResources().getString( R.string.warning_connection ), Toast.LENGTH_SHORT );
                        }
                        if(!Util.isGPSEnable( InHomeActivity.this )){
                            Toast.makeText( InHomeActivity.this, getResources().getString( R.string.warning_gps ), Toast.LENGTH_SHORT);
                        }
                        else{
                            startTimer(millisToFinish);
                        }
                    }
                } );

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void popupShoutDown(){

        AlertDialog.Builder builder = new AlertDialog.Builder(InHomeActivity.this)
                .setTitle(R.string.warning_title)
                .setMessage( getResources().getString( R.string.popup_shutdown ))
                .setPositiveButton( getResources().getString( R.string.yes_message ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executeEventsBeforeLeave();
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

    @Override
    public void onBackPressed() {
        popupAppRequest();
    }

    private void executeEventsBeforeLeave(){
        if(UtilNetwork.isNetworkEnable( InHomeActivity.this )) {
            cancelServiceCall();
        }
        finishActivityComponents();
        finishUpdatesLocation();
        finishApplicationTask();
        endActivity = true;
    }
    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           if (intent.getExtras() != null){
               double latitude = intent.getDoubleExtra(LocationUpdatesService.EXTRA_LATITUDE, 0);
               double longitude = intent.getDoubleExtra(LocationUpdatesService.EXTRA_LONGITUDE, 0);
               Location location = new Location("google provider");
               location.setLongitude(longitude);
               location.setLatitude(latitude);
               setLocationCoord(location);
           }
        }
    };

    private void checkGpsSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(Util.getUserLocation());

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnCompleteListener( new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                }catch (ApiException e){
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            finishTimer();
                            Util.showGpsDialog( e,InHomeActivity.this );
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        switch (requestCode) {
            case Constants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        checkProvidersEnabled();
                        break;
                    case RESULT_CANCELED:
                        checkGpsSettings();
                        finishTimer();
                        break;
                }
                break;
        }
    }
}
