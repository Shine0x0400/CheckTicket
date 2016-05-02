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
import com.zjl.checkticket.bll.TicketDBDao;
import com.zjl.checkticket.bll.TicketKindDBDao;
import com.zjl.checkticket.model.Ticket;
import com.zjl.checkticket.model.TicketKind;

import java.util.ArrayList;

/**
 * Created by zzg on 2016/4/30.
 * 上传Fragment
 */
public class UploadFragment extends Fragment {
    TextView xinxi1;
    TextView xinxi2;
    Button tongbu;
    int kind=0;
    ArrayList<Ticket> tickets;
    int start=0;
    int length=10;
    TicketDBDao ticketDBDao;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.tongjilist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        xinxi1 = (TextView) getView().findViewById(R.id.xinxi1);
        xinxi2 = (TextView) getView().findViewById(R.id.xinxi2);
        tongbu = (Button) getView().findViewById(R.id.button);

        ticketDBDao=new TicketDBDao(this.getActivity());
        tickets=ticketDBDao.getCount(0);
        xinxi1.setText("已同步信息 "+tickets.size()+"条");
        tickets=ticketDBDao.getCount(1);
        xinxi2.setText("未同步信息"+tickets.size()+"条");
        tongbu();
    }

    /**
     * 同步
     */
    public void tongbu() {
    tongbu.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


        }
    });

    }

}
