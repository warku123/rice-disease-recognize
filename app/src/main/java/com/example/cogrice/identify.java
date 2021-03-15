package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class identify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        View background = findViewById(R.id.identify);
        background.getBackground().setAlpha(200);
    }
    String str2;
    public void identifycode(View v){
        int s=(int)((Math.random()*9+1)*100000);
        str2=Integer.toString(s);
        Toast toast=Toast.makeText(getApplicationContext(),str2, Toast.LENGTH_SHORT);
        toast.show();
    }
    public void submit(View v){
        EditText editText4 = findViewById(R.id.editText4);
        String code = editText4.getText().toString().trim();
        if(code.equals(str2)) {
            Intent get = getIntent();
            String username = get.getStringExtra("username");
            String tel = get.getStringExtra("tel");
            Intent i = new Intent(identify.this, setpassword.class);
            i.putExtra("username",username);
            i.putExtra("tel",tel);
            startActivity(i);
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "验证码不正确！请继续获取验证码！", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}