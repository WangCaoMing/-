package com.wangmeng.phonedefender.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 用于接收开机完成发送的广播的接收者
 * @author Administrator
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	//配置文件
	private static SharedPreferences sprefs;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
	 	//获取配置文件
		sprefs = arg0.getSharedPreferences("sprefs", arg0.MODE_PRIVATE);
		
		//获取手机防盗开启的状态以决定下面的操作是否要进行
		boolean safe_start = sprefs.getBoolean("safe_start", false);
		if (safe_start)
		{
			//获取当前sim卡的序列号
			TelephonyManager telephonyManager = (TelephonyManager) arg0.getSystemService(arg0.TELEPHONY_SERVICE);
			String current_sim = telephonyManager.getSimSerialNumber();
			
			//获取配置文件中保存的sim卡的序列号
			String sprefs_sim = sprefs.getString("sim", null);
			
			if (!TextUtils.isEmpty(sprefs_sim)) //如果配置文件中存在保存的sim卡序列号的话, 则要与当前的sim卡的序列号进行比对
			{
				if (current_sim.equals(sprefs_sim))
				{
					//序列号相同, 不做任何的操作
					System.out.println("sim序列号相同");
				}
				else
				{
					//序列号不同, 要发送报警短信给设置的安全号码
					System.out.println("sim序列号不同");
					
					//获取安全号码
					String phone = sprefs.getString("safe_phone", null);
					
					//发送报警短信
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phone, null, "SIM卡发生改变, 请注意", null, null);
					
					
				}
			}
		}
	}

}
