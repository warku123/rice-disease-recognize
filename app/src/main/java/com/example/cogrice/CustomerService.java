package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomerService extends AppCompatActivity {

    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private int[] LastMsgtime={0,0,0};
    private  int[] NowMsgtime={0,0,0};
    private String source;

    private List<Msg> msgList = new ArrayList<Msg>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_service);
        Intent get = getIntent();
        source=get.getStringExtra("question");

        
        initMsgs(source);
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
                    if("".equals(content.trim())){
                        Toast.makeText(CustomerService.this,"空消息客服君不知道说什么哦！", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String time=NowTime();
                        Msg msg = new Msg(content, Msg.TYPE_SEND,time,quickresponse(LastMsgtime,NowMsgtime));
                        msgList.add(msg);
                        adapter.notifyDataSetChanged();
                        msgListView.setSelection(msgList.size());
                        inputText.setText("");
                        autoresponse(source,content);
                    }
                }
            }
        });
    }

    private boolean quickresponse(int[] LastMsgtime,int[] NowMsgtime) {
        if(NowMsgtime[0]*24*60+NowMsgtime[1]*60+NowMsgtime[2]-LastMsgtime[0]*24*60-LastMsgtime[1]*60-LastMsgtime[2]>5)
            return false;
        else
            return true;
    }

    private String NowTime(){
        Calendar calendar=Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        String date=(year+"年"+month+"月"+day+"日"+" "+hour+":"+minute);
        for(int i = 0; i < 3; i++){
            LastMsgtime[i]=NowMsgtime[i];
        }
        NowMsgtime[0]=day;
        NowMsgtime[1]=hour;
        NowMsgtime[2]=minute;

        return date;
    }

    private void initMsgs(String source) {
        Msg msg1;
        if(source.equals("product")){
            msg1 = new Msg("尊敬的用户您好！这里是客服君，请问您对本产品有什么意见吗？", Msg.TYPE_RECEIVED,NowTime(),quickresponse(LastMsgtime,NowMsgtime));
        }
        else{
            msg1 = new Msg("尊敬的用户您好！这里是客服君，请问您对本次识别结果有什么疑问吗？", Msg.TYPE_RECEIVED,NowTime(),quickresponse(LastMsgtime,NowMsgtime));
        }
        msgList.add(msg1);
    }
    private void autoresponse(String source,String content){
        Msg response;
        if(source.equals("product")){
            if(content.length()<5){
                response = new Msg("感謝您的回复！我将把您的反馈及时递交后台管理人员！", Msg.TYPE_RECEIVED,NowTime(),quickresponse(LastMsgtime,NowMsgtime));
            }
            else{
                response = new Msg("您的回复我已收到，感谢您对本产品提出的建议！后台管理员会定期查看用户回复，并对产品进行改进！", Msg.TYPE_RECEIVED,NowTime(),quickresponse(LastMsgtime,NowMsgtime));
            }
        }
        else{
            if(content.length()<5){
                response = new Msg("收到！您的回复是我们最大的动力！我将把您的意见反馈到模型训练中！", Msg.TYPE_RECEIVED,NowTime(),quickresponse(LastMsgtime,NowMsgtime));
            }
            else{
                response = new Msg("您的反馈我已收到，感谢您对识别结果提出的建议！我们会检查模型，核对结果，进一步提高识别正确率！", Msg.TYPE_RECEIVED,NowTime(),quickresponse(LastMsgtime,NowMsgtime));
            }
        }
        msgList.add(response);
    }

    public class Msg {
        public static final int TYPE_RECEIVED = 0;
        public static final int TYPE_SEND = 1;

        private String content;
        private String time;
        private int type;
        private boolean newMsg;

        public Msg(String content, int type, String time, boolean newMsg) {
            this.content = content;
            this.type = type;
            this.time = time;
            this.newMsg = newMsg;
        }

        public String getContent() {
            return content;
        }

        public String getTime(){ return time; }

        public int getType() {
            return type;
        }

        public boolean ifNewMsg() { return newMsg; }

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
                if(!msg.ifNewMsg()){
                    viewHolder.receivetime.setVisibility(View.VISIBLE);
                }
                else{
                    viewHolder.receivetime.setVisibility(View.GONE);
                }
                viewHolder.receive.setVisibility(View.VISIBLE);
                viewHolder.receivename.setVisibility(View.VISIBLE);

                viewHolder.send.setVisibility(View.GONE);
                viewHolder.sendtime.setVisibility(View.GONE);
                viewHolder.sendname.setVisibility(View.GONE);

                viewHolder.leftMsg.setText(msg.getContent());
                viewHolder.receivetime.setText(msg.getTime());
            } else if(msg.getType() == Msg.TYPE_SEND) {
                if(!msg.ifNewMsg()){
                    viewHolder.sendtime.setVisibility(View.VISIBLE);
                }
                else{
                    viewHolder.sendtime.setVisibility(View.GONE);
                }
                viewHolder.send.setVisibility(View.VISIBLE);
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