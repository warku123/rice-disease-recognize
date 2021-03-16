package com.example.cogrice.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cogrice.R;
import com.example.cogrice.dataclass.Wiki;
import com.example.cogrice.utils.SpacesItemDecoration;

import java.util.ArrayList;

public class WikiRecordsAdapter extends RecyclerView.Adapter<WikiRecordsAdapter.WikiViewHolder> {

    public static final int SPACE = 2;
    private ArrayList<Wiki> wikiRecords;

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public WikiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 初始化组件
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wiki_card, parent, false);
        return new WikiViewHolder(view);
    }

    /**
     * 获取数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull WikiViewHolder holder, int position) {
        holder.insatncePhoto.setImageBitmap(wikiRecords.get(position).getInstancePhoto());
        holder.type.setText(wikiRecords.get(position).getBriefIntro().toString());
    }

    @Override
    public int getItemCount() {
        return this.wikiRecords.size();
    }

    /**
     * 放置WikiCard
     */
    public class WikiViewHolder extends RecyclerView.ViewHolder {
        // 实际填充
        // private ConstraintLayout wikiCard;
        private ImageView insatncePhoto;
        private TextView type;

        public WikiViewHolder(@NonNull View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.wiki_card_date);
            insatncePhoto = (ImageView) itemView.findViewById(R.id.wiki_card_image);
        }
    }

    public WikiRecordsAdapter(ArrayList<Wiki> wikiRecords) {
        this.wikiRecords = wikiRecords;
    }

    public static void fillRecyclerView(RecyclerView recyclerView,Context parent){
        recyclerView.addItemDecoration(new SpacesItemDecoration(WikiRecordsAdapter.SPACE));
        System.out.println("读取远程数据填充");
        ArrayList<Wiki> localHistoriesList = Wiki.getAllRemoteWikis();
        // TODO 读取真实的远程数据
        WikiRecordsAdapter wikiRecordsAdapter = new WikiRecordsAdapter(localHistoriesList);
        LinearLayoutManager manager = new LinearLayoutManager(parent);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(wikiRecordsAdapter);
    }
}
