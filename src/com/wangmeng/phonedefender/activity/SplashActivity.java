package com.wangmeng.phonedefender.activity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.R.id;
import com.wangmeng.phonedefender.R.layout;
import com.wangmeng.phonedefender.tools.ConvertTools;
import com.wangmeng.phonedefender.tools.DisplayTools;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony.Sms.Conversations;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.widget.TextView;

public class SplashActivity extends Activity {
	
	//�����ж���Ϣ���͵ĳ���
	protected static final int UPDATE_ERROR_NETWORK = 0; //��������ʶ
	protected static final int UPDATE_ERROR_IO = 1; //IO�����ʶ
	protected static final int UPDATE_ERROR_JSON = 2; //json���������ʶ
	protected static final int UPDATE_DIALOG_SHOW = 3; //������ʾ�û����¶Ի���
	
	//�ӷ�������ȡ�İ汾������Ϣ
    private String version_name;
	private int version_code;
	private String update_info;
	private String download_url;
	
	//��������İ汾��
	private int versionCode;
	
	//��activity��Handler����, ���ڴ������̷߳���������Ϣ
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_ERROR_NETWORK:
				DisplayTools.ShowToast(SplashActivity.this, "�������");
				break;
			case UPDATE_ERROR_IO:
				DisplayTools.ShowToast(SplashActivity.this, "IO����");
				break;
			case UPDATE_ERROR_JSON:
				DisplayTools.ShowToast(SplashActivity.this, "json��������");
				break;
			case UPDATE_DIALOG_SHOW:
				AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
				builder.setTitle("������ʾ"); //���öԻ���ı���
				builder.setMessage(update_info); //���öԻ��������
				builder.setCancelable(false); //���øöԻ���Ϊ����ȡ���ĶԻ���
				//����ȡ����ť������Ӽ����¼�
				builder.setNegativeButton("�����Թ�", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				//����ȷ�����°�ť������Ӽ����¼�
				builder.setPositiveButton("��������", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				builder.show();
				break;

			default:
				break;
			}
		};
	};
	
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
			versionCode = packageInfo.versionCode; //��ȡ�汾��
			String versionName = packageInfo.versionName; //��ȡ�汾�����ƺ�
			splash_tv_version.setText("�汾:" + versionName); //����ȡ���İ汾���ƺ���ʾ��splash������
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        
        //���汾����
        CheckUpdate();
    }
    /**
     * ���汾���º���
     */
	private void CheckUpdate() {
		//����һ�����߳��������ظ�����Ϣ
		Thread thread = new Thread(){
			//��д�̵߳�run����
			@Override
			public void run() {
				super.run();
				//��ȡһ���յ���Ϣ����
				Message msg = handler.obtainMessage();
				//Url
				URL url; //����url����
				try {
					url = new URL("http://10.0.2.2:8080/update.json");
					HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					connection.setConnectTimeout(5000); //�������ӳ�ʱ
					connection.setReadTimeout(5000); //��������ʱ
					connection.setRequestMethod("GET"); //�������󷽷�
					connection.connect(); //��ʼ����
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
					{
						InputStream in = connection.getInputStream();
						String update_content = ConvertTools.StreamToString(in);//���������е�����ת��ΪString����
						JSONObject json = new JSONObject(update_content);//���ı���ԭΪjson����
						version_name = json.getString("VersionName"); //�������˵İ汾�ŵ�����
						version_code = json.getInt("VersionCode"); //�������˵İ汾��
						update_info = json.getString("UpdateInfo"); //�������˵İ汾������Ϣ
						download_url = json.getString("DownloadUrl"); //�������˵������������·��
					}
				} catch (MalformedURLException e) {//url�쳣, �������쳣
					msg.what = UPDATE_ERROR_NETWORK;
					handler.sendMessage(msg);
					e.printStackTrace();
				} catch (IOException e) {//io�쳣
					msg.what = UPDATE_ERROR_IO;
					handler.sendMessage(msg);
					e.printStackTrace();
				} catch (JSONException e) {//json�����쳣
					msg.what = UPDATE_ERROR_JSON;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
				if (version_code > versionCode)
				{
					//������ʾ�û����µĶԻ���	
					msg.what = UPDATE_DIALOG_SHOW;
					handler.sendMessage(msg);
				}
				else
				{
					//��ʱ4��ֱ����ת��������
				}
			}
		};
		thread.start(); //��ʼִ�����߳�
	}
    
}
