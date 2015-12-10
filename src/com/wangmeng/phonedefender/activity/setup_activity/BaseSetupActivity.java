package com.wangmeng.phonedefender.activity.setup_activity;

import com.wangmeng.phonedefender.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

public abstract class BaseSetupActivity extends Activity {

	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Ϊ��activity����һ������ʶ����
		gestureDetector = new GestureDetector(this,
				new SimpleOnGestureListener() {
					//��д�ü�������onfling����, �������ƻ����¼�
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						
							if (Math.abs(e2.getRawY() - e1.getRawY()) < 400) //��ָ�����켣б�Ĳ�̫��
							{
								if (e2.getRawX() - e1.getRawX() > 50) //�����ָ���������ˮƽ�����Ͼ������100
								{
									doPrior(); //��ת����һ��ҳ��
								}
								else if (e1.getRawX() - e2.getRawX() > 50)
								{
									doNext(); //��ת����һ��ҳ��
								}
								else 
									return false;
							}
						
						return false;
					}
				});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		//��activity��touchʱ���Ƚ�������ʶ��������
		gestureDetector.onTouchEvent(event); //����Ҫ
		return super.onTouchEvent(event);
	}
	
	/**
	 * ��ת����һ��ҳ��Ĳ���
	 */
	public abstract void doPrior();
	
	/**
	 * ��ת����һ��ҳ��Ĳ���
	 */
	public abstract void doNext();
	
	/**
	 * ��һ����ť�ĵ���¼�
	 * @param v
	 */
	public void prior(View v)
	{
		doPrior();
	}
	
	/**
	 * ��һ����ť�ĵ���¼�
	 * @param v
	 */
	public void next(View v)
	{
		doNext();
	}

}
