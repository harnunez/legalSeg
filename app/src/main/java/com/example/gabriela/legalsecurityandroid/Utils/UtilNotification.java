package com.example.gabriela.legalsecurityandroid.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.example.gabriela.legalsecurityandroid.R;

public class UtilNotification {
    public static void sendNotification(Context context, String notificationTitle, String notificationMessage){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent resultIntent  = new Intent( context, context.getClass() );
        PendingIntent resultPendingIntent = PendingIntent.getActivity( context, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "M_CH_ID")
                .setSound( alarmSound )
                .setAutoCancel(true)
                .setContentIntent( resultPendingIntent )
                .setDefaults( NotificationCompat.DEFAULT_ALL )
                .setSmallIcon( R.mipmap.ic_launcher )
                .setContentTitle(notificationTitle )
                .setContentText( notificationMessage );

        NotificationManager notificationManager = (NotificationManager) context.getSystemService( context.getApplicationContext().NOTIFICATION_SERVICE );
        notificationManager.notify( 1,notificationBuilder.build() );
    }
}
