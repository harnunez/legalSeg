package com.example.gabriela.legalsecurityandroid.services;

import android.content.Context;

import com.android.volley.Request;
import com.example.gabriela.legalsecurityandroid.interfaces.doConnectionEvent;

import java.util.HashMap;
import java.util.Map;

public class LoginService extends VolleyImplementation  {

    public LoginService(Context _context, doConnectionEvent _doConnectionEvent) {
       super( _context, _doConnectionEvent );
    }

    public void buildJsonLogin(String user, String password) {
        Map<String, String > params = new HashMap();
        params.put("username", user);
        params.put("password", password);

        setRequestParams( (HashMap) params );
    }

    @Override
    protected String getUrl() {
        return login_url;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

}
