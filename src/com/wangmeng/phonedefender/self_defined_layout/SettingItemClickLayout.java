package com.wangmeng.phonedefender.self_defined_layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;

/**
 * 自定义的设置中心界面的条目布局类
 * 
 * @author Administrator
 * 
 */
public class SettingItemClickLayout extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.wangmeng.phonedefender";// 自定义属性的命名空间,
																											// 获取自定义属性的值的时候使用
	// 获取配置文件
	private static SharedPreferences sprefs;

	// 布局文件中的组件
	private TextView setting_item_tv_name;
	private TextView setting_item_tv_state;
	private CheckBox setting_item_cb_switch;

	// 自定义属性
	private String name;
	private String id = String.valueOf(this.getId());

	// 组件中CheckBox组件的状态值
	private boolean state;
	


	public SettingItemClickLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		InitLayout(context);
	}

	public SettingItemClickLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取配置文件
		sprefs = getContext().getSharedPreferences("sprefs",
				getContext().MODE_PRIVATE);
		// 获取自定义属性的值
		name = attrs.getAttributeValue(NAMESPACE, "name");
		InitLayout(context);
	}

	public SettingItemClickLayout(Context context) {
		super(context);
		// 初始话布局文件
		InitLayout(context);
	}

	/**
	 * 初始化布局
	 */
	private void InitLayout(final Context context) {
		// 将布局文件填充到该类的对象中
		View.inflate(this.getContext(), R.layout.layout_setting_item_click, this);// 设置该布局的viewgroup为this,
																			// 就是将此布局填充到该类的对象中
		// 获取布局文件中的组件
		setting_item_tv_name = (TextView) findViewById(R.id.setting_item_tv_name);
		setting_item_tv_state = (TextView) findViewById(R.id.setting_item_tv_state);
	
	}
	
	/**
	 * 设置状态信息的函数
	 * @param 状态信息 
	 */
	public void SetState(String state)
	{
		setting_item_tv_state.setText(state);
	}
	
	/**
	 * 设置名字的函数
	 * @param name 名字
	 */
	public void SetName(String name)
	{
		setting_item_tv_name.setText(name);
	}
}
