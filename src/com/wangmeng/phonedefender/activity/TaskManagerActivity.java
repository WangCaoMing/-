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
 * ���̹������
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
   
    //���н��̵���Ϣ
    private List<TaskInfo> taskInfos;
    
    //handler
    Handler handler = new Handler(){
        
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0)
            {
                //����������adapter
                adapter = new MyListViewAdapter();
                lv.setAdapter(adapter);
                
                //Ϊlistview���û�����Ӧ�¼�
                lv.setOnScrollListener(new OnScrollListener() {
                    
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,
                            int visibleItemCount, int totalItemCount) {
                        if (firstVisibleItem <= (usertaskinfos.size()))
                            tv_listview.setText("�û�����(" + usertaskinfos.size() + "��)");
                        else
                            tv_listview.setText("ϵͳ����(" + systaskinfos.size() + "��)");
                    }
                });
                //�رռ���ҳ��
                rl_loading.setVisibility(View.GONE);
                
                
                
            }
        };
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_taskmanager);
        
        //��ȡ�����е����
        tv_running_process = (TextView) findViewById(R.id.layout_taskmanager_tv_running_process);
        tv_raminfo = (TextView) findViewById(R.id.layout_taskmanager_tv_raminfo);
        lv = (ListView) findViewById(R.id.layout_taskmanager_lv);
        rl_loading = (RelativeLayout) findViewById(R.id.layout_taskmanager_rl_loading);
        tv_listview = (TextView) findViewById(R.id.layout_taskmanager_tv_listview);
        //��ʼ������
        initUI();
        //��ʼ������
        initData();
        
    }

    /**
     * ��ʼ������
     */
    private void initData() {
        
        new Thread(){
            public void run() {
                
                rl_loading.setVisibility(View.VISIBLE);
                
                // ��ȡ�������н�����Ϣ�Ǻ�ʱ����, ��Ҫ�������߳���
                taskInfos = GetInfoTools.getTaskInfos(TaskManagerActivity.this);
                
                //systaskinfos���ϵͳ����, usertaskinfos����û�����
                systaskinfos = new ArrayList<TaskInfo>();
                usertaskinfos = new ArrayList<TaskInfo>();
                
                //�����̷���
                for (TaskInfo item : taskInfos) {
                    if (item.isUsertask())
                        usertaskinfos.add(item);
                    else
                        systaskinfos.add(item);
                }
                         
                //֪ͨhandler
                handler.sendEmptyMessage(0); 
            };
        }.start();
        
          
        
    }


    /**
     * ��ʼ������
     */
    private void initUI() {
        // TODO Auto-generated method stub
        //��ȡactivity������
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Service.ACTIVITY_SERVICE);
        
        //��ȡ��ǰ���еĽ��̵���Ϣ
        List<RunningAppProcessInfo> running_process = activityManager.getRunningAppProcesses();
        tv_running_process.setText(String.valueOf("������:" + running_process.size()));
        
        //��ȡmemoryinfo
        MemoryInfo outInfo = new MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        String used_mem = Formatter.formatFileSize(this, outInfo.totalMem - outInfo.availMem);
        String total_mem = Formatter.formatFileSize(this, outInfo.totalMem);
        
        tv_raminfo.setText("�ڴ�:" + used_mem + "/" + total_mem); 
        //��ʼ��tv_listview
        tv_listview.setText("������...");
        
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
            
            //���������������Ŀ
            if (position == 0)
            {
                TextView tv_user = new TextView(TaskManagerActivity.this);
                tv_user.setText("�û�����(" + usertaskinfos.size() + "��)");
                tv_user.setTextColor(Color.WHITE);
                tv_user.setBackgroundColor(new Color().rgb(153, 204, 0));
                tv_user.setTextSize(18f);
                return tv_user;
                
            }
            else if (position == (usertaskinfos.size() + 1))
            {                
                TextView tv_sys = new TextView(TaskManagerActivity.this);
                tv_sys.setText("ϵͳ����(" + systaskinfos.size() + "��)");
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
                
                //��viewHolder����convert��flag��
                convertView.setTag(viewHolder);
            }
            
            //��listview item�����ļ��е��������ֵ
            TaskInfo bean = null;
            // ��ȡ���Ӧ��bean
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
                System.out.println("beanΪ��");
                System.out.println("position:" + position);
                System.out.println("user:" + usertaskinfos.size());
                System.out.println("sys:" + systaskinfos.size());
                return null;
            }
            
            String name = bean.getName();
            if (!TextUtils.isEmpty(name))
                viewHolder.tv_name.setText(bean.getName());
            else
                viewHolder.tv_name.setText(bean.getPackagename()); // �������Ϊ������Ϊ����
            
            viewHolder.tv_mem.setText("ռ���ڴ�:" + Formatter.formatFileSize(TaskManagerActivity.this, bean.getMemsize()));
            Drawable icon = bean.getIcon();
            if (icon != null)
                viewHolder.iv_icon.setImageDrawable(bean.getIcon());
            else
                viewHolder.iv_icon.setImageResource(R.drawable.lock); //���iconΪ��, ������һ��Ĭ�ϵ�icon
            
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
