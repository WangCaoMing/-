package com.wangmeng.phonedefender.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.activity.setup_activity.Setup0Activity;
import com.wangmeng.phonedefender.receiver.MyDeviceAdminReceiver;
import com.wangmeng.phonedefender.service.LocationService;
import com.wangmeng.phonedefender.tools.DisplayTools;

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
		
		//获取布局文件中的组件
		TextView tv_safenumber = (TextView) findViewById(R.id.option0_tv_safenumber);
		ImageView iv_openstate = (ImageView) findViewById(R.id.option0_iv_openstate);
		
		//从配置文件中读取数据并设置相应组件的状态
		String phone = sprefs.getString("safe_phone", null);
		boolean state = sprefs.getBoolean("safe_start", false);
		tv_safenumber.setText(phone); //显示安全号码
		if (state) //显示防盗保护的开启状态
			iv_openstate.setImageResource(R.drawable.lock);
		else
			iv_openstate.setImageResource(R.drawable.unlock);
		
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
	 * 激活设备管理器
	 * @param componentName 跳转到激活界面所需要的组件名
	 */
	public void ActiveDeviceAdmin(View v) {
		
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		ComponentName componentName = new ComponentName(this,
				MyDeviceAdminReceiver.class); //设备管理器接受者组件名
		if (!devicePolicyManager.isAdminActive(componentName))
		{
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "设备管理器需要激活该软件才能正常工作");
			startActivity(intent);
		}
		else
		{
			DisplayTools.ShowToast(this, "设备管理器已激活, 无需重复激活");
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
