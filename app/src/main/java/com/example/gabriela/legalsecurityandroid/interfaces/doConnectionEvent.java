package com.example.gabriela.legalsecurityandroid.interfaces;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface doConnectionEvent {
    void onOk(JSONObject response);
    void onError(VolleyError error);
}
