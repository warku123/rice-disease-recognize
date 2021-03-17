package com.example.cogrice.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileService extends IntentService {


    public DownloadFileService() {
        super("DownloadFileService");
    }

    /**
     * 启动一个服务
     * @param parent
     * @param serviceConnection
     */
    public void startDownloadFileServiceAndBind(Context parent, ServiceConnection serviceConnection){
        Intent serviceIntent = new Intent(parent, DownloadFileService.class);
        startService(serviceIntent);
        bindService(serviceIntent,serviceConnection,BIND_AUTO_CREATE);
    }
    public static class DownloadFileServiceBinder extends Binder{
        // TODO 用活动控制服务
        public void startDownloadFrom(String url){

        }
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("MyIntentService", "Thread id is " + Thread.currentThread(). getId());
    }


}