package com.wangmeng.phonedefender.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.self_defined_layout.SetLocationSiteActivity;
import com.wangmeng.phonedefender.self_defined_layout.SettingItemClickLayout;
import com.wangmeng.phonedefender.self_defined_layout.SettingItemLayout;
import com.wangmeng.phonedefender.service.CallStateService;
import com.wangmeng.phonedefender.tools.CheckTools;

/**
 * 设置中心界面
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends Activity {

	// 配置文件
	private static SharedPreferences sprefs;

	// 归属地资源列表
	String[] items = new String[] { "半透明", "活力橙", "卫视蓝", "金属灰", "苹果绿" }; // 名称列表
	int[] styles = new int[] { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green }; // 资源文件列表

	// 设置界面的组件
	private SettingItemLayout sil_displaylocation;
	private SettingItemClickLayout sil_locationstyle;
	private SettingItemClickLayout sil_locationsite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting);

		// 获取配置文件
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// 获取组件
		sil_displaylocation = (SettingItemLayout) findViewById(R.id.layout_setting_sil_displaylocation);
		sil_locationstyle = (SettingItemClickLayout) findViewById(R.id.layout_setting_sil_locationstyle);
		sil_locationsite = (SettingItemClickLayout) findViewById(R.id.layout_setting_sil_locationsite);

		// 初始化组件的状态
		boolean state_callstateservice = CheckTools.CheckServiceState(this,
				"com.wangmeng.phonedefender.service.CallStateService"); // 获取当前此服务的运行状态
		sil_displaylocation.SetState(state_callstateservice); // 将状态值设置给管理组件
		
		sil_locationstyle.SetName("设置归属地显示样式");
		int style = sprefs.getInt("location_style", 0);
		sil_locationstyle.SetState(items[style]); // 初始化该组件的状态值
		
		sil_locationsite.SetName("归属地显示位置");
		sil_locationsite.SetState("设置对属地提示框的显示位置");

		// 为组件设置单击监听事件
		sil_displaylocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean state = sil_displaylocation.GetState();
				state = !state;
				Intent service = new Intent(SettingActivity.this,
						CallStateService.class);
				if (state) {
					sil_displaylocation.SetState(state);
					startService(service); // 开启电话状态监听服务
					sprefs.edit().putBoolean("callstateservice_state", true).commit();
				} else {
					sil_displaylocation.SetState(state);
					stopService(service); // 关闭电话状态监听服务
					sprefs.edit().putBoolean("callstateservice_state", false).commit();
				}
			}
		});

		sil_locationstyle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShowChooseDialog(); // 展示对话框
			}
		});
		
		sil_locationsite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转到设置归属地提示框显示位置设置界面
				startActivity(new Intent(SettingActivity.this, SetLocationSiteActivity.class));
			}
		});

	}

	/**
	 * 创建供用户选择归属地样式的对话框
	 */
	protected void ShowChooseDialog() {

		// 创建用生成dialog的builder对象
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("归属地样式"); // 设置对话框的标题
		builder.setIcon(android.R.drawable.ic_media_play); // 设置对话框的图标
		int style = sprefs.getInt("location_style", 0); // 获取配置文件中保存的样式id
		builder.setSingleChoiceItems(items, style,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						sprefs.edit().putInt("location_style", which).commit(); // 将用户选择的样式存放到配置文件中
						sil_locationstyle.SetState(items[which]); // 更新设置界面组件中的状态值
						dialog.cancel(); // 用户选择完成后关闭对话框
					}
				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel(); // 关闭该对话框
			}
		});

		builder.show();
	}
}
