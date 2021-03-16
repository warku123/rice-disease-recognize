package com.example.cogrice.utils.access;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 用于URL间接数据库请求
 */
public class SQLUrlHelper {


    /**
     * 底层封装，主方法
     * @param url
     * @param paramConcatString
     * @return
     */
    public static String get(String url,String paramConcatString)
    {
        String result = "";
        try{
            String urlName = url + "?"+param;//
            URL urlWithParams = new URL(urlName);
            URLConnection connection = urlWithParams.openConnection();
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine())!= null)
            {
                result += line;
            }
            in.close();
        }catch(Exception e){
            System.out.println("Helloword！！"+e);
        }
        return result;
    }

    public static String sendPost(String url,String param)
    {
        String result="";
        try{
            URL httpurl = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection)httpurl.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            PrintWriter out = new PrintWriter(httpConn.getOutputStream());
            out.print(param);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            while ((line = in.readLine())!= null)
            {
                result += line;
            }
            in.close();
        }catch(Exception e){
            System.out.println("Helloword！"+e);
        }
        return result;
    }
}
