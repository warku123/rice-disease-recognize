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
import com.example.cogrice.dataclass.History;
import com.example.cogrice.utils.SpacesItemDecoration;

import java.util.ArrayList;

public class HistoryRecordsAdapter extends RecyclerView.Adapter<HistoryRecordsAdapter.HistoryViewHolder> {

    public static final int SPACE = 2;
    private ArrayList<History> historyRecords;

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

    public HistoryRecordsAdapter(ArrayList<History> historyRecords) {
        this.historyRecords = historyRecords;
    }

    public static void fillRecyclerView(RecyclerView recyclerView,Context parent){
        recyclerView.addItemDecoration(new SpacesItemDecoration(HistoryRecordsAdapter.SPACE));
        System.out.println("读取远程数据填充");
        ArrayList<History> localHistoriesList = History.getRemoteHistoryRecords();
        // TODO 读取真实的远程数据
        HistoryRecordsAdapter historyRecordsAdapter = new HistoryRecordsAdapter(localHistoriesList);
        LinearLayoutManager manager = new LinearLayoutManager(parent);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(historyRecordsAdapter);
    }
}
