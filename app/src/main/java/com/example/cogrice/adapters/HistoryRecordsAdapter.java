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

import com.bumptech.glide.Glide;
import com.example.cogrice.R;
import com.example.cogrice.dataclass.History;
import com.example.cogrice.utils.AlertHelper;
import com.example.cogrice.utils.SpacesItemDecoration;

import java.util.List;

public class HistoryRecordsAdapter extends RecyclerView.Adapter<HistoryRecordsAdapter.HistoryViewHolder> {

    public static final int SPACE = 2;
    private List<History> historyRecords;
    private static Context context;

    public Context getContext() {
        return context;
    }

    public static void setContext(Context tar) {
        HistoryRecordsAdapter.context = tar;
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 初始化组件
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);
        return new HistoryViewHolder(view);
    }

    /**
     * 获取数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        // holder.photo.setImageBitmap(historyRecords.get(position).getPhoto());
        // Error
        if (this.getContext() != null) {
            Glide.with(this.getContext()).load(historyRecords.get(position).getPhotoUrl()).error(R.drawable.loadfailed).placeholder(R.drawable.loading_bg).fallback(R.drawable.loadfailed).into(holder.photo);
        }
        holder.date.setText((historyRecords.get(position).getFormattedDate()).toString());
        holder.type.setText(historyRecords.get(position).getDiseaseType().toString());
    }

    @Override
    public int getItemCount() {
        return this.historyRecords.size();
    }

    /**
     * 放置HistoryCard
     */
    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        // 实际填充
        // private ConstraintLayout historyCard;
        private ImageView photo;
        private TextView date;
        private TextView type;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.history_card_disease_type);
            type = (TextView) itemView.findViewById(R.id.history_card_date);
            photo = (ImageView) itemView.findViewById(R.id.history_card_image);
        }
    }

    public HistoryRecordsAdapter(List<History> historyRecords) {
        this.historyRecords = historyRecords;
    }


    public static void fillRecyclerView(RecyclerView recyclerView, Context parent, List<History> histories) {
        HistoryRecordsAdapter.setContext(parent);
        HistoryRecordsAdapter historyRecordsAdapter = new HistoryRecordsAdapter(histories);
        recyclerView.addItemDecoration(new SpacesItemDecoration(HistoryRecordsAdapter.SPACE));
        LinearLayoutManager manager = new LinearLayoutManager(parent);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(historyRecordsAdapter);
        System.out.println("数据填充");
    }
}
