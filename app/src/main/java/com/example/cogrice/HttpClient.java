package com.example.cogrice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.cogrice.utils.AlertHelper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * https://www.jianshu.com/p/2ba4cde90e95?utm_source=oschina-app
 * 不同的请求报文格式
 */

public class HttpClient {
    private static final String crlf = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary = "*****";

    /**
     * 通过一个URL设置
     *
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

    public static Bitmap doGetBitmap(String httpurl) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        InputStream is = null;
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
            bitmap = BitmapFactory.decodeStream(is);
        } catch (ProtocolException protocolException) {
            protocolException.printStackTrace();
        } catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            // 关闭资源
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();// 关闭远程连接
        }

        return bitmap;
    }

    public static String doPost_select_tel(String httpUrl, String tel_number) {
        String response_inner = null;
        try {
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL(httpUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            // 设置连接主机服务器的超时时间：15000毫秒
            httpUrlConnection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            httpUrlConnection.setReadTimeout(60000);
            // 设置一些传递的参数
            httpUrlConnection.setRequestMethod("POST");
            // httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Connection", "close");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            //先设置字节流
            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());
            //每次写入各类文件前要固定写入三个部分，以确定写入开始
            request.writeBytes(twoHyphens + boundary + crlf);
            //文件的头部信息，注意固定格式
            request.writeBytes("Content-Disposition: form-data;name=\"tel_number\"" + crlf);
            //最后头部信息部分总共要写两个换行（crlf）
            request.writeBytes(crlf);
            request.writeBytes(tel_number);

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
        } catch (Exception e) {
            Log.d("connection_ex", "doPost_username_tel: " + e);
            return "connection failed";
        }
        return response_inner;
    }

    public static String doPost_username_tel(
            String httpUrl, String username, String tel_number) {
        String response_inner = null;
        String keyName1 = "username";
        String keyName2 = "tel_number";
        try {
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL(httpUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            // 设置连接主机服务器的超时时间：15000毫秒
            httpUrlConnection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            httpUrlConnection.setReadTimeout(60000);
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
            request.writeBytes("Content-Disposition: form-data;name=\"" + keyName1 + "\"");
            //最后头部信息部分总共要写两个换行（crlf）
            request.writeBytes(crlf + crlf);
            request.writeBytes(username);

            request.writeBytes(crlf);
            //每次写入各类文件前要固定写入三个部分，以确定写入开始
            request.writeBytes(twoHyphens + boundary + crlf);
            //文件的头部信息，注意固定格式
            request.writeBytes("Content-Disposition: form-data;name=\"" + keyName2 + "\"");
            //最后头部信息部分总共要写两个换行（crlf）
            request.writeBytes(crlf + crlf);
            request.writeBytes(tel_number);

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
        } catch (Exception e) {
            Log.d("connection_ex", "doPost_username_tel: " + e);
            return "connection failed";
        }
        return response_inner;
    }


    public static String doPost_Usr_info(
            String httpUrl, String username, String password, String tel_number) {
        String response_inner = null;
        String keyName1 = "username";
        String keyName2 = "password";
        String keyName3 = "tel_number";
        try {
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL(httpUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setConnectTimeout(5000);
            httpUrlConnection.setReadTimeout(5000);
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
            request.writeBytes("Content-Disposition: form-data;name=\"" + keyName1 + "\"");
            //最后头部信息部分总共要写两个换行（crlf）
            request.writeBytes(crlf + crlf);
            request.writeBytes(username);

            request.writeBytes(crlf);
            //每次写入各类文件前要固定写入三个部分，以确定写入开始
            request.writeBytes(twoHyphens + boundary + crlf);
            //文件的头部信息，注意固定格式
            request.writeBytes("Content-Disposition: form-data;name=\"" + keyName2 + "\"");
            //最后头部信息部分总共要写两个换行（crlf）
            request.writeBytes(crlf + crlf);
            request.writeBytes(password);

            request.writeBytes(crlf);
            //每次写入各类文件前要固定写入三个部分，以确定写入开始
            request.writeBytes(twoHyphens + boundary + crlf);
            //文件的头部信息，注意固定格式
            request.writeBytes("Content-Disposition: form-data;name=\"" + keyName3 + "\"");
            //最后头部信息部分总共要写两个换行（crlf）
            request.writeBytes(crlf + crlf);
            request.writeBytes(tel_number);

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
        } catch (Exception e) {
            Log.d("connection_ex", "doPost_username_tel: " + e);
            return "connection failed";
        }
        return response_inner;
    }

    public static String doPost_update_password(String httpUrl, String tel_number, String password) {
        String response_inner = null;
        try {
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL(httpUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            // 设置连接主机服务器的超时时间：15000毫秒
            httpUrlConnection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            httpUrlConnection.setReadTimeout(60000);
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
            request.writeBytes("Content-Disposition: form-data;name=\"tel_number\"" + crlf);
            //最后头部信息部分总共要写两个换行（crlf）
            request.writeBytes(crlf);
            request.writeBytes(tel_number);

            request.writeBytes(crlf);
            //每次写入各类文件前要固定写入三个部分，以确定写入开始
            request.writeBytes(twoHyphens + boundary + crlf);
            //文件的头部信息，注意固定格式
            request.writeBytes("Content-Disposition: form-data;name=\"password\"");
            //最后头部信息部分总共要写两个换行（crlf）
            request.writeBytes(crlf + crlf);
            request.writeBytes(password);

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
        } catch (Exception e) {
            Log.d("connection_ex", "doPost_username_tel: " + e);
            return "connection failed";
        }
        return response_inner;
    }

    public static String doPostForm(String httpUrl, List<String> formKeys, List<String> formValues) {
        String response_inner = null;
        try {
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL(httpUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            // 设置连接主机服务器的超时时间：15000毫秒
            httpUrlConnection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            httpUrlConnection.setReadTimeout(60000);
            // 设置一些传递的参数
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            //先设置字节流
            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());

            writeFormKeyValuePairs(request, formKeys, formValues);

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
        } catch (Exception e) {
            Log.d("connection_ex", "doPost_username_tel: " + e);
            return "connection failed";
        }
        return response_inner;
    }

    private static void writeFormKeyValuePairs(DataOutputStream requestOutput, List<String> formKeys, List<String> formValues) throws IOException {
        if (formKeys.size() != formValues.size()) {
            throw new Error("写入格式错误");
        }
        for (int i = 0; i < formKeys.size(); i++) {
            String key = formKeys.get(i);
            String value = formValues.get(i);
            writeOneNameAndMsg(requestOutput, key, value);
        }
    }

    public static void writeOneNameAndMsg(DataOutputStream requestOutput, String key, String value) throws IOException {
        requestOutput.writeBytes(crlf);

        //每次写入各类文件前要固定写入三个部分，以确定写入开始
        requestOutput.writeBytes(twoHyphens + boundary + crlf);
        //文件的头部信息，注意固定格式
        requestOutput.writeBytes("Content-Disposition: form-data; name=\""+key+"\"");
        //最后头部信息部分总共要写两个换行（crlf）
        requestOutput.writeBytes(crlf + crlf);
        requestOutput.writeBytes(value);
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
            // 设置连接主机服务器的超时时间：15000毫秒
            httpUrlConnection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            httpUrlConnection.setReadTimeout(60000);
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

            Log.d("is_login", "doPostBitmap: " + Userinfo.is_login);
            if (Userinfo.is_login == true) {
                request.writeBytes(crlf);
                //每次写入各类文件前要固定写入三个部分，以确定写入开始
                request.writeBytes(twoHyphens + boundary + crlf);
                //文件的头部信息，注意固定格式
                request.writeBytes("Content-Disposition: form-data;name=\"username\"");
                //最后头部信息部分总共要写两个换行（crlf）
                request.writeBytes(crlf + crlf);
                request.writeBytes(Userinfo.username);
            }

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

        } catch (Exception e) {
            Log.d("connection_ex", "doPost_username_tel: " + e);
            return "connection failed";
        }
        return response_inner;
    }

    private static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static void main(String[] args) {
        List<String> keys = new ArrayList<String>();
        keys.add("username");
        List<String> values = new ArrayList<String>();
        values.add("zpg");

        String json = doPostForm("http://40.73.0.45:80/record/get_all_records", keys,values);
        System.out.println(json);
    }

    public static String doPostOneMsg(String httpUrl, String key,String value) {
        List<String> keys = new ArrayList<String>();
        keys.add(key);
        List<String> values = new ArrayList<String>();
        values.add(value);
        return doPostForm(httpUrl,keys,values);
    }
}
