package com.example.textrecognition;

import static android.Manifest.permission.CAMERA;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.content.ClipboardManager;
import android.content.ClipData;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.TextBlock;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


public class ScannerActivity extends AppCompatActivity {

    private ImageView captureTV;
    private TextView resultTV;
    private Button snapBtn,DetectBtn,HomeBtn,importBtn;
    public Bitmap imageBitmap=null,source=null,bitmapimage;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    int sdk = Build.VERSION.SDK_INT;
    public String CopyText = null;
    public  TextToSpeech t2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        captureTV = findViewById(R.id.idTVCaptureImage);
        snapBtn = findViewById(R.id.idBtnSnap);
        HomeBtn = findViewById(R.id.idBBtnHome);
        DetectBtn = findViewById(R.id.idBtnCopy);
        importBtn = findViewById(R.id.idBtnImport);

        DetectBtn.setVisibility(View.GONE);



        ActivityResultLauncher<String> getimage= registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        String imageuri = result.toString();
                        try {
                            bitmapimage = getBitmapFromUri(result);
                            captureTV.setImageBitmap(bitmapimage);
                        } catch (IOException e) {
                            Log.e("tag", e.toString());
                        }
                    }
                });






        DetectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();



            }
        });


        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getimage.launch("image/*");
                DetectBtn.setVisibility(View.VISIBLE);
            }
        });


        HomeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ScannerActivity.this,MainActivity.class);
                startActivity(i);

            }
        });


        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermissions()){
                    captureImage();
                    DetectBtn.setVisibility(View.VISIBLE);
                }
                else{
                    requestPermission();
                }
            }
        });

    }
    private boolean checkPermissions(){
        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;

    }


    private void requestPermission(){
        int PERMISSION_CODE =200;
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},PERMISSION_CODE);
    }

    private void captureImage()
    {
        Intent takePicture =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture,REQUEST_IMAGE_CAPTURE);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            Boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if(cameraPermission){
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
                captureImage();

            }
            else{
                Toast.makeText(this,"Permissions not Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(ScannerActivity.this,MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                captureTV.setImageBitmap(imageBitmap);
        }
    }

    private void detectText() {
        if(imageBitmap != null){
            source = imageBitmap.copy(imageBitmap.getConfig(),true);
        }
        else if(bitmapimage != null){
            source = bitmapimage.copy(bitmapimage.getConfig(),true);
        }
        if(source != null) {
            InputImage image = InputImage.fromBitmap(source, 0);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onSuccess(Text text) {
                    StringBuilder result = new StringBuilder();
                    for (Text.TextBlock block : text.getTextBlocks()) {
                        String blockText = block.getText();
                        Point[] blockCornerPointer = block.getCornerPoints();
                        Rect BlockFrame = block.getBoundingBox();
                        for (Text.Line line : block.getLines()) {
                            String lineText = line.getText();
                            Point[] lineCornerPoint = line.getCornerPoints();
                            Rect linRect = line.getBoundingBox();
                            for (Text.Element element : line.getElements()) {
                                String elementText = element.getText();
                                result.append(elementText);
                                result.append(" ");
                            }
                            CopyText = result.toString();
                            result.append("\n");

                        }
                    }
                    if(CopyText==null){
                        Toast.makeText(ScannerActivity.this, "Nothing to Copy", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(CopyText);
                        Toast.makeText(ScannerActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ScannerActivity.this,TextDisplay.class);
                        intent.putExtra("Displaydata",CopyText);
                        startActivity(intent);


                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ScannerActivity.this, "Failed to detect text", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(ScannerActivity.this, "Failed to detect text", Toast.LENGTH_SHORT).show();

        }
        imageBitmap = null;
        bitmapimage = null;



    }
    Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor=
                getContentResolver().openFileDescriptor(uri,"r");
        FileDescriptor fileDescriptor =parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

}