package com.example.cogrice;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;


public class JdbcUtil {
    private static JdbcUtil instance;

    public static JdbcUtil getInstance(){
        if (instance ==null){
            instance = new JdbcUtil();
        }
        return instance;
    }
    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://40.73.0.45:3306/tftz?characterEncoding=utf8";
            return DriverManager.getConnection(url, "root", "123456");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
