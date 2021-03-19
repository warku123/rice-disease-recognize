package com.example.cogrice.utils;

import android.content.Context;

import com.example.cogrice.dataclass.ControlMeasure;

public class GlobalHelper {
    private static String cacheDir;

    public static void initGlobaly(Context context){
        setCacheDir(context.getCacheDir().getPath());

    }

    public static String getCacheDir() {
        return cacheDir;
    }

    public static void setCacheDir(String cacheDir) {
        GlobalHelper.cacheDir = cacheDir;
    }

}
