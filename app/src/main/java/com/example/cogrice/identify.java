package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;


import cn.smssdk.SMSSDK;
import cn.smssdk.EventHandler;

public class identify extends AppCompatActivity {

    String APPKEY = "32a423ebfb6f2";
    String APPSECRET = "4a2854f3cb814426840bfc94dced5913";
    EventHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        View background = findViewById(R.id.identify);
        background.getBackground().setAlpha(200);
        Intent get = getIntent();
        String username = get.getStringExtra("username");
        String tel = get.getStringExtra("tel");
        String flag="register";
        //如果 targetSdkVersion小于或等于22，可以忽略这一步，如果大于或等于23，需要做权限的动态申请：
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
        // 启动短信验证sdk
        MobSDK.init(this, APPKEY,APPSECRET);
        handler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(identify.this,"验证成功", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(identify.this, setpassword.class);
                                i.putExtra("username",username);
                                i.putExtra("tel",tel);
                                i.putExtra("flag",flag);
                                startActivity(i);
                            }
                        });

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(identify.this,"验证码已发送", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        if (!TextUtils.isEmpty(des)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(identify.this,"提交错误信息", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        SMSSDK.registerEventHandler(handler);
    }




    //点击发送验证码
    public void identifycode(View v){
        Intent get = getIntent();
        String tel = get.getStringExtra("tel");
        SMSSDK.getVerificationCode("86",tel);
    }


    //提交验证
    public void submit(View v){
        Intent get = getIntent();
        String tel = get.getStringExtra("tel");
        EditText editText4 = findViewById(R.id.editText4);
        String code = editText4.getText().toString().trim();
        SMSSDK.submitVerificationCode("86",tel,code);

    }

}