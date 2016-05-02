package com.zjl.checkticket.util;

import com.zjl.checkticket.model.Ticket;
import com.zjl.checkticket.model.TicketPOJO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 封装为网络传递对象
 * Created by zzg on 2016/5/2.
 */
public class JsonTools {
public static String ticketJson(ArrayList<Ticket> tickets){
ArrayList<TicketPOJO> list=new ArrayList<TicketPOJO>();
    for(int i=0;i<tickets.size();i++){
        TicketPOJO pojo=new TicketPOJO();
        pojo.setName(tickets.get(i).getName());
        pojo.setStatus(tickets.get(i).getStatus());
    }
    JSONObject jsonObject=new JSONObject();
    try {
        jsonObject.put("tickets",list);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    return  jsonObject.toString();
 }
}
