package com.wangmeng.phonedefender.self_defined_layout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;
/*
 * �������ù�������ʾ��λ�õ����ý���
 */
public class SetLocationSiteActivity extends Activity {
	
	int start_x = 0;
	int start_y = 0;
	
	//�����ļ��е����
	private TextView tv_top;
	private TextView tv_buttom;
	private TextView tv_site;
	
	//�����ļ�
	private static SharedPreferences sprefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_set_location_site);
		
		//��ȡ�����ļ�
		sprefs = this.getSharedPreferences("sprefs", MODE_PRIVATE);
		
		//��ȡ�ļ��е����
		tv_top = (TextView) findViewById(R.id.layout_set_location_site_tv_top);
		tv_buttom = (TextView) findViewById(R.id.layout_set_location_site_tv_buttom);
		tv_site = (TextView) findViewById(R.id.layout_set_location_site_tv_site);
		
		//�����ļ��ĳ�ʼֵ
		tv_buttom.setVisibility(TextView.GONE);
		
		int location_l = sprefs.getInt("location_l", 0);
		int location_t = sprefs.getInt("location_t", 0);
		System.out.println("��ʼ��:" + location_l + ", " + location_t);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) tv_site.getLayoutParams(); //��ȡ������Ĳ�����
		params.leftMargin = location_l; //����topMargin����
		params.topMargin = location_t; // ����bottomMargin����
		tv_site.setLayoutParams(params); // ���������ø�tv_site���
		 
		//Ϊ������ü����¼�
		tv_site.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction(); //��ȡ�¼�������
			
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					start_x = (int) event.getRawX();
					start_y = (int) event.getRawY();
					break;
					
				case MotionEvent.ACTION_MOVE:
					//���û��ƶ����ʱ��ȡ�ƶ������������
					int end_x = (int) event.getRawX();
					int end_y = (int) event.getRawY();
					
					//����move����ʱx�����y�����ƶ��ľ���
					int dx = end_x - start_x;
					int dy = end_y - start_y;
					
					//���������λ�õ�����
					int l = tv_site.getLeft() + dx;
					int r = tv_site.getRight() + dx;
					int t = tv_site.getTop() + dy;
					int b = tv_site.getBottom() + dy;
					
					//���������λ��
					tv_site.layout(l, t, r, b);
					
					//������������ֵ
					start_x = (int) event.getRawX();
					start_y = (int) event.getRawY();
					
					//�����º��ֵ���浽�����ļ���
					Editor editor = sprefs.edit();
					editor.putInt("location_l", l);
					editor.putInt("location_t", t);
					editor.commit();
					
					System.out.println(l + ", " + t);
					break;

				case MotionEvent.ACTION_UP:
					
					break;
				default:
					break;
				}
				return true;
			}
		});
	}
}
