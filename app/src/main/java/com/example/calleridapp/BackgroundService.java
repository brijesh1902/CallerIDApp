package com.example.calleridapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Locale;

public class BackgroundService extends Service implements TextToSpeech.OnInitListener {

    TextToSpeech textToSpeech;
    String number ;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        number = intent.getExtras().getString("number");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        textToSpeech = new TextToSpeech(getApplicationContext(),this);

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speak() {
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
        textToSpeech.speak(number, TextToSpeech.QUEUE_FLUSH, bundle, null);
        System.out.println("BG_TTS : " + number);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
