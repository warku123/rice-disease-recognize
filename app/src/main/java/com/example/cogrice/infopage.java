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
                    response = doPost("http://40.73.0.45:80/upload");
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

    public String doPost(String httpUrl) {
        String attachmentName = "bitmap";
        String attachmentFileName = "bitmap.bmp";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        String response_inner = null;
        try {
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL(httpUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);

            //bitmap to byte array
            byte[] pixels = Bitmap2Bytes(bitmap);

            request.write(pixels);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

            request.flush();
            request.close();
            //accept response
            InputStream responseStream = new
                    BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            response_inner = stringBuilder.toString();
//            Log.d("Response", "doPost: "+response_inner);
            responseStream.close();

            httpUrlConnection.disconnect();
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response_inner;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}