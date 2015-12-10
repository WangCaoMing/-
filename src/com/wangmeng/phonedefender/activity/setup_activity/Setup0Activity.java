package com.wangmeng.phonedefender.activity.setup_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wangmeng.phonedefender.R;
/**
 * 手机防盗选项的设置向导的首页
 * @author Administrator
 *
 */
public class Setup0Activity extends BaseSetupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setup0);
	}

	@Override
	public void doPrior() {
		
	}

	@Override
	public void doNext() {
		startActivity(new Intent(this, Setup1Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
