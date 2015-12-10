package com.wangmeng.phonedefender.activity.setup_activity;

import com.wangmeng.phonedefender.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/**
 * 设置向导第二步
 * @author Administrator
 *
 */
public class Setup1Activity extends BaseSetupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setup1);
	}
	
	@Override
	public void doPrior() {
		startActivity(new Intent(this, Setup0Activity.class));
		overridePendingTransition(R.anim.prior_enter_anim, R.anim.prior_out_anim);
		finish();
	}


	@Override
	public void doNext() {
		startActivity(new Intent(this, Setup2Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
