package com.example.gabriela.legalsecurityandroid.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.gabriela.legalsecurityandroid.R;

public class UtilNotification {
    public static void sendNotification(Context context, String notificationTitle, String notificationMessage){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel myChannel = new NotificationChannel("NOTIF_CHANNEL_ID",
                    "MyApp events", NotificationManager.IMPORTANCE_LOW);

            myChannel.setDescription("Legal Event Controls");
            myChannel.setShowBadge(false);
            myChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = (NotificationManager) context.getSystemService(context.getApplicationContext().NOTIFICATION_SERVICE);
            manager.createNotificationChannel(myChannel);

        }else{

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
            notificationManager.notify( 1,notificationBuilder.build());

        }
    }
}
