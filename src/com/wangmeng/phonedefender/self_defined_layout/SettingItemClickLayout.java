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
 * �Զ�����������Ľ������Ŀ������
 * 
 * @author Administrator
 * 
 */
public class SettingItemClickLayout extends RelativeLayout {

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
	private String id = String.valueOf(this.getId());

	// �����CheckBox�����״ֵ̬
	private boolean state;
	


	public SettingItemClickLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		InitLayout(context);
	}

	public SettingItemClickLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// ��ȡ�����ļ�
		sprefs = getContext().getSharedPreferences("sprefs",
				getContext().MODE_PRIVATE);
		// ��ȡ�Զ������Ե�ֵ
		name = attrs.getAttributeValue(NAMESPACE, "name");
		InitLayout(context);
	}

	public SettingItemClickLayout(Context context) {
		super(context);
		// ��ʼ�������ļ�
		InitLayout(context);
	}

	/**
	 * ��ʼ������
	 */
	private void InitLayout(final Context context) {
		// �������ļ���䵽����Ķ�����
		View.inflate(this.getContext(), R.layout.layout_setting_item_click, this);// ���øò��ֵ�viewgroupΪthis,
																			// ���ǽ��˲�����䵽����Ķ�����
		// ��ȡ�����ļ��е����
		setting_item_tv_name = (TextView) findViewById(R.id.setting_item_tv_name);
		setting_item_tv_state = (TextView) findViewById(R.id.setting_item_tv_state);
	
	}
	
	/**
	 * ����״̬��Ϣ�ĺ���
	 * @param ״̬��Ϣ 
	 */
	public void SetState(String state)
	{
		setting_item_tv_state.setText(state);
	}
	
	/**
	 * �������ֵĺ���
	 * @param name ����
	 */
	public void SetName(String name)
	{
		setting_item_tv_name.setText(name);
	}
}
