package com.zjl.checkticket.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zjl.checkticket.R;
import com.zjl.checkticket.bll.TicketKindDBDao;
import com.zjl.checkticket.model.TicketKind;

import java.util.ArrayList;

/**
 * Created by zzg on 2016/4/30.
 * 二次检票Fragment
 */
public class ErCiFragment extends Fragment {
    TextView xinxi;
    ListView list;
    Button next;
    int kind=1;
    ArrayList<TicketKind> tickets;
    private LvAdapter adapter;
    TicketKindDBDao ticketKindDBDao;
    int start=0;
    int length=10;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.tongjilist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        xinxi = (TextView) getView().findViewById(R.id.xinxi);
        list = (ListView) getView().findViewById(R.id.list);
        next = (Button) getView().findViewById(R.id.button);

        ticketKindDBDao=new TicketKindDBDao(this.getActivity());
        tickets=ticketKindDBDao.getList(start,length);
        xinxi.setText("本日二次验票总计"+ticketKindDBDao.getCount(kind));
        list.setAdapter(adapter);

    }

    /**
     * 下一页点击事件处理
     */
    public void showNext() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start=+length;
                tickets=ticketKindDBDao.getList(start,length);
                if(tickets!=null){
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    class LvAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ViewHolder holder;

        public LvAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tickets.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int tempPosition = position;
            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.listitem, null);
                holder = new ViewHolder();
                holder.textView1 = (TextView) convertView.findViewById(R.id.zhonglei);
                holder.textView2 = (TextView) convertView.findViewById(R.id.shuliang);
                return convertView;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView1.setText(tickets.get(position).getKind());
            if(kind==0){
                holder.textView2.setText(tickets.get(position).getFirstNumber());
            }else   if(kind==1){
                holder.textView2.setText(tickets.get(position).getSecondNumber());
            }
            return convertView;
        }
    }


    class ViewHolder {
        public TextView textView1;
        public TextView textView2;

    }
}