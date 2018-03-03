package com.develop.app.eventtop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.develop.app.eventtop.utils.ImageUtility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewGallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gallery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        TextView title = findViewById(R.id.title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "STV_0.ttf");
        title.setTypeface(typeface);
        title.setText("الاستديو");

        SharedPreferences preferences = getSharedPreferences("window_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFromGallery", true);
        editor.apply();

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setDrawingCacheEnabled(true);
        rv.setItemViewCacheSize(20);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        rv.setLayoutManager(new GridLayoutManager(this, 3, GridLayout.VERTICAL, false));
        List<String> imagesPath = getImagesPath();
        Log.d("paths", imagesPath.toString());
        RecyclerView.Adapter adapter = new Adapter(imagesPath);
        rv.setAdapter(adapter);
    }

    private ArrayList<String> getImagesPath() {
        ArrayList<String> listOfImages = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID
        };
        String absolutePath;

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();

            while (cursor.moveToNext()) {
                absolutePath = cursor.getString(columnIndex);
                listOfImages.add(absolutePath);
            }

            cursor.close();
        }
        return listOfImages;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { startActivity(new Intent(this, ProgressSignUp.class)); }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ProgressSignUp.class));
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {
        List<String> imagesPath;

        Adapter(List<String> imagesPath) {
            this.imagesPath = imagesPath;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_layout, parent, false);
            return new ViewHolder(view, imagesPath);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                // Bitmap bitmap = ImageUtility.fileToBitmap(imagesPath.getJsonObject(position));
                // holder.imageView.setImageBitmap(bitmap);
                File file = new File(imagesPath.get(position));
                Picasso.with(getApplicationContext()).load(file).resize(300, 300).centerCrop().into(holder.imageView);
            }
        }

        @Override
        public int getItemCount() {
            return imagesPath.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        List<String> imagesPath;

        public ViewHolder(View itemView, List<String> imagesPath) {
            super(itemView);
            this.imagesPath = imagesPath;
            imageView = itemView.findViewById(R.id.imageview);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Uri imageUri = Uri.parse(imagesPath.get(getLayoutPosition()));
            Log.d("imgUri", imageUri.toString());
            Intent intent = new Intent(getApplicationContext(), CropPhoto.class);
            intent.putExtra("imageUri", imageUri.toString());
            getApplicationContext().startActivity(intent);
        }
    }
}
