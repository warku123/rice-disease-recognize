package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "注册", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(login.this,register.class);
                startActivity(intent);
                // finish();

            }
        });
        forgetpass.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(login.this,ForgetPassword.class);
                startActivity(intent);
            }
        });
    }
}