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
        //��ȡActivityManager
        activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        //��ȡwidget������
        appWidgetManager = AppWidgetManager.getInstance(UpdateWidgetData.this);
        //����һ��timer, ÿ5�����һ��widget���������
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //��ȡ���������ڴ���Ϣ
                int size = activityManager.getRunningAppProcesses().size();
                activityManager.getMemoryInfo(outInfo);
                String mem = Formatter.formatFileSize(UpdateWidgetData.this, outInfo.availMem);
                
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);
                views.setTextViewText(R.id.widget_tv_processnum, "��ǰ������:" + size);
                views.setTextViewText(R.id.widget_tv_validmem, "�����ڴ�:" + mem);
                //���尴ť�ĵ���¼�, ����ʽ��ͼ����һ���㲥�����߽��д������¼�.
                Intent intent = new Intent();
                intent.setAction("com.wangmeng.widget.buttonclick");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateWidgetData.this, 0, intent, 0);
                views.setOnClickPendingIntent(R.id.widget_bt, pendingIntent);
                ComponentName provider = new ComponentName(UpdateWidgetData.this, MyWidgetProvider.class);
                appWidgetManager.updateAppWidget(provider, views);
                System.out.println("�Ѹ�������!");
            }
        }, 0, 5000); // ����widget�е���Ϣÿ5�����һ��
    }

}
