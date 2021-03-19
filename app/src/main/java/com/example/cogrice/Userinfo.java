package com.example.cogrice;

public class Userinfo {

    public static boolean is_login = false;
    private static boolean is_init = false;
    public static String username;
    public static String tel_number;

    public static void init()
    {
        if(is_init==false) {
            is_login = false;
            username = "zpg";
            tel_number = null;
            is_init = true;
        }
    }
}
