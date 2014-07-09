package com.rogeus.watermark.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;


public class Page1 extends Activity {

    private static final int SELECT_PICTURE = 1;

    public static String selectedImagePath;
    public static String selectedWatermarkPath;

    private Uri mImageCaptureUri;

    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "wmFile";
    private SharedPreferences mSharedPreferences;

    private static final int CAMERA_PIC_REQUEST = 1337;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page1);

        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        String wmFile = mSharedPreferences.getString(PREF_NAME, "");
        if (wmFile.length() > 0) {
            selectedWatermarkPath = wmFile;
            TextView txtWatermarkPath = (TextView) findViewById(R.id.txtWatermarkPath);
            txtWatermarkPath.setText("path: "+ wmFile);
        }

        ((Button) findViewById(R.id.btnChooser01))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,
                                "Select Picture"), SELECT_PICTURE);
                    }
                });

        ((Button) findViewById(R.id.btnNext01))
                .setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.CUPCAKE)
                    @Override
                    public void onClick(View view) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        File newFile = new File(Environment.getExternalStorageDirectory(),
                                "WatermarkApp_IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        mImageCaptureUri = Uri.fromFile(newFile);
                        try {
                            cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                            cameraIntent.putExtra("return-data", true);
                            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedWatermarkPath = getRealPathFromURI(selectedImageUri);
                TextView txtWatermarkPath = (TextView) findViewById(R.id.txtWatermarkPath);
                txtWatermarkPath.setText("path: "+ selectedWatermarkPath);
                SharedPreferences.Editor e = mSharedPreferences.edit();
                e.putString(PREF_NAME, selectedWatermarkPath);
                e.apply();
            } else if (requestCode == CAMERA_PIC_REQUEST) {
                selectedImagePath = mImageCaptureUri.getPath();
                final Context context = this;
                Intent page2intent = new Intent(context, Page2.class);
                startActivity(page2intent);
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
