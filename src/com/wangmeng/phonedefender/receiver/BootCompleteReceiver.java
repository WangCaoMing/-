package com.wangmeng.phonedefender.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * ���ڽ��տ�����ɷ��͵Ĺ㲥�Ľ�����
 * @author Administrator
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	//�����ļ�
	private static SharedPreferences sprefs;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
	 	//��ȡ�����ļ�
		sprefs = arg0.getSharedPreferences("sprefs", arg0.MODE_PRIVATE);
		
		//��ȡ�ֻ�����������״̬�Ծ�������Ĳ����Ƿ�Ҫ����
		boolean safe_start = sprefs.getBoolean("safe_start", false);
		if (safe_start)
		{
			//��ȡ��ǰsim�������к�
			TelephonyManager telephonyManager = (TelephonyManager) arg0.getSystemService(arg0.TELEPHONY_SERVICE);
			String current_sim = telephonyManager.getSimSerialNumber();
			
			//��ȡ�����ļ��б����sim�������к�
			String sprefs_sim = sprefs.getString("sim", null);
			
			if (!TextUtils.isEmpty(sprefs_sim)) //��������ļ��д��ڱ����sim�����кŵĻ�, ��Ҫ�뵱ǰ��sim�������кŽ��бȶ�
			{
				if (current_sim.equals(sprefs_sim))
				{
					//���к���ͬ, �����κεĲ���
					System.out.println("sim���к���ͬ");
				}
				else
				{
					//���кŲ�ͬ, Ҫ���ͱ������Ÿ����õİ�ȫ����
					System.out.println("sim���кŲ�ͬ");
					
					//��ȡ��ȫ����
					String phone = sprefs.getString("safe_phone", null);
					
					//���ͱ�������
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phone, null, "SIM�������ı�, ��ע��", null, null);
					
					
				}
			}
		}
	}

}
