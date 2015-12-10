package com.wangmeng.phonedefender.activity.setup_activity;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.activity.Option0Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class Setup3Activity extends BaseSetupActivity {

	//�����ļ�
	private SharedPreferences sprefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setup3);
		
		//��ȡ�����ļ�
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);
	}
	
	@Override
	public void doPrior() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, Setup2Activity.class));
		overridePendingTransition(R.anim.prior_enter_anim, R.anim.prior_out_anim);
		finish();
	}

	@Override
	public void doNext() {
		//���������ļ��е������򵼱�־λΪtrue
		sprefs.edit().putBoolean("is_setup", true).commit();
		startActivity(new Intent(this, Option0Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
