package com.wangmeng.phonedefender.tools;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CheckTools {
	
	// 数据库的路径
	private static String path = "data/data/com.wangmeng.phonedefender/files/address.db";
	
	
	/**
	 * 从数据库中查询输入的电话号码的归属地
	 * @param phone_number
	 * @return
	 */
	public static String CheckPhoneNumberLocation(String phone_number)
	{
		
		if (phone_number.length() < 7)
		{
			return "归属地不清楚,哥帮不了你了";
		}
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY); // 打开数据库文件
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
	 * 返回服务器的状态(运行?结束?)
	 * @param context 传送Context对象
	 * @param service_name 要查询的service的全路径名
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
