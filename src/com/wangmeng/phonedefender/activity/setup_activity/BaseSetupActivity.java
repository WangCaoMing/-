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
		//为该activity创建一个手势识别器
		gestureDetector = new GestureDetector(this,
				new SimpleOnGestureListener() {
					//重写该监听器的onfling方法, 监听手势滑动事件
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						
							if (Math.abs(e2.getRawY() - e1.getRawY()) < 400) //手指滑动轨迹斜的不太狠
							{
								if (e2.getRawX() - e1.getRawX() > 50) //如果手指的起落点在水平方向上距离大于100
								{
									doPrior(); //跳转到上一个页面
								}
								else if (e1.getRawX() - e2.getRawX() > 50)
								{
									doNext(); //跳转到下一个页面
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
		
		//将activity的touch时间先交与手势识别器处理
		gestureDetector.onTouchEvent(event); //很重要
		return super.onTouchEvent(event);
	}
	
	/**
	 * 跳转到上一个页面的操作
	 */
	public abstract void doPrior();
	
	/**
	 * 跳转到下一个页面的操作
	 */
	public abstract void doNext();
	
	/**
	 * 下一步按钮的点击事件
	 * @param v
	 */
	public void prior(View v)
	{
		doPrior();
	}
	
	/**
	 * 下一步按钮的点击事件
	 * @param v
	 */
	public void next(View v)
	{
		doNext();
	}

}
