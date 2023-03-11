package com.example.textrecognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.TextView;

public class LoadingDialogue {
    private Activity activity;
    private AlertDialog dialog;

    LoadingDialogue(Activity myActivity){
        activity =myActivity;

    }
    void startloadingdialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog,null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }
    void  dismissdialog(){
        dialog.dismiss();
    }
}
