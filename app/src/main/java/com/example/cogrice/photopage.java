package com.example.cogrice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.BreakIterator;

public class photopage extends AppCompatActivity {
    public static BreakIterator weather;
    ImageButton camera_butt;
    ImageButton home;
    ImageButton platform;
    private static String cityName = "";
    ImageButton mine;
    Button album_butt;
    TextView tv;
    TextPaint tp;
    TextView tv2;
    TextPaint tp2;
    TextView thelocation;
    private static Context context = null;
    static final int REQUEST_IMAGE_CAPTURE = 101, REQUEST_IMAGE_ALBUM = 102;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopage);

        //组件初始化
        init();

//        context=photopage.this.getApplicationContext();
//        LocationUtils.getCNBylocation(context);
//        cityName = LocationUtils.cityName;
//
//        location.setText(cityName);
        //提供位置定位服务的位置管理器对象,中枢控制系统


        //全屏，隐藏手机上方状态栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        camera_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        album_butt = findViewById(R.id.choose_from_album);
        album_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchAlbumIntent();
            }
        });

        View.OnClickListener bottomlistener = new View.OnClickListener() {
            Intent intent=null;
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.platform:
                        intent=new Intent(photopage.this,platform.class);
                        break;
                    case R.id.mine:
                        intent=new Intent(photopage.this,mypage.class);
                        break;
                    default:
                        break;
                }
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        };
        platform.setOnClickListener(bottomlistener);
        mine.setOnClickListener(bottomlistener);
    }

    private void init(){
        //字体加粗
        tv = (TextView)findViewById(R.id.tip);
        tp = tv.getPaint();
        tp.setFakeBoldText(true);
        tv2 = (TextView)findViewById(R.id.apptitle);
        tp2 = tv2.getPaint();
        tp2.setFakeBoldText(true);
        thelocation = (TextView)findViewById(R.id.location);

        home=findViewById(R.id.home);
        platform=findViewById(R.id.platform);
        mine=findViewById(R.id.mine);
        camera_butt = findViewById(R.id.take_photo);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回数据 
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    try {
    //                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //获取图片

                        Intent intent = new Intent(photopage.this, infopage.class);
                        intent.putExtra("URI",imageUri);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_IMAGE_ALBUM:
                    imageUri = data.getData();
                    try {
                        //                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //获取图片

                        Intent intent = new Intent(photopage.this, infopage.class);
                        intent.putExtra("URI",imageUri);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                default:
                    break;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        ActivityCompat.requestPermissions(photopage.this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_DOCUMENTS,
                Manifest.permission.INTERNET
        },101);

        // 创建File对象，用于存储拍照后的图片
        //存放在手机SD卡的应用关联缓存目录下
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
               /* 从Android 6.0系统开始，读写SD卡被列为了危险权限，如果将图片存放在SD卡的任何其他目录，
                  都要进行运行时权限处理才行，而使用应用关联 目录则可以跳过这一步
                */
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
                /*
                   7.0系统开始，直接使用本地真实路径的Uri被认为是不安全的，会抛 出一个FileUriExposedException异常。
                   而FileProvider则是一种特殊的内容提供器，它使用了和内 容提供器类似的机制来对数据进行保护，
                   可以选择性地将封装过的Uri共享给外部，从而提高了应用的安全性
                 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //大于等于版本24（7.0）的场合
            imageUri = FileProvider.getUriForFile(photopage.this, "com.example.cogrice.fileprovider", outputImage);
        } else {
            //小于android 版本7.0（24）的场合
            imageUri = Uri.fromFile(outputImage);
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchAlbumIntent(){
        ActivityCompat.requestPermissions(photopage.this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_DOCUMENTS,
                Manifest.permission.INTERNET
        },102);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_ALBUM); // 打开相册

    }
}