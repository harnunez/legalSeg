package com.example.gabriela.legalsecurityandroid.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.gabriela.legalsecurityandroid.Constants.Constants;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;

public class Util extends AppCompatActivity {

    public static boolean checkCurrentAndroidVersion(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isGPSEnable(Context context){
        Activity activity = (Activity) context;
        LocationManager locationManager = (LocationManager) activity.getSystemService( Context.LOCATION_SERVICE );
        return locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
    }

    public static boolean fieldIsEmpty(EditText text) {
        return text.getText().toString().isEmpty();
    }

    public static LocationRequest getUserLocation(){
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30 * 1000)
                .setFastestInterval(5 * 1000);
        return locationRequest;
    }

    public static void showGpsDialog(ApiException e, Context context){
        try {
            Activity activity = (Activity) context;
            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
            resolvableApiException.startResolutionForResult(activity, Constants.REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException exception) {
            UtilDialog.alertError("No se pudo obtener obtener los permisos de Google" , context);
        }
    }

}
