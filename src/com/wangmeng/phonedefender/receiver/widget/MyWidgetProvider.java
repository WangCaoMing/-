package com.wangmeng.phonedefender.receiver.widget;

import com.wangmeng.phonedefender.service.UpdateWidgetData;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.sax.StartElementListener;

public class MyWidgetProvider extends AppWidgetProvider {
    
    private Intent intent;

    /**
     * 1. ��ÿ�ν��д���С������ʵ��ʱ����ô˷���
     * 2. updatePeriodMillis���Թ涨��ÿ�ε��ø÷����ļ��ʱ��, �����ԵĶ�����AppWidgetProviderInfo��С����info��xml�ļ�(��app����/res/xml/widget_info.xml)
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        System.out.println("update");
    }

    /**
     * �����յ��㲥ʱ���ô˷���, һ��������������Լ�ʵ��, 
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        System.out.println("onrecive");
    }
    
    /**
     * ��ɾ��һ��С����ʵ��ʱ���ô˷���.
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onDeleted(context, appWidgetIds);
        System.out.println("ondelete");
    }
    
    /**
     * ��һ�δ���С����ʵ��ʱ���ô˷���, �����ǵ�һ�����õķ���.
     */
    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        super.onEnabled(context);
        //����widget����ʱ�������ݸ��·���
        intent = new Intent(context, UpdateWidgetData.class);
        context.startService(intent);
        System.out.println("�������ݸ��·���");
        
    }

    /**
     * �����е�С����ʵ��(һ��С���������ж��ʵ��)����ɾ������ô˷���.
     */
    @Override
    public void onDisabled(Context context) {
        // TODO Auto-generated method stub
        super.onDisabled(context);
        System.out.println("ondisable");
        //��û��widgetʱֹͣ���ݸ��·���
        context.stopService(intent);
    }
    
    
}
