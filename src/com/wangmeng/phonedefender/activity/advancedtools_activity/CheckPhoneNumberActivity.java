package com.wangmeng.phonedefender.activity.advancedtools_activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.tools.CheckTools;
import com.wangmeng.phonedefender.tools.DisplayTools;

/**
 * �����ز�ѯ����
 * 
 * @author Administrator
 * 
 */
public class CheckPhoneNumberActivity extends Activity {

	// �����ļ��е����
	private EditText et_phonenum;
	
	private TextView tv_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_checkphonenumber);

		// ��ȡ�����ļ��е����
		et_phonenum = (EditText) findViewById(R.id.checkphonenumber_et_phonenum);
		tv_result = (TextView) findViewById(R.id.checkphonenumber_tv_result);
		
		//��ʼ�����
		et_phonenum.setText("");
		
		//Ϊet_phonenum���textchanged�¼��ļ���
		et_phonenum.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Check(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 * ��ѯ��ť�ĵ���¼�
	 * 
	 * @param v
	 */
	public void checkNow(View v) {
		String phonenum = et_phonenum.getText().toString();
		if (TextUtils.isEmpty(phonenum)) // ����ĺ����ǿ�
		{
			//����绰�����ǿվͽ��в�ѯ, ����edittext����Ч��
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake_effect);
			et_phonenum.startAnimation(animation);
			vibrate(); //�ֻ���
		}
		Check(phonenum); //��ѯ����
	}
	
	/**
	 * ʵ���ֻ�����Ч��
	 */
	public void vibrate()
	{
		Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		//vibrator.vibrate(500);
		vibrator.vibrate(new long[]{100, 200, 100, 200}, -1);
	}
	
	/**
	 * ͨ�����ݿ��ѯ��������Ĺ����ز���ʾ�������ļ��������
	 * @param phonenum
	 */
	public void Check(String phonenum) {
		int length = phonenum.length(); //��ȡ����ĳ���
		switch (length) {
		case 0:
			tv_result.setText("");
			break;
		case 1:
		case 2:
			tv_result.setText("�������");
			break;
		case 3:
			tv_result.setText("�����������");
			break;
		case 4:
			tv_result.setText("ģ��������");
			break;
		case 5:
			tv_result.setText("�ͷ�����");
			break;
		case 6:
			break;
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
			String location = CheckTools.CheckPhoneNumberLocation(phonenum);
			if (location != null)
				tv_result.setText(location);
			else 
			{
				tv_result.setText("�˺��벻����");
			}
			break;
		default:
			tv_result.setText("����������");
			break;
		}
		

	}
}
