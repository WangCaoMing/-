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
 * 软件刚打开时的logo展示界面和进行一些初始化的操作
 * 
 * @author Administrator
 * 
 */
public class SplashActivity extends Activity {

	// 配置文件
	private static SharedPreferences sprefs;

	// 用于判断消息类型的常量
	protected static final int UPDATE_ERROR_NETWORK = 0; // 网络错误标识
	protected static final int UPDATE_ERROR_IO = 1; // IO错误标识
	protected static final int UPDATE_ERROR_JSON = 2; // json解析错误标识
	protected static final int UPDATE_DIALOG_SHOW = 3; // 弹出提示用户更新对话框
	protected static final int ENTER_HOME = 4; // 跳轉到主界面

	// 从服务器获取的版本更新信息
	private String version_name;
	private int version_code;
	private String update_info;
	private String download_url;

	// 本地软件的版本号
	private int versionCode;

	// 获取显示组件
	private ProgressBar splash_pb_download;
	private TextView splash_tv_version;

	// 该activity的Handler对象, 用于处理子线程发送来的消息
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_ERROR_NETWORK:
				DisplayTools.ShowToast(SplashActivity.this, "网络错误, 获取更新失败");
				EnterHomeActivity(); // 跳转到主界面
				break;
			case UPDATE_ERROR_IO:
				DisplayTools.ShowToast(SplashActivity.this, "IO错误, 获取更新失败");
				EnterHomeActivity(); // 跳转到主界面
				break;
			case UPDATE_ERROR_JSON:
				DisplayTools.ShowToast(SplashActivity.this, "json解析错误, 获取更新失败");
				EnterHomeActivity(); // 跳转到主界面
				break;
			case UPDATE_DIALOG_SHOW:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SplashActivity.this);
				builder.setTitle("更新提示"); // 设置对话框的标题
				builder.setMessage(update_info); // 设置对话框的内容
				builder.setCancelable(false); // 设置该对话框为不可取消的对话框
				// 设置取消按钮并且添加监听事件
				builder.setNegativeButton("狠心略过", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						EnterHomeActivity(); // 跳转到主界面
					}
				});
				// 设置确定更新按钮并且添加监听事件
				builder.setPositiveButton("立即更新", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 开始下载新版本的软件
						if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {// 判断SD卡是否挂载
							DisplayTools.ShowToast(SplashActivity.this,
									"没有SD卡, 无法完成更新");
							EnterHomeActivity(); // 更新失败, 直接跳转到主界面
						}
						String target = Environment
								.getExternalStorageDirectory() + "/update.apk"; // 设置下载文件的路径
						HttpUtils http = new HttpUtils();
						HttpHandler handler = http.download(download_url,
								target, new RequestCallBack<File>() {

									@Override
									public void onSuccess(
											ResponseInfo<File> arg0) {// arg0变量中保存了下载的文件的数据
										DisplayTools
												.ShowToast(SplashActivity.this,
														"更新文件下载成功");
										// 下载完成后跳转到软件的安装界面
										Intent intent = new Intent(
												Intent.ACTION_VIEW);// 调用系统的就软件安装器需要从源码中查看此安装器activity的过滤器的设置来书写
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
														"更新文件下载失败");
									}
								});
					}
				});
				builder.show();
				break;
			case ENTER_HOME:
				EnterHomeActivity(); // 跳轉到主界面
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
		// 获取配置文件
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);

		// 获取配置文件中配置信息
		boolean auto_update = sprefs.getBoolean("auto_update", true); // 获取自动更新配置信息

		// 获取组件
		RelativeLayout splash_layout = (RelativeLayout) findViewById(R.id.splash_layout); // 获取Splash界面布局文件的跟布局组件Relatively
		splash_tv_version = (TextView) findViewById(R.id.splash_tv_version);
		splash_pb_download = (ProgressBar) findViewById(R.id.splash_pb_download);

		// 设置splash界面的版本展示文本
		PackageManager packageManager = getPackageManager();// 获取包管理器
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), PackageManager.GET_CONFIGURATIONS);// 获取包的信息
			versionCode = packageInfo.versionCode; // 获取版本号
			String versionName = packageInfo.versionName; // 获取版本的名称号
			splash_tv_version.setText("版本:" + versionName); // 将获取到的版本名称号显示到splash界面上
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// 检查版本更新
		if (auto_update)
			CheckUpdate();
		else {
			// 延时4秒跳转到主界面
			Message msg = new Message();
			msg.what = ENTER_HOME;
			handler.sendMessageDelayed(msg, 4000);
		}
		
		//拷贝数据库到files文件夹中(为归属地查询提供数据库)
		copyDB();

		// 为splash界面设置启动的动画
		ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, 360, 640);
		animation.setDuration(250);
		splash_layout.startAnimation(animation);
	}
	
	/**
	 * 拷贝数据库到files文件夹中
	 */
	private void copyDB() {
		try {
			InputStream in = getAssets().open("address.db"); //获取输入流
			File file = new File(getFilesDir().getAbsolutePath(), "address.db");
			OutputStream out = new FileOutputStream(file); //创建输出流
			
			TransportTools.In2OutStream(in, out); //将输入流中的数据传输到输出流
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 跳转到主界面函数
	 */
	public void EnterHomeActivity() {
		// 跳转到主界面
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish(); // 结束splash界面
	}

	/**
	 * 检查版本更新函数
	 */
	private void CheckUpdate() {
		// 创建一个子线程用于下载更新信息
		Thread thread = new Thread() {
			// 重写线程的run方法
			@Override
			public void run() {
				super.run();
				// 获取一个空的消息对象
				Message msg = handler.obtainMessage();
				// 開始計時
				long start_time = System.currentTimeMillis();
				// Url
				URL url; // 创建url对象
				try {
					url = new URL("http://10.0.2.2:8080/update.json");
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setConnectTimeout(4000); // 设置连接超时
					connection.setReadTimeout(4000); // 设置请求超时
					connection.setRequestMethod("GET"); // 设置请求方法
					connection.connect(); // 开始连接
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						InputStream in = connection.getInputStream();
						String update_content = ConvertTools.StreamToString(in);// 将输入流中的内容转换为String类型
						JSONObject json = new JSONObject(update_content);// 将文本还原为json对象
						version_name = json.getString("VersionName"); // 服务器端的版本号的名称
						version_code = json.getInt("VersionCode"); // 服务器端的版本号
						update_info = json.getString("UpdateInfo"); // 服务器端的版本描述信息
						download_url = json.getString("DownloadUrl"); // 服务器端的最新软件下载路径
					}
				} catch (MalformedURLException e) {// url异常, 即网络异常
					msg.what = UPDATE_ERROR_NETWORK;
					long custom_time = System.currentTimeMillis() - start_time; // 计算所用的时长
					if (custom_time >= 4000)
						handler.sendMessage(msg);
					else
						handler.sendMessageDelayed(msg, 4000 - custom_time);
					e.printStackTrace();
					return;
				} catch (IOException e) {// io异常
					msg.what = UPDATE_ERROR_IO;
					long custom_time = System.currentTimeMillis() - start_time; // 计算所用的时长
					if (custom_time >= 4000)
						handler.sendMessage(msg);
					else
						handler.sendMessageDelayed(msg, 4000 - custom_time);
					e.printStackTrace();
					return;
				} catch (JSONException e) {// json解析异常
					msg.what = UPDATE_ERROR_JSON;
					long custom_time = System.currentTimeMillis() - start_time; // 计算所用的时长
					if (custom_time >= 4000)
						handler.sendMessage(msg);
					else
						handler.sendMessageDelayed(msg, 4000 - custom_time);
					e.printStackTrace();
					return;
				}
				if (version_code > versionCode) {
					// 弹出提示用户更新的对话框
					msg.what = UPDATE_DIALOG_SHOW;
					long custom_time = System.currentTimeMillis() - start_time; // 计算所用的时长
					if (custom_time >= 4000)
						handler.sendMessage(msg);
					else
						handler.sendMessageDelayed(msg, 4000 - custom_time);
				} else {
					// 跳转到主界面
					msg.what = ENTER_HOME;
					long custom_time = System.currentTimeMillis() - start_time; // 计算所用的时长
					if (custom_time >= 4000)
						handler.sendMessage(msg);
					else
						handler.sendMessageDelayed(msg, 4000 - custom_time);
				}
			}
		};
		thread.start(); // 开始执行子线程
	}

}
