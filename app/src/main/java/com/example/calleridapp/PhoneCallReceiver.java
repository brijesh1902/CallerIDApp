package com.example.calleridapp;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
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

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.Locale;

public class PhoneCallReceiver extends BroadcastReceiver {

    TextToSpeech textToSpeech;
    String phoneIncomingNumber = Constant.IncomingNumber;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener(context);
            telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
        telephonyManager.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                phoneIncomingNumber = incomingNumber;
                if(state == (TelephonyManager.CALL_STATE_IDLE)) {
                    //state = TelephonyManager.CALL_STATE_IDLE;
                    showToast(context, "Call Ended "+incomingNumber);
                } else if(state == (TelephonyManager.CALL_STATE_OFFHOOK)) {
                    //state = TelephonyManager.CALL_STATE_OFFHOOK;
                    showToast(context, "Calling or Connected "+incomingNumber);
                } else  if(state == (TelephonyManager.CALL_STATE_RINGING)) {
                    //state=TelephonyManager.CALL_STATE_RINGING;
                    showToast(context, "Ringing "+incomingNumber);
                }
                System.out.println("incomingNumber : "+incomingNumber);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);


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

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

    }

    private class MyPhoneStateListener extends PhoneStateListener {

        Context context;
        TextToSpeech ttobj;

        MyPhoneStateListener(Context context) {
            this.context = context;

            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

            ttobj = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = textToSpeech.setLanguage(Locale.US);
                        textToSpeech.setPitch(1);
                        textToSpeech.setSpeechRate(1);
                        if (result != TextToSpeech.ERROR) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                speak();
                            }
                        }
                    }
                }
            });
        }

        public void onCallStateChanged(int state, String incomingNumber) {

            phoneIncomingNumber = incomingNumber;

            if(state == (TelephonyManager.CALL_STATE_IDLE)) {
                //state = TelephonyManager.CALL_STATE_IDLE;
                showToast(context, "Call Ended "+incomingNumber);
            } else if(state == (TelephonyManager.CALL_STATE_OFFHOOK)) {
                //state = TelephonyManager.CALL_STATE_OFFHOOK;
                showToast(context, "Calling or Connected "+incomingNumber);
            } else  if(state == (TelephonyManager.CALL_STATE_RINGING)) {
                //state=TelephonyManager.CALL_STATE_RINGING;
                showToast(context, "Ringing "+incomingNumber);
            }
            System.out.println("incomingNumber : "+incomingNumber);
        }
    }

    public void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speak() {
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
        textToSpeech.speak(phoneIncomingNumber, TextToSpeech.QUEUE_FLUSH, bundle, null);
        Log.d("=====BR-MSG=====", "WORKING , "+phoneIncomingNumber);
        System.out.println("TTS : " + phoneIncomingNumber);

    }
}
