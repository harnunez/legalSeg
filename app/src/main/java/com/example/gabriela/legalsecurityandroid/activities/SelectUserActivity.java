package com.example.gabriela.legalsecurityandroid.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.Gravity;

import android.support.annotation.NonNull;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.Utils.UtilNetwork;
import com.example.gabriela.legalsecurityandroid.adapters.ItemObject;
import com.example.gabriela.legalsecurityandroid.adapters.adapterGridView;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;
import com.example.gabriela.legalsecurityandroid.models.LoginUserModel;
import com.example.gabriela.legalsecurityandroid.services.FCMService;
import com.example.gabriela.legalsecurityandroid.services.LoginService;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class SelectUserActivity extends AppCompatActivity {
    private GridView gridView;
    private LoginUserModel userLogued;
    boolean isInit = true;


    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabPower;
    private FloatingActionButton fabSetting;

    public static final String KEY_SETTINGS_FAB="keySettings";

    private String firebaseToken;
    private String uniqueID;
    private String userAccountFCM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_user);

        // Status bar
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Se obtiene el token firebase del dispositivo
        getUUID();


        // Init set properties

        isInit = getIntent().getBooleanExtra("init",true);
        if (isInit) {
            initProperties();
        }else {
            checkExistCredentialUserLogued();
            executeFAB();
        }
    }


    // Init properties
    private void initProperties() {


        userLogued = getIntent().getParcelableExtra("useLogued");
        gridView = findViewById(R.id.grid_view_bills);

        //Floating action buttons
        executeFAB();

        List<ItemObject> allItems = getAllItemObject();
        adapterGridView adapterGridView = new adapterGridView(this, allItems);
        gridView.setAdapter(adapterGridView);

        getFirebaseToken();
        executeFCMService();

        executeEvents();
    }

    void initFromEvent(){
        gridView = findViewById(R.id.grid_view_bills);
        List<ItemObject> allItems = getAllItemObject();
        adapterGridView adapterGridView = new adapterGridView(this, allItems);
        gridView.setAdapter(adapterGridView);

        executeEvents();
    }

    private View.OnClickListener onclickFab(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v==fabSetting){
                    fabMenu.close(true);
                    String valSett="settingsFragment";
                    Intent intent = new Intent(v.getContext(), FloatingActionsContainer.class);
                    intent.putExtra(KEY_SETTINGS_FAB,valSett);
                    startActivity(intent);
                }

                if(v==fabPower){
                    fabMenu.close(true);
                    shoutDownPowerFAB();
                }
            }
        };
    }

    private void executeFAB(){

        fabMenu = findViewById(R.id.groupfab);
        fabMenu.setIconAnimated(false);

        fabSetting =  findViewById(R.id.group_fabSetting);
        fabPower = findViewById(R.id.group_fabpower);

        fabMenu.setOnClickListener(onclickFab());
        fabSetting.setOnClickListener(onclickFab());
        fabPower.setOnClickListener(onclickFab());

    }

    private void shoutDownPowerFAB(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SelectUserActivity.this)
                .setTitle(R.string.warning_title)
                .setMessage(getResources().getString( R.string.popup_logout ))
                .setPositiveButton(getResources().getString(R.string.yes_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        cleanPreferencesUserLogued();
                        backRootActivity();
                    }
                }).setNegativeButton(getResources().getString(R.string.no_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Events
    private void executeEvents() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(SelectUserActivity.this, "Position: " + position, Toast.LENGTH_SHORT).show();
                userAccountFCM = userLogued.bills[position];
                Log.d("USER CUENTA",userAccountFCM);
                startNewActivity(userLogued.bills[position], userLogued.clientId);
            }
        });
    }

    // Add items in gridView
    private List<ItemObject> getAllItemObject() {
        Integer iImg = null;
        ItemObject itemObject = null;
        List<ItemObject> items = new ArrayList<>();
        String[] avatars = new String[] {"avatar_01", "avatar_02", "avatar_03", "avatar_04"};

        Random randomGenerator = new Random();
        int rand = randomGenerator.nextInt(avatars.length);


        for (int i = 0; i < userLogued.bills.length; i++) {
            if (i <= 4) {
                iImg = i;
            } else {
                iImg = rand;
            }
            items.add(new ItemObject(userLogued.bills[i], avatars[iImg]));
        }
        return items;
    }

    // start new Activity
    private void startNewActivity(String userName, String clienteLogued) {
        Intent myIntent = new Intent(SelectUserActivity.this, HomeActivity.class);
        myIntent.putExtra("UserName", userName);
        myIntent.putExtra("idCliente", clienteLogued);
        startActivity(myIntent);
    }

    private void checkExistCredentialUserLogued() {
        SharedPreferences prefs = getSharedPreferences("CredentialsUserLogued", Context.MODE_PRIVATE);
        if (prefs.contains("username") && prefs.contains("password")) {
            String user = prefs.getString("username", "");
            String password = prefs.getString("password", "");

            //check connection
            if (UtilNetwork.isNetworkEnable(this)){
                executeService(user, password);
            }
            else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void executeService(String user, String password){
        LoginService loginService = new LoginService( this, new doConnectionEvent() {
            @Override
            public void onOk(JSONObject response) {
                Gson gson = new GsonBuilder().create();
                LoginUserModel mLogin = gson.fromJson(response.toString(), LoginUserModel.class);
                if (mLogin.codeResponse == 0) {
                   userLogued = mLogin;
                    initFromEvent();
                } else {
                   finish();
                }
            }

            @Override
            public void onError(VolleyError error) {
            }
        } );
        loginService.buildJsonLogin( user,password );
        loginService.doConnection();
    }


    private void cleanPreferencesUserLogued() {
        SharedPreferences preferences = getSharedPreferences("CredentialsUserLogued",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private void backRootActivity() {
        Intent myIntent = new Intent(SelectUserActivity.this, LoginActivity.class);
        startActivity(myIntent);
    }

    private void getUUID(){
        uniqueID =UUID.randomUUID().toString();
        Log.d("USER UUID",uniqueID);
    }

    private void getFirebaseToken(){

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {

                            firebaseToken = task.getResult().getToken();

                            SharedPreferences preferencesFCMToken = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            preferencesFCMToken.edit().putString("tokenFCMFirebase", firebaseToken).apply();

                            Log.d("USER TOKEN FIREBASE", firebaseToken);
                        }else {
                           Log.d("USER TOKEN FAILED", "NO USER TOKEN");
                        }

                    }
                });

    }

    private void executeFCMService(){
        FCMService fcmService = new FCMService(this, new doConnectionEvent() {
            @Override
            public void onOk(JSONObject response) {
                Gson gson = new GsonBuilder().create();
                Log.d("FCM SUCCESS","Se envio correctamente los parametros");
            }

            @Override
            public void onError(VolleyError error) {

                Log.d("FCM FAILED","No se envio los parametros");
            }
        });

        SharedPreferences sharedPref = getSharedPreferences("pushList", MODE_PRIVATE);
        boolean myboolNotify = sharedPref.getBoolean("pushNtf",false);
        String valStrBool = String.valueOf(myboolNotify);


        SharedPreferences preferencesFCMToken = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String fcmTkn = preferencesFCMToken.getString("tokenFCMFirebase","");

        fcmService.buildJSONFCM(fcmTkn,userLogued.clientId,uniqueID,userAccountFCM,valStrBool);
        Log.d("USER LOGEDDDD", userLogued.clientId);

        fcmService.doConnection();
    }

}
