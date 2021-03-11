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
    private Button returnbtn;
    private TextView result;
    private InputStream picstream_in;
//    OutputStream picstream_out;
    private String response;
    private Bitmap bitmap;

    private Map<String,String> respond_result = new HashMap<String, String>(){{
        put("Apple___Apple_scab","苹果黑星病");
        put("Apple___Black_rot","苹果黑腐病");
        put("Apple___Cedar_apple_rust","苹果锈病");
        put("Apple___healthy","正常苹果");
        put("Blueberry___healthy","正常蓝莓");
        put("Cherry_(including_sour)___Powdery_mildew","樱桃白粉病");
        put("Cherry_(including_sour)___healthy","正常樱桃");
        put("Corn_(maize)___Cercospora_leaf_spot","玉米叶斑病");
        put("Gray_leaf_spot","玉米灰斑病");
        put("Corn_(maize)___Common_rust_","玉米锈病");
        put("Corn_(maize)___Northern_Leaf_Blight","玉米大斑病");
        put("Corn_(maize)___healthy","正常玉米");
        put("Grape___Black_rot","葡萄黑腐病");
        put("Grape___Esca_(Black_Measles)","葡萄黑麻疹病");
        put("Grape___Leaf_blight_(Isariopsis_Leaf_Spot)","葡萄叶斑病");
        put("Grape___healthy","正常葡萄");
        put("Orange___Haunglongbing_(Citrus_greening)","柑橘黄龙病");
        put("Peach___Bacterial_spot","桃树细菌性穿孔病");
        put("Peach___healthy","正常桃");
        put("Pepper,_bell___Bacterial_spot","灯笼椒细菌性斑点病");
        put("Pepper,_bell___healthy","正常灯笼椒");
        put("Potato___Early_blight","土豆早疫病");
        put("Potato___Late_blight","土豆晚疫病");
        put("Potato___healthy","正常土豆");
        put("Raspberry___healthy","正常覆盆子");
        put("Soybean___healthy","正常黄豆");
        put("Squash___Powdery_mildew","南瓜白粉病");
        put("Strawberry___Leaf_scorch","草莓叶焦病");
        put("Strawberry___healthy","正常草莓");
        put("Tomato___Bacterial_spot","番茄细菌性斑点病");
        put("Tomato___Early_blight","番茄早疫病");
        put("Tomato___Late_blight","番茄晚疫病");
        put("Tomato___Leaf_Mold","番茄叶霉病");
        put("Tomato___Septoria_leaf_spot","番茄斑枯病");
        put("Tomato___Spider_mites","番茄蜘蛛病");
        put("Tomato___Target_Spot","番茄靶斑病");
        put("Tomato___Tomato_Yellow_Leaf_Curl_Virus","番茄黄化曲叶病毒");
        put("Tomato___Tomato_mosaic_virus","番茄花叶病毒");
        put("Tomato___healthy","正常番茄");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infopage);

        rice_image_view = findViewById(R.id.rice_image);

        imageUri = getIntent().getParcelableExtra("URI");

        result = findViewById(R.id.disease_output);

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
                    response = doPost("http://40.73.0.45:80/upload");
                }
            });
            thread.start();
            thread.join();
            String result_s = response.split("\\:")[1].trim();
            Log.d("Results", "onCreate: "+result_s);
            Log.d("Results", "onCreate: "+respond_result.get(result_s));
            result.setText(respond_result.get(result_s));

        } catch (FileNotFoundException | InterruptedException e) {
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