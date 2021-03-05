package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class photopage extends AppCompatActivity {
    ImageButton camera_butt;
    TextView tv;
    TextPaint tp;
    TextView tv2;
    TextPaint tp2;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopage);

        //全屏，隐藏手机上方状态栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //字体加粗
        tv = (TextView)findViewById(R.id.tip);
        tp = tv.getPaint();
        tp.setFakeBoldText(true);
        tv2 = (TextView)findViewById(R.id.apptitle);
        tp2 = tv2.getPaint();
        tp2.setFakeBoldText(true);

        camera_butt = findViewById(R.id.take_photo);
        camera_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        ActivityCompat.requestPermissions(photopage.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}