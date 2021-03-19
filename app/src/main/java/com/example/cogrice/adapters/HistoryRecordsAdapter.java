package com.example.cogrice.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cogrice.Intropage;
import com.example.cogrice.R;
import com.example.cogrice.dataclass.ControlMeasure;
import com.example.cogrice.dataclass.History;
import com.example.cogrice.utils.SpacesItemDecoration;

import java.util.List;

public class HistoryRecordsAdapter extends RecyclerView.Adapter<HistoryRecordsAdapter.HistoryViewHolder> {

    public static final int SPACE = 2;
    private static Handler handler;
    private List<History> historyRecords;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context tar) {
        HistoryRecordsAdapter.context = tar;
    }

    public static void setHandler(Handler handler) {
        HistoryRecordsAdapter.handler = handler;
    }

    public static Handler getHandler() {
        return handler;
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
        holder.date.setText(historyRecords.get(position).getFormattedDate());
        holder.cnTypeName.setText(ControlMeasure.getCnName(historyRecords.get(position).getDiseaseEnTypeName().toString()));
        holder.gotoDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryRecordsAdapter.getContext(), Intropage.class);
                intent.putExtra("response", Intropage.generateFormattMsgByCnName((String) holder.cnTypeName.getText()));
                // 在外部调用Activity的方法
                HistoryRecordsAdapter.getContext().startActivity(intent);
            }
        });
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
        private TextView cnTypeName;
        private Button gotoDetailsButton;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cnTypeName = (TextView) itemView.findViewById(R.id.history_card_disease_type);
            date = (TextView) itemView.findViewById(R.id.history_card_date);
            photo = (ImageView) itemView.findViewById(R.id.history_card_image);
            gotoDetailsButton = (Button)itemView.findViewById(R.id.history_card_gotodetail_button);
        }
    }

    public HistoryRecordsAdapter(List<History> historyRecords) {
        this.historyRecords = historyRecords;
    }


    public static void fillRecyclerView(RecyclerView recyclerView, Context parent, List<History> histories) {
        HistoryRecordsAdapter.setContext(parent);
        HistoryRecordsAdapter historyRecordsAdapter = new HistoryRecordsAdapter(histories);
        // 设置适配器
        recyclerView.addItemDecoration(new SpacesItemDecoration(HistoryRecordsAdapter.SPACE));
        LinearLayoutManager manager = new LinearLayoutManager(parent);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(historyRecordsAdapter);
    }
}
