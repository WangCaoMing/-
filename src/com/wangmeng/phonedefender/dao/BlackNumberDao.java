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
 * ������dao��
 * 
 * @author Administrator
 * 
 */
public class BlackNumberDao {

    // helper����
    private BlackNumHelper blackNumHelper;
    
    // ����
    private static final String tablename = "blacknumber";

    public BlackNumberDao(Context context) {
        blackNumHelper = new BlackNumHelper(context);
    }

    /**
     * ���ӵ绰����
     * 
     * @param number
     *            Ҫ���ӵĵ绰����
     * @param mode
     *            ���ص�ģʽ<br>
     *            <p>
     *            &emsp;*1 �绰����<br>
     *            &emsp;*2 ��������
     *            &emsp;*3 �绰 + ��������
     *            </p>
     * @return ��ӵĽ��
     */
    public boolean add(String number, String mode) {
        // ��ȡ���ݿ�
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();

        // �������
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);

        // �����ݲ��뵽���ݿ���
        long result = db.insert(tablename, null, values);

        // ���ؽ��
        if (result == -1)
            return false;
        else
            return true;
    }

    /**
     * �Ӻ��������Ƴ��绰����
     * 
     * @param number
     *            Ҫɾ���ĵ绷����
     * @return ɾ����״̬
     */
    public boolean delete(String number) {
        // ��ȡ���ݿ�
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        //ִ��ɾ������
        int result = db.delete(tablename, "number=?",
                new String[] { number });

        //���ز������
        if (result == 0)
            return false;
        else
            return true;
    }
    
    /**
     * ���ĵ绰���������ģʽ
     * @param number �绰����
     * @return �ı�Ľ��
     */
    public boolean changeMode(String number, String mode)
    {
        //��ȡ���ݿ�
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        //�������
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        
        //ִ�и��²���
        int result = db.update(tablename, values, "number=?", new String[]{number});
        
        //���ز������
        if (result == 0)
            return false;
        else 
            return true;
    }
    
    /**
     * ���Һ�����
     * @param number Ҫ���ҵĵ绰����
     * @return �������в��ҵĵ绰�������Ϣ, ����null��ʾδ�ҵ�
     */
    public BlackNumberBean find(String number)
    {
        //��ȡ���ݿ�
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        //ִ�в�ѯ����
        Cursor cursor = db.query(tablename, null, "number=?", new String[]{number}, null, null, null);
        
        //��ȡ�����ص�����
        if (cursor.moveToNext())
        {
            //��װ����
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
     * ���غ����������еĵ绰����Ͷ�Ӧ������ģʽ
     * @return
     */
    public List<BlackNumberBean> findAll()
    {
        //��ȡ���ݿ�
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        Cursor cursor = db.query(tablename, null, null, null, null, null, null);
        
        //���������վ�bean��ArrayList
        List<BlackNumberBean> list = new ArrayList<BlackNumberBean>();
        
        //��ȡ�����ص�����
        while (cursor.moveToNext())
        {
            //��װ����
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
     * ��ҳ��ѯ����
     * @param pagenumber Ҫ��ѯ��ҳ��
     * @param pagesize ҳ��Ĵ�С(ÿ��ҳ���������)
     * @return
     */
    public List<BlackNumberBean> findLimit(int pagenumber, int pagesize)
    {
        //��ȡ���ݿ�
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        //ִ��sql���
        String sql = "select * from " + tablename + " limit ? offset ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(pagesize), String.valueOf(pagenumber * pagesize)});
        
      //���������վ�bean��ArrayList
        List<BlackNumberBean> list = new ArrayList<BlackNumberBean>();
        
        //��ȡ�����ص�����
        while (cursor.moveToNext())
        {
            //��װ����
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
     * ��ȡ���ݿ��е����ݵ�����
     * @return ���ݵ�����, �������-1, ������ѯʧ��
     */
    public int Count()
    {
        //��ȡ���ݿ�
        SQLiteDatabase db = blackNumHelper.getWritableDatabase();
        
        //ִ��sql���
        String sql = "select count(*) from " + tablename;
        Cursor cursor = db.rawQuery(sql, null);
        
        //��ȡ���
        int count = -1;
        if (cursor.moveToNext())
        {
           count = cursor.getInt(0);
        }
    
        return count;
    }
}
