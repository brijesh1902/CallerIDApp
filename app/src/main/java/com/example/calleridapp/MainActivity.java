package com.example.calleridapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity /*implements TextToSpeech.OnInitListener*/ {

    TextToSpeech textToSpeech;
    String number ;
    Intent intent;
    IntentFilter filter;
    BroadcastReceiver mReceiver;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PRECISE_PHONE_STATE},
                    3);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PRECISE_PHONE_STATE},
                    2);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PRECISE_PHONE_STATE},
                    1);
        }

        if (Build.VERSION.SDK_INT < 23) {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else if( Build.VERSION.SDK_INT >= 23 ) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            // if user granted access else ask for permission
            if ( notificationManager.isNotificationPolicyAccessGranted()) {
                AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else{
                // Open Setting screen to ask for permisssion
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult( intent, 0 );
            }
        }

        //textToSpeech = new TextToSpeech(this, this);
        filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new PhoneCallReceiver();
        registerReceiver(mReceiver, filter);
        intent = new Intent(getApplicationContext(), PhoneCallReceiver.class);
        sendBroadcast(intent);

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sendBroadcast(intent);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendBroadcast(intent);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadcast(intent);
        registerReceiver(mReceiver, filter);
    }

    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
        textToSpeech.speak(number, TextToSpeech.QUEUE_ADD, bundle, null);
        System.out.println("N TTS : " + number);
    }

    public void onUtteranceCompleted(String utteranceId) {
        textToSpeech.shutdown();
        textToSpeech = null;
        finish();
    }*/

}