package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomerService extends AppCompatActivity {

    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private int MsgNum=0;

    private List<Msg> msgList = new ArrayList<Msg>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_service);

        initMsgs();
        adapter = new MsgAdapter(CustomerService.this, R.layout.msg_box, msgList);
        inputText = (EditText)findViewById(R.id.input_text);
        send = (Button)findViewById(R.id.send);
        msgListView = (ListView)findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = inputText.getText().toString();
                if(!"".equals(content)) {
                    String time=NowTime();
                    Msg msg = new Msg(content, Msg.TYPE_SEND,time);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                    inputText.setText("");
                }
            }
        });
    }

    private String NowTime(){
        Calendar calendar=Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        String date=(year+"年"+month+"月"+day+"日"+" "+hour+":"+minute);
        return date;
    }

    private void initMsgs() {
        Msg msg1 = new Msg("Hello, how are you?", Msg.TYPE_RECEIVED,NowTime());
        msgList.add(msg1);
        Msg msg2 = new Msg("Fine, thank you, and you?", Msg.TYPE_SEND,NowTime());
        msgList.add(msg2);
        Msg msg3 = new Msg("I am fine, too!", Msg.TYPE_RECEIVED,NowTime());
        msgList.add(msg3);
    }

    public class Msg {
        public static final int TYPE_RECEIVED = 0;
        public static final int TYPE_SEND = 1;

        private String content;
        private String time;
        private int type;

        public Msg(String content, int type, String time) {
            this.content = content;
            this.type = type;
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public String getTime(){ return time; }

        public int getType() {
            return type;
        }
    }

    public class MsgAdapter extends ArrayAdapter<Msg> {
        private int resourceId;

        public MsgAdapter(Context context, int textViewResourceId, List<Msg> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            Msg msg = getItem(position);
            View view;
            ViewHolder viewHolder;
            if(convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.receive=(RelativeLayout)view.findViewById(R.id.receive);
                viewHolder.send=(RelativeLayout)view.findViewById(R.id.send);
                viewHolder.leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
                viewHolder.rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
                viewHolder.leftMsg = (TextView)view.findViewById(R.id.left_msg);
                viewHolder.rightMsg = (TextView)view.findViewById(R.id.right_msg);
                viewHolder.sendname = (TextView)view.findViewById(R.id.send_name);
                viewHolder.receivename = (TextView)view.findViewById(R.id.receive_name);
                viewHolder.sendtime=(TextView)view.findViewById(R.id.send_time);
                viewHolder.receivetime=(TextView)view.findViewById(R.id.receive_time);
                viewHolder.receiver=(ImageView)view.findViewById(R.id.receiver);
                viewHolder.sender=(ImageView)view.findViewById(R.id.sender);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            if(msg.getType() == Msg.TYPE_RECEIVED) {
                viewHolder.receive.setVisibility(View.VISIBLE);
                viewHolder.receivetime.setVisibility(View.VISIBLE);
                viewHolder.receivename.setVisibility(View.VISIBLE);

                viewHolder.send.setVisibility(View.GONE);
                viewHolder.sendtime.setVisibility(View.GONE);
                viewHolder.sendname.setVisibility(View.GONE);

                viewHolder.leftMsg.setText(msg.getContent());
                viewHolder.receivetime.setText(msg.getTime());
            } else if(msg.getType() == Msg.TYPE_SEND) {
                viewHolder.send.setVisibility(View.VISIBLE);
                viewHolder.sendtime.setVisibility(View.VISIBLE);
                viewHolder.sendname.setVisibility(View.VISIBLE);

                viewHolder.receive.setVisibility(View.GONE);
                viewHolder.receivetime.setVisibility(View.GONE);
                viewHolder.receivename.setVisibility(View.GONE);

                viewHolder.rightMsg.setText(msg.getContent());
                viewHolder.sendtime.setText(msg.getTime());
            }
            return view;
        }

        //通过ViewHolder显示项的内容
        class ViewHolder {
            LinearLayout leftLayout;
            LinearLayout rightLayout;
            RelativeLayout send;
            RelativeLayout receive;
            TextView leftMsg;
            TextView rightMsg;
            TextView sendname;
            TextView receivename;
            TextView sendtime;
            TextView receivetime;
            ImageView sender;
            ImageView receiver;
        }
    }
}