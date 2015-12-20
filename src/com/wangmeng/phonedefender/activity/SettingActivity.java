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
 * �������Ľ���
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends Activity {

	// �����ļ�
	private static SharedPreferences sprefs;

	// ��������Դ�б�
	String[] items = new String[] { "��͸��", "������", "������", "������", "ƻ����" }; // �����б�
	int[] styles = new int[] { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green }; // ��Դ�ļ��б�

	// ���ý�������
	private SettingItemLayout sil_displaylocation;
	private SettingItemClickLayout sil_locationstyle;
	private SettingItemClickLayout sil_locationsite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting);

		// ��ȡ�����ļ�
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// ��ȡ���
		sil_displaylocation = (SettingItemLayout) findViewById(R.id.layout_setting_sil_displaylocation);
		sil_locationstyle = (SettingItemClickLayout) findViewById(R.id.layout_setting_sil_locationstyle);
		sil_locationsite = (SettingItemClickLayout) findViewById(R.id.layout_setting_sil_locationsite);

		// ��ʼ�������״̬
		boolean state_callstateservice = CheckTools.CheckServiceState(this,
				"com.wangmeng.phonedefender.service.CallStateService"); // ��ȡ��ǰ�˷��������״̬
		sil_displaylocation.SetState(state_callstateservice); // ��״ֵ̬���ø��������
		
		sil_locationstyle.SetName("���ù�������ʾ��ʽ");
		int style = sprefs.getInt("location_style", 0);
		sil_locationstyle.SetState(items[style]); // ��ʼ���������״ֵ̬
		
		sil_locationsite.SetName("��������ʾλ��");
		sil_locationsite.SetState("���ö�������ʾ�����ʾλ��");

		// Ϊ������õ��������¼�
		sil_displaylocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean state = sil_displaylocation.GetState();
				state = !state;
				Intent service = new Intent(SettingActivity.this,
						CallStateService.class);
				if (state) {
					sil_displaylocation.SetState(state);
					startService(service); // �����绰״̬��������
					sprefs.edit().putBoolean("callstateservice_state", true).commit();
				} else {
					sil_displaylocation.SetState(state);
					stopService(service); // �رյ绰״̬��������
					sprefs.edit().putBoolean("callstateservice_state", false).commit();
				}
			}
		});

		sil_locationstyle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShowChooseDialog(); // չʾ�Ի���
			}
		});
		
		sil_locationsite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//��ת�����ù�������ʾ����ʾλ�����ý���
				startActivity(new Intent(SettingActivity.this, SetLocationSiteActivity.class));
			}
		});

	}

	/**
	 * �������û�ѡ���������ʽ�ĶԻ���
	 */
	protected void ShowChooseDialog() {

		// ����������dialog��builder����
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��������ʽ"); // ���öԻ���ı���
		builder.setIcon(android.R.drawable.ic_media_play); // ���öԻ����ͼ��
		int style = sprefs.getInt("location_style", 0); // ��ȡ�����ļ��б������ʽid
		builder.setSingleChoiceItems(items, style,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						sprefs.edit().putInt("location_style", which).commit(); // ���û�ѡ�����ʽ��ŵ������ļ���
						sil_locationstyle.SetState(items[which]); // �������ý�������е�״ֵ̬
						dialog.cancel(); // �û�ѡ����ɺ�رնԻ���
					}
				});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel(); // �رոöԻ���
			}
		});

		builder.show();
	}
}
