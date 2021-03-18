package com.example.cogrice.http;

import android.content.Context;
import android.util.Log;

import com.example.cogrice.utils.GlobalHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/*https://stackoverflow.com/questions/36952836/download-file-with-android-download-manager-and-save-in-application-folder*/
public class Downloader implements Runnable {

    public static String fileName;
    public String urlString;
    public String folder;
    public InputStream inputStream;
    public URLConnection urlConnection;
    public String filePath;
    public DownloaderCallbackListener listener = null;

    public boolean finished;
    private boolean error;

    public Downloader(String urlString, String folder) {
        this.urlString = urlString;
        this.finished = false;
        this.error = false;
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = null;
            urlConnection = url.openConnection();
            urlConnection.connect();
            this.urlConnection = urlConnection;
            this.inputStream = url.openStream();
            fileName = urlConnection.getHeaderField("Content-Disposition");
            // your filename should be in this header... adapt the next line for your case
            fileName = fileName.substring(fileName.indexOf("filename") + 9, fileName.length());
            this.folder = GlobalHelper.getCacheDir() + folder + "/";
            this.filePath = this.folder + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFilePath(String urlString, String path) {
        return this.filePath;
    }

    public void run() {
        try {
            int total = urlConnection.getContentLength();
            int count;

            InputStream input = new BufferedInputStream(this.inputStream);
            File file = new File(this.folder);
            if (!file.exists()) {
                try {
                    //按照指定的路径创建文件夹
                    file.mkdirs();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            File outputFile = new File(this.filePath);
            if (outputFile.exists() && outputFile.isDirectory()) {
                outputFile.delete();
                outputFile.createNewFile();
            }
            OutputStream output = new FileOutputStream(this.filePath);

            byte data[] = new byte[4096];
            long current = 0;

            while ((count = input.read(data)) != -1) {
                current += count;
                if (listener != null) {
                    listener.onProgress((int) ((current * 100) / total));
                }
                output.write(data, 0, count);
            }

            output.flush();

            output.close();
            input.close();

            if (listener != null) {
                listener.onFinish();
            }
        } catch (Exception e) {
            if (listener != null)
                listener.onError(e.getMessage());
        }
    }

    public void setDownloaderCallback(DownloaderCallbackListener listener) {
        this.listener = listener;
    }

    public interface DownloaderCallbackListener {
        void onProgress(int progress);

        void onFinish();

        void onError(String message);
    }

    public String startSilently() {
        this.setDownloaderCallback(new DownloaderCallbackListener() {
            @Override
            public void onProgress(int progress) {
                // Log.d("Downloading", progress + "%");
            }

            @Override
            public void onFinish() {
                Downloader.this.finished = true;
            }

            @Override
            public void onError(String message) {
                Log.d("SilentDownloader", "ERROR");
                // FIXME 下载失败
                Downloader.this.error = true;
            }
        });
        this.run();
        // 阻塞
        while (!this.finished && !this.error) ;
        return this.error == true ? null : this.filePath;
    }

    public static InputStream getInputStream(String recordImagePath) {
        InputStream result = null;
        try {
            URL imageUrl = new URL(recordImagePath);
            URLConnection conn = imageUrl.openConnection();
            result = imageUrl.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
