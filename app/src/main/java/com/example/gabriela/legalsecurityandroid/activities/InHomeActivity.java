package com.example.gabriela.legalsecurityandroid.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.Utils.NetworkUtil;
import com.example.gabriela.legalsecurityandroid.Utils.Util;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;
import com.example.gabriela.legalsecurityandroid.models.NewsModel;
import com.example.gabriela.legalsecurityandroid.services.VolleyImplementation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

public class InHomeActivity extends AppCompatActivity implements LocationListener {

    private ImageButton shutDown;
    private View viewTimer;
    private ProgressBar progressBar;
    private TextView title_header_event;
    private TextView titleHeader;
    private TextView timerBack;
    private TextView timerMessage;
    private Button buttonDefault;
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
    private Boolean timerActive;
    private int secondsTillServiceCall = 5;

    private CountDownTimer timerCount;
    private long millisToFinish;
    private boolean isOperationEnd = false;
    private boolean isAppOnBackground = false;
    private boolean endResponseApp = false;

    private static final long STARTING_COUNTDOWN_TIME = 121000;
    private static final int SERVICE_INTERVAL_TIME = 5;
    private static final int ACCESS_FINE_LOCATION_CODE = 100;

    private static final int OPERATOR_NOT_RESPONDING = 0;
    private static final int OPERATION_OK_RESPONSE = 1;
    private static final int WAIT_RESPONSE = 2;
    private static final int DANGER_RESPONSE = 3;
    private static final int END_RESPONSE = 4;
    private static final int OUTSIDE_COVERAGE_AREA_RESPONSE = 5;
    private static final int CANCEL_BACKEND_CALL_RESPONSE = 6;

    private static final String CANCEL_BACKEND_CALL = "5";
    private static final String EVENT_ENTER_HOME = "3";
    //int contador = 0;//FIXME -- BORRAR VARIABLE SOLO PARA TESTING

