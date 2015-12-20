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
 * �Զ�����������Ľ������Ŀ������
 * 
 * @author Administrator
 * 
 */
public class SettingItemLayout extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.wangmeng.phonedefender";// �Զ������Ե������ռ�,
																											// ��ȡ�Զ������Ե�ֵ��ʱ��ʹ��
	// ��ȡ�����ļ�
	private static SharedPreferences sprefs;

	// �����ļ��е����
	private TextView setting_item_tv_name;
	private TextView setting_item_tv_state;
	private CheckBox setting_item_cb_switch;

	// �Զ�������
	private String name;
	private String state_on;
	private String state_off;
	private String id = String.valueOf(this.getId());

	// �����CheckBox�����״ֵ̬
	private boolean state;

	public SettingItemLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		InitLayout();
	}

	public SettingItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// ��ȡ�����ļ�
		sprefs = getContext().getSharedPreferences("sprefs",
				getContext().MODE_PRIVATE);
		// ��ȡ�Զ������Ե�ֵ
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
	 * ��ʼ������
	 */
	private void InitLayout() {
		// �������ļ���䵽����Ķ�����
		View.inflate(this.getContext(), R.layout.layout_setting_item, this);// ���øò��ֵ�viewgroupΪthis,
																			// ���ǽ��˲�����䵽����Ķ�����
		// ��ȡ�����ļ��е����
		setting_item_tv_name = (TextView) findViewById(R.id.setting_item_tv_name);
		setting_item_tv_state = (TextView) findViewById(R.id.setting_item_tv_state);
		setting_item_cb_switch = (CheckBox) findViewById(R.id.setting_item_cb_switch);

		// Ϊ������ó�ʼֵ
		state = sprefs.getBoolean(id, true); // �������ļ��л�ȡ�Զ���������,
														// Ĭ����true
		setting_item_tv_name.setText(name);
		SetState(state);

		// Ϊ���Զ���ؼ����õ����ļ����¼�
		setting_item_cb_switch.setClickable(false); // ����checkboxΪ���ɵ�����Ҳ��ɻ�ý���,
													// �����ĵ����¼�ͳһ�������ռ䴦��
		setting_item_cb_switch.setFocusable(false);
		setting_item_cb_switch.setFocusableInTouchMode(false);
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (state) {
					state = false; // ���µ�ǰ��checkbox��״̬
				} else {
					state = true; // ���µ�ǰ��checkbox��״̬
				}
				SetState(state); // ����״̬˵���ַ���
			}
		});
	}

	/**
	 * ����״̬˵���ַ����͸���checkbox��״̬, ��д�뵽�����ļ���
	 * 
	 * @param state
	 */
	public void SetState(boolean state) {
		if (state) {
			this.state = state;
			setting_item_tv_state.setText(state_on);
			setting_item_cb_switch.setChecked(state);
			sprefs.edit().putBoolean(id, state).commit(); // �����µ����û�д�������ļ���
		} else {
			this.state = state;
			setting_item_tv_state.setText(state_off);
			setting_item_cb_switch.setChecked(state);
			sprefs.edit().putBoolean(id, state).commit(); // �����µ����û�д�������ļ���
		}
	}
	
	/**
	 * ���ص�ǰ��״̬��Ϣ
	 * @return ��ǰ��״̬��Ϣ
	 */
	public boolean GetState()
	{
		return state;
	}
}
