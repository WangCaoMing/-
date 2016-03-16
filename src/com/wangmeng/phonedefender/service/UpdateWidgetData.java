package com.wangmeng.phonedefender.service;

import java.util.Timer;
import java.util.TimerTask;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.receiver.widget.MyWidgetProvider;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetData extends Service {

    private AppWidgetManager appWidgetManager;
    private ActivityManager activityManager = null;
    private MemoryInfo outInfo = null;
    @Override
    public IBinder onBind(Intent intent) {
        
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        //获取ActivityManager
        activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        //获取widget管理器
        appWidgetManager = AppWidgetManager.getInstance(UpdateWidgetData.this);
        //开启一个timer, 每5秒更新一次widget上组件数据
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //获取进程数和内存信息
                int size = activityManager.getRunningAppProcesses().size();
                activityManager.getMemoryInfo(outInfo);
                String mem = Formatter.formatFileSize(UpdateWidgetData.this, outInfo.availMem);
                
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);
                views.setTextViewText(R.id.widget_tv_processnum, "当前进程数:" + size);
                views.setTextViewText(R.id.widget_tv_validmem, "可用内存:" + mem);
                //定义按钮的点击事件, 用隐式意图触发一个广播接收者进行处理单击事件.
                Intent intent = new Intent();
                intent.setAction("com.wangmeng.widget.buttonclick");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateWidgetData.this, 0, intent, 0);
                views.setOnClickPendingIntent(R.id.widget_bt, pendingIntent);
                ComponentName provider = new ComponentName(UpdateWidgetData.this, MyWidgetProvider.class);
                appWidgetManager.updateAppWidget(provider, views);
                System.out.println("已更新数据!");
            }
        }, 0, 5000); // 设置widget中的信息每5秒更新一次
    }

}
