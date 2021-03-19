package com.example.cogrice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ForgetPassword extends AppCompatActivity {

    EditText login_tel;
    EditText login_pin;
    EventHandler handler;
    String APPKEY = "32a423ebfb6f2";
    String APPSECRET = "4a2854f3cb814426840bfc94dced5913";
    String tel;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        String flag="login";
        //如果 targetSdkVersion小于或等于22，可以忽略这一步，如果大于或等于23，需要做权限的动态申请：
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
        // 启动短信验证sdk
        MobSDK.init(this, APPKEY,APPSECRET);
        login_tel = findViewById(R.id.login_tel);
        login_pin = findViewById(R.id.login_pin);
        handler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        // 先检查有没有手机号存在于这个表中
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String res = HttpClient.doPost_select_tel(
                                        "http://40.73.0.45/user/fetch_by_tel",tel).trim();
                                Log.d("Forget", "run: "+res);
                                if(res.equals("connection failed")) {
                                    ForgetPassword.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder failed = new AlertDialog.Builder(ForgetPassword.this);
                                            failed.setTitle("网络连接失败");
                                            failed.setMessage("请检查网络连接");
                                            failed.setPositiveButton("重新找回",new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                            failed.show();
                                        }
                                    });
                                }
                                else if(res.split("####").length>1)
                                {
                                    //提交验证码成功
                                    ForgetPassword.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tel = login_tel.getText().toString().trim();
                                            Toast.makeText(ForgetPassword.this,"验证成功", Toast.LENGTH_SHORT).show();
                                            Intent get = getIntent();
                                            Intent i = new Intent(ForgetPassword.this, setpassword.class);
                                            i.putExtra("tel",tel);
                                            i.putExtra("flag",flag);
                                            startActivity(i);
                                        }
                                    });
                                }
                                else
                                {
                                    ForgetPassword.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder failed = new AlertDialog.Builder(ForgetPassword.this);
                                            failed.setTitle("修改失败");
                                            failed.setMessage("手机号不存在！");
                                            failed.setPositiveButton("重新找回",new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                            failed.show();
                                        }
                                    });
                                }

                            }
                        });
                        thread.start();
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ForgetPassword.this,"验证码已发送", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(ForgetPassword.this,"提交错误信息", Toast.LENGTH_SHORT).show();
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


    public void identifycode2(View v){
        tel = login_tel.getText().toString().trim();
        SMSSDK.getVerificationCode("86",tel);
    }

    public void submitpin(View v){
        login_pin = findViewById(R.id.login_pin);
        code = login_pin.getText().toString().trim();
        tel = login_tel.getText().toString().trim();
        SMSSDK.submitVerificationCode("86",tel,code);
//=======
//
//        EditText login_tel = findViewById(R.id.login_tel);
//        String code = login_pin.getText().toString().trim();
//        String tel = login_tel.getText().toString().trim();
//        String flag="login";
//        if(code.equals(str2)) {
//            // 先检查有没有手机号存在于这个表中
//            // 待更新
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    String res = HttpClient.doPost_select_tel(
//                            "http://40.73.0.45/user/fetch_by_tel",tel).trim();
//                    Log.d("Forget", "run: "+res);
//                    if(res.equals("connection failed")) {
//                        ForgetPassword.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                AlertDialog.Builder failed = new AlertDialog.Builder(ForgetPassword.this);
//                                failed.setTitle("网络连接失败");
//                                failed.setMessage("请检查网络连接");
//                                failed.setPositiveButton("重新找回",new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                    }
//                                });
//                                failed.show();
//                            }
//                        });
//                    }
//                    else if(res.split("####").length>1)
//                    {
//                        ForgetPassword.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent i = new Intent(ForgetPassword.this, setpassword.class);
//                                i.putExtra("tel",tel);
//                                i.putExtra("flag",flag);
//                                startActivity(i);
//                            }
//                        });
//                    }
//                    else
//                    {
//                        ForgetPassword.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                AlertDialog.Builder failed = new AlertDialog.Builder(ForgetPassword.this);
//                                failed.setTitle("修改失败");
//                                failed.setMessage("手机号不存在！");
//                                failed.setPositiveButton("重新找回",new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                    }
//                                });
//                                failed.show();
//                            }
//                        });
//                    }
//
//                }
//            });
//            thread.start();
//        }
//        else {
//            Toast toast = Toast.makeText(getApplicationContext(), "验证码不正确！请继续获取验证码！", Toast.LENGTH_SHORT);
//            toast.show();
//        }
//>>>>>>> d71989e182297e5229346e501bfc78fa7a6449ec
    }

}