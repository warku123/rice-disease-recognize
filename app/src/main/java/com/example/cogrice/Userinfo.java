package com.example.cogrice;

import com.example.cogrice.utils.AlertHelper;

public class Userinfo {

    public static boolean is_login = false;
    private static boolean is_init = false;

    public Userinfo() {
        AlertHelper.warn("用户状态初始化");
    }

    public static String username;
    public static String tel_number;

    public static void init()
    {
        if(is_init==false) {
            is_login = false;
            username = null;
            tel_number = null;
            is_init = true;
        }
    }
}
