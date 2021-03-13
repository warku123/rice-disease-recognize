package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class Intropage extends AppCompatActivity {
    String response;
    TextView disease_name,date_intro,intro_text,method_text,treat_text;
    Button intro_rtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intropage);

        intro_rtn = findViewById(R.id.returninfo);
        intro_rtn.setBackgroundColor(Color.TRANSPARENT);
        intro_rtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intropage.this,infopage.class);
                startActivity(intent);
            }
        });

        response = getIntent().getStringExtra("response");
        Log.i("intropage", "onCreate: "+response);
        String[] result = response.split("####");
        String E_name = result[0].trim();
        String C_name = result[1].trim();
        String Intro = result[2].trim();
        String Method = result[3].trim();
        String Treat = result[4].trim();

        disease_name = findViewById(R.id.intro_disease_name);
        disease_name.setText(C_name+"\n防治指南");

        date_intro = findViewById(R.id.date_intro);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        date_intro.setText(year+"年"+month+"月"+day+"日");

        intro_text = findViewById(R.id.intro_text);
        method_text = findViewById(R.id.method_text);
        treat_text = findViewById(R.id.treat_text);

        intro_text.setText(Intro+"\n\n");
        method_text.setText(Method+"\n\n");
        treat_text.setText(Treat+"\n\n");
    }
}