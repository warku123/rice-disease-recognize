package com.example.cogrice;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.SearchView;

import com.example.cogrice.adapters.WikiRecordsAdapter;

public class DiseaseWikiActivity extends AppCompatActivity {
    SearchView searchView = null;
    RecyclerView wikiRecycler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_wiki);
        initComponents();
    }

    private void initComponents() {
        this.searchView = (SearchView)findViewById(R.id.wiki_search_bar);
        this.wikiRecycler = (RecyclerView) findViewById(R.id.wiki_cards_recycler_view);
        WikiRecordsAdapter.fillRecyclerView(this.wikiRecycler,this);
    }
}