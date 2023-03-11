package com.example.textrecognition;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

public class TextDisplay extends AppCompatActivity {


    private TextView Display;
    private Button Btnback,BtnShare,BtnSpeak,BtnPause;
    public String data,toSpeak=null;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_display);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Display = findViewById(R.id.idDisplayText);
        BtnShare = findViewById(R.id.idBtnShare);
        Btnback = findViewById(R.id.idBtnBack);
        BtnSpeak = findViewById(R.id.idBtnSpeak);

        LoadingDialogue loadingDialogue = new LoadingDialogue(TextDisplay.this);


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });



        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            data = extras.getString("Displaydata");
            Display.setText(data);
            toSpeak=data;

        }
        else{
            Display.setText("Not found");
        }







        Btnback.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TextDisplay.this,ScannerActivity.class);
                startActivity(i);
                if(textToSpeech!=null){
                    textToSpeech.stop();
                }

            }
        });

        BtnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toSpeak==null)
                {
                    Toast.makeText(TextDisplay.this, "Nothing to Speak", Toast.LENGTH_SHORT).show();

                }
                int time=toSpeak.length();
                time *= 9;
                loadingDialogue.startloadingdialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialogue.dismissdialog();
                    }
                },time);
                textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                }

        });

        BtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String sharebody = data;
                String sharesub= "Detected Text";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,sharesub);
                myIntent.putExtra(Intent.EXTRA_TEXT,sharebody);
                startActivity(Intent.createChooser(myIntent,"Share using"));
            }
        });















        }
}