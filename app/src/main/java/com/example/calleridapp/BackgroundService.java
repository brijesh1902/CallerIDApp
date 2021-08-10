package com.example.calleridapp;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;
import java.util.logging.Logger;

public class BackgroundService extends Service implements TextToSpeech.OnInitListener {

    TextToSpeech textToSpeech;
    String number="" , Name  , talk;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Intent notintent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notintent, 0);
        Notification notification = new NotificationCompat.Builder(this, "channel")
                .setContentTitle("")
                .setContentText("")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            textToSpeech = new TextToSpeech(getApplicationContext(), this);

            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            telephonyManager.listen(new PhoneStateListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    super.onCallStateChanged(state, incomingNumber);

                    if (state == (TelephonyManager.CALL_STATE_IDLE)) {
                        showToast(getApplicationContext(), "Call Ended " + incomingNumber);
                        audioManager.adjustVolume(AudioManager.STREAM_RING, AudioManager.RINGER_MODE_NORMAL);
                        //audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
                    }
                    if (state == (TelephonyManager.CALL_STATE_OFFHOOK)) {
                        showToast(getApplicationContext(), "Calling or Connected " + incomingNumber);
                        audioManager.adjustVolume(AudioManager.STREAM_RING, AudioManager.RINGER_MODE_NORMAL);
                        //audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
                    }
                    if (state == (TelephonyManager.CALL_STATE_RINGING)) {
                        number = incomingNumber;
                        showToast(getApplicationContext(), "Ringing " + incomingNumber);
                        //audioManager.adjustVolume(AudioManager.STREAM_RING, AudioManager.RINGER_MODE_SILENT);
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);

                        ContentResolver resolver = getApplicationContext().getContentResolver();
                        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
                        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                Name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            }
                            cursor.close();
                        }
                        speak();
                    }
                    System.out.println("BG_incomingNumber : " + number + "====" + Name);
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            textToSpeech.setPitch(1);
            textToSpeech.setSpeechRate((float) 0.8);
            if (result != TextToSpeech.ERROR) {
               speak();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speak() {
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.FLAG_PLAY_SOUND);
        if (Name != null && number!=null) {
            String tts_value = "Calling \n"+Name+" \n"+Name+" \n"+Name+" \n"+Name;
            textToSpeech.speak(tts_value , TextToSpeech.QUEUE_FLUSH, bundle, null);
            System.out.println("Name BG_TTS : " + tts_value);
            Name=null;
        } else if (Name==null && number!=null){
            String tts_value = "Calling \n" +number+" \n"+number+" \n"+number+" \n"+number;
            textToSpeech.speak(tts_value , TextToSpeech.QUEUE_FLUSH, bundle, null);
            System.out.println("Number BG_TTS : " + tts_value);
            number=null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakName() {
        String tts_value = "Calling "+Name+" "+Name+" "+Name+" "+Name;
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
        textToSpeech.speak(tts_value , TextToSpeech.QUEUE_FLUSH, bundle, null);
        System.out.println("Name BG_TTS : " + tts_value);
        Name=null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakNumber() {
        String tts_value = "Calling "+number+" "+number+" "+number+" "+number;
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.FLAG_PLAY_SOUND);
        textToSpeech.speak(tts_value , TextToSpeech.QUEUE_FLUSH, bundle, null);
        System.out.println("number BG_TTS : " + tts_value);

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
