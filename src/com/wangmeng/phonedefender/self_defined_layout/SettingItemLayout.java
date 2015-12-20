package com.wangmeng.phonedefender.self_defined_layout;

import com.wangmeng.phonedefender.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义的设置中心界面的条目布局类
 * 
 * @author Administrator
 * 
 */
public class SettingItemLayout extends RelativeLayout {

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
	private String state_on;
	private String state_off;
	private String id = String.valueOf(this.getId());

	// 组件中CheckBox组件的状态值
	private boolean state;

	public SettingItemLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		InitLayout();
	}

	public SettingItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取配置文件
		sprefs = getContext().getSharedPreferences("sprefs",
				getContext().MODE_PRIVATE);
		// 获取自定义属性的值
		name = attrs.getAttributeValue(NAMESPACE, "name");
		state_on = attrs.getAttributeValue(NAMESPACE, "state_on");
		state_off = attrs.getAttributeValue(NAMESPACE, "state_off");
		InitLayout();
	}

	public SettingItemLayout(Context context) {
		super(context);
		InitLayout();
	}

	/**
	 * 初始化布局
	 */
	private void InitLayout() {
		// 将布局文件填充到该类的对象中
		View.inflate(this.getContext(), R.layout.layout_setting_item, this);// 设置该布局的viewgroup为this,
																			// 就是将此布局填充到该类的对象中
		// 获取布局文件中的组件
		setting_item_tv_name = (TextView) findViewById(R.id.setting_item_tv_name);
		setting_item_tv_state = (TextView) findViewById(R.id.setting_item_tv_state);
		setting_item_cb_switch = (CheckBox) findViewById(R.id.setting_item_cb_switch);

		// 为组件设置初始值
		state = sprefs.getBoolean(id, true); // 从配置文件中获取自动更新配置,
														// 默认是true
		setting_item_tv_name.setText(name);
		SetState(state);

		// 为该自定义控件设置单击的监听事件
		setting_item_cb_switch.setClickable(false); // 设置checkbox为不可点击并且不可获得焦点,
													// 将它的单击事件统一交给父空间处理
		setting_item_cb_switch.setFocusable(false);
		setting_item_cb_switch.setFocusableInTouchMode(false);
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (state) {
					state = false; // 更新当前的checkbox的状态
				} else {
					state = true; // 更新当前的checkbox的状态
				}
				SetState(state); // 更新状态说明字符串
			}
		});
	}

	/**
	 * 设置状态说明字符串和更新checkbox的状态, 并写入到配置文件中
	 * 
	 * @param state
	 */
	public void SetState(boolean state) {
		if (state) {
			this.state = state;
			setting_item_tv_state.setText(state_on);
			setting_item_cb_switch.setChecked(state);
			sprefs.edit().putBoolean(id, state).commit(); // 将更新的配置回写到配置文件中
		} else {
			this.state = state;
			setting_item_tv_state.setText(state_off);
			setting_item_cb_switch.setChecked(state);
			sprefs.edit().putBoolean(id, state).commit(); // 将更新的配置回写到配置文件中
		}
	}
	
	/**
	 * 返回当前的状态信息
	 * @return 当前的状态信息
	 */
	public boolean GetState()
	{
		return state;
	}
}
