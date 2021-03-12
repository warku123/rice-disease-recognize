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
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.Iterator;

public class photopage extends AppCompatActivity {
    ImageButton camera_butt;
    ImageButton home;
    ImageButton platform;
    ImageButton mine;
    Button album_butt;
    TextView tv;
    TextPaint tp;
    TextView tv2;
    TextPaint tp2;
    TextView thelocation;
    private LocationManager myLocationManager;
    private GpsStatus.Listener myListener;
    private String citylocation;
    private static Context context = null;
    private Location myLocation;
    private MyAsyncExtue myAsyncExtue;
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
            Intent intent = null;

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.platform:
                        intent = new Intent(photopage.this, platform.class);
                        break;
                    case R.id.mine:
                        intent = new Intent(photopage.this, mypage.class);
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

        myListener = new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int i) {
                switch (i) {
                    //第一次定位
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        Log.i("TAG", "第一次定位");
                        break;
                    //卫星状态改变
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        Log.i("TAG", "卫星状态改变");
                        //获取当前状态
                        if (ActivityCompat.checkSelfPermission(photopage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        GpsStatus gpsStatus = myLocationManager.getGpsStatus(null);
                        //获取卫星颗数的默认最大值
                        int maxSatellites = gpsStatus.getMaxSatellites();
                        //创建一个迭代器保存所有卫星
                        Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                        int count = 0;
                        while (iters.hasNext() && count <= maxSatellites) {
                            GpsSatellite s = iters.next();
                            count++;
                        }
                        System.out.println("搜索到："+count+"颗卫星");
                        break;
                    //定位启动
                    case GpsStatus.GPS_EVENT_STARTED:
                        Log.i("TAG", "定位启动");
                        break;
                    //定位结束
                    case GpsStatus.GPS_EVENT_STOPPED:
                        Log.i("TAG", "定位结束");
                        break;
                }
            }
        };


//        new Thread(){
//            @Override
//            public void run() {
//                Looper.prepare();//增加部分
//                myLocation=getLocation();
//                citylocation=myAsyncExtue.doInBackground(myLocation);
//                myAsyncExtue.onPostExecute(citylocation);
//                Looper.loop();//增加部分
//
//            }
//        }.start();
    }

    private void init() {
        //字体加粗
        tv = (TextView) findViewById(R.id.tip);
        tp = tv.getPaint();
        tp.setFakeBoldText(true);
        tv2 = (TextView) findViewById(R.id.apptitle);
        tp2 = tv2.getPaint();
        tp2.setFakeBoldText(true);
        thelocation = (TextView) findViewById(R.id.location);

        home = findViewById(R.id.home);
        platform = findViewById(R.id.platform);
        mine = findViewById(R.id.mine);
        camera_butt = findViewById(R.id.take_photo);

        myLocationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                        intent.putExtra("URI", imageUri);
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
                        intent.putExtra("URI", imageUri);
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
        }, 101);

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

    private void dispatchAlbumIntent() {
        ActivityCompat.requestPermissions(photopage.this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_DOCUMENTS,
                Manifest.permission.INTERNET
        }, 102);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_ALBUM); // 打开相册

    }

    private Location getLocation() {
        //获取位置管理服务

        //查找服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //定位精度: 最高
        criteria.setAltitudeRequired(false); //海拔信息：不需要
        criteria.setBearingRequired(false); //方位信息: 不需要
        criteria.setCostAllowed(true);  //是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_LOW); //耗电量: 低功耗
//        String provider = myLocationManager.getBestProvider(criteria, true); //获取GPS信息
//        myLocationManager.requestLocationUpdates(provider,2000,5,locationListener);
//        Log.e("provider", provider);
//        List<String> list = myLocationManager.getAllProviders();
//        Log.e("provider", list.toString());
//
        Location gpsLocation = null;
        Location netLocation = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { }
        myLocationManager.addGpsStatusListener(myListener);
        if (netWorkIsOpen()) {
            //2000代表每2000毫秒更新一次，5代表每5秒更新一次
            myLocationManager.requestLocationUpdates("network", 2000, 5, locationListener);
            netLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (gpsIsOpen()) {
            myLocationManager.requestLocationUpdates("gps", 2000, 5, locationListener);
            gpsLocation = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (gpsLocation == null && netLocation == null) {
            return null;
        }
        if (gpsLocation != null && netLocation != null) {
            if (gpsLocation.getTime() < netLocation.getTime()) {
                gpsLocation = null;
                return netLocation;
            } else {
                netLocation = null;
                return gpsLocation;
            }
        }
        if (gpsLocation == null) {
            return netLocation;
        } else {
            return gpsLocation;
        }
    }
    private boolean gpsIsOpen() {
        boolean isOpen = true;
        if (!myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//没有开启GPS
            isOpen = false;
        }
        return isOpen;
    }

    private boolean netWorkIsOpen() {
        boolean netIsOpen = true;
        if (!myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {//没有开启网络定位
            netIsOpen = false;
        }
        return netIsOpen;
    }

    //监听GPS位置改变后得到新的经纬度
    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.e("location", location.toString() + "....");
            // TODO Auto-generated method stub
            if (location != null) {
                //获取国家，省份，城市的名称
                Log.e("location", location.toString());
//                List<Address> m_list = getAddress(location);
                new MyAsyncExtue().execute(location);
//                Log.e("str", m_list.toString());
//                String city = "";
//                if (m_list != null && m_list.size() > 0) {
//                    city = m_list.get(0).getLocality();//获取城市
//                }
//                city = m_list;
//                show_GPS.setText("location:" + m_list.toString() + "\n" + "城市:" + city + "\n精度:" + location.getLongitude() + "\n纬度:" + location.getLatitude() + "\n定位方式:" + location.getProvider());
            } else {
                thelocation.setText("获取不到数据");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };
    private class MyAsyncExtue extends AsyncTask<Location, Void, String> {

        @Override
        protected String doInBackground(Location... params) {
            HttpClient client = new DefaultHttpClient();
            StringBuilder stringBuilder = new StringBuilder();
            HttpGet httpGet = new HttpGet("http://api.map.baidu.com/geocoder?output=json&location=23.131427,113.379763&ak=esNPFDwwsXWtsQfw4NMNmur1");
            try {
                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String b;
                while ((b = bufferedReader.readLine()) != null) {
                    stringBuilder.append(b + "\n");
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String m_list) {
            super.onPostExecute(m_list);
            Log.e("str", m_list.toString());
            String city = "";
//                if (m_list != null && m_list.size() > 0) {
//                    city = m_list.get(0).getLocality();//获取城市
//                }
            city = m_list;
            thelocation.setText("城市:" + city);
        }
    }
}

