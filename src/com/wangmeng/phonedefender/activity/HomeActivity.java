package com.wangmeng.phonedefender.activity;

import com.wangmeng.phonedefender.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 软件的主界面
 * 
 * @author Administrator
 * 
 */
public class HomeActivity extends Activity {
	String[] gv_item_name = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理",
			"流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };
	int[] gv_item_resources = new int[] { R.drawable.p0, R.drawable.p1,
			R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5,
			R.drawable.p6, R.drawable.p7, R.drawable.p8 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_home);
		// 获取显示组件
		GridView home_gv_main = (GridView) findViewById(R.id.home_gv_main);

		// 为GridView设置适配器
		home_gv_main.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = View.inflate(HomeActivity.this, R.layout.view_main_item_gv, null); //获取item的布局文件
				ImageView view_item_gv_image = (ImageView) view.findViewById(R.id.view_item_gv_image);
				TextView view_item_gv_name = (TextView) view.findViewById(R.id.view_item_gv_name);
				view_item_gv_image.setImageResource(gv_item_resources[position]);
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

	}
}
