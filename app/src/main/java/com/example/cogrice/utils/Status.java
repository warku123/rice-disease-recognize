package com.example.cogrice.utils;

public class Status {
    private enum StatusEnum{
        LOGGED_IN,
        LOGGED_OUT
    };

    private StatusEnum userStatus;

    private String userName = null;

    public void login(String username){
        userStatus = StatusEnum.LOGGED_IN;
        this.userName = username;
    }

    public void logout(){
        userStatus = StatusEnum.LOGGED_OUT;
        this.userName = null;
    }


}
