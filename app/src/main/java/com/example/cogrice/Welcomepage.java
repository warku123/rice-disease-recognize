package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Welcomepage extends AppCompatActivity implements View.OnClickListener{

    private int recLen = 4;//跳过倒计时提示3秒
    private TextView countdown;
    Timer timer = new Timer();  //定义一个计时器
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomepage);

        initView();
        timer.schedule(task, 1000, 1000);//等待时间一秒，停顿时间一秒
        /**
         * 正常情况下不点击跳过
         */
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                //从闪屏界面跳转到首界面
                Intent intent = new Intent(Welcomepage.this, photopage.class);
                startActivity(intent);
                finish();
            }
        }, 4000);//延迟4S后发送handler信息
    }

    private void initView() {
        countdown = findViewById(R.id.countdown); //跳过
        countdown.setOnClickListener(this); //跳过监听
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @Override
                public void run() {
                    recLen--;
                    countdown.setText("跳过 " + recLen);
                    if (recLen < 0) {
                        timer.cancel();
                        countdown.setVisibility(View.GONE);//倒计时到0隐藏字体
                    }
                }
            });
        }
    };

    /**
     * 点击跳过
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.countdown:
                //从闪屏界面跳转到首界面
                Intent intent = new Intent(Welcomepage.this, photopage.class);
                startActivity(intent);
                finish();
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                break;
            default:
                break;
        }
    }
}