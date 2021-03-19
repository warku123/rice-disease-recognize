package com.example.cogrice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.cogrice.adapters.HistoryRecordsAdapter;
import com.example.cogrice.dataclass.History;
import com.example.cogrice.utils.AlertHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MyHistoryActivity extends AppCompatActivity {

    private RecyclerView historyCardRecyclerView;

    private Handler historyViewHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case History.GOT_ALL_HISTORIES:
                    // 在内部内中使用Parent.this
                    Bundle bundle = msg.getData();
                    List<History> histories = (List<History>) bundle.get("historyList");
                    // 填充信息
                    HistoryRecordsAdapter.fillRecyclerView(historyCardRecyclerView, MyHistoryActivity.this, histories);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
    }

    public void initComponents() {
        setContentView(R.layout.activity_my_history);
        this.historyCardRecyclerView = (RecyclerView) findViewById(R.id.history_card_recycler_view);
        // HistoryRecordsAdapter.fillRecyclerView(this.historyCardRecyclerView,this,new ArrayList<>());
        AlertHelper.toastAlert("正在拉取历史记录，请稍候……");
        HistoryRecordsAdapter.setContext(this);
        History.startDownloadingHistories(historyViewHandler);
    }

}