package com.example.gabriela.legalsecurityandroid.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.gabriela.legalsecurityandroid.R;

public class Util extends AppCompatActivity {
    public static MediaPlayer alarm;
    private static AlertDialog.Builder dlgAlert;
    private static AlertDialog.Builder dlgWarning;

    public static void  alertError(String message, final Context context) {
        dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Error");
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.finishAffinity((Activity) context);
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

    }

    public static void warningDialog(String message,Context context){
        dlgWarning  = new AlertDialog.Builder(context)
            .setMessage(message)
            .setTitle(R.string.warning_title)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .setCancelable(false);

        dlgWarning.create().show();
    }

    public static boolean checkCurrentAndroidVersion(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isGPSEnable(Context context){
        Activity activity = (Activity) context;
        LocationManager locationManager = (LocationManager) activity.getSystemService( Context.LOCATION_SERVICE );
        return locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
    }

    public static boolean isPasiveProviderEnable(Context context){
        Activity activity = (Activity) context;
        LocationManager locationManager = (LocationManager) activity.getSystemService( Context.LOCATION_SERVICE );
        return locationManager.isProviderEnabled( LocationManager.PASSIVE_PROVIDER );
    }

    public static boolean fieldIsEmpty(EditText text) {
        return text.getText().toString().isEmpty();
    }
}
