package com.example.gabriela.legalsecurityandroid.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.TextView;

import com.example.gabriela.legalsecurityandroid.R;
import com.example.gabriela.legalsecurityandroid.activities.InHomeActivity;

public class UtilDialog {
    public static boolean showingDialogMessage = false;
    private static AlertDialog.Builder dlgAlert;
    private static AlertDialog.Builder dlgWarning;
    private static AlertDialog.Builder dlgError;

    public static void  alertError(String message, final Context context) {
        dlgAlert  = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle("Error")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Activity)context).finish();
                        showingDialogMessage = false;
                    }
                })
                .setCancelable(true);

        if(!showingDialogMessage){
            showingDialogMessage = true;
            dlgAlert.create().show();
        }
    }

    public static void warningDialog(String message,Context context){

        dlgWarning  = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(R.string.warning_title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showingDialogMessage = false;
                    }
                })
                .setCancelable(false);

        if(!showingDialogMessage){
            showingDialogMessage = true;
            dlgWarning.create().show();
        }
    }

    public static void errorDialog(String message, final Context context){

        dlgError  = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(R.string.warning_title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((InHomeActivity)context).finishApplicationTask();
                        showingDialogMessage = false;
                    }
                })
                .setCancelable(false);

        if(!showingDialogMessage){
            showingDialogMessage = true;
            dlgError.create().show();
        }
    }
    public static void warningOutDialog(String message, final Context context){

        dlgWarning  = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(R.string.warning_title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Activity)context).finish();
                        showingDialogMessage = false;
                    }
                })
                .setCancelable(false);

        if(!showingDialogMessage){
            showingDialogMessage = true;
            dlgWarning.create().show();
        }
    }
}
