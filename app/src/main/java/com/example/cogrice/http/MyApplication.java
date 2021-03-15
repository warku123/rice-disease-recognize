package com.example.cogrice.http;



import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import org.litepal.LitePalApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


//import cn.jpush.android.api.JPushInterface;


/**
 * Created by gaopeng on 2018/5/7.
 */

public class MyApplication extends LitePalApplication {

    public static final String TAG = "-----------";
    public static final String TAG2 = "++++++++++";
    public static final String TAG_finger = "finger-----------";
    public static String imgPath;//拍照的img路径
    private static MyApplication singleton;

    public static String curUser;

    //单例模式获取MyApplication
    public static MyApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        Logger.init("Logger").methodCount(2).logLevel(LogLevel.FULL);//初始化Logger,设置开启、关闭日志输出
        initOkGo();//初始化okgo
    }

    //init okgo
    private void initOkGo() {
        long timeOut =  20;//配置超时时长
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //全局的读取超时时间
        builder.readTimeout(timeOut, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(timeOut, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(timeOut, TimeUnit.MILLISECONDS);
    }

}
