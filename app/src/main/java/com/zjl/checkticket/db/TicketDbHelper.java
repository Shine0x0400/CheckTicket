package com.zjl.checkticket.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by zjl on 2016/5/10.
 */
public class TicketDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "TicketDbHelper";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "check_ticket.db";

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_LONG = " INT8";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_CHECK_TICKET_TABLE =
            "CREATE TABLE " + CheckTicketContract.CheckTicketEntry.TABLE_NAME + " (" +
                    CheckTicketContract.CheckTicketEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    CheckTicketContract.CheckTicketEntry.COLUMN_TICKET_ID + TYPE_LONG + COMMA_SEP +
                    CheckTicketContract.CheckTicketEntry.COLUMN_FIRST_CHECK_TIME + TYPE_TEXT + COMMA_SEP +
                    // Any other options for the CREATE command
                    " )";

    private static final String SQL_DELETE_CHECK_TICKET_TABLE =
            "DROP TABLE IF EXISTS " + CheckTicketContract.CheckTicketEntry.TABLE_NAME;

    public TicketDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: create tables");
        db.execSQL(SQL_CREATE_CHECK_TICKET_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_CHECK_TICKET_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onDowngrade: oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }
}
