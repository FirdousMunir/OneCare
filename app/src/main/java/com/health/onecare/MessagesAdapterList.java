package com.health.onecare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.onecare.R;
import com.health.onecare.model.ChatModel;

import java.util.ArrayList;

public class MessagesAdapterList extends BaseAdapter {

    Context context;
    ArrayList<ChatModel> list;
    LayoutInflater inflater = null;
    SharedPrefsData sharedPrefsData;
    String userID = null;
    String adminID = "1";
    String userName = null;
    String adminName = "Admin";

    public MessagesAdapterList(Context context, ArrayList<ChatModel> msgDetails) {
        this.context = context;
        this.list = msgDetails;
        sharedPrefsData = new SharedPrefsData(context);
        sharedPrefsData.getUSerID();
        userID = sharedPrefsData.userID;
        userName = sharedPrefsData.userName;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.layout_messages, null);
        }
        LinearLayout receiver = vi.findViewById(R.id.layout_receiverList);
        LinearLayout sender = vi.findViewById(R.id.layout_senderList);

        TextView tvMsgSent = vi.findViewById(R.id.textViewMessageSenderList);
        TextView tvMsgReceive = vi.findViewById(R.id.textViewMessageReceiverList);
        TextView tvSenderName = vi.findViewById(R.id.tvSenderNameList);
        TextView tvReceiverName = vi.findViewById(R.id.tvReceiverNameList);

        TextView tvSenderTime = vi.findViewById(R.id.textViewSenderTime);
        TextView tvReceiverTime = vi.findViewById(R.id.textViewReceiverTime);

        TextView tvSenderID = vi.findViewById(R.id.tvIdSenderList);
        TextView tvReceiverId = vi.findViewById(R.id.tvIdReceiverList);

        TextView tvDate = vi.findViewById(R.id.textViewDate);

//        if(list.get(position).getSentAt() .equals(list.get(position+1).getSentAt())){
//            tvDate.setVisibility(View.GONE);
//        }else
//        {
//            tvDate.setVisibility(View.VISIBLE);
//
//        }

        if (list.get(position).getSentById().equals(userID)) {
            sender.setVisibility(View.VISIBLE);
            receiver.setVisibility(View.GONE);
            tvMsgSent.setText(list.get(position).getMessage());
            tvSenderName.setText(list.get(position).getSenderName());
            tvSenderID.setText(list.get(position).getId());
            tvSenderTime.setText(list.get(position).getSentAt());
            tvDate.setText(list.get(position).getSentDate());

            if(position > 0 && list.get(position).getSentDate().equals(list.get(position-1).getSentDate())){
                tvDate.setVisibility(View.GONE);
            }else{
                tvDate.setVisibility(View.VISIBLE);
            }


        } else if (list.get(position).getSentById().equals(adminID)) {
            sender.setVisibility(View.GONE);
            receiver.setVisibility(View.VISIBLE);
            tvMsgReceive.setText(list.get(position).getMessage());
            tvReceiverName.setText(list.get(position).getSenderName());
            tvReceiverId.setText(list.get(position).getId());
            tvReceiverTime.setText(list.get(position).getSentAt());
            tvDate.setText(list.get(position).getSentDate());

            if(position > 0 && list.get(position).getSentDate().equals(list.get(position-1).getSentDate())){
                tvDate.setVisibility(View.GONE);
            }else{
                tvDate.setVisibility(View.VISIBLE);
            }
        }
        return vi;
    }

}