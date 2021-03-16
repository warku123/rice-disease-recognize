package com.example.cogrice.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URLEncoder;
import java.util.Base64;

public class ImageHelper {

    @SuppressLint("NewApi")
    public static String byteArrayToBase64String(byte[] bytes) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        return Base64.getEncoder().encodeToString(bytes);
    }

    @SuppressLint("NewApi")
    public static Bitmap base64ToBitmap(String base64ForImage) {
        byte[] imageByteArray = Base64.getDecoder().decode(base64ForImage);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        return bitmap;
    }
}
