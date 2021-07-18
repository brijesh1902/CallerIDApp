package com.example.calleridapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Locale;
import java.util.logging.Logger;

public class BackgroundService extends Service implements TextToSpeech.OnInitListener {

    TextToSpeech textToSpeech;
    String number ;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Intent notintent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notintent, 0);
        Notification notification = new NotificationCompat.Builder(this, "channel")
                .setContentTitle("CallerID")
                .setContentText("Running")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        textToSpeech = new TextToSpeech(getApplicationContext(),this);

        AudioManager audioManager=(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManager.listen(new PhoneStateListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                number = incomingNumber;

                if(state == (TelephonyManager.CALL_STATE_IDLE)) {
                    //state = TelephonyManager.CALL_STATE_IDLE;
                    showToast(getApplicationContext(), "Call Ended "+incomingNumber);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                if(state == (TelephonyManager.CALL_STATE_OFFHOOK)) {
                    //state = TelephonyManager.CALL_STATE_OFFHOOK;
                    showToast(getApplicationContext(), "Calling or Connected "+incomingNumber);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                if(state == (TelephonyManager.CALL_STATE_RINGING)) {
                    //state=TelephonyManager.CALL_STATE_RINGING;
                    showToast(getApplicationContext(), "Ringing "+incomingNumber);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                    speak();
                }
                System.out.println("BG_incomingNumber : "+incomingNumber);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            textToSpeech.setPitch(1);
            textToSpeech.setSpeechRate(1);
            if (result != TextToSpeech.ERROR) {
                speak();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speak() {
        String tts_value = number+" "+number+" "+number+" "+number;
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.FLAG_PLAY_SOUND);
        textToSpeech.speak(tts_value , TextToSpeech.QUEUE_FLUSH, bundle, null);
        System.out.println("BG_TTS : " + number);
        /*textToSpeech.speak(number, TextToSpeech.QUEUE_ADD, bundle, null);
        System.out.println("BG_TTS 1: " + number);
        textToSpeech.speak(number, TextToSpeech.QUEUE_ADD, bundle, null);
        System.out.println("BG_TTS 2: " + number);
        textToSpeech.speak(number, TextToSpeech.QUEUE_ADD, bundle, null);
        System.out.println("BG_TTS 3: " + number);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

}
