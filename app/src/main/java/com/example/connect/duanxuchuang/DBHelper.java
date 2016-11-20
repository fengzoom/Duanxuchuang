package com.example.connect.duanxuchuang;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/11/10.
 */

public class DBHelper extends SQLiteOpenHelper {
    private final static String CREAT = "create table info(id integer primary key autoincrement," +
            "thread_id integer,url text,start integer,end integer,finished integer)";
    private final static String DROP ="drop table if exists info";
    private final static String dbname="info.db";
    private final static int VERSION =1;

    public DBHelper(Context context) {
        super(context, dbname, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP);
        db.execSQL(CREAT);
    }
}
