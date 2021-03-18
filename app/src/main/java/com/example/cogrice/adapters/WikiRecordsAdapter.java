package com.example.cogrice.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cogrice.R;
import com.example.cogrice.dataclass.Wiki;
import com.example.cogrice.utils.AlertHelper;
import com.example.cogrice.utils.SpacesItemDecoration;

import java.util.List;

public class WikiRecordsAdapter extends RecyclerView.Adapter<WikiRecordsAdapter.WikiViewHolder> {

    public static final int SPACE = 2;

    private static Context context;
    private List<Wiki> wikiList;

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

    /**
     * 获取数据
     * TODO
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull WikiViewHolder holder, int position) {
        if (this.getContext() != null) {
            Glide.with(this.getContext()).load(wikiList.get(position).getImgUrl()).error(R.drawable.loadfailed).placeholder(R.drawable.loading_bg).fallback(R.drawable.loadfailed).into(holder.insatncePhoto);
        }
        if(wikiList.get(position).getEnTypeName().toLowerCase().contains("healthy")){
            holder.setVisibility(false);
        }
        holder.diseaseTypeName.setText(wikiList.get(position).getCnTypename());
        holder.diseaseBriefInfo.setText(wikiList.get(position).getDiseaseFeature());
        AlertHelper.warnNotImplemented("绑定Wiki控件");
    }

    private Context getContext() {
        return WikiRecordsAdapter.context;
    }

    @Override
    public int getItemCount() {
        return this.wikiList.size();
    }

    /**
     * 放置WikiCard
     */
    public class WikiViewHolder extends RecyclerView.ViewHolder {
        // 实际填充
        // private ConstraintLayout wikiCard;
        private ImageView insatncePhoto;
        private TextView diseaseTypeName;
        private TextView diseaseBriefInfo;

        public void setVisibility(boolean isVisible){
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            if (isVisible){
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            }else{
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }

        public WikiViewHolder(@NonNull View itemView) {
            super(itemView);
            diseaseTypeName = (TextView) itemView.findViewById(R.id.wiki_card_disease_type);
            diseaseBriefInfo = (TextView) itemView.findViewById(R.id.wiki_card_brief_intro);
            insatncePhoto = (ImageView) itemView.findViewById(R.id.wiki_card_image);
        }
    }

    public WikiRecordsAdapter(List<Wiki> wikiRecords) {
        this.wikiList = wikiRecords;
    }

    public static void fillRecyclerView(RecyclerView recyclerView, Context parent, List<Wiki> wikiList) {
        WikiRecordsAdapter.setContext(parent);
        WikiRecordsAdapter wikiRecordsAdapter = new WikiRecordsAdapter(wikiList);
        // 设置适配器
        recyclerView.addItemDecoration(new SpacesItemDecoration(WikiRecordsAdapter.SPACE));
        LinearLayoutManager manager = new LinearLayoutManager(parent);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(wikiRecordsAdapter);
    }
}
