package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class mypage extends AppCompatActivity {
    ImageButton home;
    ImageButton platform;
    ImageButton mine;

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

        home.setOnClickListener(bottomlistener);
        platform.setOnClickListener(bottomlistener);
        // TODO 修改buttom Listener
        goto_history_button.setOnClickListener(bottomlistener);
    }
    private void init(){
        home=findViewById(R.id.home);
        platform=findViewById(R.id.platform);
        mine=findViewById(R.id.mine);
        // 加载组件
        goto_history_button = findViewById(R.id.goto_history_button);
    }
}