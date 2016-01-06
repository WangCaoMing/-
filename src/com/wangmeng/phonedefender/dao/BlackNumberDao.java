package com.wangmeng.phonedefender.dao;

import java.util.ArrayList;
import java.util.List;

import com.wangmeng.phonedefender.Helpers.BlackNumHelper;
import com.wangmeng.phonedefender.bean.BlackNumberBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.OpenableColumns;
import android.provider.SyncStateContract.Helpers;

/**
 * 黑名单dao层
 * 
 * @author Administrator
 * 
 */
public class BlackNumberDao {

    // helper对象
    private BlackNumHelper blackNumHelper;
    
    // 表名
    private static final String tablename = "blacknumber";

    public BlackNumberDao(Context context) {
        blackNumHelper = new BlackNumHelper(context);
    }

    /**
     * 增加电话号码
     * 
     * @param number
     *            要增加的电话号码
     * @param mode
     *            拦截的模式<br>
     *            <p>
     *            &emsp;*1 电话拦截<br>
     *            &emsp;*2 短信拦截
     *            &emsp;*3 电话 + 短信拦截
     *            </p>
     * @return 添加的结果
     */
    public boolean add(String number, String mode) {
        // 获取数据库
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();

        // 打包数据
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);

        // 将数据插入到数据库中
        long result = db.insert(tablename, null, values);

        // 返回结果
        if (result == -1)
            return false;
        else
            return true;
    }

    /**
     * 从黑名单中移除电话号码
     * 
     * @param number
     *            要删除的电环号码
     * @return 删除的状态
     */
    public boolean delete(String number) {
        // 获取数据库
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        //执行删除操作
        int result = db.delete(tablename, "number=?",
                new String[] { number });

        //返回操作结果
        if (result == 0)
            return false;
        else
            return true;
    }
    
    /**
     * 更改电话号码的拦截模式
     * @param number 电话号码
     * @return 改变的结果
     */
    public boolean changeMode(String number, String mode)
    {
        //获取数据库
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        //打包数据
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        
        //执行更新操作
        int result = db.update(tablename, values, "number=?", new String[]{number});
        
        //返回操作结果
        if (result == 0)
            return false;
        else 
            return true;
    }
    
    /**
     * 查找黑名单
     * @param number 要查找的电话号码
     * @return 黑名单中查找的电话号码的信息, 返回null表示未找到
     */
    public BlackNumberBean find(String number)
    {
        //获取数据库
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        //执行查询操作
        Cursor cursor = db.query(tablename, null, "number=?", new String[]{number}, null, null, null);
        
        //获取待返回的数据
        if (cursor.moveToNext())
        {
            //封装数据
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            BlackNumberBean bean = new BlackNumberBean();
            bean.setNumber(number);
            bean.setMode(mode);
            return bean;
            
        }
        else 
            return null;
        
    }
    
    /**
     * 返回黑名单中所有的电话号码和对应的拦截模式
     * @return
     */
    public List<BlackNumberBean> findAll()
    {
        //获取数据库
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        Cursor cursor = db.query(tablename, null, null, null, null, null, null);
        
        //创建用于收据bean的ArrayList
        List<BlackNumberBean> list = new ArrayList<BlackNumberBean>();
        
        //获取待返回的数据
        while (cursor.moveToNext())
        {
            //封装数据
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            BlackNumberBean bean = new BlackNumberBean();
            bean.setNumber(number);
            bean.setMode(mode);
            list.add(bean);
        }
        
        return list;

    }

    /**
     * 分页查询数据
     * @param pagenumber 要查询的页面
     * @param pagesize 页面的大小(每个页面的数据量)
     * @return
     */
    public List<BlackNumberBean> findLimit(int pagenumber, int pagesize)
    {
        //获取数据库
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        //执行sql语句
        String sql = "select * from " + tablename + " limit ? offset ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(pagesize), String.valueOf(pagenumber * pagesize)});
        
      //创建用于收据bean的ArrayList
        List<BlackNumberBean> list = new ArrayList<BlackNumberBean>();
        
        //获取待返回的数据
        while (cursor.moveToNext())
        {
            //封装数据
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            BlackNumberBean bean = new BlackNumberBean();
            bean.setNumber(number);
            bean.setMode(mode);
            list.add(bean);
        }
        
        return list;
    }
    
    /**
     * 获取数据库中的数据的数量
     * @return 数据的数量, 如果返回-1, 则代表查询失败
     */
    public int Count()
    {
        //获取数据库
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        //执行sql语句
        String sql = "select count(*) from " + tablename;
        Cursor cursor = db.rawQuery(sql, null);
        
        //获取结果
        int count = -1;
        if (cursor.moveToNext())
        {
           count = cursor.getInt(0);
        }
    
        return count;
    }
}
