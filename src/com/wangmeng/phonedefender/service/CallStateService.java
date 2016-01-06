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
 * ���ڼ�������ĵ绰״̬�ķ���, ���������ȥ��
 * @author Administrator
 *
 */
public class CallStateService extends Service {

	//�����ļ�
	private static SharedPreferences sprefs;
	
	//��������ʽ����Դ�б�
	int[] styles = new int[] { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green }; // ��Դ�ļ��б�
	
	private TelephonyManager manager;
	private MyPhoneStateListener listener;
	
	private WindowManager windowManager;
	private View view = null;
	
	//ȥ��Ĺ㲥������
	private OutCallReceiver receiver;

    //onTouch�¼��еı���, ���ڱ�������
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
		
		//��ȡ�����ļ�
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);
		
		//��ϵͳ�����л�ȡ�绰������
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE); //Ϊ�绰���������ü���
		
		//����ȥ��㲥�����ߵļ���, ��ע��ù㲥������
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter); //ע��㲥������
	} 
	
	class MyPhoneStateListener extends PhoneStateListener
	{
		//���µ绰״̬�ı�ķ���
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("���绰��");
				String location = CheckTools.CheckPhoneNumberLocation(incomingNumber);
				if (location != null)
				{
					Toast.makeText(CallStateService.this, location, Toast.LENGTH_SHORT).show();
					showDefinedToast(location);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				System.out.println("�绰��������");
				
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println("�绰�ִ��ڿ��е�״̬��");
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
		//�������������ʱ, ��Ҫ�����Ե绰״̬�ļ���
		manager.listen(listener, PhoneStateListener.LISTEN_NONE); 
		//�������������ʱ, ȥ��Ĺ㲥������Ҳ��Ҫ��ע��
		unregisterReceiver(receiver);
	}
	
	/**
	 * ��ʾ������ʾ������
	 * �ڵ绰���浯��alert��ҪȨ��: android.permission.SYSTEM_ALERT_WINDOW
	 * @param text Ҫ��ʾ������
	 */
	public void showDefinedToast(String text)
	{
		//��ȡwindowManager
		
		windowManager = (WindowManager) getSystemService(this.WINDOW_SERVICE);
		//����һ��LayoutParams����������һ��layout�����ļ���
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; //�����ڵ绰���ڽ��н���
		params.setTitle("Toast");
		
		//�������λ�ø���Ϊ�û����õ�λ��
		    //��ȡ�����ļ��б�����û��趨��λ��
		int location_l = sprefs.getInt("location_l", 0); 
		int location_t = sprefs.getInt("location_t", 0);
		    //������ĳ�ʼλ���趨Ϊwindow�����Ϸ�
		params.gravity = Gravity.TOP + Gravity.LEFT;
		    //���û��趨��ֵ���µ����֮��
		params.x = location_l;
		params.y = location_t;
		
		//����һ��TextView������䵽�Զ����Toast��
		view = View.inflate(this, R.layout.view_defined_toast, null);
		    //�����û����������趨�����������style
		int style = sprefs.getInt("location_style", 0); //��ȡ�����ļ��е��û�ѡ�Ĺ�������ʽ
		view.setBackgroundResource(styles[style]); //���ù����ص���ʽ
		    //Ϊview����onTouch�¼�, ʵ�ֿ���ק��Ч��
		view.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                case MotionEvent.ACTION_DOWN:
                    //��ȡ�û�����ʱ������
                    start_x = (int) event.getRawX();
                    start_y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //��ȡ�����������
                    int end_x = (int) event.getRawX();
                    int end_y = (int) event.getRawY();
                    
                    //����ƫ����
                    int dx = end_x - start_x;
                    int dy = end_y - start_y;
                    
                    //��ƫ�������µ����֮��
                    params.x += dx;
                    params.y += dy;
                    
                    //����view��window�еĲ�����Ϣ
                    windowManager.updateViewLayout(view, params);
                    
                    //���¿�ʼ����
                    start_x = (int) event.getRawX();
                    start_y = (int) event.getRawY();
                    
                    break;
                case MotionEvent.ACTION_UP:
                    //�����º��ֵд�������ļ���
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
		// ��ȡview�е�TextView, ��text�������
		TextView tv_location = (TextView) view.findViewById(R.id.view_defined_toast_tv_location);
		tv_location.setText(text);
		
		windowManager.addView(view, params); //�������õ��Զ���Toast��䵽windows��			
	}
	

}
