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
 * ����sms���ŵĹ㲥, ʶ����ָ�������Ӧ�Ķ���
 * 
 * @author Administrator
 * 
 */
public class SMSReceiver extends BroadcastReceiver {

	// �����ļ�
	private static SharedPreferences sprefs;
	
	//�豸��������ض���
	private ComponentName componentName;
	private DevicePolicyManager devicePolicyManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		// ��ȡ���ŵ����ݺͷ��Ͷ��ŵ�Դ��ַ
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objs) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			final String address = message.getOriginatingAddress(); // ��ȡ���Ͷ��ŵ��ֻ���
			String body = message.getMessageBody(); // ��ȡ���ŵ�����

			if ("#*alarm*#".equals(body)) // �����ǲ��ű������ֵ�ָ��
			{
				abortBroadcast(); // ֹͣ���ö��ŵĹ㲥���¼�������
				// ���ű�������
				MediaPlayer player = MediaPlayer.create(context, R.raw.hyj);
				player.setLooping(true); // ����ѭ������
				player.setVolume(1f, 1f);
				player.start();
			} else if ("#*location*#".equals(body)) // ��������Ϊ��ȡλ�õ�ָ��
			{
				abortBroadcast(); // ֹͣ���ö��ŵĹ㲥���¼�������

				Intent service = new Intent(context, LocationService.class);
				context.startService(service); // ������ȡλ�õķ���

				// ����ָ����ŵĺ��뷢��λ����Ϣ
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
						// �������ļ��л�ȡλ����Ϣ
						String latitudeString = sprefs.getString("latitude",
								null);
						String longitudeString = sprefs.getString("longitude",
								null);

						// ƴ��Ҫ���͵Ķ���
						final String sendtext = "����:" + longitudeString
								+ ", γ��:" + latitudeString;
						SmsManager smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(address, null, sendtext,
								null, null);
					};
				};
				thread.start();

				sprefs.edit().remove("location_enable").commit(); // ɾ��λ����Ϣ��׼���õı�ʶ
			} else if ("#*lockscreen*#".equals(body)) {
				//��Ҫ�����������豸������, ��HomeActivity���漤��.
				
				componentName = new ComponentName(context,
						MyDeviceAdminReceiver.class); //�豸�����������������
				devicePolicyManager = (DevicePolicyManager) context
						.getSystemService(context.DEVICE_POLICY_SERVICE); //��ȡ�豸������
				if (devicePolicyManager.isAdminActive(componentName))
					
					devicePolicyManager.lockNow(); //����
				
			}
		}
	}
}
