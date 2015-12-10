package com.wangmeng.phonedefender.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.tools.ConvertTools;
import com.wangmeng.phonedefender.tools.DisplayTools;

/**
 * 软件的主界面
 * 
 * @author Administrator
 * 
 */
public class HomeActivity extends Activity {

	// 配置文件
	private static SharedPreferences sprefs;

	// GridVeiw所需的资源数组
	String[] gv_item_name = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理",
			"流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };
	int[] gv_item_resources = new int[] { R.drawable.p0, R.drawable.p1,
			R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5,
			R.drawable.p6, R.drawable.p7, R.drawable.p8 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_home);
		// 获取配置文件
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// 获取显示组件
		GridView home_gv_main = (GridView) findViewById(R.id.home_gv_main);

		// 为GridView设置适配器
		home_gv_main.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = View.inflate(HomeActivity.this,
						R.layout.view_main_item_gv, null); // 获取item的布局文件
				ImageView view_item_gv_image = (ImageView) view
						.findViewById(R.id.view_item_gv_image);
				TextView view_item_gv_name = (TextView) view
						.findViewById(R.id.view_item_gv_name);
				view_item_gv_image
						.setImageResource(gv_item_resources[position]);
				view_item_gv_name.setText(gv_item_name[position]);
				return view;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return gv_item_name.length;
			}
		});
		// 为GridView设置单击事件的监听
		home_gv_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0: // 点击手机防盗按钮
					String password = sprefs.getString("password", null);
					if (TextUtils.isEmpty(password))
						SetPassword();
					else
						Login(password);
					break;
				case 8: // 点击设置中心按钮
					startActivity(new Intent(HomeActivity.this,
							SettingActivity.class));
					break;

				default:
					break;
				}
			}
		});

	}

	/**
	 * 登录手机防盗界面验证密码
	 */
	protected void Login(final String correct_password) {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		final AlertDialog dialog = builder.create(); // 创建一个dialog控件对象
		View view = View.inflate(HomeActivity.this, R.layout.dialog_home_login,
				null);

		// 获取布局文件中的组件
		final EditText et_password = (EditText) view
				.findViewById(R.id.dialog_home_login_et_password);
		Button ok = (Button) view.findViewById(R.id.dialog_home_login_bt_ok);
		Button cancel = (Button) view
				.findViewById(R.id.dialog_home_login_bt_cancel);

		// 为按钮设置监听事件
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 检查用户输入密码的有效性
				String password = et_password.getText().toString();
				if (TextUtils.isEmpty(password)) // 密码为空
					DisplayTools.ShowToast(HomeActivity.this, "密码不能为空");
				else if (!correct_password.equals(ConvertTools.MD5(password)))
					DisplayTools.ShowToast(HomeActivity.this,
							"密码错误, 错误超过十次手机会自爆, 请提好裤子操作");
				else {
					// 退出对话框并进入手机防盗主界面
					dialog.dismiss();
					dialog.cancel();
					startActivity(new Intent(HomeActivity.this, Option0Activity.class)); //跳转到手机防盗的主界面

				}
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 退出该对话框
				dialog.dismiss();
				dialog.cancel();
			}
		});
		dialog.setView(view);
		dialog.show(); // 显示该对话框
	}

	/**
	 * 为手机防盗按钮设置密码
	 */
	protected void SetPassword() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		final AlertDialog dialog = builder.create(); // 创建一个dialog控件对象
		View view = View.inflate(HomeActivity.this,
				R.layout.dialog_home_setpassword, null);

		// 获取布局文件中的组件
		final EditText dialog_home_et_password = (EditText) view
				.findViewById(R.id.dialog_home_et_password);
		final EditText dialog_home_et_repassword = (EditText) view
				.findViewById(R.id.dialog_home_et_repassword);
		Button dialog_home_bt_ok = (Button) view
				.findViewById(R.id.dialog_home_bt_ok);
		Button dialog_home_bt_cancel = (Button) view
				.findViewById(R.id.dialog_home_bt_cancel);

		// 为按钮设置监听事件
		dialog_home_bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 检查用户设置密码的有效性
				String password = dialog_home_et_password.getText().toString();
				String repassword = dialog_home_et_repassword.getText()
						.toString();
				if (TextUtils.isEmpty(password)
						|| TextUtils.isEmpty(repassword)) // 密码为空
					DisplayTools.ShowToast(HomeActivity.this, "密码不能为空");
				else if (!password.equals(repassword)) // 密码两次输入不一致
					DisplayTools.ShowToast(HomeActivity.this, "两次密码输入不一致");
				else {
					// 将设置好的密码写入到配置文件中
					password = ConvertTools.MD5(password); // 将密码MD5加密
					sprefs.edit().putString("password", password).commit(); // 一定要记得commit,
																			// 否则后果很严重,
																			// 你会很长时间找不到错误在哪里
					// 退出对话框并显示登录界面
					dialog.dismiss();
					dialog.cancel();
					DisplayTools.ShowToast(HomeActivity.this, "密码设置成功");
					Login(password);
				}
			}
		});
		dialog_home_bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 退出该对话框
				dialog.dismiss();
				dialog.cancel();
			}
		});
		dialog.setView(view);
		dialog.show(); // 显示该对话框

	}
}
