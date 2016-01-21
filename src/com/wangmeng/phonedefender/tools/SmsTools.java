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
 * 与短信相关的操作
 * 
 * @author Administrator
 * 
 */
public class SmsTools {

    /**
     * 将手机上的短信备份到SD卡中
     * 
     * @param context 上下文
     * @param path 要备份的位置
     * @param smsBackupCallback 用于控制进度条UI的回调函数, 如果不需要则设为null
     */
    public static void backup(Context context, String path, ISmsBackupCallback smsBackupCallback) {
        // 获取短信数据库的uri
        Uri uri = Uri.parse("content://sms/");

        // 通过sms的内容提供者获取所有的短信
        Cursor cursor = context.getContentResolver().query(uri,
                new String[] { "address", "date", "type", "body" }, null, null,
                null);
        //如果回调函数不为空, 则将短信的条数设置给进度条
        if (smsBackupCallback != null)
            smsBackupCallback.beforeBackup(cursor.getCount());
        //遍历短信并转换为xml格式
        XmlSerializer serializer = Xml.newSerializer(); // 创建xml序列器
        File file = new File(path); // 输出文件位置
        System.out.println(file.toString()); //调试用
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);//创建输出流
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        //完成设置并开始转换
        try {
            serializer.setOutput(out, "utf-8"); //为序列器设置输出流和编码类型
            serializer.startDocument("utf-8", true); //设置开始节点
            serializer.startTag(null, "smss");
            
            //开始遍历短信并进行转换
            int i = 0;
            while (cursor.moveToNext())
            {
                //构造短信节点
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
                    //为防止内容中有xml的保留字符, 需要对内容中的保留字符进行转义
                    String new_body = StringEscapeUtils.escapeXml(body);
                    serializer.comment(new_body);
                    serializer.endTag(null, "sms");
                }
                else
                    serializer.comment(" ");
                //如果回调函数不为空, 则将当前进度设置给进度条
                ++i; //更新进度
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
            return ; //发生异常, 直接退出程序
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        //如果回调函数不为空, 则调用afterBackup函数
        smsBackupCallback.afteronBackup();
       
    }
}
