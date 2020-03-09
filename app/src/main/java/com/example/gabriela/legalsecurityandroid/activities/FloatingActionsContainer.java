package com.example.gabriela.legalsecurityandroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.fragments.SettingsFragmentFab;

public class FloatingActionsContainer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floating_actions_container);

        configureToolbar();

        showSelectedFragment();


    }


    private void configureToolbar(){
        Toolbar myToolbar = findViewById(R.id.toolbar_utility);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private String getValueFromSelectUserActivity(){
        Intent intent = getIntent();
        String message = intent.getStringExtra(SelectUserActivity.KEY_SETTINGS_FAB);
        return message;
    }

    private void showSelectedFragment(){
        String keyVal = getValueFromSelectUserActivity();

        if(keyVal!=null){

            switch (keyVal){
                case "settingsFragment":
                    SettingsFragmentFab setFragmentFab = new SettingsFragmentFab();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_menufab,setFragmentFab)
                            .commit();
                    break;
            }
        }
    }

}
