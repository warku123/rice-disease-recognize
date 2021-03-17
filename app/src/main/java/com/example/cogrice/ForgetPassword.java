package com.example.cogrice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
            // 先检查有没有手机号存在于这个表中
            // 待更新
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String res = HttpClient.doPost_select_tel(
                            "http://40.73.0.45/user/fetch_by_tel",tel);
                    Log.d("Forget", "run: "+res);
                    if(res.split("####").length>1)
                    {
                        ForgetPassword.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "验证码不正确！请继续获取验证码！", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}