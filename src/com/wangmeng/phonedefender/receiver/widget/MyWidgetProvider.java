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
     * 1. 当每次进行创建小部件的实例时会调用此方法
     * 2. updatePeriodMillis属性规定了每次调用该方法的间隔时间, 此属性的定义在AppWidgetProviderInfo中小部件info的xml文件(本app中是/res/xml/widget_info.xml)
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        System.out.println("update");
    }

    /**
     * 当接收到广播时调用此方法, 一般这个方法不用自己实现, 
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        System.out.println("onrecive");
    }
    
    /**
     * 当删除一个小部件实例时调用此方法.
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onDeleted(context, appWidgetIds);
        System.out.println("ondelete");
    }
    
    /**
     * 第一次创建小部件实例时调用此方法, 并且是第一个调用的方法.
     */
    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        super.onEnabled(context);
        //当有widget存在时开启数据更新服务
        intent = new Intent(context, UpdateWidgetData.class);
        context.startService(intent);
        System.out.println("开启数据更新服务");
        
    }

    /**
     * 当所有的小部件实例(一个小部件可以有多个实例)都被删除后调用此方法.
     */
    @Override
    public void onDisabled(Context context) {
        // TODO Auto-generated method stub
        super.onDisabled(context);
        System.out.println("ondisable");
        //当没有widget时停止数据更新服务
        context.stopService(intent);
    }
    
    
}
