package com.example.cogrice;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class UserDao {
    Connection conn = JdbcUtil.getInstance().getConnection();

    String [] organize =  {"dautoid","su_account","su_password","su_mobilephone",
            "provice","su_realname","city","su_registrationno","su_unifiedsocialcreditcode",
            "su_contactman","su_idnumber","su_email","su_identify"};

    String [] person =  {"dautoid","su_account","su_password","su_mobilephone",
            "provice","su_realname","su_age","su_gender","su_from",
            "su_idnumber","su_email","city","su_identify"};

    //注册
    public  boolean register(String [] s){
        if (conn==null){
            return false;
        }else {
            //进行数据库操作
            String sql0 = "INSERT into sys_user" +
                    "(su_identify, su_account, su_password, su_mobilephone," +
                    "sys_user.provice, su_realname, su_age, su_gender," +
                    "su_from, su_idnumber, su_email, city)" +
                    "values(?,?,?,?,   ?,?,?,?,   ?,?,?,?)";
            String sql1 = "INSERT into sys_user" +
                    "(su_identify, su_account, su_password, su_mobilephone, sys_user.provice," +
                    "su_realname,  city, su_registrationno, su_unifiedsocialcreditcode," +
                    "su_contactman, su_idnumber, su_email)" +
                    "values(?,?,?,?,   ?,?,?,?,   ?,?,?,?)";
            try {
                PreparedStatement pre;
                if(s[0]=="0")
                    pre = conn.prepareStatement(sql0);
                else
                    pre = conn.prepareStatement(sql1);

                for(int i=0;i<s.length;i++)
                    pre.setString(i+1,s[i]);//setString会对SQL语句的第一个？处开始幅值，不会从0开始

                Log.i(TAG,"杜尚丰："+pre.toString());
                return !pre.execute();
            } catch (SQLException e) {
                Log.i(TAG,"here:"+e.getMessage());
                return false;
            }finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    Log.i(TAG,"finally::"+e.getMessage());
                }
            }
        }
    }

    //登录
    public Map login(String name, String password){
        Map <String,String> map=new HashMap<>();
        if (conn==null){
            Log.i(TAG,"login:conn is null");
        }else {
            //这里实现用户名和手机号同时登陆
            String sql = "select * from sys_user where su_account=? and su_password=?" +
                    "UNION select * from sys_user where su_mobilephone=? and su_password=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1,name);
                pres.setString(2,password);
                pres.setString(3,name);
                pres.setString(4,password);
                ResultSet res = pres.executeQuery();
                while(res.next())
                {
                    if(res.getString("su_identify").equals("0"))//字符串判断不能写等于
                        for(int i=0;i<person.length;i++)
                            map.put(person[i],res.getString(person[i]));
                    else
                        for(int i=0;i<organize.length;i++)
                            map.put(organize[i],res.getString(organize[i]));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return map;
    }
    //忘记密码
    public boolean forget_password(String phone,String password){
        if (conn==null){
            Log.i(TAG,"login:conn is null");
            return false;
        }else {
            String sql = "update sys_user set su_password=? where su_mobilephone=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1,password);
                pres.setString(2,phone);
                Log.i(TAG,"pre:\n"+pres.toString());
                return !pres.execute();
            } catch (SQLException e) {
                e.getErrorCode();
                Log.i(TAG,"lkzlkz");
                return false;
            }
        }
    }
    //忘记密码
    public boolean is_exist(String phone_name){
        if (conn==null){
            Log.i(TAG,"login:conn is null");
            return false;
        }else {
            String sql = "select * from sys_user where su_account=?" +
                    "UNION select * from sys_user where su_mobilephone=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1,phone_name);
                pres.setString(2,phone_name);
                Log.i(TAG,"pre:\n"+pres.toString());
                ResultSet res = pres.executeQuery();
                if (res.next()) return true;
                else  return false;
            } catch (SQLException e) {
                e.getErrorCode();
                Log.i(TAG,"lkzlkz2222");
                return false;
            }
        }
    }

}