package com.example.cogrice.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Base64;

public class ImageHelper {
    public static Bitmap base64ToBitmap(String base64ForImage){
        @SuppressLint("NewApi") byte[] imageByteArray = Base64.getDecoder().decode(base64ForImage);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        return bitmap;
    }
}
