package com.example.cogrice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class TupianFragment extends Fragment {

    private TextView tv, tv1, tv2, tv3, tv4, tv5;
    private ImageView iv_one, iv_two;
    private static String cityName = "";
    private String result = "";
    private static Context context = null;
    private Bitmap bitmap1, bitmap2;
    private static TupianFragment tupian = null;
    public static int tupian_hour = 60;
    private static Handler handler3 = new Handler();
    public final static String url1 = "http://api.map.baidu.com/telematics/v3/weather?location=";
    public final static String url2 = "&output=json&ak=9cCAXQFB468dsH11GOWL8Lx4";
    @SuppressWarnings("deprecation")
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tupian.getActivity().removeDialog(0);
            Toast.makeText(tupian.getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
            //		handler3.postDelayed(this, 2000);    //每两秒执行一次runnable
        }
    };
    //自动刷新
    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            tupian.send(cityName);
            Message m = tupian.handler.obtainMessage();
            tupian.handler.sendMessage(m);
            handler3.postDelayed(this, tupian_hour*3600*1000);
        }
    };

    @SuppressLint("HandlerLeak")
    @SuppressWarnings("deprecation")
    public static Handler handler1 = new Handler(){
        public void handleMessage(Message msg){
            tupian.getActivity().showDialog(0);
            //启动定时器
            handler3.postDelayed(runnable, 5000);   //五秒后执行
            new Thread(new Runnable() {
                @Override
                public void run() {
                    tupian.send(cityName);
                    Message m = tupian.handler.obtainMessage();
                    tupian.handler.sendMessage(m);
                }
            }).start();
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if(result != null){
                try {
                    JSONObject datajson = new JSONObject(result);  //第一步，将String格式转换回json格式
                    JSONArray results = datajson.getJSONArray("results");  //获取results数组

                    JSONObject city = results.getJSONObject(0);
                    String currentCity = city.getString("currentCity");  //获取city名字
                    String pm25 = city.getString("pm25");   //获取pm25
                    tv.setText("城市："+currentCity+"\n"+"pm25："+pm25);  //测试城市和pm25
                    JSONArray index = city.getJSONArray("index"); //获取index里面的JSONArray
                    //获取穿衣
                    JSONObject cy = index.getJSONObject(0);
                    String titlec = cy.getString("title");
                    String zsc = cy.getString("zs");
                    String tiptc = cy.getString("tipt");
                    String desc = cy.getString("des");
                    //获取洗车
                    JSONObject xc = index.getJSONObject(1);
                    String titlex = xc.getString("title");
                    String zsx = xc.getString("zs");
                    String tiptx = xc.getString("tipt");
                    String desx = xc.getString("des");
                    tv1.setText(titlec+" : "+zsc+"\n"+tiptc+" : "+desc);
                    tv2.setText(titlex+" : "+zsx+"\n"+tiptx+" : "+desx);

                    //weather_data, 未来几天
                    JSONArray weather_data = city.getJSONArray("weather_data");
                    //获取今天
                    JSONObject today = weather_data.getJSONObject(0);
                    String date0 = today.getString("date");
                    final String dayPictureUrl0 = today.getString("dayPictureUrl");
                    final String nightPictureUrl0 = today.getString("nightPictureUrl");
                    String weather0 = today.getString("weather");
                    String wind0 = today.getString("wind");
                    String temperature0 = today.getString("temperature");
                    tv3.setText("\n"+"今天："+date0+"\n"+"实时："+weather0+"\n"+"风力："+
                            wind0+"\n"+"温度范围："+temperature0+"\n");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            bitmap1 = returnBitMap(dayPictureUrl0);
                            bitmap2 = returnBitMap(nightPictureUrl0);
                            Message m = handler2.obtainMessage();
                            handler2.sendMessage(m);
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.handleMessage(msg);
        }
    };
    @SuppressWarnings("deprecation")
    @SuppressLint("HandlerLeak")
    private Handler handler2 = new Handler(){
        public void handleMessage(Message msg){
            if(bitmap1!=null)
                iv_one.setImageBitmap(bitmap1);
            if(bitmap2!=null)
                iv_two.setImageBitmap(bitmap2);
            if(bitmap1!=null&&bitmap2!=null){
                //停止计时器
                handler3.removeCallbacks(runnable);
                tupian.getActivity().removeDialog(0);
            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = TupianFragment.this.getActivity();
        tupian = TupianFragment.this;
        LocationUtils.getCNBylocation(context);
        cityName = LocationUtils.cityName;
        photopage.weather.setText(cityName);

        View view = inflater.inflate(R.layout.fragment_tupian, container,false);
        iv_one = (ImageView) view.findViewById(R.id.iv_one);
        iv_two = (ImageView) view.findViewById(R.id.iv_two);
        tv = (TextView) view.findViewById(R.id.tv);
        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv2 = (TextView) view.findViewById(R.id.tv2);
        tv3 = (TextView) view.findViewById(R.id.tv3);
        tv4 = (TextView) view.findViewById(R.id.tv4);
        tv5 = (TextView) view.findViewById(R.id.tv5);
        //启动计时器
        handler3.postDelayed(runnable2, tupian_hour*3600*1000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                send(cityName);
                Message m = handler.obtainMessage();
                handler.sendMessage(m);
            }
        }).start();

        return view;
    }
    private String send(String city){
        String target = url1+city+url2;  //要提交的目标地址
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpRequest = new HttpGet(target);  //创建HttpGet对象
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpclient.execute(httpRequest);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                result = EntityUtils.toString(httpResponse.getEntity()).trim();  //获取返回的字符串
            }else{
                result = "fail";
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //以Bitmap的方式获取一张图片
    public Bitmap returnBitMap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try{
            myFileUrl = new URL(url);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        try{
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }
    @Override
    public void onDestroy() {
        //停止计时器
        handler3.removeCallbacks(runnable2);
        super.onDestroy();
    }
}