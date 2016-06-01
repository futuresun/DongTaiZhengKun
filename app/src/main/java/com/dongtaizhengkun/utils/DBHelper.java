package com.dongtaizhengkun.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/5/31.
 */
public class DBHelper extends SQLiteOpenHelper {

    private String sql;
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, String sql) {
        super(context, name, factory, version);
        this.sql = sql;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
