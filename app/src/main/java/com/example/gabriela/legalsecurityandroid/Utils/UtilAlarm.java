package com.example.gabriela.legalsecurityandroid.Utils;

import android.content.Context;
import android.media.MediaPlayer;

public class UtilAlarm {
    private static MediaPlayer alarm;

    public static void startAlarm(Context context, int alarmSound) {
        UtilAlarm.alarm = MediaPlayer.create(context,alarmSound);
        UtilAlarm.alarm.start();
        UtilAlarm.alarm.setLooping(true);
    }

    public static boolean alarmIsPlaying(){
        return alarm != null ? alarm.isPlaying() : false;
    }

    public static void stopAlarm(){
        if(alarm != null){
            alarm.stop();
            alarm.setLooping(false);
        }
    }
}
