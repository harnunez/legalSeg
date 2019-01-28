package com.example.gabriela.legalsecurityandroid.services;

import android.content.Context;

import com.android.volley.Request;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;

import java.util.HashMap;
import java.util.Map;

public class NewsService extends VolleyImplementation {

    public NewsService(Context _context, doConnectionEvent _doConnectionEvent) {
        super( _context, _doConnectionEvent );
    }

    public void buildJsonNews(String event, String latitude, String longitude, String billActive, String idCliente) {
        Map<String, String > params = new HashMap();
        params.put("evento", event);
        params.put("latitud", latitude);
        params.put("longitud", longitude);
        params.put("cuenta", billActive);
        params.put("idCliente", idCliente);
       // this.mParametros = new JSONObject(params);

        setRequestParams( (HashMap) params );
    }

    @Override
    protected String getUrl() {
        return news_url;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

}
