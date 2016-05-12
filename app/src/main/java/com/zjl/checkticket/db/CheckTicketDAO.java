package com.zjl.checkticket.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zjl.checkticket.CheckTicketApplication;
import com.zjl.checkticket.db.CheckTicketContract.CheckTicketEntry;
import com.zjl.checkticket.model.Ticket;

import java.util.ArrayList;


/**
 * Created by zjl on 2016/5/11.
 */
public class CheckTicketDAO {
    private static final String TAG = "CheckTicketDAO";
    private TicketDbHelper dbHelper;

    private static volatile CheckTicketDAO sInstance;
//
//    private CheckTicketDAO() {
//
//    }
//
public static CheckTicketDAO getInstance() {
    if (sInstance == null) {
        synchronized (CheckTicketDAO.class) {
            if (sInstance == null) {
                sInstance = new CheckTicketDAO();
            }
        }
    }
    return sInstance;
}


    private CheckTicketDAO() {
        dbHelper = new TicketDbHelper(CheckTicketApplication.sApplicationContext);
    }

    public Ticket queryTicket(String ticketId) {
        Log.i(TAG, "queryTicket: ticketId=" + ticketId);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                CheckTicketEntry._ID,
                CheckTicketEntry.COLUMN_TICKET_ID,
                CheckTicketEntry.COLUMN_IS_CHECKED,
                CheckTicketEntry.COLUMN_FIRST_CHECK_TIME
        };

//// How you want the results sorted in the resulting Cursor
//        String sortOrder =
//                CheckTicketEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor cursor = db.query(
                CheckTicketEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                CheckTicketEntry.COLUMN_TICKET_ID + " = ?",                                 // The columns for the WHERE clause
                new String[]{ticketId},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex(CheckTicketEntry.COLUMN_TICKET_ID));
            String isChecked = cursor.getString(cursor.getColumnIndex(CheckTicketEntry.COLUMN_IS_CHECKED));
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(CheckTicketEntry.COLUMN_FIRST_CHECK_TIME));

            return new Ticket(id, isChecked, time);
        }
        return null;
    }

    public int deleteCheckedTicketsBeforeTime(long millis) {
        Log.i(TAG, "deleteCheckedTicketsBeforeTime: millis=" + millis);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(CheckTicketEntry.TABLE_NAME, CheckTicketEntry.COLUMN_IS_CHECKED + " = " + CheckTicketEntry.VALUE_IS_CHECKED + " AND " + CheckTicketEntry.COLUMN_FIRST_CHECK_TIME + " < ?", new String[]{Long.toString(millis)});
    }

    public void inflateTableWithFreshData(ArrayList<Ticket> tickets) {
        Log.i(TAG, "inflateTableWithFreshData: ");
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(CheckTicketEntry.TABLE_NAME, null, null);

        for (Ticket t : tickets) {
            insertTicket(db, t);
        }
    }

    public void updateTableWithFreshData(ArrayList<Ticket> tickets) {
        Log.i(TAG, "updateTableWithFreshData: ");
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        for (Ticket t : tickets) {
            int rows = updateTicket(db, t);

            if (rows <= 0) {
                insertTicket(db, t);
            }
        }
    }

    public int updateTicket(Ticket t) {
        Log.i(TAG, "updateTicket: ");
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return updateTicket(db, t);
    }

    private int updateTicket(SQLiteDatabase db, Ticket t) {
        Log.i(TAG, "updateTicket: ticket=" + t);
        ContentValues values = new ContentValues();
        values.put(CheckTicketEntry.COLUMN_IS_CHECKED, t.getIsChecked());
        values.put(CheckTicketEntry.COLUMN_FIRST_CHECK_TIME, t.getFirstCheckTime());

        return db.update(CheckTicketEntry.TABLE_NAME, values, CheckTicketEntry.COLUMN_TICKET_ID + " = ?", new String[]{t.getId()});
    }


    private void insertTicket(SQLiteDatabase db, Ticket ticket) {
        Log.i(TAG, "insertTicket: ticket=" + ticket);
        ContentValues values = new ContentValues();
        values.put(CheckTicketEntry.COLUMN_TICKET_ID, ticket.getId());
        values.put(CheckTicketEntry.COLUMN_IS_CHECKED, ticket.getIsChecked());
        values.put(CheckTicketEntry.COLUMN_FIRST_CHECK_TIME, ticket.getFirstCheckTime());

        db.insert(CheckTicketEntry.TABLE_NAME, null, values);
    }

    private void dropCheckTicketTable(SQLiteDatabase db) {
        Log.i(TAG, "dropCheckTicketTable: ");
        db.execSQL(TicketDbHelper.SQL_DELETE_CHECK_TICKET_TABLE);
    }

    private void createCheckTicketTable(SQLiteDatabase db) {
        Log.i(TAG, "createCheckTicketTable: ");
        db.execSQL(TicketDbHelper.SQL_CREATE_CHECK_TICKET_TABLE);
    }


}
