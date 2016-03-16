package com.wangmeng.phonedefender.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FindKillWormDao {
    
    private static SQLiteDatabase database;
    
    public FindKillWormDao(Context context)
    {
        //���Ӳ������ݿ�
        File file = new File(context.getFilesDir().getAbsolutePath(), "antivirus.db");
        database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
    }
    /**
     * ��������md5ֵ�Ƿ�����ڲ������ݿ���
     * @param Md5 ������md5ֵ
     * @return �����򷵻�������Ϣ, �����ڷ���null
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
     * ������ݿ������ر����ݿ�����
     */
    public void closeDatabase()
    {
        if (database != null)
            database.close();
    }
}
