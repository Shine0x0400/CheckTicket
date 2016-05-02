package com.zjl.checkticket.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zjl.checkticket.R;
import com.zjl.checkticket.bll.TicketDBDao;
import com.zjl.checkticket.bll.TicketKindDBDao;
import com.zjl.checkticket.model.Ticket;
import com.zjl.checkticket.util.ResultAnlyze;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 检票结果
 * Created by zzg on 2016/5/1.
 */
public class JianPiaoResultActivity extends Activity {
    TextView result;
    TextView result2;
    LinearLayout show;
    ResultAnlyze anlyze;
    TicketDBDao ticketDBDao;
    TicketKindDBDao ticketKindDBDao;
    SimpleDateFormat sdf;
    String today;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jianpiaoresult);
        anlyze=new ResultAnlyze();
        ticketDBDao=new TicketDBDao(this);
        ticketKindDBDao=new TicketKindDBDao(this);
        Date date = new Date();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        today=sdf.format(date);
        inti();

        Button mButtonBack = (Button) findViewById(R.id.back_btn);
        mButtonBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                JianPiaoResultActivity.this.finish();

            }
        });
    }
    public void inti(){

        String resultString = getIntent().getStringExtra("result");
        Toast.makeText(this,resultString,Toast.LENGTH_LONG).show();
        result=(TextView) findViewById(R.id.result);
        result2=(TextView) findViewById(R.id.result2);
        show=(LinearLayout) findViewById(R.id.show);
        GradientDrawable myGrad = (GradientDrawable)show.getBackground();
        myGrad.setColor(Color.WHITE);

        anylzeString(resultString);
    }
    public void anylzeString(String resultString){
        Ticket ticket=anlyze.jsonAnylze(resultString);
        if(ticket.getName()!=null){
            ticket=ticketDBDao.getTicket(ticket.getName());
            if(ticket.getStatus()==1){
                result.setText("检票成功有效票！");
                ticketDBDao.update(ticket.getName(),2);
                ticketKindDBDao.update(ticket.getKind(),0);
            }else if(ticket.getStatus()==2&&today.equals(ticket.getRiqi())){
                result.setText("已检票初次检票时间为"+today);
                result2.setVisibility(View.VISIBLE);
                result2.setText("票号："+ticket.getName()+"/n票种："+ticket.getKind()+"/n检票时间："+ticket.getTime());
                ticketDBDao.update(ticket.getName(),3);
                ticketKindDBDao.update(ticket.getKind(),1);
            }else if (ticket.getStatus()==3&&today.equals(ticket.getRiqi())){
                result.setText("已检票初次检票时间为"+today);
                result2.setVisibility(View.VISIBLE);
                result2.setText("票号："+ticket.getName()+"/n票种："+ticket.getKind()+"/n检票时间："+ticket.getTime());
                ticketKindDBDao.update(ticket.getKind(),1);
            }
            else{
                result.setText("无效票");
            }
        }else{
            result.setText("无效票");
        }

    }
}
