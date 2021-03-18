package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zhenzi.sms.ZhenziSmsClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class identify extends AppCompatActivity {
    private static final long serialVersionUID = 1L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        View background = findViewById(R.id.identify);
        background.getBackground().setAlpha(200);
    }
    String str2;
    public void identifycode(View v){
        Intent get = getIntent();
        String tel = get.getStringExtra("tel");
        int s=(int)((Math.random()*9+1)*100000);
        str2=Integer.toString(s);
        Toast toast=Toast.makeText(getApplicationContext(),str2, Toast.LENGTH_SHORT);
        toast.show();
    }
    public void submit(View v){
        Intent get = getIntent();
        String username = get.getStringExtra("username");
        String tel = get.getStringExtra("tel");
        EditText editText4 = findViewById(R.id.editText4);
        String code = editText4.getText().toString().trim();
        String flag="register";
        if(code.equals(str2)) {
            Intent i = new Intent(identify.this, setpassword.class);
            i.putExtra("username",username);
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