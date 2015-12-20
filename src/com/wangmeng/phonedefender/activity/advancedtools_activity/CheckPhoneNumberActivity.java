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
 * 归属地查询界面
 * 
 * @author Administrator
 * 
 */
public class CheckPhoneNumberActivity extends Activity {

	// 布局文件中的组件
	private EditText et_phonenum;
	
	private TextView tv_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_checkphonenumber);

		// 获取布局文件中的组件
		et_phonenum = (EditText) findViewById(R.id.checkphonenumber_et_phonenum);
		tv_result = (TextView) findViewById(R.id.checkphonenumber_tv_result);
		
		//初始化组件
		et_phonenum.setText("");
		
		//为et_phonenum添加textchanged事件的监听
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
	 * 查询按钮的点击事件
	 * 
	 * @param v
	 */
	public void checkNow(View v) {
		String phonenum = et_phonenum.getText().toString();
		if (TextUtils.isEmpty(phonenum)) // 输入的号码是空
		{
			//如果电话号码是空就进行查询, 发生edittext抖动效果
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake_effect);
			et_phonenum.startAnimation(animation);
			vibrate(); //手机震动
		}
		Check(phonenum); //查询号码
	}
	
	/**
	 * 实现手机的震动效果
	 */
	public void vibrate()
	{
		Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		//vibrator.vibrate(500);
		vibrator.vibrate(new long[]{100, 200, 100, 200}, -1);
	}
	
	/**
	 * 通过数据库查询给定号码的归属地并显示到布局文件的组件中
	 * @param phonenum
	 */
	public void Check(String phonenum) {
		int length = phonenum.length(); //获取号码的长度
		switch (length) {
		case 0:
			tv_result.setText("");
			break;
		case 1:
		case 2:
			tv_result.setText("号码过短");
			break;
		case 3:
			tv_result.setText("公共服务号码");
			break;
		case 4:
			tv_result.setText("模拟器号码");
			break;
		case 5:
			tv_result.setText("客服号码");
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
				tv_result.setText("此号码不存在");
			}
			break;
		default:
			tv_result.setText("输入号码过长");
			break;
		}
		

	}
}
