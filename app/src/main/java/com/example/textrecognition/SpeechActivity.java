package com.example.textrecognition;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SpeechActivity extends AppCompatActivity {

    private Button speakbtn, pasteBtn, stopBtn, homeBtn;
    TextToSpeech t1;
    EditText edit;
    String paste, Speaktext = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        speakbtn = findViewById(R.id.idBtnspeak);
        pasteBtn = findViewById(R.id.idBBtnPaste);
        homeBtn = findViewById(R.id.idBBtnHome);
        stopBtn = findViewById(R.id.idBtnStop);
        edit = (EditText) findViewById(R.id.ideditText);
        LoadingDialogue loadingDialogue = new LoadingDialogue(SpeechActivity.this);



        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (t1 != null) {
                    t1.stop();
                }
                Intent i = new Intent(SpeechActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (t1 != null) {
                    t1.stop();
                }
            }
        });

        pasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData data = myClipboard.getPrimaryClip();
                ClipData.Item item = data.getItemAt(0);
                String text = item.getText().toString();
                edit.setText(text);

            }
        });


        speakbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Speaktext = edit.getText().toString();
                int time=Speaktext.length();
                time *= 9;
                loadingDialogue.startloadingdialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialogue.dismissdialog();
                    }
                },time);
                if (Speaktext == null) {
                    Toast.makeText(SpeechActivity.this, "Nothing to Speak", Toast.LENGTH_SHORT).show();

                }
                t1.speak(Speaktext, TextToSpeech.QUEUE_FLUSH, null);
                }





        });


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SpeechActivity.this,MainActivity.class);
        startActivity(i);
    }
}