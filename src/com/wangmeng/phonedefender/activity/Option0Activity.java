package com.wangmeng.phonedefender.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.activity.setup_activity.Setup0Activity;

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
	 * ���½��뵽�����򵼽��水ť�ĵ����¼�
	 */
	public void ReSetup(View v) {
		startActivity(new Intent(this, Setup0Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
