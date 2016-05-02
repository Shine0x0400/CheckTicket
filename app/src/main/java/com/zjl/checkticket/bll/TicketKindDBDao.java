package com.zjl.checkticket.bll;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zjl.checkticket.model.TicketKind;
import com.zjl.checkticket.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zzg on 2016/5/1.
 * 操作票类数据库
 */
public class TicketKindDBDao {
    DBHelper dbHelper;
    SQLiteDatabase db;
    SimpleDateFormat sdf;
    String today;

    public TicketKindDBDao(Context context) {
        dbHelper = DBHelper.getDBHelper(context);
        db = dbHelper.getWritableDatabase();
        Date date = new Date();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        today = sdf.format(date);
    }

    public int getCount(int zhonglei) {
        Cursor c = db.rawQuery("SELECT * FROM ticketkind where riqi=?  ", new String[]{today});
        int count = 0;
        if (c.getCount() > 0) {
        if (zhonglei == 0) {

            while (c.moveToNext()) {
                // 更新数据

                count = +c.getInt(c.getColumnIndex("firstnumber"));
            }
        } else {
            while (c.moveToNext()) {
            count = +c.getInt(c.getColumnIndex("secondnumber"));
            }
        }
        }
        return count;

    }


    /**
     * 获取今日种类列表
     *
     * @param start
     * @param length
     * @return
     */
    public ArrayList<TicketKind> getList(int start, int length) {
        ArrayList<TicketKind> list = new ArrayList<TicketKind>();
        Cursor c = db.rawQuery("SELECT * FROM ticketkind where riqi=? limit ?, ?", new String[]{today, "" + start, "" + length});
        while (c.moveToNext()) {
            // 更新数据
            TicketKind ticketKind = new TicketKind();
            ticketKind.setKind(c.getString(c.getColumnIndex("kind")));
            ticketKind.setFirstNumber(c.getInt(c.getColumnIndex("firstnumber")));
            ticketKind.setSecondNumber(c.getInt(c.getColumnIndex("secondnumber")));
            list.add(ticketKind);
        }
        return list;
    }

    /**
     * 插入操作
     *
     * @param kind
     * @param first
     * @param second
     */
    public void insert(String kind, int first, int second) {
        Cursor c = db.rawQuery("SELECT * FROM ticketkind WHERE kind=? ", new String[]{kind});
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                // 更新数据


                ContentValues cv = new ContentValues();
                cv.put("kind", kind);
                cv.put("firstnumber", first);
                cv.put("secondnumber", second);
                cv.put("riqi", today);

                db.update(
                        "ticketkind",
                        cv,
                        "kind=?",
                        new String[]{kind});

            }
        } else {
            ContentValues cv = new ContentValues();
            cv.put("kind", kind);
            cv.put("firstnumber", first);
            cv.put("secondnumber", second);
            cv.put("riqi", today);
            db.insert("ticketkind", null, cv);
        }


    }

    /**
     * 修改种类张数
     *
     * @param kind
     * @param zhonglei 0 代表检票 1 代表二检
     */
    public void update(String kind, int zhonglei) {
        Cursor c = db.rawQuery("SELECT * FROM ticketkind WHERE kind=? ", new String[]{kind});
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                // 更新数据
                if (zhonglei == 0) {
                    int number = c.getInt(c
                            .getColumnIndex("firstnumber"));
                    ContentValues cv = new ContentValues();
                    cv.put("firstnumber", number + 1);
                    db.update(
                            "ticketkind",
                            cv,
                            "kind=?",
                            new String[]{kind});
                } else if (zhonglei == 1) {
                    int number = c.getInt(c
                            .getColumnIndex("secondnumber"));
                    ContentValues cv = new ContentValues();
                    cv.put("secondnumber", number + 1);
                    db.update(
                            "ticketkind",
                            cv,
                            "kind=?",
                            new String[]{kind});
                }
            }
        }
    }
}

