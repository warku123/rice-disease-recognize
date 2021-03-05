package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class infopage extends AppCompatActivity {
    private Uri imageUri;
    private ImageView rice_image_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infopage);

        rice_image_view = findViewById(R.id.rice_image);

        imageUri = getIntent().getParcelableExtra("URI");

        try {
            // 将拍摄的照片显示出来
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            rice_image_view.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}