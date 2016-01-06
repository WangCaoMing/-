package com.wangmeng.phonedefender.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.receiver.OutCallReceiver;
import com.wangmeng.phonedefender.tools.CheckTools;

/**
 * 用于监听来电的电话状态的服务, 包括来电和去电
 * @author Administrator
 *
 */
public class CallStateService extends Service {

	//配置文件
	private static SharedPreferences sprefs;
	
	//归属地样式的资源列表
	int[] styles = new int[] { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green }; // 资源文件列表
	
	private TelephonyManager manager;
	private MyPhoneStateListener listener;
	
	private WindowManager windowManager;
	private View view = null;
	
	//去电的广播接收者
	private OutCallReceiver receiver;

    //onTouch事件中的变量, 用于保存坐标
    private int start_x;
    private int start_y;
    private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//获取配置文件
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);
		
		//从系统服务中获取电话管理器
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE); //为电话管理器设置监听
		
		//创建去电广播接收者的监听, 并注册该广播接收者
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter); //注册广播接受者
	} 
	
	class MyPhoneStateListener extends PhoneStateListener
	{
		//重新电话状态改变的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("来电话了");
				String location = CheckTools.CheckPhoneNumberLocation(incomingNumber);
				if (location != null)
				{
					Toast.makeText(CallStateService.this, location, Toast.LENGTH_SHORT).show();
					showDefinedToast(location);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				System.out.println("电话被接听了");
				
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println("电话又处于空闲的状态了");
				if (view != null)
					windowManager.removeView(view);
			default:
				break;
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//当这个服务销毁时, 需要结束对电话状态的监听
		manager.listen(listener, PhoneStateListener.LISTEN_NONE); 
		//当这个服务销毁时, 去电的广播接收者也需要反注册
		unregisterReceiver(receiver);
	}
	
	/**
	 * 显示浮窗显示归属地
	 * 在电话界面弹出alert需要权限: android.permission.SYSTEM_ALERT_WINDOW
	 * @param text 要显示的文字
	 */
	public void showDefinedToast(String text)
	{
		//获取windowManager
		
		windowManager = (WindowManager) getSystemService(this.WINDOW_SERVICE);
		//创建一个LayoutParams对象来定义一个layout参数的集合
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; //用于在电话窗口进行交互
		params.setTitle("Toast");
		
		//将组件的位置更新为用户设置的位置
		    //获取配置文件中保存的用户设定的位置
		int location_l = sprefs.getInt("location_l", 0); 
		int location_t = sprefs.getInt("location_t", 0);
		    //将组件的初始位置设定为window的左上方
		params.gravity = Gravity.TOP + Gravity.LEFT;
		    //将用户设定的值更新到组件之上
		params.x = location_l;
		params.y = location_t;
		
		//创建一个TextView用于填充到自定义的Toast中
		view = View.inflate(this, R.layout.view_defined_toast, null);
		    //根据用户的设置来设定归属地组件的style
		int style = sprefs.getInt("location_style", 0); //获取配置文件中的用户选的归属地样式
		view.setBackgroundResource(styles[style]); //设置归属地的样式
		    //为view设置onTouch事件, 实现可拖拽的效果
		view.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                case MotionEvent.ACTION_DOWN:
                    //获取用户按下时的坐标
                    start_x = (int) event.getRawX();
                    start_y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //获取结束点的坐标
                    int end_x = (int) event.getRawX();
                    int end_y = (int) event.getRawY();
                    
                    //计算偏移量
                    int dx = end_x - start_x;
                    int dy = end_y - start_y;
                    
                    //将偏移量更新到组件之上
                    params.x += dx;
                    params.y += dy;
                    
                    //更新view在window中的参数信息
                    windowManager.updateViewLayout(view, params);
                    
                    //更新开始坐标
                    start_x = (int) event.getRawX();
                    start_y = (int) event.getRawY();
                    
                    break;
                case MotionEvent.ACTION_UP:
                    //将更新后的值写到配置文件中
                    Editor editor = sprefs.edit();
                    editor.putInt("location_l", params.x);
                    editor.putInt("location_t", params.y);
                    editor.commit();
                    break;
                default:
                    break;
                }
                return true;
            }
        });
		// 获取view中的TextView, 将text填充其内
		TextView tv_location = (TextView) view.findViewById(R.id.view_defined_toast_tv_location);
		tv_location.setText(text);
		
		windowManager.addView(view, params); //将创建好的自定义Toast填充到windows中			
	}
	

}
