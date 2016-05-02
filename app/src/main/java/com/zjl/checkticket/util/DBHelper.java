package com.zjl.checkticket.util;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 数据库帮助类
 */
public class DBHelper extends SQLiteOpenHelper {
	  
    private static final String DATABASE_NAME = "checkticket.db";
    private static final int DATABASE_VERSION = 1;  
      private  static DBHelper dbHelper;
    public DBHelper(Context context) {  
        //CursorFactory设置为null,使用默认值  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    //数据库第一次被创建时onCreate会被调用  
    @Override  
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE  IF NOT EXISTS ticketkind( _id INTEGER PRIMARY KEY AUTOINCREMENT,kind VARCHAR, firstnumber INTEGER, secondnumber INTEGER,riqi VARCHAR)");
        db.execSQL("CREATE TABLE  IF NOT EXISTS ticket( _id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR,kind VARCHAR ,status INTEGER ,uploadstatus INTEGER,time VARCHAR,riqi VARCHAR)");

    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

    /** 单例模式
     * @param context
     * @return
     */
   public static DBHelper getDBHelper(Context context){
       if(dbHelper==null){
           dbHelper=new DBHelper(context);
       }
        return dbHelper;
   }
} 