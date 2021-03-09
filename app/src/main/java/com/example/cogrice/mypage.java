package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class mypage extends AppCompatActivity {
    ImageButton home;
    ImageButton platform;
    ImageButton mine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        init();
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
                    default:
                        break;
                }
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        };
        home.setOnClickListener(bottomlistener);
        platform.setOnClickListener(bottomlistener);

    }
    private void init(){
        home=findViewById(R.id.home);
        platform=findViewById(R.id.platform);
        mine=findViewById(R.id.mine);
    }
}