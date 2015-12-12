package com.wangmeng.phonedefender.activity.setup_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.activity.Option0Activity;

public class Setup3Activity extends BaseSetupActivity {

	// 配置文件
	private SharedPreferences sprefs;
	private CheckBox cb_safe_start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setup3);

		// 获取配置文件
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// 获取布局文件中的组件
		cb_safe_start = (CheckBox) findViewById(R.id.cb_safe_start);

		// 初始化组件的状态
		boolean safe_start = sprefs.getBoolean("safe_start", false);
		cb_safe_start.setChecked(safe_start);
		setCBtext(safe_start);

		// 为checkbox添加点击事件的监听
		cb_safe_start.setOnClickListener(new OnClickListener() {
			// 一旦checkbox响应单击事件后, checkbox的状态即会发生反转,
			// 所以在onclick中用ischecked()返回的状态是已经反转后的状态. 所以不用在取反了.
			// 在这里显然用这个监听器比较好//cb_safe_start.setOnCheckedChangeListener(),
			// 但是既然用了onclicklistener(), 就用着吧, 反正可以用
			@Override
			public void onClick(View v) {
				boolean state = cb_safe_start.isChecked();
				cb_safe_start.setChecked(state);
				setCBtext(state);
				sprefs.edit().putBoolean("safe_start", state).commit(); // 将改变后的状态写入到配置文件中

				boolean safe_start = sprefs.getBoolean("safe_start", false);
				System.out.println(safe_start);
			}
		});
	}

	/**
	 * 设置checkbox显示的字符串
	 * 
	 * @param safe_start
	 *            checkbox组件的状态
	 */
	private void setCBtext(boolean safe_start) {

		if (safe_start)
			cb_safe_start.setText("防盗保护已经开启");
		else
			cb_safe_start.setText("防盗保护已经关闭");
	}

	@Override
	public void doPrior() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, Setup2Activity.class));
		overridePendingTransition(R.anim.prior_enter_anim,
				R.anim.prior_out_anim);
		finish();
	}

	@Override
	public void doNext() {
		// 设置配置文件中的设置向导标志位为true
		sprefs.edit().putBoolean("is_setup", true).commit();
		startActivity(new Intent(this, Option0Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
