package com.wangmeng.phonedefender.activity;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.bean.TaskInfo;
import com.wangmeng.phonedefender.tools.DisplayTools;
import com.wangmeng.phonedefender.tools.GetInfoTools;

/**
 * 进程管理界面
 * 
 * @author Administrator
 * 
 */
public class TaskManagerActivity extends Activity {

    
    private SharedPreferences sprefs;
    private TextView tv_running_process;
    private TextView tv_raminfo;
    private ListView lv;
    private RelativeLayout rl_loading;
    private MyListViewAdapter adapter;
    private TextView tv_listview;

    private List<TaskInfo> systaskinfos;
    private List<TaskInfo> usertaskinfos;

    private long used_mem;
    private long total_mem;
    private MemoryInfo outInfo;
    private int processCount;
    // 运行进程的信息
    private List<TaskInfo> taskInfos;

    // handler
    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                // 创建并设置adapter
                adapter = new MyListViewAdapter();
                lv.setAdapter(adapter);

                // 为listview设置滑动响应事件
                lv.setOnScrollListener(new OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(AbsListView view,
                            int scrollState) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onScroll(AbsListView view,
                            int firstVisibleItem, int visibleItemCount,
                            int totalItemCount) {
                        // 更新显示的内容的类别: 用户进程or系统进程
                        if (firstVisibleItem <= (usertaskinfos.size()))
                            tv_listview.setText("用户进程(" + usertaskinfos.size()
                                    + "个)");
                        else
                            tv_listview.setText("系统进程(" + systaskinfos.size()
                                    + "个)");
                    }
                });
                

                // 为listview设置单击响应事件
                lv.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        System.out.println("检测到item被点击, position:" + position);
                        // 如果点击的是标题位置则直接返回
                        if (position == 0
                                || position == systaskinfos.size() + 1) {
                            return;
                        } else {
                            Object obj = lv.getItemAtPosition(position);
                            ViewHolder vh = (ViewHolder) view.getTag();
                            if (obj != null && obj instanceof TaskInfo) {
                                //判断是否是本应用, 如果是直接返回, 不做任何动作.
                                if (((TaskInfo) obj).getPackagename().equals(getPackageName()))
                                    return;
                                    
                                ((TaskInfo) obj).setChecked(!vh.cb.isChecked()); // 更改的是数据源中checkbox的状态.
                                vh.cb.setChecked(!vh.cb.isChecked()); // 更改的是界面显示的状态.
                            }

                        }
                    }
                });
                
                //更新界面进程数.
                if (sprefs.getBoolean("show_sys_task", true))
                    processCount = taskInfos.size();
                else
                    processCount = usertaskinfos.size();
                tv_running_process.setText(String.valueOf("进程数:" + processCount)); 
                

                // 关闭加载页面
                rl_loading.setVisibility(View.GONE);

            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_taskmanager);

        //获取sharedpreference对象
        sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);
        // 获取布局中的组件
        tv_running_process = (TextView) findViewById(R.id.layout_taskmanager_tv_running_process);
        tv_raminfo = (TextView) findViewById(R.id.layout_taskmanager_tv_raminfo);
        lv = (ListView) findViewById(R.id.layout_taskmanager_lv);
        rl_loading = (RelativeLayout) findViewById(R.id.layout_taskmanager_rl_loading);
        tv_listview = (TextView) findViewById(R.id.layout_taskmanager_tv_listview);
        // 初始化界面
        initUI();
        // 初始化数据
        initData();

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if (resultCode == 1 && requestCode == 1)
        {
            adapter.notifyDataSetChanged();
            
            //更新界面进程数.
            if (sprefs.getBoolean("show_sys_task", true))
                processCount = taskInfos.size();
            tv_running_process.setText(String.valueOf("进程数:" + processCount));
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        new Thread() {
            public void run() {

                rl_loading.setVisibility(View.VISIBLE);

                // 获取所有运行进程信息是耗时操作, 需要放入子线程中
                taskInfos = GetInfoTools.getTaskInfos(TaskManagerActivity.this);

                // systaskinfos存放系统进程, usertaskinfos存放用户进程
                systaskinfos = new ArrayList<TaskInfo>();
                usertaskinfos = new ArrayList<TaskInfo>();

                // 将进程分类
                for (TaskInfo item : taskInfos) {
                    if (item.isUsertask())
                        usertaskinfos.add(item);
                    else
                        systaskinfos.add(item);
                }

                // 通知handler
                handler.sendEmptyMessage(0);
            };
        }.start();

    }

    /**
     * 初始化界面
     */
    private void initUI() {
        // TODO Auto-generated method stub
        // 获取activity管理器
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(Service.ACTIVITY_SERVICE);

        // 获取当前运行的进程的信息
        List<RunningAppProcessInfo> running_process = activityManager
                .getRunningAppProcesses();
        processCount = running_process.size();
        tv_running_process.setText(String.valueOf("进程数: *"));

        // 获取当前的内存信息
        outInfo = new MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        total_mem = outInfo.totalMem;
        used_mem = total_mem - outInfo.availMem;
        String total_mem_str = Formatter.formatFileSize(this, total_mem);
        String used_mem_str = Formatter.formatFileSize(this, used_mem);
        tv_raminfo.setText("内存:" + used_mem_str + "/" + total_mem_str);
        // 初始化tv_listview
        tv_listview.setText("加载中...");

    }

    class MyListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (!sprefs.contains("show_sys_task"))
                sprefs.edit().putBoolean("show_sys_task", true).commit();
            boolean show_sys_task = sprefs.getBoolean("show_sys_task", true);
            if (show_sys_task)
                return taskInfos.size() + 2;
            else
                return usertaskinfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position > 0 && position <= usertaskinfos.size()) {
                return usertaskinfos.get(position - 1);

            } else if (position >= usertaskinfos.size() + 2) {
                int relative_position = position - usertaskinfos.size() - 2;
                return systaskinfos.get(relative_position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 设置两个特殊的条目
            if (position == 0) {
                TextView tv_user = new TextView(TaskManagerActivity.this);
                tv_user.setText("用户进程(" + usertaskinfos.size() + "个)");
                tv_user.setTextColor(Color.WHITE);
                tv_user.setBackgroundColor(new Color().rgb(153, 204, 0));
                tv_user.setTextSize(18f);
                return tv_user;

            } else if (position == (usertaskinfos.size() + 1)) {
                TextView tv_sys = new TextView(TaskManagerActivity.this);
                tv_sys.setText("系统进程(" + systaskinfos.size() + "个)");
                tv_sys.setTextColor(Color.WHITE);
                tv_sys.setBackgroundColor(new Color().rgb(153, 204, 0));
                tv_sys.setTextSize(18f);
                return tv_sys;
            }

            ViewHolder viewHolder;
            if (convertView != null && convertView instanceof LinearLayout) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(TaskManagerActivity.this,
                        R.layout.layout_taskmanager_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = (TextView) convertView
                        .findViewById(R.id.layout_taskmanager_listview_item_tv_name);
                viewHolder.tv_mem = (TextView) convertView
                        .findViewById(R.id.layout_taskmanager_listview_item_tv_mem);
                viewHolder.iv_icon = (ImageView) convertView
                        .findViewById(R.id.layout_taskmanager_listview_item_iv_icon);
                viewHolder.cb = (CheckBox) convertView
                        .findViewById(R.id.layout_taskmanager_listview_item_cb);

                // 将viewHolder放入convert的flag中
                convertView.setTag(viewHolder);
            }

            // 根据位置获取相对应的bean
            TaskInfo bean = null;
            // 获取相对应的bean
            if (position > 0 && position <= usertaskinfos.size()) {
                bean = usertaskinfos.get(position - 1);

            } else if (position >= usertaskinfos.size() + 2) {
                int relative_position = position - usertaskinfos.size() - 2;
                bean = systaskinfos.get(relative_position);
            }
            if (bean == null) {
                System.out.println("bean为空");
                System.out.println("position:" + position);
                System.out.println("user:" + usertaskinfos.size());
                System.out.println("sys:" + systaskinfos.size());
                return null;
            }

            // 将bean中的数据设置给相应的组件
            String name = bean.getName();
            if (!TextUtils.isEmpty(name))
                viewHolder.tv_name.setText(bean.getName());
            else
                viewHolder.tv_name.setText(bean.getPackagename()); // 如果名字为空则设为包名

            viewHolder.tv_mem.setText("占用内存:"
                    + Formatter.formatFileSize(TaskManagerActivity.this,
                            bean.getMemsize()));
            Drawable icon = bean.getIcon();
            if (icon != null)
                viewHolder.iv_icon.setImageDrawable(bean.getIcon());
            else
                viewHolder.iv_icon.setImageResource(R.drawable.lock); // 如果icon为空,
                                                                      // 则设置一个默认的icon
            if (bean.getPackagename().equals(getPackageName()))
            {
                viewHolder.cb.setVisibility(CheckBox.GONE);
            }
            else
            {
                viewHolder.cb.setVisibility(CheckBox.VISIBLE);
            }
            viewHolder.cb.setChecked(bean.isChecked());
            return convertView;
        }

    }

    class ViewHolder {
        public TextView tv_name;
        public TextView tv_mem;
        public ImageView iv_icon;
        public CheckBox cb;
    }

    /**
     * 全选按钮的单击事件
     * 
     * @param v
     */
    public void selectAll(View v) {
        for (int i = 0; i < systaskinfos.size() + usertaskinfos.size() + 2; ++i) {
            Object obj = adapter.getItem(i);
            if (obj != null && obj instanceof TaskInfo) {
                ((TaskInfo) obj).setChecked(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选按钮的单击事件
     * 
     * @param v
     */
    public void selectInvert(View v) {
        for (int i = 0; i < systaskinfos.size() + usertaskinfos.size() + 2; ++i) {
            Object obj = adapter.getItem(i);
            if (obj != null && obj instanceof TaskInfo) {
                TaskInfo bean = (TaskInfo) obj;
                bean.setChecked(!bean.isChecked());
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 清理按钮的单击事件
     * 
     * @param v
     */
    public void killProcess(View v) {
        int count = 0; // 用于存放删除的进程数
        long freeMemory = 0; // 用于存放释放的内存空间
        List<TaskInfo> waitForKillList = new ArrayList<TaskInfo>(); // 存放待删除的进程信息.

        // 获取系统的进程管理服务
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);

        // 遍历用户和系统进程, 并记录被选中的taskinfo
        for (TaskInfo bean : usertaskinfos) {
            if (bean.isChecked()) {
                waitForKillList.add(bean);
                count++;
                freeMemory += bean.getMemsize();
            }

        }

        for (TaskInfo bean : systaskinfos) {
            if (bean.isChecked()) {
                waitForKillList.add(bean);
                count++;
                freeMemory += bean.getMemsize();
            }
        }

        // 开始遍历waitForKillList杀死进程
        for (TaskInfo bean : waitForKillList) {
            if (bean.isUsertask()) {
                usertaskinfos.remove(bean);
                activityManager.killBackgroundProcesses(bean.getPackagename());
            } else {
                systaskinfos.remove(bean);
                activityManager.killBackgroundProcesses(bean.getPackagename());
            }
        }

        // 弹出吐司显示清理结果
        String freeMemory_str = Formatter.formatFileSize(this, freeMemory);
        DisplayTools.ShowToast(this, "本次清理共清理了" + count + "个进程, 释放了"
                + freeMemory_str + "的空间");

        // 更新界面显示的进程数和剩余内存的大小
        processCount -= count;
        tv_running_process.setText("进程数:" + processCount);
        used_mem = used_mem - freeMemory;
        tv_raminfo.setText("内存:" + Formatter.formatFileSize(this, used_mem)
                + "/" + Formatter.formatFileSize(this, total_mem));

        // 更新界面的进程列表
        adapter.notifyDataSetChanged();

    }

    /**
     * 设置按钮的单击事件
     * 
     * @param v
     */
    public void setting(View v) {
        // 跳转到设置页面
        Intent intent = new Intent(this, TaskManagerActivitySetting.class);
        startActivityForResult(intent, 1);
    }
}
