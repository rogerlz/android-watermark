package com.rogeus.watermark.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Page2 extends Activity {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page2);

        Bitmap newImageBitmap = addWaterMark(Page1.selectedImagePath, Page1.selectedWatermarkPath);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(newImageBitmap);

        final File newFile = new File(Environment.getExternalStorageDirectory(),
                "WatermarkApp_IMG_Edited" + String.valueOf(System.currentTimeMillis()) + ".jpg");

        try {
            OutputStream fileOut = new FileOutputStream(newFile);
            newImageBitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOut);
            fileOut.flush();
            fileOut.close();
            MediaStore.Images.Media.insertImage(getContentResolver(),newFile.getAbsolutePath(),newFile.getName(),newFile.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //File origImage = new File(Page1.selectedImagePath);
        //origImage.delete();

        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "lalalalalalalalala :D");
                shareIntent.setType("image/*");

                File file = new File(newFile.getAbsolutePath());
                Uri uri = Uri.fromFile(file);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(shareIntent);

            }
        });
    }

    public static Bitmap addWaterMark(String ImageSrcPath, String WatermarkPath) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 1;
        boolean imageSet = false;

        // TODO http://developer.android.com/training/displaying-bitmaps/load-bitmap.html


        while(!imageSet) {
            try {
                Bitmap PhotoBitmap = BitmapFactory.decodeFile(ImageSrcPath, bitmapOptions);
                Bitmap WatermarkBitmap = BitmapFactory.decodeFile(WatermarkPath);

                int w = WatermarkBitmap.getWidth();
                int h = WatermarkBitmap.getHeight();

                Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

                Canvas canvas = new Canvas(result);

                //canvas.drawBitmap(PhotoBitmap, 0, 0, null);

                canvas.drawBitmap(PhotoBitmap, new Rect(0, 0, PhotoBitmap.getWidth(), PhotoBitmap.getHeight()),
                        new Rect(0, 0, w, h), null);

                canvas.drawBitmap(WatermarkBitmap, 0, 0, null);

                imageSet = true;

                return result;
            } catch (OutOfMemoryError E) {
                bitmapOptions.inSampleSize *= 2;
            }
        }
        //TODO legenda
        //Paint paint = new Paint();
        //paint.setColor(Color.RED);
        //paint.setTextSize(18);
        //paint.setAntiAlias(true);
        //paint.setUnderlineText(true);
        //canvas.drawText(WatermarkText, 20, 25, paint);
        return null;
    }

}