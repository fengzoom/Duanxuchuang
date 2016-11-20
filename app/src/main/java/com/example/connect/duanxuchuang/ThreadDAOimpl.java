package com.example.connect.duanxuchuang;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */

public class ThreadDAOimpl implements IThread {
    private DBHelper dbHelper=null;

    public ThreadDAOimpl(Context context) {
        dbHelper=new DBHelper(context);
    }
   /* (id integer primary key autoincrement,thread_id integer,url text,start integer,end integer,finished integer)*/
    @Override
    public void insertThread(ThreadInfo info) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into info (thread_id,url,start,end,finished) values(?,?,?,?,?)"
                ,new Object[]{info.getId(),info.getUrl(),info.getStart(),info.getEnd(),info.getFinished()});
        db.close();
    }

    @Override
    public void deleteThread(String url, int threadID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from info where url =? and thread_id =?",new Object[]{url,threadID});
        db.close();
    }

    @Override
    public void updataThread(String url, int threadID, long finished) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update info set finished =? where url=? and thread_id = ?",new Object[]{finished,url,threadID});
        db.close();
    }

    @Override
    public List<ThreadInfo> getThreads(String url) {
        List<ThreadInfo> list=new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from info where url =?", new String[]{url});
        while (cursor.moveToNext()){
            ThreadInfo threadInfo=new ThreadInfo();
            threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(threadInfo);
        }
        db.close();
        cursor.close();

        return list;
    }

    @Override
    public boolean isExists(String url, int threadID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from info where url =? and thread_id=?", new String[]{url,threadID+""});
        boolean isExists=cursor.moveToNext();
        db.close();
        cursor.close();
        return isExists;
    }
}
