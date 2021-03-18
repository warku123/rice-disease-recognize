package com.example.cogrice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.SearchView;

import com.example.cogrice.adapters.WikiRecordsAdapter;
import com.example.cogrice.dataclass.ControlMeasure;
import com.example.cogrice.dataclass.Wiki;
import com.example.cogrice.utils.AlertHelper;

import java.util.List;

public class DiseaseWikiActivity extends AppCompatActivity {
    SearchView searchView = null;
    RecyclerView wikiRecycler = null;

    private Handler wikiViewHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Wiki.GOT_ALL_WIKIS:
                    // 在内部内中使用Parent.this
                    Bundle bundle = msg.getData();
                    List<Wiki> wikis = (List<Wiki>) bundle.get("wikiList");
                    AlertHelper.warnNotImplemented("正在初始化本地防治信息列表");
                    ControlMeasure.initControlMearsures(wikis);
                    // 填充信息
                    WikiRecordsAdapter.fillRecyclerView(wikiRecycler, DiseaseWikiActivity.this, wikis);
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

    private void initComponents() {
        setContentView(R.layout.activity_disease_wiki);
        this.searchView = (SearchView) findViewById(R.id.wiki_search_bar);
        this.wikiRecycler = (RecyclerView) findViewById(R.id.wiki_cards_recycler_view);
        AlertHelper.warnNotImplemented("WIKI INIT");
        WikiRecordsAdapter.setContext(this);
        Wiki.startDownloadingWikis(wikiViewHandler);
    }
}