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
import com.example.gabriela.legalsecurityandroid.activities.HomeActivity;
import com.example.gabriela.legalsecurityandroid.activities.InHomeActivity;

public class Util extends AppCompatActivity {
    public static MediaPlayer alarm;

    public static void  alertError(String message, final Context context) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
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

    public static void warningDialog(String message, final Context context){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(R.string.warning_title);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

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
