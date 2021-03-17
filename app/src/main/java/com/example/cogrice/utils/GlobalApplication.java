package com.example.cogrice.utils;

import android.app.Application;
import android.content.Context;

public class GlobalApplication extends Application {

    private static Context globalContext;

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = getApplicationContext();
    }

    public static Context getGlobalContext() {
        return globalContext;
    }

}
