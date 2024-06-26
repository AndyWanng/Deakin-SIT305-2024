package com.example.AskMe;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.AskMe.chatView.ChatView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
public class SpeechToText  {

    private static final int SilenceTimeOut = 3000;
    private ChatView activity;

    private TextInputEditText text;

    private Button button;

    public SpeechToText(TextInputEditText text, Button button, ChatView activity){

        this.text=text;
        this.button=button;
        this.activity=activity;
    }

    public void checkPermissionAndStartSpeechRecognition(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        } else {
            // Permission is granted, start speech recognition
            startSpeechRecognition(activity);
        }
    }

    private void startSpeechRecognition(Activity activity) {
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN");

        // Load the animation from the XML file
        Animator scaleAnimation = AnimatorInflater.loadAnimator(activity, R.animator.scale_animation);

        scaleAnimation.setTarget(button);

        scaleAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Animation ended
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Animation canceled
                button.setScaleX(1.0f);
                button.setScaleY(1.0f);
                button.setRotation(0);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // Animation repeated
            }
        });

        scaleAnimation.start();

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            private long lastSpeechTime = 0;
            private final Handler handler = new Handler();
            private final Runnable silenceRunnable = new Runnable() {
                @Override
                public void run() {
                    checkForSilence();
                    handler.postDelayed(silenceRunnable, 1000);
                }
            };

            @Override
            public void onReadyForSpeech(Bundle params) {
                lastSpeechTime = System.currentTimeMillis();
                handler.postDelayed(silenceRunnable, 1000); // seconds delay before the first silence check
            }

            @Override
            public void onBeginningOfSpeech() {
                lastSpeechTime = System.currentTimeMillis();
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                lastSpeechTime = System.currentTimeMillis();
            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // Called when partial recognition results are available
                ArrayList<String> partialResult = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.d("SpeechRecognition", "Partial result: " + partialResult.get(0));
                if(partialResult.get(0).length()>text.getText().length())
                    text.setText(partialResult.get(0));
                lastSpeechTime=System.currentTimeMillis();

            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                Log.d("SpeechRecognition", "Event: " + i + bundle.toString());
            }

            @Override
            public void onResults(Bundle results) {
                // Called when the final recognition results are available
                ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(result.get(0).length()>text.getText().length())
                    text.setText(result.get(0));
                Log.d("SpeechRecognition", "Recognized text: " + result.get(0));
            }

            @Override
            public void onError(int error) {
                // Called when an error occurs during speech recognition
                Log.d("SpeechRecognition", "Speech recognition error: " + error);
            }

            private void checkForSilence() {

                if (System.currentTimeMillis() - lastSpeechTime > SilenceTimeOut) {
                    speechRecognizer.stopListening();
                    speechRecognizer.cancel();
                    speechRecognizer.destroy();

                    scaleAnimation.cancel();
                    button.setScaleX(1.0f);
                    button.setScaleY(1.0f);
                    button.setRotation(0);

                    Log.d("SpeechRecognition", "Speech recognition end: SILENCE TIMEOUT");

                }
            }
        });

        speechRecognizer.startListening(recognizerIntent);
    }


}
