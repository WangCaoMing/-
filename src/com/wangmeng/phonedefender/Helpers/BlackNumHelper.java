package com.wangmeng.phonedefender.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 黑名单数据库的Helper类
 * @author Administrator
 *
 */
public class BlackNumHelper extends SQLiteOpenHelper {

    public BlackNumHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table blacknumber (_id integer primary key autoincrement,number varchar(20),mode varchar(2))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
