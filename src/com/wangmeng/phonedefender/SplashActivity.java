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
        //��ȡ�������ʾ���
        TextView splash_tv_version = (TextView) findViewById(R.id.splash_tv_version);//��ȡ��ʾ�汾�ŵ�TextView
        //����splash����İ汾չʾ�ı�
        PackageManager packageManager = getPackageManager();//��ȡ��������
        try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);//��ȡ������Ϣ
			int versionCode = packageInfo.versionCode; //��ȡ�汾��
			String versionName = packageInfo.versionName; //��ȡ�汾�����ƺ�
			splash_tv_version.setText("�汾:" + versionName);//����ȡ���İ汾���ƺ���ʾ��splash������
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    }
    
}
