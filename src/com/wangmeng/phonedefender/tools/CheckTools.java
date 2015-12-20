package com.wangmeng.phonedefender.tools;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CheckTools {
	
	// ���ݿ��·��
	private static String path = "data/data/com.wangmeng.phonedefender/files/address.db";
	
	
	/**
	 * �����ݿ��в�ѯ����ĵ绰����Ĺ�����
	 * @param phone_number
	 * @return
	 */
	public static String CheckPhoneNumberLocation(String phone_number)
	{
		
		if (phone_number.length() < 7)
		{
			return "�����ز����,��ﲻ������";
		}
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY); // �����ݿ��ļ�
		String subnum = phone_number.substring(0, 7);
		System.out.println(subnum);
		String sql = "select location from data2 where id=(select outkey from data1 where id=?)";
		Cursor cursor = database.rawQuery(sql, new String[]{subnum});
		String location = null;
		if (cursor.moveToNext())
		{
			location = cursor.getString(cursor.getColumnIndex("location"));
		}
		else
		{
			location = null;
		}
		return location;
	}
	
	/**
	 * ���ط�������״̬(����?����?)
	 * @param context ����Context����
	 * @param service_name Ҫ��ѯ��service��ȫ·����
	 * @return
	 */
	public static boolean CheckServiceState(Context context, String service_name)
	{
		ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = manager.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : list) {
			String classname = runningServiceInfo.service.getClassName();
			
			if (classname.equals(service_name))
			{
				System.out.println(classname);
				return true;
			}
		}
		
		return false; 
	}
}
