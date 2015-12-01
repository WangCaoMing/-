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
	
	//用于判断消息类型的常量
	protected static final int UPDATE_ERROR_NETWORK = 0; //网络错误标识
	protected static final int UPDATE_ERROR_IO = 1; //IO错误标识
	protected static final int UPDATE_ERROR_JSON = 2; //json解析错误标识
	protected static final int UPDATE_DIALOG_SHOW = 3; //弹出提示用户更新对话框
	
	//从服务器获取的版本更新信息
    private String version_name;
	private int version_code;
	private String update_info;
	private String download_url;
	
	//本地软件的版本号
	private int versionCode;
	
	//该activity的Handler对象, 用于处理子线程发送来的消息
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_ERROR_NETWORK:
				DisplayTools.ShowToast(SplashActivity.this, "网络错误");
				break;
			case UPDATE_ERROR_IO:
				DisplayTools.ShowToast(SplashActivity.this, "IO错误");
				break;
			case UPDATE_ERROR_JSON:
				DisplayTools.ShowToast(SplashActivity.this, "json解析错误");
				break;
			case UPDATE_DIALOG_SHOW:
				AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
				builder.setTitle("更新提示"); //设置对话框的标题
				builder.setMessage(update_info); //设置对话框的内容
				builder.setCancelable(false); //设置该对话框为不可取消的对话框
				//设置取消按钮并且添加监听事件
				builder.setNegativeButton("狠心略过", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				//设置确定更新按钮并且添加监听事件
				builder.setPositiveButton("立即更新", new OnClickListener() {
					
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
        //获取界面的显示组件
        TextView splash_tv_version = (TextView) findViewById(R.id.splash_tv_version);//获取显示版本号的TextView
        //设置splash界面的版本展示文本
        PackageManager packageManager = getPackageManager();//获取包管理器
        try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);//获取包的信息
			versionCode = packageInfo.versionCode; //获取版本号
			String versionName = packageInfo.versionName; //获取版本的名称号
			splash_tv_version.setText("版本:" + versionName); //将获取到的版本名称号显示到splash界面上
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        
        //检查版本更新
        CheckUpdate();
    }
    /**
     * 检查版本更新函数
     */
	private void CheckUpdate() {
		//创建一个子线程用于下载更新信息
		Thread thread = new Thread(){
			//重写线程的run方法
			@Override
			public void run() {
				super.run();
				//获取一个空的消息对象
				Message msg = handler.obtainMessage();
				//Url
				URL url; //创建url对象
				try {
					url = new URL("http://10.0.2.2:8080/update.json");
					HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					connection.setConnectTimeout(5000); //设置连接超时
					connection.setReadTimeout(5000); //设置请求超时
					connection.setRequestMethod("GET"); //设置请求方法
					connection.connect(); //开始连接
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
					{
						InputStream in = connection.getInputStream();
						String update_content = ConvertTools.StreamToString(in);//将输入流中的内容转换为String类型
						JSONObject json = new JSONObject(update_content);//将文本还原为json对象
						version_name = json.getString("VersionName"); //服务器端的版本号的名称
						version_code = json.getInt("VersionCode"); //服务器端的版本号
						update_info = json.getString("UpdateInfo"); //服务器端的版本描述信息
						download_url = json.getString("DownloadUrl"); //服务器端的最新软件下载路径
					}
				} catch (MalformedURLException e) {//url异常, 即网络异常
					msg.what = UPDATE_ERROR_NETWORK;
					handler.sendMessage(msg);
					e.printStackTrace();
				} catch (IOException e) {//io异常
					msg.what = UPDATE_ERROR_IO;
					handler.sendMessage(msg);
					e.printStackTrace();
				} catch (JSONException e) {//json解析异常
					msg.what = UPDATE_ERROR_JSON;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
				if (version_code > versionCode)
				{
					//弹出提示用户更新的对话框	
					msg.what = UPDATE_DIALOG_SHOW;
					handler.sendMessage(msg);
				}
				else
				{
					//延时4秒直接跳转到主界面
				}
			}
		};
		thread.start(); //开始执行子线程
	}
    
}
