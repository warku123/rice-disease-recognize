package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class infopage extends AppCompatActivity{
    private Uri imageUri;
    private String imagepath;
    private ImageView rice_image_view;
    private Button returnbtn,introbtn;
    private TextView result_view;
    private InputStream picstream_in;
    private String response;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infopage);

        rice_image_view = findViewById(R.id.rice_image);

        imageUri = getIntent().getParcelableExtra("URI");

        result_view = findViewById(R.id.disease_output);

        returnbtn=findViewById(R.id.returnbtn);
        returnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(infopage.this,photopage.class);
                startActivity(intent);
            }
        });

        introbtn = findViewById(R.id.disease_introduce);
        introbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(infopage.this,Intropage.class);
                intent.putExtra("response",response);
                startActivity(intent);
            }
        });

        introbtn = findViewById(R.id.disease_introduce);

        try {
            // 将拍摄的照片显示出来
            picstream_in = getContentResolver().openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(picstream_in);
            rice_image_view.setImageBitmap(bitmap);
            imagepath = imageUri.getPath();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    response = HttpClient.doPostBitmap("http://40.73.0.45:80/upload",bitmap);
                    infopage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String[] result = response.split("####");
                            String E_name = result[0].trim();
                            String C_name = result[1].trim();
                            String Intro = result[2].trim();
                            String Method = result[3].trim();
                            String Treat = result[4].trim();

                            result_view.setText(C_name);
                            introbtn.setVisibility(View.VISIBLE);
                            introbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(infopage.this,Intropage.class);
                                    intent.putExtra("response",response);
                                    startActivity(intent);
                                }
                            });
//                            Log.d("Results", "onCreate: "+result_s);
                        }
                    });
                }
            });
            thread.start();
            result_view.setText("识别中...");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}