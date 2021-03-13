package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.cogrice.dataclass.HistoryRecord;
import com.example.cogrice.viewadapters.MyHistoryAdapter;

import java.util.ArrayList;

public class MyHistoryActivity extends AppCompatActivity {

    private RecyclerView historyCardRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);
        showAllHistories();
    }

    private void showAllHistories() {
        ArrayList<HistoryRecord> localHistoriesList = HistoryRecord.getHistoryRecords();
        MyHistoryAdapter myHistoryAdapter = new MyHistoryAdapter();
        this.historyCardRecyclerView.setAdapter(myHistoryAdapter);
    }

    public void initComponents(){
        setContentView(R.layout.activity_my_history);
        this.historyCardRecyclerView = (RecyclerView)findViewById(R.id.history_card_recycler_view);
    }
}