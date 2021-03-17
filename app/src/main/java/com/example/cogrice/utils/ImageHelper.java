package com.example.cogrice.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.cogrice.http.HttpFileDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

    /**
     * TODO 根据URL列表载图像文件
     * @param urlList
     * @return
     */
    public static List<Bitmap> downloadImagesAndLoadAsBitmap(List<String> urlList) {
        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
        for(String url : urlList){
            bitmapArrayList.add(downloadImageAndLoadAsBitmap(url));
        }
        return null;
    }

    /**
     * 根据单个url下载图像
     * @param recordImagePath
     * @return
     */
    public static Bitmap downloadImageAndLoadAsBitmap(String recordImagePath) {
        Bitmap result = null;
        try {
            InputStream inputStream = HttpFileDownloader.getInputStream(recordImagePath);
            result = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
