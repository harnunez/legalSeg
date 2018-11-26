package com.example.gabriela.legalsecurityandroid.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;
import com.example.gabriela.legalsecurityandroid.models.EventModel;
import com.example.gabriela.legalsecurityandroid.models.LoginUserModel;
import com.example.gabriela.legalsecurityandroid.models.NewsModel;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initProperties();
    }


    // Init properties
    private void initProperties() {
        useNameSelect = getIntent().getExtras().getString("UserName");
        idCliente = getIntent().getExtras().getString("idCliente");
        userName = findViewById(R.id.TextUserNameSelected);
        inHome = findViewById(R.id.ingresar_btn);
        outHome = findViewById(R.id.salir_btn);
        shutDown = findViewById(R.id.icon_shut_down);

        userName.setText("Hola " + useNameSelect);


        executeActionButtons();
    }

    // Execute Buttons
    private void executeActionButtons() {
        executeEventEnterHome();
        executeEventLeaveHome();
        executeShutDown();
    }

    // Event enter
    private void executeEventEnterHome() {

       final String eventSelected = "Entrando";
        inHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeService(eventSelected);
            }
        });
    }

    // Event leave
    private void executeEventLeaveHome() {
        // out
        final String eventSelected = "Saliendo";
        outHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeService(eventSelected);
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
                    showAlertError(event.message);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.d("Event", "Error Respuesta en JSON: " + error.getMessage());
            }
        });
        vimp.buildJsonEvents(idCliente);
        vimp.doConnectionEvents();
    }

    // Action shut Down
    private void executeShutDown() {
        // shutDown
        shutDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPreferencesUserLogued();
                backRootActivity();
            }
        });
    }

    // root Activity
    private void backRootActivity() {
        Intent myIntent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(myIntent);
    }

    // Clean preferences session
    private void cleanPreferencesUserLogued() {
        SharedPreferences preferences =getSharedPreferences("CredentialsUserLogued",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    // Start new Activity
    private  void showNewActivity(String event) {
        Intent myIntent = new Intent(HomeActivity.this, InHomeActivity.class);
        myIntent.putExtra("event", event);
        myIntent.putExtra("UserName", useNameSelect);
        startActivity(myIntent);
    }

    // Alert dialog error
    private void showAlertError(String message) {
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
