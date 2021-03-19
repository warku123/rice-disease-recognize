package com.example.cogrice.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.cogrice.Intropage;
import com.example.cogrice.R;
import com.example.cogrice.dataclass.Wiki;
import com.example.cogrice.utils.AlertHelper;
import com.example.cogrice.utils.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class WikiRecordsAdapter extends RecyclerView.Adapter<WikiRecordsAdapter.WikiViewHolder> {

    public static final int SPACE = 2;

    private static Context context;
    private final ArrayList<Wiki> fullWikiList;
    private List<Wiki> filteredWikiList;

    private static WikiRecordsAdapter instance;

    public static WikiRecordsAdapter getInstance() {
        return instance;
    }

    public static void setContext(Context diseaseWikiActivity) {
        WikiRecordsAdapter.context = diseaseWikiActivity;
    }


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

    @Override
    public void onBindViewHolder(@NonNull WikiViewHolder holder, int position) {
        if (this.getContext() != null) {
            Glide.with(this.getContext()).load(filteredWikiList.get(position).getImgUrl())
                    .error(R.drawable.loadfailed)
                    .placeholder(R.drawable.loading_bg)
                    .fallback(R.drawable.loadfailed)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.insatncePhoto);
        }
        if (filteredWikiList.get(position).getEnTypeName().toLowerCase().contains("healthy")) {
            holder.setVisibility(false);
        }
        holder.diseaseCnTypeName.setText(filteredWikiList.get(position).getCnTypename());
        holder.diseaseBriefInfo.setText(filteredWikiList.get(position).getDiseaseFeature());
        holder.gotoDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WikiRecordsAdapter.getContext(), Intropage.class);
                intent.putExtra("response", Intropage.generateFormattMsgByCnName((String) holder.diseaseCnTypeName.getText()));
                // 在外部调用Activity的方法
                WikiRecordsAdapter.getContext().startActivity(intent);
            }
        });
        AlertHelper.warnNotImplemented("绑定Wiki控件");
    }

    private static Context getContext() {
        return WikiRecordsAdapter.context;
    }

    @Override
    public int getItemCount() {
        return this.filteredWikiList.size();
    }

    public int setFilter(String filterWord) {
        this.filteredWikiList = new ArrayList<Wiki>();
        for(Wiki wiki : this.fullWikiList){
            if(wiki.containsKeyword(filterWord)){
                this.filteredWikiList.add(wiki);
            }
        }
        notifyDataSetChanged();
        return this.getItemCount();
    }


    /**
     * 放置WikiCard
     */
    public class WikiViewHolder extends RecyclerView.ViewHolder {
        // 实际填充
        // private ConstraintLayout wikiCard;
        private ImageView insatncePhoto;
        private TextView diseaseCnTypeName;
        private TextView diseaseBriefInfo;
        private Button gotoDetailsButton;

        public void setVisibility(boolean isVisible) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (isVisible) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }

        public WikiViewHolder(@NonNull View itemView) {
            super(itemView);
            diseaseCnTypeName = (TextView) itemView.findViewById(R.id.wiki_card_disease_type);
            diseaseBriefInfo = (TextView) itemView.findViewById(R.id.wiki_card_brief_intro);
            insatncePhoto = (ImageView) itemView.findViewById(R.id.wiki_card_image);
            gotoDetailsButton = (Button)itemView.findViewById(R.id.wiki_card_gotodetail_button);
        }
    }

    public WikiRecordsAdapter(List<Wiki> wikiRecords) {
        this.filteredWikiList = new ArrayList<Wiki>(wikiRecords);
        this.fullWikiList = new ArrayList<Wiki>(wikiRecords);
    }

    public static void fillRecyclerView(RecyclerView recyclerView, Context parent, List<Wiki> wikiList) {
        WikiRecordsAdapter.setContext(parent);
        WikiRecordsAdapter wikiRecordsAdapter = new WikiRecordsAdapter(wikiList);
        WikiRecordsAdapter.setInstance(wikiRecordsAdapter);
        // 设置适配器
        recyclerView.addItemDecoration(new SpacesItemDecoration(WikiRecordsAdapter.SPACE));
        LinearLayoutManager manager = new LinearLayoutManager(parent);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(wikiRecordsAdapter);
    }

    private static void setInstance(WikiRecordsAdapter wikiRecordsAdapter) {
        WikiRecordsAdapter.instance = wikiRecordsAdapter;
    }
}
