package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.cogrice.dataclass.History;
import com.example.cogrice.adapters.HistoryRecordsAdapter;
import com.example.cogrice.utils.SpacesItemDecoration;

import java.util.ArrayList;

public class MyHistoryActivity extends AppCompatActivity {

    private RecyclerView historyCardRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
        // setContentView(R.layout.activity_my_history);
        showAllHistories();
    }

    private void showAllHistories() {
        ArrayList<History> localHistoriesList = History.getHistoryRecords();
        // TODO 读取真实的本地数据
        HistoryRecordsAdapter historyRecordsAdapter = new HistoryRecordsAdapter(localHistoriesList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        this.historyCardRecyclerView.setLayoutManager(manager);
        this.historyCardRecyclerView.setHasFixedSize(true);
        this.historyCardRecyclerView.setAdapter(historyRecordsAdapter);
    }

    public void initComponents(){
        setContentView(R.layout.activity_my_history);
        this.historyCardRecyclerView = (RecyclerView)findViewById(R.id.history_card_recycler_view);
        this.historyCardRecyclerView.addItemDecoration(new SpacesItemDecoration(HistoryRecordsAdapter.SPACE));
    }
}