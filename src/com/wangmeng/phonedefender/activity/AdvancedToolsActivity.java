package com.wangmeng.phonedefender.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.activity.advancedtools_activity.CheckPhoneNumberActivity;

/**
 * �߼����߽���
 * @author Administrator
 *
 */
public class AdvancedToolsActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_advancedtools);
	}
	
	/**
	 * �����ز�ѯ��ť�ĵ���¼�
	 * @param v
	 */
	public void checkPhoneNumber(View v)
	{
		//��ת�����ݿ��ѯ����
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		startActivity(new Intent(this, CheckPhoneNumberActivity.class));
	}
}
