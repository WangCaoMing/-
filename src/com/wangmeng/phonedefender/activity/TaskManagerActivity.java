package com.wangmeng.phonedefender.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.bean.TaskInfo;
import com.wangmeng.phonedefender.tools.GetInfoTools;

/**
 * 进程管理界面
 * @author Administrator
 *
 */
public class TaskManagerActivity extends Activity {
    
    
    private TextView tv_running_process;
    private TextView tv_raminfo;
    private ListView lv;
    private RelativeLayout rl_loading;
    private MyListViewAdapter adapter;
    private TextView tv_listview; 
    
    private List<TaskInfo> systaskinfos;
    private List<TaskInfo> usertaskinfos;
   
    //运行进程的信息
    private List<TaskInfo> taskInfos;
    
    //handler
    Handler handler = new Handler(){
        
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0)
            {
                //创建并设置adapter
                adapter = new MyListViewAdapter();
                lv.setAdapter(adapter);
                
                //为listview设置滑动响应事件
                lv.setOnScrollListener(new OnScrollListener() {
                    
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,
                            int visibleItemCount, int totalItemCount) {
                        if (firstVisibleItem <= (usertaskinfos.size()))
                            tv_listview.setText("用户进程(" + usertaskinfos.size() + "个)");
                        else
                            tv_listview.setText("系统进程(" + systaskinfos.size() + "个)");
                    }
                });
                //关闭加载页面
                rl_loading.setVisibility(View.GONE);
                
                
                
            }
        };
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_taskmanager);
        
        //获取布局中的组件
        tv_running_process = (TextView) findViewById(R.id.layout_taskmanager_tv_running_process);
        tv_raminfo = (TextView) findViewById(R.id.layout_taskmanager_tv_raminfo);
        lv = (ListView) findViewById(R.id.layout_taskmanager_lv);
        rl_loading = (RelativeLayout) findViewById(R.id.layout_taskmanager_rl_loading);
        tv_listview = (TextView) findViewById(R.id.layout_taskmanager_tv_listview);
        //初始化界面
        initUI();
        //初始化数据
        initData();
        
    }

    /**
     * 初始化数据
     */
    private void initData() {
        
        new Thread(){
            public void run() {
                
                rl_loading.setVisibility(View.VISIBLE);
                
                // 获取所有运行进程信息是耗时操作, 需要放入子线程中
                taskInfos = GetInfoTools.getTaskInfos(TaskManagerActivity.this);
                
                //systaskinfos存放系统进程, usertaskinfos存放用户进程
                systaskinfos = new ArrayList<TaskInfo>();
                usertaskinfos = new ArrayList<TaskInfo>();
                
                //将进程分类
                for (TaskInfo item : taskInfos) {
                    if (item.isUsertask())
                        usertaskinfos.add(item);
                    else
                        systaskinfos.add(item);
                }
                         
                //通知handler
                handler.sendEmptyMessage(0); 
            };
        }.start();
        
          
        
    }


    /**
     * 初始化界面
     */
    private void initUI() {
        // TODO Auto-generated method stub
        //获取activity管理器
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Service.ACTIVITY_SERVICE);
        
        //获取当前运行的进程的信息
        List<RunningAppProcessInfo> running_process = activityManager.getRunningAppProcesses();
        tv_running_process.setText(String.valueOf("进程数:" + running_process.size()));
        
        //获取memoryinfo
        MemoryInfo outInfo = new MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        String used_mem = Formatter.formatFileSize(this, outInfo.totalMem - outInfo.availMem);
        String total_mem = Formatter.formatFileSize(this, outInfo.totalMem);
        
        tv_raminfo.setText("内存:" + used_mem + "/" + total_mem); 
        //初始化tv_listview
        tv_listview.setText("加载中...");
        
    }
    
    class MyListViewAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return taskInfos.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return taskInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            //设置两个特殊的条目
            if (position == 0)
            {
                TextView tv_user = new TextView(TaskManagerActivity.this);
                tv_user.setText("用户进程(" + usertaskinfos.size() + "个)");
                tv_user.setTextColor(Color.WHITE);
                tv_user.setBackgroundColor(new Color().rgb(153, 204, 0));
                tv_user.setTextSize(18f);
                return tv_user;
                
            }
            else if (position == (usertaskinfos.size() + 1))
            {                
                TextView tv_sys = new TextView(TaskManagerActivity.this);
                tv_sys.setText("系统进程(" + systaskinfos.size() + "个)");
                tv_sys.setTextColor(Color.WHITE);
                tv_sys.setBackgroundColor(new Color().rgb(153, 204, 0));
                tv_sys.setTextSize(18f);
                return tv_sys;
            }
            
            ViewHolder viewHolder;
            if (convertView != null && convertView instanceof LinearLayout)
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            else
            {
                convertView  = View.inflate(TaskManagerActivity.this, R.layout.layout_taskmanager_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.layout_taskmanager_listview_item_tv_name);
                viewHolder.tv_mem = (TextView) convertView.findViewById(R.id.layout_taskmanager_listview_item_tv_mem);
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.layout_taskmanager_listview_item_iv_icon);
                viewHolder.cb = (CheckBox) convertView.findViewById(R.id.layout_taskmanager_listview_item_cb);
                
                //将viewHolder放入convert的flag中
                convertView.setTag(viewHolder);
            }
            
            //将listview item布局文件中的组件设置值
            TaskInfo bean = null;
            // 获取相对应的bean
            if (position > 0 && position <= usertaskinfos.size())
            {
                bean = usertaskinfos.get(position - 1);
                
            }
            else if (position >= usertaskinfos.size() + 2) 
            {
                int relative_position = position - usertaskinfos.size() - 2;
                bean = systaskinfos.get(relative_position);
            }
            if (bean == null)
            {
                System.out.println("bean为空");
                System.out.println("position:" + position);
                System.out.println("user:" + usertaskinfos.size());
                System.out.println("sys:" + systaskinfos.size());
                return null;
            }
            
            String name = bean.getName();
            if (!TextUtils.isEmpty(name))
                viewHolder.tv_name.setText(bean.getName());
            else
                viewHolder.tv_name.setText(bean.getPackagename()); // 如果名字为空则设为包名
            
            viewHolder.tv_mem.setText("占用内存:" + Formatter.formatFileSize(TaskManagerActivity.this, bean.getMemsize()));
            Drawable icon = bean.getIcon();
            if (icon != null)
                viewHolder.iv_icon.setImageDrawable(bean.getIcon());
            else
                viewHolder.iv_icon.setImageResource(R.drawable.lock); //如果icon为空, 则设置一个默认的icon
            
            return convertView;
        }
        
    }
    
    class ViewHolder
    {
        public TextView tv_name;
        public TextView tv_mem;
        public ImageView iv_icon;
        public CheckBox cb;
    }
}
