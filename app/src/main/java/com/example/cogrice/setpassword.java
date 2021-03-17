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
import android.widget.TextView;
import android.widget.Toast;

public class setpassword extends AppCompatActivity {

    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpassword);
        View background = findViewById(R.id.setpassword);
        background.getBackground().setAlpha(200);
        final Button submit;
        final EditText password,password2;
        final TextView title;
        Intent get = getIntent();
        flag = get.getStringExtra("flag");
        title = findViewById(R.id.textView4);
        title.setText("重新设置密码");
        if(flag.equals("login")){
            title.setText("重新设置密码");
        }
        password = findViewById(R.id.editText5);
        password2 = findViewById(R.id.editText6);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View arg0, boolean hasFocus) {
                if(password.getText().toString().trim().length()!=0 || password2.getText().toString().trim().length()!=0) {
                    if (hasFocus && password.getText().toString().trim().length() == 0) {
                        password.setError("密码不能为空！");
                    }
                    if (hasFocus && password2.getText().toString().trim().length() == 0) {
                        password2.setError("密码不能为空！");
                    }
                }
            }
        });
        password2.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View arg0, boolean hasFocus) {
                if(password.getText().toString().trim().length()!=0 || password2.getText().toString().trim().length()!=0) {
                    if (hasFocus && password.getText().toString().trim().length() == 0) {
                        password.setError("密码不能为空！");
                    }
                    if (hasFocus && password2.getText().toString().trim().length() == 0) {
                        password2.setError("密码不能为空！");
                    }
                }
            }
        });
        submit = findViewById(R.id.button8);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = password.getText().toString().trim();
                String str2 = password2.getText().toString().trim();
                if (!str.equals("") && !str2.equals("")) {
                    submit.setBackgroundColor(Color.parseColor("#ffff0000"));
                } else {
                    submit.setBackgroundColor(Color.parseColor("#9bff0000"));
                }
            }
        });
        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = password.getText().toString().trim();
                String str2 = password2.getText().toString().trim();
                if (!str.equals("") && !str2.equals("")) {
                    submit.setBackgroundColor(Color.parseColor("#ffff0000"));
                } else {
                    submit.setBackgroundColor(Color.parseColor("#9bff0000"));
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = password.getText().toString().trim();
                String str2 = password2.getText().toString().trim();
                int count_abc=0, count_num=0, count_oth=0;
                int length=str2.length();
                char[] chars = str.toCharArray();
                for(int i = 0; i < length; i++){
                    if((chars[i] >= 65 && chars[i] <= 90) || (chars[i] >= 97 && chars[i] <=122)){
                        count_abc++;
                    }else if(chars[i] >= 48 && chars[i] <= 57){
                        count_num++;
                    }else{
                        count_oth++;
                    }
                }

                if (str.equals(str2) && length>=6 && count_abc>0 && count_num>0 && count_oth>0) {
                    String username = getIntent().getStringExtra("username");
                    String tel_number  = getIntent().getStringExtra("tel");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String res = HttpClient.doPost_Usr_info(
                                    "http://40.73.0.45/user/insert",
                                    username,str,tel_number).trim();
                            Log.d("register_res", "run: "+res);
                            if(res.trim().equals("1")){
                                setpassword.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder login = new AlertDialog.Builder(setpassword.this);
                                        login.setTitle("注册成功");
                                        login.setMessage("你已经注册成功了，是否要立马登录？");
                                        login.setIcon(R.drawable.happy);
                                        login.setPositiveButton("欣然接受", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                //跳转
                                                Intent link = new Intent(setpassword.this, login.class);
                                                startActivity(link);
                                            }
                                        });
                                        login.setNegativeButton("残忍拒绝", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent link = new Intent(setpassword.this, mypage.class);
                                                startActivity(link);
                                            }
                                        });
                                        login.show();
                                    }
                                });
                            }
                            else {
                                setpassword.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder failed = new AlertDialog.Builder(setpassword.this);
                                        failed.setTitle("注册失败");
                                        failed.setMessage("用户名或手机号已存在！");
                                        failed.setPositiveButton("重新注册", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent link = new Intent(setpassword.this,register.class);
                                                startActivity(link);
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
                else if(!str.equals(str2)){
                    Toast toast=Toast.makeText(getApplicationContext(), "两次输入的密码不一致！", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(length<6 && str.equals(str2)){
                    Toast toast=Toast.makeText(getApplicationContext(), "密码长度必须大于6位！", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if((count_abc==0 || count_num==0 || count_oth==0) && length>=6 && str.equals(str2) ){
                    Toast toast=Toast.makeText(getApplicationContext(), "密码中字符类型过少！", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}