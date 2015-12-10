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
 * �����������
 * 
 * @author Administrator
 * 
 */
public class HomeActivity extends Activity {

	// �����ļ�
	private static SharedPreferences sprefs;

	// GridVeiw�������Դ����
	String[] gv_item_name = new String[] { "�ֻ�����", "ͨ����ʿ", "�������", "���̹���",
			"����ͳ��", "�ֻ�ɱ��", "��������", "�߼�����", "��������" };
	int[] gv_item_resources = new int[] { R.drawable.p0, R.drawable.p1,
			R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5,
			R.drawable.p6, R.drawable.p7, R.drawable.p8 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_home);
		// ��ȡ�����ļ�
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// ��ȡ��ʾ���
		GridView home_gv_main = (GridView) findViewById(R.id.home_gv_main);

		// ΪGridView����������
		home_gv_main.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = View.inflate(HomeActivity.this,
						R.layout.view_main_item_gv, null); // ��ȡitem�Ĳ����ļ�
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
		// ΪGridView���õ����¼��ļ���
		home_gv_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0: // ����ֻ�������ť
					String password = sprefs.getString("password", null);
					if (TextUtils.isEmpty(password))
						SetPassword();
					else
						Login(password);
					break;
				case 8: // ����������İ�ť
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
	 * ��¼�ֻ�����������֤����
	 */
	protected void Login(final String correct_password) {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		final AlertDialog dialog = builder.create(); // ����һ��dialog�ؼ�����
		View view = View.inflate(HomeActivity.this, R.layout.dialog_home_login,
				null);

		// ��ȡ�����ļ��е����
		final EditText et_password = (EditText) view
				.findViewById(R.id.dialog_home_login_et_password);
		Button ok = (Button) view.findViewById(R.id.dialog_home_login_bt_ok);
		Button cancel = (Button) view
				.findViewById(R.id.dialog_home_login_bt_cancel);

		// Ϊ��ť���ü����¼�
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ����û������������Ч��
				String password = et_password.getText().toString();
				if (TextUtils.isEmpty(password)) // ����Ϊ��
					DisplayTools.ShowToast(HomeActivity.this, "���벻��Ϊ��");
				else if (!correct_password.equals(ConvertTools.MD5(password)))
					DisplayTools.ShowToast(HomeActivity.this,
							"�������, ���󳬹�ʮ���ֻ����Ա�, ����ÿ��Ӳ���");
				else {
					// �˳��Ի��򲢽����ֻ�����������
					dialog.dismiss();
					dialog.cancel();
					startActivity(new Intent(HomeActivity.this, Option0Activity.class)); //��ת���ֻ�������������

				}
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �˳��öԻ���
				dialog.dismiss();
				dialog.cancel();
			}
		});
		dialog.setView(view);
		dialog.show(); // ��ʾ�öԻ���
	}

	/**
	 * Ϊ�ֻ�������ť��������
	 */
	protected void SetPassword() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		final AlertDialog dialog = builder.create(); // ����һ��dialog�ؼ�����
		View view = View.inflate(HomeActivity.this,
				R.layout.dialog_home_setpassword, null);

		// ��ȡ�����ļ��е����
		final EditText dialog_home_et_password = (EditText) view
				.findViewById(R.id.dialog_home_et_password);
		final EditText dialog_home_et_repassword = (EditText) view
				.findViewById(R.id.dialog_home_et_repassword);
		Button dialog_home_bt_ok = (Button) view
				.findViewById(R.id.dialog_home_bt_ok);
		Button dialog_home_bt_cancel = (Button) view
				.findViewById(R.id.dialog_home_bt_cancel);

		// Ϊ��ť���ü����¼�
		dialog_home_bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ����û������������Ч��
				String password = dialog_home_et_password.getText().toString();
				String repassword = dialog_home_et_repassword.getText()
						.toString();
				if (TextUtils.isEmpty(password)
						|| TextUtils.isEmpty(repassword)) // ����Ϊ��
					DisplayTools.ShowToast(HomeActivity.this, "���벻��Ϊ��");
				else if (!password.equals(repassword)) // �����������벻һ��
					DisplayTools.ShowToast(HomeActivity.this, "�����������벻һ��");
				else {
					// �����úõ�����д�뵽�����ļ���
					password = ConvertTools.MD5(password); // ������MD5����
					sprefs.edit().putString("password", password).commit(); // һ��Ҫ�ǵ�commit,
																			// ������������,
																			// ���ܳ�ʱ���Ҳ�������������
					// �˳��Ի�����ʾ��¼����
					dialog.dismiss();
					dialog.cancel();
					DisplayTools.ShowToast(HomeActivity.this, "�������óɹ�");
					Login(password);
				}
			}
		});
		dialog_home_bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �˳��öԻ���
				dialog.dismiss();
				dialog.cancel();
			}
		});
		dialog.setView(view);
		dialog.show(); // ��ʾ�öԻ���

	}
}
