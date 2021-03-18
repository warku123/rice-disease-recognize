package com.example.cogrice.utils;

import android.util.Log;
import android.widget.Toast;

public class AlertHelper {
    public static void toastAlert(String msg){
        Toast.makeText(GlobalApplication.getGlobalContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void warnNotImplemented(String msg){
        System.out.println("【NOT IMPLEMENTED】" + msg);
    }
}
