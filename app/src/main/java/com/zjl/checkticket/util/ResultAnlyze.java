package com.zjl.checkticket.util;

import com.zjl.checkticket.model.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 分析扫码信息
 * Created by zzg on 2016/5/2.
 */
public class ResultAnlyze  {
    public Ticket jsonAnylze(String result){
        Ticket ticket=new Ticket();
        try {
            JSONObject jsonObject=new JSONObject(result);

            if(jsonObject.getString("name")!=null&&jsonObject.getString("kind")!=null){
               ticket.setKind(jsonObject.getString("kind"));
                ticket.setName(jsonObject.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
   return ticket;

    }

}
