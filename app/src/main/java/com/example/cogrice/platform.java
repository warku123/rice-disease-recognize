package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class platform extends AppCompatActivity {
    ImageButton home;
    ImageButton platform;
    ImageButton mine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform);
        init();
        View.OnClickListener bottomlistener = new View.OnClickListener() {
            Intent intent=null;
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.home:
                        intent=new Intent(platform.this,photopage.class);
                        break;
                    case R.id.mine:
                        intent=new Intent(platform.this,mypage.class);
                        break;
                    default:
                        break;
                }
                startActivity(intent);
                //去掉跳转动画
                overridePendingTransition(0, 0);
            }
        };
        home.setOnClickListener(bottomlistener);
        mine.setOnClickListener(bottomlistener);
    }
    private void init(){
        home=findViewById(R.id.home);
        platform=findViewById(R.id.platform);
        mine=findViewById(R.id.mine);
    }
}