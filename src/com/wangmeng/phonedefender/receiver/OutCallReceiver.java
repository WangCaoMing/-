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
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
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
	
	//onTouch事件中的变量, 用于保存坐标
    private int start_x;
    private int start_y;
    private int window_width;
    private int window_height;
    
    private WindowManager.LayoutParams params;
    
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
			    //在电话界面弹出alert需要权限: android.permission.SYSTEM_ALERT_WINDOW
				//获取windowManager
				windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
				//创建一个LayoutParams对象来定义一个layout参数的集合
				
				//获取屏幕的宽度和高度
				window_width = windowManager.getDefaultDisplay().getWidth();
				window_height = windowManager.getDefaultDisplay().getHeight();
				
				params = new WindowManager.LayoutParams();
				params.height = WindowManager.LayoutParams.WRAP_CONTENT;
				params.width = WindowManager.LayoutParams.WRAP_CONTENT;
				params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
				params.format = PixelFormat.TRANSLUCENT;
				params.type = WindowManager.LayoutParams.TYPE_TOAST;
				params.setTitle("Toast"); 
				
		        //将组件的位置更新为用户设置的位置
	                //1. 获取配置文件中保存的用户设定的位置
    	        int location_l = sprefs.getInt("location_l", 0); 
    	        int location_t = sprefs.getInt("location_t", 0);
    	            //2. 将组件的初始位置设定为window的左上方
    	        params.gravity = Gravity.TOP + Gravity.LEFT;
    	            //将用户设定的值更新到组件之上
    	        params.x = location_l; //该值是以当前组件所在的坐标为起始坐标(组件的左上角为(0,0)点, 然后再加上给定的值)
    	        params.y = location_t; //所以在使用的时候先将该组件初始化到屏幕的左上角(第2.步就是干这个的), 然后再设定新值就是以屏幕左上角为起始点了.
				
				//创建一个TextView用于填充到自定义的Toast中
				view = View.inflate(context, R.layout.view_defined_toast, null);
				int style = sprefs.getInt("location_style", 0); //获取配置文件中的用户选的归属地样式
				view.setBackgroundResource(styles[style]); //设置归属地的样式
				// 获取view中的TextView, 将text填充其内
				TextView tv_location = (TextView) view.findViewById(R.id.view_defined_toast_tv_location);
				if (location != null)
					tv_location.setText(location);
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
                            
                            //判断params.x和params.y的值, 避免其越过边界
                            if (params.x < 0)
                                params.x = 0;
                            if (params.y < 0)
                                params.y = 0;
                            if (params.x > window_width - view.getWidth())
                                params.x = window_width - view.getWidth();
                            if (params.y > window_height - 50 - view.getHeight())
                                params.y = window_height - 50 - view.getHeight();
                                
                            
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
