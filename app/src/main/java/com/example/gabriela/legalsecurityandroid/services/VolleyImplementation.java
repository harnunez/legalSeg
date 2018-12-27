package com.example.gabriela.legalsecurityandroid.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyImplementation {
    private doConnectionEvent mDoConnectionEvent;
    private Context mContext;
    private RequestQueue mRequestQueue;

    private String base_url = "http://10.1.10.99:8081/LegalSecurity";
    private String login_url = base_url + "/loginWS";
    private String news_url = base_url + "/novedadesWS";
    private String event_url = base_url + "/eventosWS";
    private JSONObject mParametros;


    public VolleyImplementation(Context _context, doConnectionEvent _doConnectionEvent) {
        this.mContext = _context;
        this.mDoConnectionEvent = _doConnectionEvent;
    }

    public void buildJsonLogin(String user, String password) {
        Map<String, String > params = new HashMap();
        params.put("username", user);
        params.put("password", password);

        this.mParametros = new JSONObject(params);
    }

    public void buildJsonNews(String event, String latitude, String longitude, String billActive, String idCliente) {
        Map<String, String > params = new HashMap();
        params.put("evento", event);
        params.put("latitud", latitude);
        params.put("longitud", longitude);
        params.put("cuenta", billActive);
        params.put("idCliente", idCliente);
        this.mParametros = new JSONObject(params);
    }

    public void buildJsonEvents(String idCliente) {
        Map<String, String > params = new HashMap();
        params.put("idCliente", idCliente);
        this.mParametros = new JSONObject(params);
    }


    // Custom DoConecction
    public void setDoConnection(Integer method, String url, JSONObject parameters) {
        mRequestQueue = Volley.newRequestQueue(this.mContext);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse response
                        mDoConnectionEvent.onOk(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDoConnectionEvent.onError(error);
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    // Login
    public void doConnectionLogin() {
        setDoConnection(Request.Method.POST, login_url, mParametros);
    }


    // Novedades
    public void doConnectionNovedades() {
        setDoConnection(Request.Method.POST, news_url, mParametros);
    }

    public void doConnectionEvents() {
        setDoConnection(Request.Method.POST, event_url, mParametros);
    }
}

