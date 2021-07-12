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

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextToSpeech textToSpeech;
    String number = "";
    AudioManager audioManager;
    PhoneCallReceiver phoneCallReceiver;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PRECISE_PHONE_STATE},
                    3);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_CONTACTS }, 1);
        }

        if (Build.VERSION.SDK_INT < 23) {
            AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else if( Build.VERSION.SDK_INT >= 23 ) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            // if user granted access else ask for permission
            if ( notificationManager.isNotificationPolicyAccessGranted()) {
                AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else{
                // Open Setting screen to ask for permisssion
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult( intent, 1 );
            }
        }

        textToSpeech = new TextToSpeech(this, this);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        TelephonyManager telephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                // not getting incoming number in latest version of android
                number = incomingNumber;
                System.out.println("incomingNumberA1 : " + incomingNumber);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);

    }



    /*public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            number = intent.getStringExtra(telephonyManager.EXTRA_INCOMING_NUMBER);
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //state = TelephonyManager.CALL_STATE_IDLE;
                showToast(context, "Call Ended "+number);
            } else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //state = TelephonyManager.CALL_STATE_OFFHOOK;
                showToast(context, "Calling or Connected "+number);
            } else  if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //state=TelephonyManager.CALL_STATE_RINGING;
                showToast(context, "Ringing "+number);
            }

        }
    };*/

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
        if (number != null || phoneCallReceiver.phoneIncomingNumber != null) {
            Log.d("=====MESSAGE=====", "WORKING  " + number);
            Bundle bundle = new Bundle();
            bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
            textToSpeech.speak(number, TextToSpeech.QUEUE_FLUSH, bundle, null);
        }
    }

    public void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

}