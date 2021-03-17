package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

    }

    String str2;
    public void identifycode(View v){
        int s=(int)((Math.random()*9+1)*100000);
        str2=Integer.toString(s);
        Toast toast=Toast.makeText(getApplicationContext(),str2, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void submitpin(View v){
        EditText login_pin = findViewById(R.id.login_pin);
        EditText login_tel = findViewById(R.id.login_tel);
        String code = login_pin.getText().toString().trim();
        String tel = login_tel.getText().toString().trim();
        String flag="login";
        if(code.equals(str2)) {
            Intent get = getIntent();
            Intent i = new Intent(ForgetPassword.this, setpassword.class);
            i.putExtra("tel",tel);
            i.putExtra("flag",flag);
            startActivity(i);
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "验证码不正确！请继续获取验证码！", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}