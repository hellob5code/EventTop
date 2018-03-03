package com.develop.app.eventtop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.develop.app.eventtop.utils.ImageUtility;
import com.lyft.android.scissors.CropView;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class CropPhoto extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "CropPhoto callback";
    private CropView cropView;
    private FloatingActionButton fab1, fab2, fab3;
    private Bitmap bitmap;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_photo);

        cropView = findViewById(R.id.crop_view);
        // cropView.extensions().load(uri);

        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);

        String imageUri = getIntent().getExtras().getString("imageUri");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            bitmap = ImageUtility.fileToBitmap(imageUri);
            cropView.setImageBitmap(bitmap);

            cropView.setOnTouchListener(this);

            fab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bitmap croppedBitmap = cropView.crop();
                    cropView.refreshDrawableState();
                    cropView.setImageBitmap(croppedBitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] bytes = stream.toByteArray();
                    Intent intent = new Intent(getApplicationContext(), ProgressSignUp.class);
                    intent.putExtra("croppedImage", bytes);
                    startActivity(intent);
                }
            });

            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            fab3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] bytes = stream.toByteArray();
                    Intent intent = new Intent(getApplicationContext(), ProgressSignUp.class);
                    intent.putExtra("croppedImage", bytes);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            fab1.hide();
            fab2.hide();
            fab3.hide();
            return true;
        } else {
            fab1.show();
            fab2.show();
            fab3.show();
            return false;
        }
    }
}