    private final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    // Model News
    private NewsModel newsModel;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_home);
        initProperties();
        getLocation();
        executeEventCancel();
        executeEventShutDown();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //startTimer(STARTING_COUNTDOWN_TIME);
        setRegisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseTimer();
        unregisterReceiver(networkStatus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAppOnBackground = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAppOnBackground = false;
    }

    private  void initProperties() {
        title_header_event = findViewById(R.id.access_title_header);
        shutDown = findViewById(R.id.icon_shut_down);
        viewTimer = findViewById(R.id.view_timer);
        progressBar = findViewById(R.id.progressBarCircle);
        titleHeader = findViewById(R.id.description_access_strong);
        timerBack = findViewById(R.id.timer_back);
        timerMessage = findViewById(R.id.description_access);
        buttonDefault = findViewById(R.id.cancel_button_image);
        loadingService = findViewById(R.id.loadingService);

        event = getIntent().getExtras().getString("event");
        useNameSelect = getIntent().getExtras().getString("userName");
        cliente = getIntent().getExtras().getString("idCliente");

        if (event.equals(EVENT_ENTER_HOME)) {
            title_header_event.setText(R.string.enter_title_header);
        }
        else {
            title_header_event.setText(R.string.leave_title_header);
        }
        resetTimer();
    }

    private void startTimer(long timeLeftInMillis){
        timerCount = new CountDownTimer( timeLeftInMillis, 1000 ){

            @Override
            public void onTick(long millisUntilFinished) {
                millisToFinish = millisUntilFinished;
                secondsTillServiceCall--;
                updateProgressBar(millisUntilFinished);


                if(secondsTillServiceCall == 0){
                    secondsTillServiceCall = SERVICE_INTERVAL_TIME;
                    executeService();
                }
            }

            @Override
            public void onFinish() {
                finishTimer();
                loadingService.setVisibility(View.GONE);
                setSuccessViewLevel();
                hideProgressBar();
                if(!endResponseApp){
                    startTimer( STARTING_COUNTDOWN_TIME );
                }
            }
        }.start();
        timerActive = true;
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

    private void pauseTimer(){
        if(timerCount != null){
            timerCount.cancel();
        }
    }

    private void resetTimer(){
        timerActive = true;
        millisToFinish = STARTING_COUNTDOWN_TIME;
    }

    private void finishTimer(){
        if(timerCount != null){
            timerCount.cancel();
            timerActive = false;
        }
    }

    public void activateAlarm() {
        alarm = MediaPlayer.create(InHomeActivity.this,R.raw.alarm);
        alarm.start();
        alarm.setLooping(true);
    }

    public boolean alarmIsPlaying(){
        if(alarm != null){
            return alarm.isPlaying();
        }
        return false;
    }

    public void stopAlarm(){
        alarm.stop();
        alarm.setLooping(false);
    }

    // Get Current Location
    private void getLocation() {
        if( Util.checkCurrentAndroidVersion() ){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE );
        }
        else {
            executeLocationManager();
        }
    }

    private void executeLocationManager(){
        try {
            // CheckPermissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);

            Location location = locationManager.getLastKnownLocation(provider);

              if(location ==null){
                  provider = LocationManager.NETWORK_PROVIDER;
                  location = locationManager.getLastKnownLocation(provider );
              }
            locationManager.requestLocationUpdates(provider, 0, 0, this);

            setLocationCoord(location);

        }catch (SecurityException e) {
            executeErrorLocationEvent();
        }catch (Exception e){
            executeErrorLocationEvent();
        }

    }

    private void executeErrorLocationEvent() {
        executeEventsBeforeLeave();
        Util.alertError( getResources().getString( R.string.error_login ), InHomeActivity.this );
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
        if(location != null){
            latitud = String.valueOf( location.getLatitude() );
            longitud = String.valueOf( location.getLongitude() );
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocationCoord( location );
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {
        if(Util.isGPSEnable(InHomeActivity.this ) && NetworkUtil.isNetworkEnable( InHomeActivity.this )){
            Toast.makeText(this,"Ubicacion adquirida", Toast.LENGTH_SHORT).show();
            startTimer(millisToFinish);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if(! Util.isGPSEnable(InHomeActivity.this)){
            pauseTimer();
            Util.warningDialog( getResources().getString( R.string.warning_gps ), InHomeActivity.this);
        }
        else if(! NetworkUtil.isNetworkEnable( InHomeActivity.this )){
            pauseTimer();
            Util.warningDialog( getResources().getString( R.string.warning_connection ), InHomeActivity.this);
        }
    }

    private void executeService() {
       //contador = contador + 1;
       //Toast.makeText(this,"longitud: " + longitud + " latitud: " + latitud + " \n contador: " + contador, Toast.LENGTH_SHORT).show();

        if(checkCoordStatus()){
            VolleyImplementation vimp = new VolleyImplementation(this, new doConnectionEvent() {
                @Override
                public void onOk(JSONObject response) {
                    Gson gson = new GsonBuilder().create();
                    newsModel = gson.fromJson(response.toString(), NewsModel.class);

                    if (newsModel.codeResponse == 0) {
                        changeViewForLevelAlert();
                    } else {
                        errorConnectionMessage();
                        finishTimer();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    errorConnectionMessage();
                }
            });

            vimp.buildJsonNews(event, latitud, longitud, useNameSelect, cliente);
            vimp.doConnectionNovedades();

        }else{
            executeEventsBeforeLeave();
            Util.alertError( getResources().getString( R.string.error_location ), InHomeActivity.this );
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
            Util.alertError(getResources().getString(R.string.error_connection), InHomeActivity.this);
        }
    }

    private void changeViewForLevelAlert(){
        switch (newsModel.alertLevel){
            case OPERATOR_NOT_RESPONDING :
                //default response from service
                break;
            case OPERATION_OK_RESPONSE :
                reset();
                setSuccessViewLevel();
                break;
            case WAIT_RESPONSE:
                reset();
                setViewLevel(R.drawable.prueba_aguada, R.string.message_aguarda_icon);
                showNotificationMessage( getResources().getString( R.string.notification_title ), getResources().getString( R.string.message_aguarda_icon ));
                break;
            case DANGER_RESPONSE :
                reset();
                finishTimer();
                activateAlarm();
                cancelServiceCall();
                endResponseApp = true;
                isOperationEnd = true;
                buttonDefault.setText(R.string.salir_btn);
                setViewLevel(R.drawable.prueba_peligro, R.string.message_peligro);
                showNotificationMessage( getResources().getString( R.string.notification_title_alert ), getResources().getString( R.string.message_call911 ));
                break;
            case END_RESPONSE:
                reset();
                finishTimer();
                cancelServiceCall();
                endResponseApp = true;
                isOperationEnd = true;
                buttonDefault.setText(R.string.salir_btn);
                setSuccessViewLevel();
                break;
            case OUTSIDE_COVERAGE_AREA_RESPONSE:
                Toast.makeText( InHomeActivity.this, "fuera de rango" , Toast.LENGTH_SHORT);
                cancelServiceCall();
                showNotificationMessage( getResources().getString( R.string.notification_title ), "Te encontras fuera del área de cobertura" );
               //TODO: comportamiento para cuando esta fuera del área de cobertura
                // reset();
               // pauseTimer();
               // Util.warningDialog(getResources().getString(R.string.warning_out_of_coverage), InHomeActivity.this);
                break;
            case CANCEL_BACKEND_CALL_RESPONSE:
                // response succesfull of backend's call
                break;
            default:
                Util.alertError( getResources().getString(R.string.error_default), InHomeActivity.this);
                break;
        }
    }

    private int setViewLevelOkOperationMessage() {
        return  event.equals(EVENT_ENTER_HOME) ?  R.string.message_succes_entry  : R.string.message_succes ;
    }

    private void setSuccessViewLevel() {
        setViewLevel(R.drawable.prueba_ok, setViewLevelOkOperationMessage() );
        showNotificationMessage( getResources().getString( R.string.notification_title ), getResources().getString( setViewLevelOkOperationMessage() ));
    }

    private void showNotificationMessage(String notificationTitle, String notificationMessage ) {
        if(isAppOnBackground){
            notificationServiceCall(notificationTitle, notificationMessage);
        }
    }

    private void reset(){
        timerBack.setVisibility(View.INVISIBLE);
        loadingService.setVisibility(View.INVISIBLE);
        timerBack.setText("");
    }

    private void setViewLevel(Integer image, Integer description) {
        title_header_event.setText("");
        viewTimer.setBackgroundResource(image);
        timerMessage.setText(description);
        timerBack.setText("");
        hideProgressBar();
    }

    private void executeEventCancel() {
        buttonDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAppRequest();
            }
        });
    }

    private void finishApplicationTask() {
        if(isOperationEnd ){  finishAffinity(); }
        else {  finish();  }
    }

    private void executeEventShutDown() {
        shutDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupShoutDown();
            }
        });
    }

    private void executeEventsBeforeLeave(){
        if(alarmIsPlaying()){
            stopAlarm();
        }
        if(timerActive){
            finishTimer();
        }
    }

    private void backRootActivity() {
        Intent myIntent = new Intent(InHomeActivity.this, LoginActivity.class);
        startActivity(myIntent);
    }

    private void cleanPreferencesUserLogued() {
        SharedPreferences preferences = getSharedPreferences("CredentialsUserLogued",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private void setRegisterReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        registerReceiver(networkStatus, intentFilter);
    }

    private BroadcastReceiver networkStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(! NetworkUtil.isNetworkEnable(context)){
                pauseTimer();
                Util.warningDialog(getResources().getString(R.string.warning_lost_connection), InHomeActivity.this);
            }else {
                if(timerActive){
                    startTimer(millisToFinish);
                }
            }
        }
    };

    private void notificationServiceCall(String notificationTitle, String notificationMessage){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent resultIntent  = new Intent( this, InHomeActivity.class );
        PendingIntent resultPendingIntent = PendingIntent.getActivity( this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "M_CH_ID")
                    .setSound( alarmSound )
                    .setAutoCancel(true)
                    .setContentIntent( resultPendingIntent )
                    .setDefaults( NotificationCompat.DEFAULT_ALL )
                    .setSmallIcon( R.mipmap.ic_launcher )
                    .setContentTitle(notificationTitle )
                    .setContentText( notificationMessage );

        NotificationManager notificationManager = (NotificationManager) getSystemService( getApplicationContext().NOTIFICATION_SERVICE );
        notificationManager.notify( 1,notificationBuilder.build() );
    }

    private void cancelServiceCall(){
        event = CANCEL_BACKEND_CALL;
        executeService();
    }

    private void popupAppRequest(){
        if(timerActive){ pauseTimer(); }

        AlertDialog.Builder builder = new AlertDialog.Builder(InHomeActivity.this)
                .setTitle( getResources().getString( R.string.warning_title ) )
                .setMessage( getResources().getString( R.string.popup_message ))
                .setPositiveButton( getResources().getString( R.string.yes_message ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelServiceCall();
                        executeEventsBeforeLeave();
                        finishApplicationTask();
                    }
                } )
                .setNegativeButton( getResources().getString( R.string.no_message ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!NetworkUtil.isNetworkEnable( InHomeActivity.this )) {
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
                .setTitle( getResources().getString( R.string.warning_title ) )
                .setMessage( getResources().getString( R.string.popup_shutdown ))
                .setPositiveButton( getResources().getString( R.string.yes_message ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelServiceCall();
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

}
