package com.example.empty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class camera extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Button camera_butt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        camera_butt = findViewById(R.id.take_photo)
    }

}