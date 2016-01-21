package com.wangmeng.phonedefender.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang.StringEscapeUtils;
import org.xmlpull.v1.XmlSerializer;

import com.wangmeng.phonedefender.myinterface.ISmsBackupCallback;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

/**
 * �������صĲ���
 * 
 * @author Administrator
 * 
 */
public class SmsTools {

    /**
     * ���ֻ��ϵĶ��ű��ݵ�SD����
     * 
     * @param context ������
     * @param path Ҫ���ݵ�λ��
     * @param smsBackupCallback ���ڿ��ƽ�����UI�Ļص�����, �������Ҫ����Ϊnull
     */
    public static void backup(Context context, String path, ISmsBackupCallback smsBackupCallback) {
        // ��ȡ�������ݿ��uri
        Uri uri = Uri.parse("content://sms/");

        // ͨ��sms�������ṩ�߻�ȡ���еĶ���
        Cursor cursor = context.getContentResolver().query(uri,
                new String[] { "address", "date", "type", "body" }, null, null,
                null);
        //����ص�������Ϊ��, �򽫶��ŵ��������ø�������
        if (smsBackupCallback != null)
            smsBackupCallback.beforeBackup(cursor.getCount());
        //�������Ų�ת��Ϊxml��ʽ
        XmlSerializer serializer = Xml.newSerializer(); // ����xml������
        File file = new File(path); // ����ļ�λ��
        System.out.println(file.toString()); //������
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);//���������
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        //������ò���ʼת��
        try {
            serializer.setOutput(out, "utf-8"); //Ϊ����������������ͱ�������
            serializer.startDocument("utf-8", true); //���ÿ�ʼ�ڵ�
            serializer.startTag(null, "smss");
            
            //��ʼ�������Ų�����ת��
            int i = 0;
            while (cursor.moveToNext())
            {
                //������Žڵ�
                serializer.startTag(null, "sms");
                String address = cursor.getString(cursor.getColumnIndex("address"));
                if (address != null)
                    serializer.attribute(null, "address", address);
                else
                    serializer.attribute(null, "address", "null");
                String type = cursor.getString(cursor.getColumnIndex("type"));
                if (type != null)
                    serializer.attribute(null, "type", type);
                else
                    serializer.attribute(null, "type", "null");
                String date = cursor.getString(cursor.getColumnIndex("date"));
                if (date != null)
                    serializer.attribute(null, "date", date);
                else
                    serializer.attribute(null, "date", "null");
                String body = cursor.getString(cursor.getColumnIndex("body"));
                if (body != null)
                {
                    System.out.println(body);
                    //Ϊ��ֹ��������xml�ı����ַ�, ��Ҫ�������еı����ַ�����ת��
                    String new_body = StringEscapeUtils.escapeXml(body);
                    serializer.comment(new_body);
                    serializer.endTag(null, "sms");
                }
                else
                    serializer.comment(" ");
                //����ص�������Ϊ��, �򽫵�ǰ�������ø�������
                ++i; //���½���
                System.out.println(i);
                if (smsBackupCallback != null)
                    smsBackupCallback.onBackup(i);
            }
          
            serializer.endTag(null, "smss");
            serializer.endDocument();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ; //�����쳣, ֱ���˳�����
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        //����ص�������Ϊ��, �����afterBackup����
        smsBackupCallback.afteronBackup();
       
    }
}
