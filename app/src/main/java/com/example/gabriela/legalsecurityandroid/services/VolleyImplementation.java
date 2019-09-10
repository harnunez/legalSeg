package com.example.gabriela.legalsecurityandroid.services;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gabriela.legalsecurityandroid.Utils.UtilDialog;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;

import org.json.JSONObject;

import java.util.HashMap;

public abstract class VolleyImplementation {
    private doConnectionEvent mDoConnectionEvent;
    private RequestQueue mRequestQueue;
    protected Context mContext;

    protected String base_url = "https://legalsat24.com:8443/LegalSecurity/";
//    protected String base_url =  "http://10.1.7.0:8080/LegalSecurity";
    protected String login_url = base_url + "/loginWS";
    protected String news_url = base_url + "/novedadesWS";
    protected String event_url = base_url + "/eventosWS";
    private JSONObject mParametros;


    public VolleyImplementation(Context _context, doConnectionEvent _doConnectionEvent) {
        this.mContext = _context;
        this.mDoConnectionEvent = _doConnectionEvent;
    }

    public void doConnection() {
        doConnectionService();
    }

    private void doConnectionService() {
        mRequestQueue = Volley.newRequestQueue(this.mContext);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                getMethod(),
                getUrl(),
                getRequestParams(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mDoConnectionEvent.onOk(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mDoConnectionEvent.onError(error);
                    }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    protected abstract String getUrl();

    protected abstract int getMethod();

    protected JSONObject getRequestParams(){
        return mParametros;
    }

    protected void setRequestParams(HashMap hmParams){

        try {
            this.mParametros =  new JSONObject( hmParams );
        }catch (Exception e){
            //TODO
            UtilDialog.alertError( "HashMap is null", mContext );
        }
    }


}

