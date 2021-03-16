package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

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

        String s = "success\nsuccess2\nsuccess3";

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("get", "onClick: "+HttpClient.doGet("http://40.73.0.45/get"));
//                        Log.d("post", "run: "+HttpClient.doPostString("http://40.73.0.45/post",s));
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