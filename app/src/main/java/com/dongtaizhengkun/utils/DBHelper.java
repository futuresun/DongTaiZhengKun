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
        db.execSQL("insert into packing values('纸',1),('胶',1),('编',1),('编纸',1),('布',1);");
        db.execSQL("insert into product_name values('神柜',1),('药',1),('花格',1),('窗配',1),('百货',1),('渔具',1);");
        db.execSQL("insert into out_station values('梁平',1),('北碚',1),('二郎',1),('桐梓',1);");
        db.execSQL("insert into destination values('眉山',1),('重庆',1),('德阳',1),('泸州',1),('达州',1);");
        db.execSQL("insert into recv_contact values('测试用户1',13888888888,1),('测试用户2',13999999999,1);");
        db.execSQL("insert into send_contact values('测试用户1',18888888888,511311199909098888,1),('测试用户2',18999999999,511311199909099999,1);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
