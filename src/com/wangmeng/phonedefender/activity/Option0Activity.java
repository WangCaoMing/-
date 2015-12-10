package com.wangmeng.phonedefender.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.activity.setup_activity.Setup0Activity;

/**
 * 手机防盗选项的主界面
 * 
 * @author Administrator
 * 
 */
public class Option0Activity extends Activity {

	// 配置文件
	private SharedPreferences sprefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_option0);

		// 获取配置文件
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// 获取是否进入过设置向导界面
		boolean is_setup = sprefs.getBoolean("is_setup", false);
		if (!is_setup) // 如果没有进行过设置, 则需要跳转到设置向导界面进行设置
		{
			// 跳转到设置向导界面
			Intent intent = new Intent(this, Setup0Activity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
			finish();
		}

	}

	/**
	 * 重新进入到设置向导界面按钮的单击事件
	 */
	public void ReSetup(View v) {
		startActivity(new Intent(this, Setup0Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
