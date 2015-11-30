package com.wangmeng.phonedefender;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.widget.TextView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        //获取界面的显示组件
        TextView splash_tv_version = (TextView) findViewById(R.id.splash_tv_version);//获取显示版本号的TextView
        //设置splash界面的版本展示文本
        PackageManager packageManager = getPackageManager();//获取包管理器
        try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);//获取包的信息
			int versionCode = packageInfo.versionCode; //获取版本号
			String versionName = packageInfo.versionName; //获取版本的名称号
			splash_tv_version.setText("版本:" + versionName);//将获取到的版本名称号显示到splash界面上
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    }
    
}
