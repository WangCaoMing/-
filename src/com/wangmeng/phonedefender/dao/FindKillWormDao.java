package com.wangmeng.phonedefender.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FindKillWormDao {
    
    private static SQLiteDatabase database;
    
    public FindKillWormDao(Context context)
    {
        //连接病毒数据库
        File file = new File(context.getFilesDir().getAbsolutePath(), "antivirus.db");
        database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
    }
    /**
     * 检查给定的md5值是否存在于病毒数据库中
     * @param Md5 待检查的md5值
     * @return 存在则返回描述信息, 不存在返回null
     */
    public static String CheckAppMD5(String Md5)
    {
        String result = null;
        String sql = "select desc from datable where md5=?";
        Cursor cursor = database.rawQuery(sql, new String[]{Md5});
        if (cursor.moveToNext())
            result = cursor.getString(cursor.getColumnIndex("desc"));
        return result;
    }
    
    /**
     * 如果数据库存在则关闭数据库连接
     */
    public void closeDatabase()
    {
        if (database != null)
            database.close();
    }
}
