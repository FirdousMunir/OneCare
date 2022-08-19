package com.health.onecare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.onecare.R;
import com.health.onecare.model.CallLogsModel;
import com.health.onecare.model.ChatModel;
import com.health.onecare.SharedPrefsData;

import java.util.ArrayList;

public class CallLogsListAdapter extends BaseAdapter {

    Context context;
    ArrayList<CallLogsModel> list;
    LayoutInflater inflater = null;

    public CallLogsListAdapter(Context context, ArrayList<CallLogsModel> callDetails) {
        this.context = context;
        this.list = callDetails;

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
            vi = inflater.inflate(R.layout.layout_call_logs, null);
        }

        TextView tvDateTime = vi.findViewById(R.id.textViewCallLogDate);
        TextView tvLogNum = vi.findViewById(R.id.textViewCallLogNum);
        TextView tvDuration = vi.findViewById(R.id.textViewCallLogDuration);
        ImageView ivCallType = vi.findViewById(R.id.imageViewCallLogType);

        tvDateTime.setText(list.get(position).getDateTime());
        tvLogNum.setText(list.get(position).getNumber());
        tvDuration.setText(list.get(position).getCallDuration());
//        ivCallType.setText(list.get(position).getCallType());
//        ivCallType.setImageDrawable(list.get(position).getCallType());
        ivCallType.setImageResource(list.get(position).getCallType());

//        if (position > 0 && list.get(position).getNumber().equals(list.get(position - 1).getNumber()))
//            tvLogNum.setVisibility(View.GONE);

        return vi;
    }

}