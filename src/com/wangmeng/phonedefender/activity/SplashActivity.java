package com.wangmeng.phonedefender.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.R.id;
import com.wangmeng.phonedefender.R.layout;
import com.wangmeng.phonedefender.tools.ConvertTools;
import com.wangmeng.phonedefender.tools.DisplayTools;
import com.wangmeng.phonedefender.tools.TransportTools;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony.Sms.Conversations;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.Notification.Action;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Display;
import android.view.Menu;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ����մ�ʱ��logoչʾ����ͽ���һЩ��ʼ���Ĳ���
 * 
 * @author Administrator
 * 
 */
public class SplashActivity extends Activity {

	// �����ļ�
	private static SharedPreferences sprefs;

	// �����ж���Ϣ���͵ĳ���
	protected static final int UPDATE_ERROR_NETWORK = 0; // ��������ʶ
	protected static final int UPDATE_ERROR_IO = 1; // IO�����ʶ
	protected static final int UPDATE_ERROR_JSON = 2; // json���������ʶ
	protected static final int UPDATE_DIALOG_SHOW = 3; // ������ʾ�û����¶Ի���
	protected static final int ENTER_HOME = 4; // ���D��������

	// �ӷ�������ȡ�İ汾������Ϣ
	private String version_name;
	private int version_code;
	private String update_info;
	private String download_url;

	// ��������İ汾��
	private int versionCode;

	// ��ȡ��ʾ���
	private ProgressBar splash_pb_download;
	private TextView splash_tv_version;

