package com.example.cogrice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        View background = findViewById(R.id.register);
        background.getBackground().setAlpha(200);
        final Button register;
        final EditText editText, editText2;
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        register = findViewById(R.id.button);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View arg0, boolean hasFocus) {
                if(editText.getText().toString().trim().length()!=0 || editText2.getText().toString().trim().length()!=0) {
                    if (hasFocus && editText.getText().toString().trim().length() == 0) {
                        editText.setError("用户名不能为空！");
                    }
                    if (hasFocus && editText2.getText().toString().trim().length() != 11) {
                        editText2.setError("手机号码格式不正确！");
                    }
                }
            }
        });
        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View arg0, boolean hasFocus) {
                if(editText.getText().toString().trim().length()!=0 || editText2.getText().toString().trim().length()!=0) {
                    if (hasFocus && editText.getText().toString().trim().length() == 0) {
                        editText.setError("用户名不能为空！");
                    }
                    if (hasFocus && editText2.getText().toString().trim().length() != 11) {
                        editText2.setError("手机号码格式不正确！");
                    }
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editText.getText().toString().trim();
                String str2 = editText2.getText().toString().trim();
                if (!str.equals("") && !str2.equals("")) {
                    register.setBackgroundColor(Color.parseColor("#ffff0000"));
                } else {
                    register.setBackgroundColor(Color.parseColor("#9bff0000"));
                }
            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editText.getText().toString().trim();
                String str2 = editText2.getText().toString().trim();
                if (!str.equals("") && !str2.equals("")) {
                    register.setBackgroundColor(Color.parseColor("#ffff0000"));
                } else {
                    register.setBackgroundColor(Color.parseColor("#9bff0000"));
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editText.getText().toString().trim();
                String tel_number
                        = editText2.getText().toString().trim();
                checkfun checktel=new checkfun();
                boolean correct=checktel.checktel(tel_number);
                if(username.equals("") || tel_number.equals("")){

                }
                else if (!username.equals("") && correct) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String res = HttpClient.doPost_username_tel(
                                    "http://40.73.0.45/user/detetive_user_repeat"
                                    ,username,tel_number).trim();
                            Log.d("Register", "run: "+res);
                            if(res.equals("0"))
                            {
                                register.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(register.this, identify.class);
                                        i.putExtra("username",username);
                                        i.putExtra("tel",tel_number);
                                        startActivity(i);
                                    }
                                });
                            }
                            else
                            {
                                register.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder failed = new AlertDialog.Builder(register.this);
                                        failed.setTitle("注册失败");
                                        failed.setMessage("手机号或用户名存在！");
                                        failed.setPositiveButton("重新注册",new DialogInterface.OnClickListener(){
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
                }
                else if(!username.equals("") && !correct){
                    Toast toast=Toast.makeText(getApplicationContext(), "手机号码格式不正确！", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(!username.equals("")){
                    Toast toast=Toast.makeText(getApplicationContext(), "此手机号码和用户名均已被注册！", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(!username.equals("")){
                    Toast toast=Toast.makeText(getApplicationContext(), "此手机号码已被注册！", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(!username.equals("")){
                    Toast toast=Toast.makeText(getApplicationContext(), "此用户名已被注册！", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}