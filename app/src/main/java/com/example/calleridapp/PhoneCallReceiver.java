package com.example.calleridapp;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.Locale;

public class PhoneCallReceiver extends BroadcastReceiver  {

    String number ;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT < 23) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else if( Build.VERSION.SDK_INT >= 23 ) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // if user granted access else ask for permission
            if ( notificationManager.isNotificationPolicyAccessGranted()) {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManager.listen(new PhoneStateListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                number = incomingNumber;

                if(state == (TelephonyManager.CALL_STATE_IDLE)) {
                    //state = TelephonyManager.CALL_STATE_IDLE;
                    showToast(context, "Call Ended "+incomingNumber);
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                if(state == (TelephonyManager.CALL_STATE_OFFHOOK)) {
                    //state = TelephonyManager.CALL_STATE_OFFHOOK;
                    showToast(context, "Calling or Connected "+incomingNumber);
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                if(state == (TelephonyManager.CALL_STATE_RINGING)) {
                    //state=TelephonyManager.CALL_STATE_RINGING;
                    showToast(context, "Ringing "+incomingNumber);
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    Intent i = new Intent(context, BackgroundService.class);
                    i.putExtra("number", number);
                    i.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                    | Intent.FLAG_RECEIVER_FOREGROUND);
                    context.startForegroundService(i);
                }
                System.out.println("BG_incomingNumber : "+incomingNumber);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

}
