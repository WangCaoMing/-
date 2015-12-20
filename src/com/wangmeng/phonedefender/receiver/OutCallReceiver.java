package com.wangmeng.phonedefender.receiver;

import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.service.CallStateService;
import com.wangmeng.phonedefender.tools.CheckTools;
import com.wangmeng.phonedefender.tools.DisplayTools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class OutCallReceiver extends BroadcastReceiver {

	//常亮
	private static final int DISPLAY_LOCATION = 0; // 显示归属地信息
	private static final int REMOVE_LOCATION = 1; // 移除归属地信息
	
	//配置文件
	private static SharedPreferences sprefs;
	
	private WindowManager windowManager;
	private View view = null;
	private Context context;
	
	//归属地样式的资源列表
	int[] styles = new int[] { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green }; // 资源文件列表
	
	//handler对象
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case DISPLAY_LOCATION:
				//获取windowManager
				windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
				//创建一个LayoutParams对象来定义一个layout参数的集合
				WindowManager.LayoutParams params = new WindowManager.LayoutParams();
				params.height = WindowManager.LayoutParams.WRAP_CONTENT;
				params.width = WindowManager.LayoutParams.WRAP_CONTENT;
				params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
				params.format = PixelFormat.TRANSLUCENT;
				params.type = WindowManager.LayoutParams.TYPE_TOAST;
				params.setTitle("Toast");
				//创建一个TextView用于填充到自定义的Toast中
				view = View.inflate(context, R.layout.view_defined_toast, null);
				int style = sprefs.getInt("location_style", 0); //获取配置文件中的用户选的归属地样式
				view.setBackgroundResource(styles[style]); //设置归属地的样式
				// 获取view中的TextView, 将text填充其内
				TextView tv_location = (TextView) view.findViewById(R.id.view_defined_toast_tv_location);
				if (location != null)
					tv_location.setText(location);
				
				windowManager.addView(view, params); //将创建好的自定义Toast填充到windows中
				break;
				
			case REMOVE_LOCATION:
				if (view != null)
					windowManager.removeView(view); //5秒后移除归属地显示
				break;

			default:
				break;
			}
			
			super.handleMessage(msg);
		}
	};
	private String location;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		context = arg0;
		
		//获取配置文件
		sprefs = arg0.getSharedPreferences("sprefs", arg0.MODE_PRIVATE);
		
		//获取去电的号码
		String phone_number = getResultData();
		
		//获取归属地信息
		location = CheckTools.CheckPhoneNumberLocation(phone_number);
		
		//将查询到的结果显示出来
		DisplayTools.ShowToast(arg0, location);
		showDefinedToast(location);
			
	}
	
	/**
	 * 显示浮窗显示归属地
	 * @param text 要显示的文字
	 */
	public void showDefinedToast(final String text)
	{
		Message msg = new Message();
		msg.what = DISPLAY_LOCATION;
		handler.sendMessage(msg); //发送显示归属地的消息
		Message msg1 = new Message();
		msg1.what = REMOVE_LOCATION;
		handler.sendMessageDelayed(msg1, 5000); //5秒后发送移除归属地的消息
	}
}
