package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

import javax.net.ssl.HttpsURLConnection;

public class infopage extends AppCompatActivity{
    private Uri imageUri;
    private String imagepath;
    private ImageView rice_image_view;
    private Button returnbtn;
    InputStream picstream_in;
//    OutputStream picstream_out;
    String response;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infopage);

        rice_image_view = findViewById(R.id.rice_image);

        imageUri = getIntent().getParcelableExtra("URI");

        returnbtn=findViewById(R.id.returnbtn);
        returnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(infopage.this,photopage.class);
                startActivity(intent);
            }
        });



        try {
            // 将拍摄的照片显示出来
            picstream_in = getContentResolver().openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(picstream_in);
            rice_image_view.setImageBitmap(bitmap);
            imagepath = imageUri.getPath();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    doPost("http://40.73.0.45:80/upload");
                }
            });

            thread.start();

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

        try {
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL(httpUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

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
            //I want to send only 8 bit black & white bitmaps
//            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
//            for (int i = 0; i < bitmap.getWidth(); ++i) {
//                for (int j = 0; j < bitmap.getHeight(); ++j) {
//                    //we're interested only in the MSB of the first byte,
//                    //since the other 3 bytes are identical for B&W images
//                    pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
//                }
//            }
            request.write(pixels);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

            request.flush();
            request.close();

            int responseCode = httpUrlConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
                Log.d("response", "doPost: "+responseCode);
            }

            httpUrlConnection.disconnect();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Default return";
    }

    public static File saveBitmapFile(Bitmap bitmap, String filepath){
        File file=new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}