package com.example.cogrice.utils;

import android.util.Log;
import android.widget.Toast;

public class AlertHelper {
    public static void toastAlert(String msg){
        Toast.makeText(GlobalApplication.getGlobalContext(), "发送广播成功", Toast.LENGTH_SHORT).show();
    }

    public static void log(String msg){
        Log.d("ALERT_HELPER_LOG",msg);
    }
}
