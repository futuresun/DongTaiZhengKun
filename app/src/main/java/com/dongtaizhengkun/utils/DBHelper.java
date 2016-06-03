package com.dongtaizhengkun.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/5/31.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbHelper;

    private DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DBHelper getInstance(Context context) {
        if(dbHelper == null) {
            dbHelper = new DBHelper(context, "local_data", null, 1);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBAdapter.SEND_CONTACT_SQL);
        db.execSQL(DBAdapter.RECV_CONTACT_SQL);
        db.execSQL(DBAdapter.TODAY_ORDERS_SQL);
        db.execSQL(DBAdapter.DESTINATION_SQL);
        db.execSQL(DBAdapter.OUT_STATION_SQL);
        db.execSQL(DBAdapter.PRODUCT_NAME_SQL);
        db.execSQL(DBAdapter.PACKING_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