	// ��activity��Handler����, ���ڴ������̷߳���������Ϣ
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_ERROR_NETWORK:
				DisplayTools.ShowToast(SplashActivity.this, "�������, ��ȡ����ʧ��");
				EnterHomeActivity(); // ��ת��������
				break;
			case UPDATE_ERROR_IO:
				DisplayTools.ShowToast(SplashActivity.this, "IO����, ��ȡ����ʧ��");
				EnterHomeActivity(); // ��ת��������
				break;
			case UPDATE_ERROR_JSON:
				DisplayTools.ShowToast(SplashActivity.this, "json��������, ��ȡ����ʧ��");
				EnterHomeActivity(); // ��ת��������
				break;
			case UPDATE_DIALOG_SHOW:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SplashActivity.this);
				builder.setTitle("������ʾ"); // ���öԻ���ı���
				builder.setMessage(update_info); // ���öԻ��������
				builder.setCancelable(false); // ���øöԻ���Ϊ����ȡ���ĶԻ���
				// ����ȡ����ť������Ӽ����¼�
				builder.setNegativeButton("�����Թ�", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						EnterHomeActivity(); // ��ת��������
					}
				});
				// ����ȷ�����°�ť������Ӽ����¼�
				builder.setPositiveButton("��������", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// ��ʼ�����°汾�����
						if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {// �ж�SD���Ƿ����
							DisplayTools.ShowToast(SplashActivity.this,
									"û��SD��, �޷���ɸ���");
							EnterHomeActivity(); // ����ʧ��, ֱ����ת��������
						}
						String target = Environment
								.getExternalStorageDirectory() + "/update.apk"; // ���������ļ���·��
						HttpUtils http = new HttpUtils();
						HttpHandler handler = http.download(download_url,
								target, new RequestCallBack<File>() {

									@Override
									public void onSuccess(
											ResponseInfo<File> arg0) {// arg0�����б��������ص��ļ�������
										DisplayTools
												.ShowToast(SplashActivity.this,
														"�����ļ����سɹ�");
										// ������ɺ���ת������İ�װ����
										Intent intent = new Intent(
												Intent.ACTION_VIEW);// ����ϵͳ�ľ������װ����Ҫ��Դ���в鿴�˰�װ��activity�Ĺ���������������д
										intent.addCategory(Intent.CATEGORY_DEFAULT);
										intent.setDataAndType(
												Uri.fromFile(arg0.result),
												"application/vnd.android.package-archive");
										startActivity(intent);
									}

									@Override
									public void onLoading(long total,
											long current, boolean isUploading) {
										// TODO Auto-generated method stub
										super.onLoading(total, current,
												isUploading);
										splash_pb_download
												.setProgress((int) current);
										splash_pb_download.setMax((int) total);
										splash_pb_download
												.setVisibility(ProgressBar.VISIBLE);
									}

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										DisplayTools
												.ShowToast(SplashActivity.this,
														"�����ļ�����ʧ��");
									}
								});
					}
				});
				builder.show();
				break;
			case ENTER_HOME:
				EnterHomeActivity(); // ���D��������
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_splash);
		// ��ȡ�����ļ�
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// ��ȡ�����ļ���������Ϣ
		boolean auto_update = sprefs.getBoolean("auto_update", true); // ��ȡ�Զ�����������Ϣ

		// ��ȡ���
		RelativeLayout splash_layout = (RelativeLayout) findViewById(R.id.splash_layout); // ��ȡSplash���沼���ļ��ĸ��������Relatively
		splash_tv_version = (TextView) findViewById(R.id.splash_tv_version);
		splash_pb_download = (ProgressBar) findViewById(R.id.splash_pb_download);

		// ����splash����İ汾չʾ�ı�
		PackageManager packageManager = getPackageManager();// ��ȡ��������
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), PackageManager.GET_CONFIGURATIONS);// ��ȡ������Ϣ
			versionCode = packageInfo.versionCode; // ��ȡ�汾��
			String versionName = packageInfo.versionName; // ��ȡ�汾�����ƺ�
			splash_tv_version.setText("�汾:" + versionName); // ����ȡ���İ汾���ƺ���ʾ��splash������
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// ���汾����
		if (auto_update)
			CheckUpdate();
		else {
			// ��ʱ4����ת��������
			Message msg = new Message();
			msg.what = ENTER_HOME;
			handler.sendMessageDelayed(msg, 4000);
		}
		
		//�������ݿ⵽files�ļ�����(Ϊ�����ز�ѯ�ṩ���ݿ�)
		copyDB();

		// Ϊsplash�������������Ķ���
		ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, 360, 640);
		animation.setDuration(250);
		splash_layout.startAnimation(animation);
	}
	
	/**
	 * �������ݿ⵽files�ļ�����
	 */
	private void copyDB() {
		try {
			InputStream in = getAssets().open("address.db"); //��ȡ������
			File file = new File(getFilesDir().getAbsolutePath(), "address.db");
			OutputStream out = new FileOutputStream(file); //���������
			
			TransportTools.In2OutStream(in, out); //���������е����ݴ��䵽�����
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��ת�������溯��
	 */
	public void EnterHomeActivity() {
		// ��ת��������
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish(); // ����splash����
	}

	/**
	 * ���汾���º���
	 */
	private void CheckUpdate() {
		// ����һ�����߳��������ظ�����Ϣ
		Thread thread = new Thread() {
			// ��д�̵߳�run����
			@Override
			public void run() {
				super.run();
				// ��ȡһ���յ���Ϣ����
				Message msg = handler.obtainMessage();
				// �_ʼӋ�r
				long start_time = System.currentTimeMillis();
				// Url
				URL url; // ����url����
				try {
					url = new URL("http://10.0.2.2:8080/update.json");
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setConnectTimeout(4000); // �������ӳ�ʱ
					connection.setReadTimeout(4000); // ��������ʱ
					connection.setRequestMethod("GET"); // �������󷽷�
					connection.connect(); // ��ʼ����
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						InputStream in = connection.getInputStream();
						String update_content = ConvertTools.StreamToString(in);// ���������е�����ת��ΪString����
						JSONObject json = new JSONObject(update_content);// ���ı���ԭΪjson����
						version_name = json.getString("VersionName"); // �������˵İ汾�ŵ�����
						version_code = json.getInt("VersionCode"); // �������˵İ汾��
						update_info = json.getString("UpdateInfo"); // �������˵İ汾������Ϣ
						download_url = json.getString("DownloadUrl"); // �������˵������������·��
					}
				} catch (MalformedURLException e) {// url�쳣, �������쳣
					msg.what = UPDATE_ERROR_NETWORK;
					long custom_time = System.currentTimeMillis() - start_time; // �������õ�ʱ��
					if (custom_time >= 4000)
						handler.sendMessage(msg);
					else
						handler.sendMessageDelayed(msg, 4000 - custom_time);
					e.printStackTrace();
					return;
				} catch (IOException e) {// io�쳣
					msg.what = UPDATE_ERROR_IO;
					long custom_time = System.currentTimeMillis() - start_time; // �������õ�ʱ��
					if (custom_time >= 4000)
						handler.sendMessage(msg);
					else
						handler.sendMessageDelayed(msg, 4000 - custom_time);
					e.printStackTrace();
					return;
				} catch (JSONException e) {// json�����쳣
					msg.what = UPDATE_ERROR_JSON;
					long custom_time = System.currentTimeMillis() - start_time; // �������õ�ʱ��
					if (custom_time >= 4000)
						handler.sendMessage(msg);
					else
						handler.sendMessageDelayed(msg, 4000 - custom_time);
					e.printStackTrace();
					return;
				}
				if (version_code > versionCode) {
					// ������ʾ�û����µĶԻ���
					msg.what = UPDATE_DIALOG_SHOW;
					long custom_time = System.currentTimeMillis() - start_time; // �������õ�ʱ��
					if (custom_time >= 4000)
						handler.sendMessage(msg);
					else
						handler.sendMessageDelayed(msg, 4000 - custom_time);
				} else {
					// ��ת��������
					msg.what = ENTER_HOME;
					long custom_time = System.currentTimeMillis() - start_time; // �������õ�ʱ��
					if (custom_time >= 4000)
						handler.sendMessage(msg);
					else
						handler.sendMessageDelayed(msg, 4000 - custom_time);
				}
			}
		};
		thread.start(); // ��ʼִ�����߳�
	}

}
