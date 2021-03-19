package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class mypage extends AppCompatActivity {
    ImageButton home;
    ImageButton platform;
    ImageButton mine;
    TextView login;
    TextView question;
    TextView call;
    TextView tel_number;

    // 需要把将要操作的组件激活
    Button goto_history_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        init();
        // TODO 改成viewListener
        View.OnClickListener bottomlistener = new View.OnClickListener() {
            Intent intent=null;
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.home:
                        intent=new Intent(mypage.this,photopage.class);
                        break;
                    case R.id.platform:
                        intent=new Intent(mypage.this,platform.class);
                        break;
                    case R.id.goto_history_button:
                        intent = new Intent(mypage.this, MyHistoryActivity.class);
                        break;
                    default:
                        break;
                }
                // 打开新活动页面
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        };
        login.setOnClickListener(new View.OnClickListener() {
              public void onClick(View view) {
                  // TODO Auto-generated method stub
                  Intent intent = new Intent(mypage.this, login.class);
                  startActivity(intent);
              }
        });
        question.setOnClickListener(new View.OnClickListener(){
           public void onClick(View view){
               Intent intent = new Intent(mypage.this,CustomerService.class);
               intent.putExtra("question","product");
               startActivity(intent);
           }
        });


        home.setOnClickListener(bottomlistener);
        platform.setOnClickListener(bottomlistener);
        // TODO 修改buttom Listener
        goto_history_button.setOnClickListener(bottomlistener);
    }
    private void init(){
        home=findViewById(R.id.home);
        platform=findViewById(R.id.platform);
        mine=findViewById(R.id.mine);
        login=findViewById(R.id.pleaselogin);
        question=findViewById(R.id.question_feedback);
        call=findViewById(R.id.customer_service);
        tel_number = findViewById(R.id.history_login);
        // 加载组件
        goto_history_button = findViewById(R.id.goto_history_button);

        if(Userinfo.is_login==true)
        {
            login.setText(Userinfo.username);
            tel_number.setText(Userinfo.tel_number);
        }
    }
    public  void calltel(View view){
        String number = "18801013877";
        Log.i("mw","===="+number);

        //android6版本获取动态权限
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.CALL_PHONE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

        //如果需要手动拨号将Intent.ACTION_CALL改为Intent.ACTION_DIAL（跳转到拨号界面，用户手动点击拨打）
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" +number);
        intent.setData(data);
        startActivity(intent);
    }
}