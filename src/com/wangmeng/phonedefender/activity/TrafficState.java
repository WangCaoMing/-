package com.wangmeng.phonedefender.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.bean.TrafficInfoBean;

public class TrafficState extends Activity {
    
    protected static final int DATA_LOAD_COMPLETE = 0;

    private static final int DATA_LOADING = 1;
    
    //listView适配器
    private MyListViewAdapter adapter;
    private LinearLayout ll;    
    
    //控件
    private ListView lv;

    //用于存放每个应用流量信息的list.
    List<TrafficInfoBean> list;
    
    Handler handler = new Handler(){
      

    @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case DATA_LOADING:
                //将listview控件设为不可见
                lv.setVisibility(View.INVISIBLE);
                ll.setVisibility(View.VISIBLE);
                break;
            case DATA_LOAD_COMPLETE:
                adapter = new MyListViewAdapter();
                lv.setAdapter(adapter);
                
                //将listview控件设为可见
                lv.setVisibility(View.VISIBLE);
                ll.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
            }
        }  
    };

    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_trafficstate);
        
        initUI();
        initData();
    }

    private void initUI() {
        lv = (ListView)findViewById(R.id.layout_trafficstate_lv);
        ll = (LinearLayout) findViewById(R.id.layout_trafficstate_ll);
        
        //控件的初始状态设置, ll可见, lv不可见
        lv.setVisibility(View.INVISIBLE);
        ll.setVisibility(View.VISIBLE);
        
    }
    private void initData() {
        handler.sendEmptyMessage(DATA_LOADING);
        new Thread(){
            public void run() {
                list = new ArrayList<TrafficInfoBean>();
                //获取所有应用的流量信息. 封装到bean中
                PackageManager pm = getPackageManager();
                List<ApplicationInfo> installedApplications = pm.getInstalledApplications(PackageManager.GET_ACTIVITIES);
                for (ApplicationInfo appinfo : installedApplications) {
                    TrafficInfoBean bean = new TrafficInfoBean();
                    int uid = appinfo.uid;
                    long rx = TrafficStats.getUidRxBytes(uid); //获取该应用的总下载流量
                    long tx = TrafficStats.getUidTxBytes(uid); // 获取该应用的总上传流量
                    
                    String name = appinfo.loadLabel(pm).toString();
                    String packagename = appinfo.packageName;
                    Drawable icon =  appinfo.loadIcon(pm);
                    
                    bean.setName(name);
                    bean.setIcon(icon);
                    bean.setPackagename(packagename);
                    bean.setRx(rx);
                    bean.setTx(tx);
                    bean.setTotaltraffic(tx + rx);
                    list.add(bean);
                }
                //对加载完成的数据进行排序
                Collections.sort(list, new Mycomparator());
                for (TrafficInfoBean b  : list) {
                    System.out.println(b.toString());
                }
                //数据加载完成, 向handler发送消息
                handler.sendEmptyMessage(DATA_LOAD_COMPLETE);
            };
        }.start();
    }
    
    /**
     * 自定义的listview适配器
     * @author Administrator
     *
     */
    class MyListViewAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = View.inflate(TrafficState.this, R.layout.layout_trafficstate_item, null);
                TextView tv_name = (TextView) convertView.findViewById(R.id.layout_trafficstate_tv_name);
                TextView tv_totaltraffic = (TextView) convertView.findViewById(R.id.layout_trafficstate_tv_totaltraffic);
                ImageView iv_icon = (ImageView) convertView.findViewById(R.id.layout_trafficstate_iv_icon);
                TextView tv_rx = (TextView) convertView.findViewById(R.id.layout_trafficstate_tv_rx);
                TextView tv_tx = (TextView) convertView.findViewById(R.id.layout_trafficstate_tv_tx);
                
                holder = new ViewHolder();
                holder.tv_name = tv_name;
                holder.tv_totaltraffic = tv_totaltraffic;
                holder.tv_rx = tv_rx;
                holder.tv_tx = tv_tx;
                holder.iv_icon = iv_icon;
                
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            
            // 将数据填充到每个条目的控件之中.
            TrafficInfoBean bean =  list.get(position);
            String totaltraffic = Formatter.formatFileSize(TrafficState.this, bean.totalTraffic());
            String rx = Formatter.formatFileSize(TrafficState.this, bean.getRx());
            String tx = Formatter.formatFileSize(TrafficState.this, bean.getTx());
            holder.iv_icon.setImageDrawable(bean.getIcon());
            holder.tv_name.setText(bean.getName());
            holder.tv_totaltraffic.setText(totaltraffic);
            holder.tv_rx.setText("下载流量:" + rx);
            holder.tv_tx.setText("上传流量:" + tx);
            
            return convertView;
        }
        
    }
    class ViewHolder
    {
        TextView tv_name;
        TextView tv_totaltraffic;
        TextView tv_rx;
        TextView tv_tx;
        ImageView iv_icon;
    }
    
    class Mycomparator implements Comparator<TrafficInfoBean>
    {

        @Override
        public int compare(TrafficInfoBean lhs, TrafficInfoBean rhs) {
            long l = lhs.getTotaltraffic();
            long r = rhs.getTotaltraffic();
            if (l > r)
                return -1;
            else if (l == r)
                return 0;
            else
                return 1;
           //返回一个>0的数表示左值大于右值, 返回=0的数表示左值等于右值, 返回<0的数表示左值小于右值.
        }
        
    }
}

