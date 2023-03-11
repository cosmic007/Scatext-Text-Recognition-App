package com.example.textrecognition;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.icu.text.CaseMap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    RelativeLayout myLayout;
    AnimationDrawable animationDrawable;
    private Button captureBtn,ttsbtn,feedbtn;
    private TextView title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        captureBtn = findViewById(R.id.BtnCapture);
        ttsbtn = findViewById(R.id.BtnTTS);
        feedbtn = findViewById(R.id.Btnfeed);
        ttsbtn.setTranslationY(50);
        ttsbtn.animate().translationYBy(-50).setDuration(1500);
        captureBtn.setTranslationY(50);
        captureBtn.animate().translationYBy(-50).setDuration(1500);
        title = findViewById(R.id.idTVHead);
        title.setAlpha(0f);
        title.animate().alpha(1f).setDuration(1500);

        String device = Build.MODEL;

        feedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                String UriText = "mailto:"+ Uri.encode("cosmicriderrr@gmail.com") +"?subject="+Uri.encode("Feedback from "+device);
                Uri uri = Uri.parse(UriText);
                intent.setData(uri);
                startActivity(Intent.createChooser(intent,"Send email"));
            }
        });


        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,ScannerActivity.class);
                startActivity(i);


            }
        });


        ttsbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,SpeechActivity.class);
                startActivity(i);
            }
        });


    }
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Exit");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Purple));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.Purple));






    }


}