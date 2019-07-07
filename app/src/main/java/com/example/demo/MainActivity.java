package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button button, button2;
    TextToSpeech textToSpeech;
    private ArrayList<String> strings;
    String TAG = "MainActivity";
    int play_button_pressed = 0;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        button2 = findViewById(R.id.button2);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                    Locale locale = new Locale("en", "IN");  // Indian Accent
                    textToSpeech.setLanguage(locale);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (play_button_pressed == 0) {
                    play_button_pressed = 1;

                    final String toSpeak = textView.getText().toString();

                    // spliting string into chunks

                    strings = new ArrayList<String>();
                    int index = 0, count = 0;
                    while (index < toSpeak.length()) {
                        count++;
                        strings.add(toSpeak.substring(index, toSpeak.indexOf(".", index)));
                        index = toSpeak.indexOf(".", index) + 1;

                        Log.d(TAG, "strings ----------- " + strings.size() + "index: " + index + "toSpeak.length -----" + toSpeak.length() + "count ----" + count);
                    }

                    //controlling the speed of reading
                    textToSpeech.setSpeechRate((float) 0.8);
                    Bundle params = new Bundle();
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");

                //speaking started  working while the words are spoken
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        String highlightedText = "";

                        @Override
                        public void onStart(String s) {
                            highlightedText = "";
                            Log.d(TAG, s);

                            try {

                                int i = Integer.parseInt(s);

                                for (int j = 0; j < strings.size(); j++) {

                                    Log.d(TAG, "strings.size ---- " + strings.size());

                                    Log.d(TAG, "onStart: " + strings.get(j));
                                    if (i == j) {
                                        highlightedText = highlightedText + "<font color='red'>" + strings.get(j) + "</font>" + ".";
                                    } else {
                                        highlightedText = highlightedText +  strings.get(j) + ".";
                                    }
                                }
                            } catch (NumberFormatException e) {
                                highlightedText = toSpeak;
                            } finally {

                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        textView.setText(Html.fromHtml(highlightedText));
                                    }
                                });

                            }
                        }

                        @Override
                        public void onDone(String s) {
                            //when tts is finished speaking
                            Log.d(TAG, s);
                            Log.d(TAG, String.valueOf(strings.size()));
                        }

                        @Override
                        public void onError(String s) {
                            Log.d(TAG, "onError() called with: s = [" + s + "]");
                        }


                    });

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textToSpeech.speak(strings.get(0), TextToSpeech.QUEUE_FLUSH, params, "0");
                        //misses the first chunk therefore oth element playing it first
                    }

                    for (int i = 1; i < count; i++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            textToSpeech.speak(strings.get(i), TextToSpeech.QUEUE_ADD, params, i + "");
                        }
                    }
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (play_button_pressed == 1) {
                    play_button_pressed = 0;
                    textToSpeech.stop();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();
    }
}
