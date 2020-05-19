package com.example.gabriela.legalsecurityandroid.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMService extends VolleyImplementation {

    public FCMService(Context _context, doConnectionEvent _doConnectionEvent) {
        super(_context, _doConnectionEvent);
    }

    public void buildJSONFCM(String token, String idCliente){ //, String device, String UUID, String pushNotification, String cuenta,

        Map<String, String> params= new HashMap();
        params.put("token", token);
      /*  params.put("device", device);
        params.put("identifier", UUID);
        params.put("cuenta", cuenta);*/
        params.put("idCliente", idCliente);
        // this.mParametros = new JSONObject(params);

        Log.e("params", new JSONObject(params).toString());
        setRequestParams( (HashMap) params );

    }

    @Override
    protected String getUrl() {
        return FCM_URL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }
}