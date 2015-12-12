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
 * �����򵼵ڶ���
 * 
 * @author Administrator
 * 
 */
public class Setup1Activity extends BaseSetupActivity {

	// �����ļ�
	private static SharedPreferences sprefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setup1);

		// ��ȡ�����ļ�
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// ��ȡ�����ļ��е����
		final SettingItemLayout si_sim = (SettingItemLayout) findViewById(R.id.layout_step1_si_sim);

		// ��ʼ����ʾ�����״̬
		String sim = sprefs.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			// ����ǿ���ʹsi_sim��״̬Ϊδѡ��
			si_sim.SetState(false);
		} else {
			// ������ǿ���ʹsi_sim��״̬Ϊѡ��
			si_sim.SetState(true);
		}

		// Ϊsi_sim��ӵ����¼��ļ���
		si_sim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean current_state = si_sim.GetState(); // ��ȡ��ǰ�����ѡ��״̬
				si_sim.SetState(!current_state); // �����뵱ǰ״̬�෴��״̬
				if (si_sim.GetState()) // �жϵ���������ѡ��״̬
				{
					// ѡ��״̬, ���sim�����кŵ������ļ���
					TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String sim = telephonyManager.getSimSerialNumber(); // ��ȡsim�������к�
					sprefs.edit().putString("sim", sim).commit(); // �����к�д�������ļ���
					System.out.println("sim�����к�:" + sim); // ������, ���sim�������к�
				} else {
					// δѡ��, ��������ļ��д���sim�����к�, ɾ��֮
					String sim = sprefs.getString("sim", null);
					if (!TextUtils.isEmpty(sim)) {
						// �������sim�����к�, ɾ��֮
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
