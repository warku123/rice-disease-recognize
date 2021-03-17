package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    private EditText id_login;
    private EditText password_login;
    private ImageView avatar_login;
    private CheckBox rememberpassword_login;
    private CheckBox auto_login;
    private Button button_login;
    private Button button_register;
    private SharedPreferences sp;
    private String idvalue;
    private String passwordvalue;
    private TextView forgetpass;
    private static final int PASSWORD_MIWEN = 0x81;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        id_login=(EditText) findViewById(R.id.login_id);
        password_login=(EditText) findViewById(R.id.login_password);
        avatar_login=(ImageView) findViewById(R.id.login_avatar);
        rememberpassword_login=(CheckBox) findViewById(R.id.login_rememberpassword);
        auto_login=(CheckBox) findViewById(R.id.login_autologin);
        button_login=(Button) findViewById(R.id.login_button);
        button_register=(Button)findViewById(R.id.register_button);
        forgetpass=(TextView)findViewById(R.id.forgetpass);



        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String login_result = null;
                        String name_tel,password;
                        name_tel = id_login.getText().toString();
                        password = password_login.getText().toString();
                        login_result = HttpClient.doPost_Usr_info(
                                "http://40.73.0.45/user/fetch_one",
                                name_tel,password,name_tel);
                        Log.d("login", "run: "+login_result);
                        String[] user_info = login_result.split("####");
                        if (user_info.length==3) {
                            Userinfo.is_login = true;
                            Userinfo.username = user_info[0];
                            Userinfo.tel_number = user_info[2];
                            login.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(login.this, mypage.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        else
                        {
                            login.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                thread.start();

            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "注册", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(login.this,register.class);
                startActivity(intent);
                // finish();

            }
        });
    }
}