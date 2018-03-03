package com.develop.app.eventtop.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class ImageUtility {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int ROTATION_DEGREES = 90;

    public static Bitmap fileToBitmap(String path) {
        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        bitmap = rotateImage(bitmap, image.getAbsolutePath());

//        if (getFileSize(image) <= 1) {
//            float ratio = 0.2f;
//            return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * ratio), (int) (bitmap.getHeight() * ratio), true);
//        }
//
//        if (getFileSize(image) >= 2 && getFileSize(image) <= 3) {
//            float ratio = 0.4f;
//            return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * ratio), (int) (bitmap.getHeight() * ratio), true);
//        }
//
//        if (getFileSize(image) >= 4) {
//            float ratio = 0.06f;
//            return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * ratio), (int) (bitmap.getHeight() * ratio), true);
//        }

        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
    }

    public static String bitmapToFile(Bitmap bitmap, Context context, String fileName, Activity activity) {
        File f = new File(context.getExternalCacheDir(), fileName);
        try {
            boolean isPermitted = verifyStoragePermissions(activity);

            if (isPermitted) {
                f.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90 /*ignored for PNG*/, bos);
                byte[] bitmapData = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapData);
                fos.flush();
                fos.close();
            } else {
                Log.e("bitmapToFile", "not permitted");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("err bitmapToFile", e.getMessage());
        }
        return f.getAbsolutePath();
    }

    private static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        return true;
    }

    private static long getFileSize(File file) {
        long fileSizeInBytes = file.length();
        long fileSizeInKB = fileSizeInBytes / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;

        Log.d("size", String.valueOf(fileSizeInMB));
        return fileSizeInMB;
    }

    public static Bitmap rotateImage(Bitmap image, String path) {
        Bitmap rotatedBitmap = null;

        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Matrix matrix = new Matrix();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = ROTATION_DEGREES;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = ROTATION_DEGREES * 2;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = ROTATION_DEGREES * 3;
                    break;
                default:
                    break;
            }

            matrix.postRotate(orientation);
            rotatedBitmap = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("err rotateImage", e.getMessage());
        }
        return rotatedBitmap;
    }

    public static int getOrientation(String path) {
        int orientation = 0;

        try {
            ExifInterface exifInterface = new ExifInterface(path);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = ROTATION_DEGREES;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = ROTATION_DEGREES * 2;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = ROTATION_DEGREES * 3;
                    break;
                default:
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("err getOrientation", e.getMessage());
        }
        return orientation;
    }

    public static String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static String getImagePath(Context context, Uri filePath) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(filePath,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String documentID = cursor.getString(0);

            String[] selectionArgs = new String[] {documentID};

            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Images.Media._ID + " = ?",
                    selectionArgs,
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
        }
        return path;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static String downloadImageFromUrl(String url, Context context) {
        final File f = new File(context.getExternalCacheDir(), "fbImage" + UUID.randomUUID().toString() + ".jpg");

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            f.createNewFile();
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                            byte[] bitmapData = bos.toByteArray();

                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(bitmapData);
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("errorDrawable", errorDrawable.toString());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(context).load(url).into(target);
        return f.getAbsolutePath();
    }
}
