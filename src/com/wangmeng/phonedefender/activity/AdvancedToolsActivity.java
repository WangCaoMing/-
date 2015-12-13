package com.wangmeng.phonedefender.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.activity.advancedtools_activity.CheckPhoneNumberActivity;

/**
 * 高级工具界面
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
	 * 归属地查询按钮的点击事件
	 * @param v
	 */
	public void checkPhoneNumber(View v)
	{
		//跳转到数据库查询界面
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		startActivity(new Intent(this, CheckPhoneNumberActivity.class));
	}
}
