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

	// �����ļ�
	private SharedPreferences sprefs;
	private CheckBox cb_safe_start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setup3);

		// ��ȡ�����ļ�
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// ��ȡ�����ļ��е����
		cb_safe_start = (CheckBox) findViewById(R.id.cb_safe_start);

		// ��ʼ�������״̬
		boolean safe_start = sprefs.getBoolean("safe_start", false);
		cb_safe_start.setChecked(safe_start);
		setCBtext(safe_start);

		// Ϊcheckbox��ӵ���¼��ļ���
		cb_safe_start.setOnClickListener(new OnClickListener() {
			// һ��checkbox��Ӧ�����¼���, checkbox��״̬���ᷢ����ת,
			// ������onclick����ischecked()���ص�״̬���Ѿ���ת���״̬. ���Բ�����ȡ����.
			// ��������Ȼ������������ȽϺ�//cb_safe_start.setOnCheckedChangeListener(),
			// ���Ǽ�Ȼ����onclicklistener(), �����Ű�, ����������
			@Override
			public void onClick(View v) {
				boolean state = cb_safe_start.isChecked();
				cb_safe_start.setChecked(state);
				setCBtext(state);
				sprefs.edit().putBoolean("safe_start", state).commit(); // ���ı���״̬д�뵽�����ļ���

				boolean safe_start = sprefs.getBoolean("safe_start", false);
				System.out.println(safe_start);
			}
		});
	}

	/**
	 * ����checkbox��ʾ���ַ���
	 * 
	 * @param safe_start
	 *            checkbox�����״̬
	 */
	private void setCBtext(boolean safe_start) {

		if (safe_start)
			cb_safe_start.setText("���������Ѿ�����");
		else
			cb_safe_start.setText("���������Ѿ��ر�");
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
		// ���������ļ��е������򵼱�־λΪtrue
		sprefs.edit().putBoolean("is_setup", true).commit();
		startActivity(new Intent(this, Option0Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
