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

	// 配置文件
	private static SharedPreferences sprefs;

	// 布局文件中的组件
	private EditText et_phonenumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setup2);

		// 获取配置文件
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// 获取组件
		et_phonenumber = (EditText) findViewById(R.id.et_phone_number);
		
		//初始化组件
		String safe_phone = sprefs.getString("safe_phone", null);
		if (safe_phone != null)
			et_phonenumber.setText(safe_phone);
	}

	/**
	 * 请选择联系人按钮的单击事件
	 * 
	 * @param v
	 */
	public void ChooseContact(View v) {
		startActivityForResult(new Intent(this, DisplayContactsActivity.class),
				1); // 跳转到展示联系人的页面
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == this.RESULT_OK) {
			// 获取返回的数据并设置给edittext中
			String name = data.getStringExtra("name");
			String phone = data.getStringExtra("phone");
			et_phonenumber.setText(phone); // 将选择的联系人的号码填充到edittext中
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
		// 设置匹配安全号码的正则表达式验证
		Pattern pattern = Pattern.compile("^(\\+)?\\d+$");

		// 安全号码的判断
		String phone = et_phonenumber.getText().toString();
		Matcher matcher = pattern.matcher(phone); // 匹配正则表达式
		if (TextUtils.isEmpty(phone)) // 判断安全号码的格式是否正确
		{
			DisplayTools.ShowToast(this, "安全号码不能为空");
			return;
		} else if (!matcher.matches()) {
			DisplayTools.ShowToast(this, "安全号码格式不正确");
			return;
		} else {
			sprefs.edit().putString("safe_phone", phone).commit(); // 将安全号码写入到配置文件中
		}
		startActivity(new Intent(this, Setup3Activity.class));
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		finish();
	}
}
