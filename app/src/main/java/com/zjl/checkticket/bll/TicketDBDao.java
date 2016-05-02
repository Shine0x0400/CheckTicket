package com.zjl.checkticket.bll;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zjl.checkticket.model.Ticket;
import com.zjl.checkticket.model.TicketKind;
import com.zjl.checkticket.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zzg on 2016/5/1.
 * 操作票信息数据库
 */
public class TicketDBDao {
    DBHelper dbHelper;
    SQLiteDatabase db;
    SimpleDateFormat sdf;
    String today;
    String time;

    public TicketDBDao(Context context) {
        dbHelper = DBHelper.getDBHelper(context);
        db = dbHelper.getWritableDatabase();
        Date date = new Date();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        today = sdf.format(date);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time=sdf.format(date);
    }
     public Ticket getTicket(String name){
         Cursor c = db.rawQuery("SELECT * FROM ticket where name=?  ", new String[]{name});
         int count = 0;
         Ticket ticket= new Ticket();
         if (c.getCount() > 0) {
             ticket.setKind(c.getString(c.getColumnIndex("kind")));
             ticket.setName(c.getString(c.getColumnIndex("name")));
             ticket.setStatus(c.getInt(c.getColumnIndex("status")));
             ticket.setTime(c.getString(c.getColumnIndex("time")));


         }
         return ticket;

         }
    /**获取是否同步
     * @param uploadstatus
     * @return
     */
    public   ArrayList<Ticket> getCount(int uploadstatus) {
        ArrayList<Ticket> list = new ArrayList<Ticket>();
        Cursor c = db.rawQuery("SELECT * FROM ticket where riqi=? and uploadstatus= ?", new String[]{today,""+uploadstatus});
        while (c.moveToNext()) {
            // 更新数据
            Ticket ticket= new Ticket();
            ticket.setKind(c.getString(c.getColumnIndex("kind")));
            ticket.setName(c.getString(c.getColumnIndex("name")));
            ticket.setStatus(c.getInt(c.getColumnIndex("status")));
            list.add(ticket);
        }
        return list;
    }






    /**
     * 获取今日种类列表
     *
     * @param start
     * @param length
     * @return
     */
    public ArrayList<Ticket> getList(int start, int length) {
        ArrayList<Ticket> list = new ArrayList<Ticket>();
        Cursor c = db.rawQuery("SELECT * FROM ticket where riqi=? limit ?, ?", new String[]{today, "" + start, "" + length});
        while (c.moveToNext()) {
            // 更新数据
            Ticket ticket= new Ticket();
            ticket.setKind(c.getString(c.getColumnIndex("kind")));
            ticket.setName(c.getString(c.getColumnIndex("name")));
            ticket.setStatus(c.getInt(c.getColumnIndex("status")));
             list.add(ticket);
        }
        return list;
    }

    /**
     * 插入操作
     *
     */
    public void insert(String kind, String name,int status) {
        Cursor c = db.rawQuery("SELECT * FROM ticket WHERE name=? ", new String[]{name});
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                // 更新数据


                ContentValues cv = new ContentValues();
                cv.put("kind", kind);
                cv.put("name", name);
                cv.put("status",status);
                cv.put("time", time);
                cv.put("riqi", today);

                db.update(
                        "ticket",
                        cv,
                        "name=?",
                        new String[]{name});

            }
        } else {
            ContentValues cv = new ContentValues();
            cv.put("kind", kind);
            cv.put("name", name);
            cv.put("status",status);
            cv.put("time", time);
            cv.put("riqi", today);
            db.insert("ticket", null, cv);
        }


    }

    /**
     * 修改种类张数
     *
     */
    public void update(String name,int status) {
        Cursor c = db.rawQuery("SELECT * FROM ticket WHERE name=? ", new String[]{name});
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                // 更新数据


                    ContentValues cv = new ContentValues();
                    cv.put("status", status);
                    cv.put("uploadstatus",1);
                    db.update(
                            "ticketkind",
                            cv,
                            "name=?",
                            new String[]{name});
                }
                }
            }

    }


