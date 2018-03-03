package com.develop.app.eventtop.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.develop.app.eventtop.R;

public class ProgressDialog {

    private Context context;
    private Activity activity;
    private AlertDialog alertDialog;

    public ProgressDialog(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.progress_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setContentView(view);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "STV_0.ttf");
        TextView messageTxt = alertDialog.findViewById(R.id.dialog_text);
        messageTxt.setTypeface(typeface);
        messageTxt.setText(message);
        alertDialog.show();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}
