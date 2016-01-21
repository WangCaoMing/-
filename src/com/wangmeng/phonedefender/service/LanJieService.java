package com.wangmeng.phonedefender.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.bean.BlackNumberBean;
import com.wangmeng.phonedefender.dao.BlackNumberDao;
import com.wangmeng.phonedefender.receiver.MyDeviceAdminReceiver;

import android.R.integer;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * �������غ���������Ӧ����ģʽ�ĵ绰����ķ���
 * 
 * @author Administrator
 * 
 */
public class LanJieService extends Service {

    // ���ڷ��ʺ��������ݿ��dao
    private BlackNumberDao dao;

    // �㲥������
    private SMSreceiver smsreSreceiver;

    // �绰�������
    private TelephonyManager tm;
    private MyPhoneStateListener listener;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // ��ȡ��������dao
        dao = new BlackNumberDao(LanJieService.this);

        // ע����ŵĹ㲥�������������ض���
        IntentFilter SMSfilter = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");
        SMSfilter.setPriority(Integer.MAX_VALUE); // �������ص����ȼ�Ϊ���
        smsreSreceiver = new SMSreceiver();
        registerReceiver(smsreSreceiver, SMSfilter);

        // ������������
        tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                System.out.println("�绰������");
                // �鿴�绰�����Ƿ��ں�������
                BlackNumberBean bean = dao.find(incomingNumber);
                if (bean != null) {
                    // �鿴�õ绰�����Ƿ���Ҫ����
                    String mode = bean.getMode();
                    if ("1".equals(mode) || "3".equals(mode)) {
                        // ���ظõ绰
                        // ��ΪendCall()������Ա����׵��û��Σ��, ������Դ�������������������, �����Ҫʹ�ø÷���,
                        // ��Ҫͨ����������ȡԭ����TelephonyManager��ʹ��endCall����
                        try {
                            Class clazz = LanJieService.this.getClassLoader()
                                    .loadClass("android.os.ServiceManager");
                            Method method = clazz.getMethod("getService",
                                    String.class);
                            IBinder b = (IBinder) method.invoke(null,
                                    TELEPHONY_SERVICE);
                            ITelephony telephony = ITelephony.Stub
                                    .asInterface(b);
                            telephony.endCall(); // ����endCall()�������ر�����
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Log.println(1, "system.out",
                                    "������!" + e.getMessage());
                        }

                        // ���Զ���������ṩ��ɾ��ͨ����¼
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(
                                uri,
                                true,
                                new MyContentObserver(new Handler(),
                                        incomingNumber));

                    }
                }

                break;

            default:
                break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // �÷�������ʱͬʱ��ע��㲥������
        unregisterReceiver(smsreSreceiver);
        // ȡ��������ļ���
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * �Զ���������ṩ��
     * 
     * @author Administrator
     * 
     */
    private class MyContentObserver extends ContentObserver {
        private String number;

        public MyContentObserver(Handler handler, String number) {
            super(handler);
            this.number = number;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // ɾ��ͨ����¼
            deleteCallLog(number);
            getContentResolver().unregisterContentObserver(this);
        }

    }

    /**
     * ɾ��ͨ����¼
     */
    public void deleteCallLog(String number) {
        // TODO Auto-generated method stub
        // ɾ��ͨ����¼
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?", new String[] { number });
    }

    class SMSreceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // ��ȡ���ŵ����ݺͷ��Ͷ��ŵ�Դ��ַ
            Object[] objs = (Object[]) arg1.getExtras().get("pdus");
            for (Object object : objs) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                final String address = message.getOriginatingAddress(); // ��ȡ���Ͷ��ŵ��ֻ���
                String body = message.getMessageBody(); // ��ȡ���ŵ�����

                // �ж�Դ�ֻ����Ƿ��ں�������
                BlackNumberBean bean = null;
                bean = dao.find(address);
                if (bean != null) {
                    // ��ȡ����ģʽ
                    // 1 �绰����
                    // 2 ��������
                    // 3 �绰 + ��������
                    String mode = bean.getMode();

                    // �����ģʽ1, �򲻽�������, ��������
                    if ("1".equals(mode))
                        return;
                    else
                        abortBroadcast();
                }

            }
        }

    }

}
