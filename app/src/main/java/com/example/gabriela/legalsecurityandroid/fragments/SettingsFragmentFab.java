package com.example.gabriela.legalsecurityandroid.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.gabriela.legalsecurityandroid.R;

import static android.content.Context.MODE_PRIVATE;

//import ar.com.legalsecurity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragmentFab extends Fragment {


    private SwitchCompat mySwitchAlarm;
    private SwitchCompat pushNotifySwitch;
    /*  public SettingsFragmentFab() {
        // Required empty public constructor
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings_fab, container, false);

        mySwitchAlarm = rootView.findViewById(R.id.switch_alarm);
        pushNotifySwitch = rootView.findViewById(R.id.push_notify);

        saveCustomConfiguration();
        pushSaved();
        return rootView;
    }


    private void saveCustomConfiguration(){
        //Save switch state in shared Preferences
        SharedPreferences sharedPref = getContext().getSharedPreferences("configList", MODE_PRIVATE);
        mySwitchAlarm.setChecked(sharedPref.getBoolean("soundAlarm", true));

        mySwitchAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mySwitchAlarm.isChecked()){
                    SharedPreferences sharedPref = getContext().getSharedPreferences("configList", MODE_PRIVATE);

                    SharedPreferences.Editor shPrefEditor = sharedPref.edit();
                    shPrefEditor.putBoolean("soundAlarm",true);

                    shPrefEditor.commit();
                    mySwitchAlarm.setChecked(true);

                }else {
                    SharedPreferences sharedPref = getContext().getSharedPreferences("configList", MODE_PRIVATE);

                    SharedPreferences.Editor shPrefEditor = sharedPref.edit();
                    shPrefEditor.putBoolean("soundAlarm",false);

                    shPrefEditor.commit();
                    mySwitchAlarm.setChecked(false);
                }

            }
        });


    }

    private void pushSaved(){
        SharedPreferences sharedPrefNotif = getContext().getSharedPreferences("pushList", MODE_PRIVATE);
        pushNotifySwitch.setChecked(sharedPrefNotif.getBoolean("pushNtf",true));

        pushNotifySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pushNotifySwitch.isChecked()){
                    SharedPreferences sharedPrefNotif = getContext().getSharedPreferences("pushList", MODE_PRIVATE);
                    SharedPreferences.Editor shPrefEditor = sharedPrefNotif.edit();
                    shPrefEditor.putBoolean("pushNtf",true);

                    shPrefEditor.commit();
                    pushNotifySwitch.setChecked(true);
                }
                else{
                    SharedPreferences sharedPrefNotif = getContext().getSharedPreferences("pushList", MODE_PRIVATE);
                    SharedPreferences.Editor shPrefEditor = sharedPrefNotif.edit();
                    shPrefEditor.putBoolean("pushNtf",false);

                    shPrefEditor.commit();
                    pushNotifySwitch.setChecked(false);
                }
            }
        });

    }

}
