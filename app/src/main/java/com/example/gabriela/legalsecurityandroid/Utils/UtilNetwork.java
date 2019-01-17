package com.example.gabriela.legalsecurityandroid.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UtilNetwork {
    public static final boolean NETWORK_ENABLE = true;
    public static final boolean NETWORK_DISABLE = false;


    public static boolean isNetworkEnable(Context context) {
        //check connection
        ConnectivityManager cm =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null ? NETWORK_ENABLE : NETWORK_DISABLE ;
    }
}
