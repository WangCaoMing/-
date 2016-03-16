package com.wangmeng.phonedefender.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wangmeng.phonedefender.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class TaskManagerActivitySetting extends Activity {
    
    private SharedPreferences sprefs;
    
    @ViewInject(R.id.layout_taskmanager_setting_cb)
    private CheckBox cb;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.layout_taskmanager_setting);
        sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);
        ViewUtils.inject(this);
        
        cb.setChecked(sprefs.getBoolean("show_sys_task", true));
        
        //为checkbox设置单击事件响应.
        cb.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cb.isChecked())
                {
                    sprefs.edit().putBoolean("show_sys_task", true).commit();
                    setResult(1);
                }
                else{
                    sprefs.edit().putBoolean("show_sys_task", false).commit();
                    setResult(1);    
                }
                    
            }
        });
    }
}
