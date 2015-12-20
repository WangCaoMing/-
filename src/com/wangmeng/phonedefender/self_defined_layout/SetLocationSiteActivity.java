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
 * 用于设置归属地提示框位置的设置界面
 */
public class SetLocationSiteActivity extends Activity {
	
	int start_x = 0;
	int start_y = 0;
	
	//布局文件中的组件
	private TextView tv_top;
	private TextView tv_buttom;
	private TextView tv_site;
	
	//配置文件
	private static SharedPreferences sprefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_set_location_site);
		
		//获取配置文件
		sprefs = this.getSharedPreferences("sprefs", MODE_PRIVATE);
		
		//获取文件中的组件
		tv_top = (TextView) findViewById(R.id.layout_set_location_site_tv_top);
		tv_buttom = (TextView) findViewById(R.id.layout_set_location_site_tv_buttom);
		tv_site = (TextView) findViewById(R.id.layout_set_location_site_tv_site);
		
		//设置文件的初始值
		tv_buttom.setVisibility(TextView.GONE);
		
		int location_l = sprefs.getInt("location_l", 0);
		int location_t = sprefs.getInt("location_t", 0);
		System.out.println("初始化:" + location_l + ", " + location_t);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) tv_site.getLayoutParams(); //获取该组件的参数表
		params.leftMargin = location_l; //设置topMargin参数
		params.topMargin = location_t; // 设置bottomMargin参数
		tv_site.setLayoutParams(params); // 将参数设置给tv_site组件
		 
		//为组件设置监听事件
		tv_site.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction(); //获取事件的类型
			
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					start_x = (int) event.getRawX();
					start_y = (int) event.getRawY();
					break;
					
				case MotionEvent.ACTION_MOVE:
					//当用户移动组件时获取移动后组件的坐标
					int end_x = (int) event.getRawX();
					int end_y = (int) event.getRawY();
					
					//计算move结束时x方向和y方向移动的距离
					int dx = end_x - start_x;
					int dy = end_y - start_y;
					
					//计算组件新位置的数据
					int l = tv_site.getLeft() + dx;
					int r = tv_site.getRight() + dx;
					int t = tv_site.getTop() + dy;
					int b = tv_site.getBottom() + dy;
					
					//更新组件的位置
					tv_site.layout(l, t, r, b);
					
					//更新起点坐标的值
					start_x = (int) event.getRawX();
					start_y = (int) event.getRawY();
					
					//将更新后的值保存到配置文件中
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
