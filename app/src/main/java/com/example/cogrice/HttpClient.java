package com.example.cogrice;

import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpClient {
    private static final String crlf = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary =  "*****";

    /**
     * 通过一个URL设置
     * @param httpurl
     * @return 字符串形式的Response报文
     */
    public static String doGet(String httpurl) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpurl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            is = connection.getInputStream();
            // 封装输入流is，并指定字符集
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            // 存放数据
            StringBuffer sbf = new StringBuffer();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sbf.append(temp);
                sbf.append("\r\n");
            }
            result = sbf.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();// 关闭远程连接
        }

        return result;
    }

    public static String doPostString(String httpUrl, String request_string)
    {
        String response_inner = null;
        String attachmentName = "string";
        try {
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL(httpUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            // 设置一些传递的参数
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            //先设置字节流
            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());
            //每次写入各类文件前要固定写入三个部分，以确定写入开始
            request.writeBytes(twoHyphens + boundary + crlf);
            //文件的头部信息，注意固定格式
            request.writeBytes("Content-Disposition: form-data;name=\""+attachmentName+"\"" + crlf);
            //最后头部信息部分总共要写两个换行（crlf）
            request.writeBytes(crlf);
            request.writeBytes(request_string);
            request.writeBytes(crlf);
            //整个输入流结尾的标识
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

            request.flush();
            request.close();
            //accept response
            InputStream responseStream = new
                    BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            response_inner = stringBuilder.toString();
//            Log.d("Response", "doPost: "+response_inner);
            responseStream.close();

            httpUrlConnection.disconnect();
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response_inner;
    }

    public static String doPostBitmap(String httpUrl, Bitmap bitmap) {
        String attachmentName = "bitmap";
        String attachmentFileName = "bitmap.bmp";
        String response_inner = null;
        try {
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL(httpUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            // 设置一些传递的参数
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            //写入图片
            //先设置字节流
            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());
            //每次写入各类文件前要固定写入三个部分，以确定写入开始
            request.writeBytes(twoHyphens + boundary + crlf);
            //文件的头部信息，注意固定格式
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            //最后头部信息部分总共要写两个换行（crlf）
            request.writeBytes(crlf);

            //bitmap to byte array
            byte[] pixels = Bitmap2Bytes(bitmap);
            //正式写入pixel文件
            request.write(pixels);

            //整个输入流结尾的标识
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

            request.flush();
            request.close();
            //accept response
            InputStream responseStream = new
                    BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            response_inner = stringBuilder.toString();
//            Log.d("Response", "doPost: "+response_inner);
            responseStream.close();

            httpUrlConnection.disconnect();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response_inner;
    }
    private static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
