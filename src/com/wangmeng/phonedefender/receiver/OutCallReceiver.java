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

	//����
	private static final int DISPLAY_LOCATION = 0; // ��ʾ��������Ϣ
	private static final int REMOVE_LOCATION = 1; // �Ƴ���������Ϣ
	
	//�����ļ�
	private static SharedPreferences sprefs;
	
	private WindowManager windowManager;
	private View view = null;
	private Context context;
	
	//��������ʽ����Դ�б�
	int[] styles = new int[] { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green }; // ��Դ�ļ��б�
	
	//handler����
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case DISPLAY_LOCATION:
				//��ȡwindowManager
				windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
				//����һ��LayoutParams����������һ��layout�����ļ���
				WindowManager.LayoutParams params = new WindowManager.LayoutParams();
				params.height = WindowManager.LayoutParams.WRAP_CONTENT;
				params.width = WindowManager.LayoutParams.WRAP_CONTENT;
				params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
				params.format = PixelFormat.TRANSLUCENT;
				params.type = WindowManager.LayoutParams.TYPE_TOAST;
				params.setTitle("Toast");
				//����һ��TextView������䵽�Զ����Toast��
				view = View.inflate(context, R.layout.view_defined_toast, null);
				int style = sprefs.getInt("location_style", 0); //��ȡ�����ļ��е��û�ѡ�Ĺ�������ʽ
				view.setBackgroundResource(styles[style]); //���ù����ص���ʽ
				// ��ȡview�е�TextView, ��text�������
				TextView tv_location = (TextView) view.findViewById(R.id.view_defined_toast_tv_location);
				if (location != null)
					tv_location.setText(location);
				
				windowManager.addView(view, params); //�������õ��Զ���Toast��䵽windows��
				break;
				
			case REMOVE_LOCATION:
				if (view != null)
					windowManager.removeView(view); //5����Ƴ���������ʾ
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
		
		//��ȡ�����ļ�
		sprefs = arg0.getSharedPreferences("sprefs", arg0.MODE_PRIVATE);
		
		//��ȡȥ��ĺ���
		String phone_number = getResultData();
		
		//��ȡ��������Ϣ
		location = CheckTools.CheckPhoneNumberLocation(phone_number);
		
		//����ѯ���Ľ����ʾ����
		DisplayTools.ShowToast(arg0, location);
		showDefinedToast(location);
			
	}
	
	/**
	 * ��ʾ������ʾ������
	 * @param text Ҫ��ʾ������
	 */
	public void showDefinedToast(final String text)
	{
		Message msg = new Message();
		msg.what = DISPLAY_LOCATION;
		handler.sendMessage(msg); //������ʾ�����ص���Ϣ
		Message msg1 = new Message();
		msg1.what = REMOVE_LOCATION;
		handler.sendMessageDelayed(msg1, 5000); //5������Ƴ������ص���Ϣ
	}
}
