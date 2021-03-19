package com.example.cogrice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.cogrice.adapters.WikiRecordsAdapter;
import com.example.cogrice.dataclass.ControlMeasure;
import com.example.cogrice.dataclass.Wiki;
import com.example.cogrice.utils.AlertHelper;

import java.util.ArrayList;
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
                    ControlMeasure.initControlMeasures(wikis);
                    // 填充信息
                    WikiRecordsAdapter.fillRecyclerView(wikiRecycler, DiseaseWikiActivity.this, wikis);
                    break;
                case Wiki.NETWORK_ERROR:
                    AlertHelper.toastAlert("网络繁忙，请稍候或致电客服");
                    break;
                default:
                    break;
            }
        }
    };
    private WikiRecordsAdapter wikiAdapter;
    private ImageButton home;
    private ImageButton mine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
    }

    private void initComponents() {
        setContentView(R.layout.activity_disease_wiki);
        this.searchView = (SearchView) findViewById(R.id.wiki_search_bar);
        this.searchView.setIconified(false);
        this.wikiRecycler = (RecyclerView) findViewById(R.id.wiki_cards_recycler_view);
        AlertHelper.toastAlert("正在获取防治指南");
        WikiRecordsAdapter.setContext(this);
        Wiki.startDownloadingWikis(wikiViewHandler);

        /*添加底部栏监听器*/
        home = findViewById(R.id.home);
        mine = findViewById(R.id.mine);
        View.OnClickListener bottomlistener = new View.OnClickListener() {
            Intent intent = null;

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.home:
                        intent = new Intent(DiseaseWikiActivity.this, photopage.class);
                        break;
                    case R.id.mine:
                        AlertHelper.warnNotImplemented("公共平台跳转到Wiki");
                        intent = new Intent(DiseaseWikiActivity.this, mypage.class);
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
        mine.setOnClickListener(bottomlistener);

        // 设置搜索框的监听器
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                int num = WikiRecordsAdapter.getInstance().setFilter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                int num = WikiRecordsAdapter.getInstance().setFilter(s);
                return true;
            }
        });
    }
}