package com.wangmeng.phonedefender.activity.setup_activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.tools.DisplayTools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class Setup2Activity extends BaseSetupActivity {

	// �����ļ�
	private static SharedPreferences sprefs;

	// �����ļ��е����
	private EditText et_phonenumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setup2);

		// ��ȡ�����ļ�
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// ��ȡ���
		et_phonenumber = (EditText) findViewById(R.id.et_phone_number);
		
		//��ʼ�����
		String safe_phone = sprefs.getString("safe_phone", null);
		if (safe_phone != null)
			et_phonenumber.setText(safe_phone);
	}

	/**
	 * ��ѡ����ϵ�˰�ť�ĵ����¼�
	 * 
	 * @param v
	 */
	public void ChooseContact(View v) {
		startActivityForResult(new Intent(this, DisplayContactsActivity.class),
				1); // ��ת��չʾ��ϵ�˵�ҳ��
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == this.RESULT_OK) {
			// ��ȡ���ص����ݲ����ø�edittext��
			String name = data.getStringExtra("name");
			String phone = data.getStringExtra("phone");
			et_phonenumber.setText(phone); // ��ѡ�����ϵ�˵ĺ�����䵽edittext��
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void doPrior() {
		startActivity(new Intent(this, Setup1Activity.class));
		overridePendingTransition(R.anim.prior_enter_anim,
				R.anim.prior_out_anim);
		finish();
	}

	@Override
	public void doNext() {
		// ����ƥ�䰲ȫ�����������ʽ��֤
		Pattern pattern = Pattern.compile("^(\\+)?\\d+$");

		// ��ȫ������ж�
		String phone = et_phonenumber.getText().toString();
		Matcher matcher = pattern.matcher(phone); // ƥ��������ʽ
		if (TextUtils.isEmpty(phone)) // �жϰ�ȫ����ĸ�ʽ�Ƿ���ȷ
		{
			DisplayTools.ShowToast(this, "��ȫ���벻��Ϊ��");
			return;
		} else if (!matcher.matches()) {
			DisplayTools.ShowToast(this, "��ȫ�����ʽ����ȷ");
			return;
		} else {
			sprefs.edit().putString("safe_phone", phone).commit(); // ����ȫ����д�뵽�����ļ���
		}
		startActivity(new Intent(this, Setup3Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
