package com.wangmeng.phonedefender.self_defined_layout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wangmeng.phonedefender.R;

/*
 * 用于设置归属地提示框位置的设置界面
 */
public class SetLocationSiteActivity extends Activity {

    int start_x = 0;
    int start_y = 0;

    // 布局文件中的组件
    private TextView tv_top;
    private TextView tv_buttom;
    private TextView tv_site;
    
    //双击居中功能所需要的资源
    final long[] mHits = new long[2];// 数组长度表示要点击的次数
    
    // 配置文件
    private static SharedPreferences sprefs;
    private int window_width;
    private int window_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_location_site);
        
        // 获取配置文件
        sprefs = this.getSharedPreferences("sprefs", MODE_PRIVATE);

        // 获取文件中的组件
        tv_top = (TextView) findViewById(R.id.layout_set_location_site_tv_top);
        tv_buttom = (TextView) findViewById(R.id.layout_set_location_site_tv_buttom);
        tv_site = (TextView) findViewById(R.id.layout_set_location_site_tv_site);

        // 设置文件的初始值
        tv_buttom.setVisibility(TextView.GONE);
        
        //获取屏幕的宽度和高度
        window_width = SetLocationSiteActivity.this
                .getWindowManager().getDefaultDisplay().getWidth();
        window_height = SetLocationSiteActivity.this
                .getWindowManager().getDefaultDisplay().getHeight();
       
        
        int location_l = sprefs.getInt("location_l", 0);
        int location_t = sprefs.getInt("location_t", 0);
        if (location_t < window_height / 2) // 设置tv_top 和 tv_buttom 的显示状态
        {
            // tv_top隐藏, tv_buttom显示
            tv_top.setVisibility(TextView.INVISIBLE);
            tv_buttom.setVisibility(TextView.VISIBLE);
        } else {
            // tv_top显示, tv_buttom隐藏
            tv_top.setVisibility(TextView.VISIBLE);
            tv_buttom.setVisibility(TextView.INVISIBLE);
        }
        // 设置tv_site的状态
        System.out.println("初始化:" + location_l + ", " + location_t);
        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) tv_site
                .getLayoutParams(); // 获取该组件的参数表
        params.leftMargin = location_l; // 设置topMargin参数
        params.topMargin = location_t; // 设置bottomMargin参数
        tv_site.setLayoutParams(params); // 将参数设置给tv_site组件
        
        // 为组件设置监听事件
        tv_site.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction(); // 获取事件的类型

                switch (action) {
                case MotionEvent.ACTION_DOWN:
                    start_x = (int) event.getRawX();
                    start_y = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    // 当用户移动组件时获取移动后组件的坐标
                    int end_x = (int) event.getRawX();
                    int end_y = (int) event.getRawY();

                    // 计算move结束时x方向和y方向移动的距离
                    int dx = end_x - start_x;
                    int dy = end_y - start_y;

                    // 计算组件新位置的数据
                    int l = tv_site.getLeft() + dx;
                    int r = tv_site.getRight() + dx;
                    int t = tv_site.getTop() + dy;
                    int b = tv_site.getBottom() + dy;

                    // 判断组件的新位置是否越界(即组件有一部分跑到屏幕外面去了)
                    if (l < 0 || r > window_width || t < 0
                            || b > window_height - 50) // 减去50是为了去除状态栏的高度
                        break;

                    // 设置tv_top 和 tv_buttom 的显示规则
                    if (t < window_height / 2) {
                        // tv_top隐藏, tv_buttom显示
                        tv_top.setVisibility(TextView.INVISIBLE);
                        tv_buttom.setVisibility(TextView.VISIBLE);
                    } else {
                        // tv_top显示, tv_buttom隐藏
                        tv_top.setVisibility(TextView.VISIBLE);
                        tv_buttom.setVisibility(TextView.INVISIBLE);
                    }

                    // 更新组件的位置
                    tv_site.layout(l, t, r, b);

                    // 更新起点坐标的值
                    start_x = (int) event.getRawX();
                    start_y = (int) event.getRawY();

                    // 将更新后的值保存到配置文件中
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
                return false; // 返回false代表该事件将继续向下传递, 不会在此处终止
            }
        });
            //为组件设置双击居中的功能
        tv_site.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //此处引用了android源码的牛逼做法, 详见笔记中的解释
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
                if (mHits[0] >= (SystemClock.uptimeMillis() - 300)) {
                    //设置tv_site组件居中
                        //获取组件的宽和高
                    int w = tv_site.getWidth();
                    int h = tv_site.getHeight();
                    tv_site.layout(window_width/2 - w/2, window_height/2 - h/2, window_width/2 + w/2, window_height/2 + h/2);
                }
            }
        });
    }
}
