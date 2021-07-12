package com.example.calleridapp;

import android.annotation.SuppressLint;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

public class PhoneCallReceiver extends BroadcastReceiver /*implements TextToSpeech.OnInitListener*/ {

    TextToSpeech textToSpeech;
    String phoneIncomingNumber = "0";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManager.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                Constant.IncomingNumber = incomingNumber;
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
                    //speak();
                }
                System.out.println("incomingNumber : "+incomingNumber);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            textToSpeech.setPitch(1);
            textToSpeech.setSpeechRate(1);
            if (result != TextToSpeech.ERROR) {
               // speak();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speak() {
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
        textToSpeech.speak(phoneIncomingNumber, TextToSpeech.QUEUE_FLUSH, bundle, null);
        Log.d("=====BR-MSG=====", "WORKING , "+phoneIncomingNumber);
    }*/
}
