package com.example.gabriela.legalsecurityandroid.services;

import android.content.Context;
import com.android.volley.Request;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;

import java.util.HashMap;
import java.util.Map;

public class EventsService extends VolleyImplementation {

    public EventsService(Context _context, doConnectionEvent _doConnectionEvent) {
        super( _context, _doConnectionEvent );
    }

    public void buildJsonEvents(String idCliente) {
        Map<String, String > params = new HashMap();
        params.put("idCliente", idCliente);

        setRequestParams( (HashMap) params );
    }

    @Override
    protected String getUrl() {
        return event_url;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

}
