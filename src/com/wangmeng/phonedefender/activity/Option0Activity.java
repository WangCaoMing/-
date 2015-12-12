package com.wangmeng.phonedefender.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.activity.setup_activity.Setup0Activity;
import com.wangmeng.phonedefender.receiver.MyDeviceAdminReceiver;
import com.wangmeng.phonedefender.service.LocationService;
import com.wangmeng.phonedefender.tools.DisplayTools;

/**
 * �ֻ�����ѡ���������
 * 
 * @author Administrator
 * 
 */
public class Option0Activity extends Activity {

	// �����ļ�
	private SharedPreferences sprefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_option0);
		
		// ��ȡ�����ļ�
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);
		
		//��ȡ�����ļ��е����
		TextView tv_safenumber = (TextView) findViewById(R.id.option0_tv_safenumber);
		ImageView iv_openstate = (ImageView) findViewById(R.id.option0_iv_openstate);
		
		//�������ļ��ж�ȡ���ݲ�������Ӧ�����״̬
		String phone = sprefs.getString("safe_phone", null);
		boolean state = sprefs.getBoolean("safe_start", false);
		tv_safenumber.setText(phone); //��ʾ��ȫ����
		if (state) //��ʾ���������Ŀ���״̬
			iv_openstate.setImageResource(R.drawable.lock);
		else
			iv_openstate.setImageResource(R.drawable.unlock);
		
		// ��ȡ�Ƿ����������򵼽���
		boolean is_setup = sprefs.getBoolean("is_setup", false);
		if (!is_setup) // ���û�н��й�����, ����Ҫ��ת�������򵼽����������
		{
			// ��ת�������򵼽���
			Intent intent = new Intent(this, Setup0Activity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
			finish();
		}
		
	}

	/**
	 * �����豸������
	 * @param componentName ��ת�������������Ҫ�������
	 */
	public void ActiveDeviceAdmin(View v) {
		
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		ComponentName componentName = new ComponentName(this,
				MyDeviceAdminReceiver.class); //�豸�����������������
		if (!devicePolicyManager.isAdminActive(componentName))
		{
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "�豸��������Ҫ��������������������");
			startActivity(intent);
		}
		else
		{
			DisplayTools.ShowToast(this, "�豸�������Ѽ���, �����ظ�����");
		}
	}
	
	
	/**
	 * ���½��뵽�����򵼽��水ť�ĵ����¼�
	 */
	public void ReSetup(View v) {
		startActivity(new Intent(this, Setup0Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
