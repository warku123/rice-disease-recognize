package com.example.cogrice;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cogrice.dataclass.History;
import com.example.cogrice.http.GsonUtil;
import com.example.cogrice.http.HttpHelp;
import com.example.cogrice.http.I_failure;
import com.example.cogrice.http.I_success;
import com.example.cogrice.http.WeBean;
import com.example.cogrice.utils.AlertHelper;
import com.example.cogrice.utils.GlobalHelper;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    TextView weather;
    TextView thelocation;

    static final int REQUEST_IMAGE_CAPTURE = 101, REQUEST_IMAGE_ALBUM = 102;

    private Uri imageUri;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopage);


        // 全局初始化
        GlobalHelper.initGlobaly(this);

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
        getLocation2();
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
                        // intent = new Intent(photopage.this, platform.class);
                        AlertHelper.warnNotImplemented("公共平台跳转到Wiki");
                        intent = new Intent(photopage.this, DiseaseWikiActivity.class);
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

    }

    private void init() {
        //字体加粗
        weather = (TextView) findViewById(R.id.weather);
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
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 响应拍照返回结果
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

//入口是getLocation

    /**
     * 定位：权限判断
     */
    @RequiresApi(api = 24)
    private void getLocation2() {
        //检查定位权限
        ArrayList<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(photopage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(photopage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        //判断
        if (permissions.size() == 0) {//有权限，直接获取定位
            getLocationLL();
        } else {//没有权限，获取定位权限
            requestPermissions(permissions.toArray(new String[permissions.size()]), 2);


        }
    }

    //根据经纬度，获取对应的城市
    public static String getCity(Context context, double latitude, double longitude) {
        /*public void login(String username){
            userStatus = Status.StatusEnum.LOGGED_IN;
            this.userName = username;
        }

        public void logout(){
            userStatus = Status.StatusEnum.LOGGED_OUT;
            this.userName = null;
        }*/
        String cityName = "";
        List<Address> addList = null;
        Geocoder ge = new Geocoder(context);
        try {
            addList = ge.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address ad = addList.get(i);
                cityName += ad.getCountryName() + ";" + ad.getLocality();
            }
        }
        Log.v("-----", "city:" + cityName);
        return cityName;
    }

    /**
     * 定位：获取经纬度
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getLocationLL() {

        Location location = getLastKnownLocation();
        if (location != null) {
            //传递经纬度给网页
            String result = "{code: '0',type:'2',data: {longitude: '" + location.getLongitude() + "',latitude: '" + location.getLatitude() + "'}}";
//            tex.loadUrl("javascript:callback(" + result + ");");

            //日志
            String locationStr = "维度：" + location.getLatitude() + "\n"
                    + "经度：" + location.getLongitude();
//            tv.setText(  "经纬度：\n" + locationStr);
            Log.v("-----", "经纬度：\n" + locationStr);

            String temp = getCity(photopage.this, location.getLatitude(), location.getLongitude());
            temp = temp.replace("市", "").replace("中国;", "");
            isRights(temp);
        } else {
//            Toast.makeText(this, "位置信息获取失败", Toast.LENGTH_SHORT).show();
//            tv.setText(  "获取定位权限7 - " + "位置获取失败");
            Log.v("-----", "获取定位权限7 - " + "位置获取失败");
        }
    }

    /**
     * 定位：得到位置对象
     *
     * @return
     */
    private Location getLastKnownLocation() {
        //获取地理位置管理器
        LocationManager mLocationManager = (LocationManager) photopage.this.getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }

    /**
     * 定位：权限监听
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2://定位
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v("-----", "同意定位权限");
                    getLocationLL();
                } else {
                    Log.v("-----", "未同意获取定位权限");
                }
                break;
            default:
        }
    }


    String http;

    @SuppressLint("CheckResult")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void isRights(String curCity) {

        http = "https://api.jisuapi.com/weather/query?appkey=4a77836304a7e3cd&city=" + curCity;

        new HttpHelp(new I_success() {
            @Override
            public void doSuccess(String t) throws JSONException {
                WeBean bean = GsonUtil.getInstance().fromJson(t, WeBean.class);
                Log.v("----------", bean.toString());
//                tv_00.setText(bean.getResult().getDaily().get(0).getDate()+"\n"+bean.getResult().getDaily().get(0).getWeek());
//                tv_01.setText(bean.getResult().getDaily().get(0).getDay().getWeather());
//                tv_02.setText(bean.getResult().getDaily().get(0).getDay().getWinddirect());
//                tv_03.setText(bean.getResult().getDaily().get(0).getDay().getTemphigh()+" ℃");
                weather.setText(curCity+":"+
                        bean.getResult().getDaily().get(0).getDay().getWeather() + "," +
                        bean.getResult().getDaily().get(0).getDay().getWinddirect() + "," +
                        bean.getResult().getDaily().get(0).getDay().getTemphigh() + "℃"
                );

                String sdt="";
                for (int i = 0; i < bean.getResult().getIndex().size(); i++) {
                    sdt = sdt + bean.getResult().getIndex().get(i).getIname()+":"+bean.getResult().getIndex().get(i).getIvalue()+"。"+bean.getResult().getIndex().get(i).getDetail()+"\n";
                }

                String finalSdt = sdt;
                weather.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TigDialog(photopage.this, finalSdt).showDialog();
                    }
                });

            }
        }, new I_failure() {
            @Override
            public void doFailure() {

            }
        }, photopage.this, http).getHttp2();
    }

}