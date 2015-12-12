package com.wangmeng.phonedefender.receiver;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.service.LocationService;

import android.R.string;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.sax.StartElementListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * 接收sms短信的广播, 识别功能指令并作出相应的动作
 * 
 * @author Administrator
 * 
 */
public class SMSReceiver extends BroadcastReceiver {

	// 配置文件
	private static SharedPreferences sprefs;
	
	//设备管理器相关对象
	private ComponentName componentName;
	private DevicePolicyManager devicePolicyManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取短信的内容和发送短信的源地址
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objs) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			final String address = message.getOriginatingAddress(); // 获取发送短信的手机号
			String body = message.getMessageBody(); // 获取短信的内容

			if ("#*alarm*#".equals(body)) // 短信是播放报警音乐的指令
			{
				abortBroadcast(); // 停止将该短信的广播向下继续传递
				// 播放报警音乐
				MediaPlayer player = MediaPlayer.create(context, R.raw.hyj);
				player.setLooping(true); // 设置循环播放
				player.setVolume(1f, 1f);
				player.start();
			} else if ("#*location*#".equals(body)) // 短信内容为获取位置的指令
			{
				abortBroadcast(); // 停止将该短信的广播向下继续传递

				Intent service = new Intent(context, LocationService.class);
				context.startService(service); // 开启获取位置的服务

				// 向发送指令短信的号码发送位置信息
				Thread thread = new Thread() {
					public void run() {
						boolean location_enable = sprefs.getBoolean(
								"location_enable", false);
						while (!location_enable) {
							try {
								this.sleep(5000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						// 从配置文件中获取位置信息
						String latitudeString = sprefs.getString("latitude",
								null);
						String longitudeString = sprefs.getString("longitude",
								null);

						// 拼接要发送的短信
						final String sendtext = "经度:" + longitudeString
								+ ", 纬度:" + latitudeString;
						SmsManager smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(address, null, sendtext,
								null, null);
					};
				};
				thread.start();

				sprefs.edit().remove("location_enable").commit(); // 删除位置信息已准备好的标识
			} else if ("#*lockscreen*#".equals(body)) {
				//先要激活该软件的设备管理器, 在HomeActivity界面激活.
				
				componentName = new ComponentName(context,
						MyDeviceAdminReceiver.class); //设备管理器接受者组件名
				devicePolicyManager = (DevicePolicyManager) context
						.getSystemService(context.DEVICE_POLICY_SERVICE); //获取设备管理器
				if (devicePolicyManager.isAdminActive(componentName))
					
					devicePolicyManager.lockNow(); //锁屏
				
			}
		}
	}
}
