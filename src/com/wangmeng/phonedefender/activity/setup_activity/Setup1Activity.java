package com.wangmeng.phonedefender.activity.setup_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.self_defined_layout.SettingItemLayout;

/**
 * 设置向导第二步
 * 
 * @author Administrator
 * 
 */
public class Setup1Activity extends BaseSetupActivity {

	// 配置文件
	private static SharedPreferences sprefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setup1);

		// 获取配置文件
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// 获取布局文件中的组件
		final SettingItemLayout si_sim = (SettingItemLayout) findViewById(R.id.layout_step1_si_sim);

		// 初始化显示组件的状态
		String sim = sprefs.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			// 如果是空则使si_sim的状态为未选中
			si_sim.SetState(false);
		} else {
			// 如果不是空则使si_sim的状态为选中
			si_sim.SetState(true);
		}

		// 为si_sim添加单击事件的监听
		si_sim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean current_state = si_sim.GetState(); // 获取当前组件的选中状态
				si_sim.SetState(!current_state); // 设置与当前状态相反的状态
				if (si_sim.GetState()) // 判断点击后组件的选中状态
				{
					// 选中状态, 添加sim卡序列号到配置文件中
					TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String sim = telephonyManager.getSimSerialNumber(); // 获取sim卡的序列号
					sprefs.edit().putString("sim", sim).commit(); // 将序列号写入配置文件中
					System.out.println("sim卡序列号:" + sim); // 调试用, 输出sim卡的序列号
				} else {
					// 未选中, 如果配置文件中存在sim卡序列号, 删除之
					String sim = sprefs.getString("sim", null);
					if (!TextUtils.isEmpty(sim)) {
						// 如果存在sim卡序列号, 删除之
						sprefs.edit().remove("sim").commit();
					}
				}
			}
		});
	}

	@Override
	public void doPrior() {
		startActivity(new Intent(this, Setup0Activity.class));
		overridePendingTransition(R.anim.prior_enter_anim,
				R.anim.prior_out_anim);
		finish();
	}

	@Override
	public void doNext() {
		startActivity(new Intent(this, Setup2Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
