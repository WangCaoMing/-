package com.wangmeng.phonedefender.receiver.widget;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillProcessReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApp = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo info : runningApp) {
            activityManager.killBackgroundProcesses(info.processName);
        }
        System.out.println("清理完成!");
    }

}
