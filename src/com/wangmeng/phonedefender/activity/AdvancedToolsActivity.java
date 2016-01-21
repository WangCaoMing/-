package com.wangmeng.phonedefender.activity;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.activity.advancedtools_activity.CheckPhoneNumberActivity;
import com.wangmeng.phonedefender.myinterface.ISmsBackupCallback;
import com.wangmeng.phonedefender.tools.DisplayTools;
import com.wangmeng.phonedefender.tools.SmsTools;

/**
 * �߼����߽���
 * @author Administrator
 *
 */
public class AdvancedToolsActivity extends Activity {
	
    //handler
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1)
            {
                dialog.show();
            }
            else if (msg.what == 2)
            {
                dialog.dismiss();
            }
        }
    };
	
    //���ű��ݵĽ�����
    private ProgressDialog dialog;	
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_advancedtools);
	}
	
	/**
	 * �����ز�ѯ��ť�ĵ���¼�
	 * @param v
	 */
	public void checkPhoneNumber(View v)
	{
		//��ת�����ݿ��ѯ����
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_out_anim);
		startActivity(new Intent(this, CheckPhoneNumberActivity.class));
	}
	
	/**
	 * ���ű��ݰ�ť����¼�
	 * @param v
	 */
	public void smsBackup(View v)
	{
	    //�ж�SD���Ƿ����
	    if (Environment.isExternalStorageEmulated())
	    {
	        dialog = new ProgressDialog(this);
	        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        
	        //���ñ����ļ���·��
	        final File SDpath = new File(Environment.getExternalStorageDirectory().toString() + "/smsbackup.xml");
	        new Thread(){
	            public void run() {
	                //��ȡ�����Ǻ�ʱ����, ��Ҫ�����߳��н���
	                SmsTools.backup(AdvancedToolsActivity.this, SDpath.toString(), new ISmsBackupCallback() {
                        
                        @Override
                        public void onBackup(int progress) {
                            // TODO Auto-generated method stub
                            dialog.setProgress(progress);
                        }
                        
                        @Override
                        public void beforeBackup(int total) {
                            // TODO Auto-generated method stub
                            dialog.setMax(total);  
                            handler.sendEmptyMessage(1);
                        }

                        @Override
                        public void afteronBackup() {
                            // TODO Auto-generated method stub
                            handler.sendEmptyMessage(2);
                        }
                    });
	            };
	        }.start();
	        
	        DisplayTools.ShowToast(this, "���ű��ݳɹ�");
	    }
	    else
	    {
	        DisplayTools.ShowToast(this, "SD��������, �޷���ɱ���");
	    }
	    
	    
	    
	    
	}
}
