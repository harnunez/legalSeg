package com.example.gabriela.legalsecurityandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.adapters.ItemObject;
import com.example.gabriela.legalsecurityandroid.adapters.adapterGridView;
import com.example.gabriela.legalsecurityandroid.models.LoginUserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SelectUserActivity extends AppCompatActivity {
    private GridView gridView;
    private LoginUserModel userLogued;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_user);

        // Status bar
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // Init set properties
        initProperties();
    }

    // Init properties
    private void initProperties() {


        userLogued = getIntent().getParcelableExtra("useLogued");
        gridView = findViewById(R.id.grid_view_bills);


        List<ItemObject> allItems = getAllItemObject();
        adapterGridView adapterGridView = new adapterGridView(this, allItems);
        gridView.setAdapter(adapterGridView);



        executeEvents();
    }

    // Events
    private void executeEvents() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(SelectUserActivity.this, "Position: " + position, Toast.LENGTH_SHORT).show();
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

}
