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
 * 用于拦截黑名单中相应拦截模式的电话号码的服务
 * 
 * @author Administrator
 * 
 */
public class LanJieService extends Service {

    // 用于访问黑名单数据库的dao
    private BlackNumberDao dao;

    // 广播接收者
    private SMSreceiver smsreSreceiver;

    // 电话管理服务
    private TelephonyManager tm;
    private MyPhoneStateListener listener;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 获取黑名单的dao
        dao = new BlackNumberDao(LanJieService.this);

        // 注册短信的广播接收者用于拦截短信
        IntentFilter SMSfilter = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");
        SMSfilter.setPriority(Integer.MAX_VALUE); // 设置拦截的优先级为最大
        smsreSreceiver = new SMSreceiver();
        registerReceiver(smsreSreceiver, SMSfilter);

        // 用于拦截来电
        tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                System.out.println("电话铃响了");
                // 查看电话号码是否在黑名单内
                BlackNumberBean bean = dao.find(incomingNumber);
                if (bean != null) {
                    // 查看该电话号码是否需要拦截
                    String mode = bean.getMode();
                    if ("1".equals(mode) || "3".equals(mode)) {
                        // 拦截该电话
                        // 因为endCall()如果可以被轻易调用会很危险, 所以在源码里这个方法被隐藏了, 如果想要使用该方法,
                        // 需要通过反射来获取原生的TelephonyManager来使用endCall方法
                        try {
                            Class clazz = LanJieService.this.getClassLoader()
                                    .loadClass("android.os.ServiceManager");
                            Method method = clazz.getMethod("getService",
                                    String.class);
                            IBinder b = (IBinder) method.invoke(null,
                                    TELEPHONY_SERVICE);
                            ITelephony telephony = ITelephony.Stub
                                    .asInterface(b);
                            telephony.endCall(); // 调用endCall()方法来关闭来电
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Log.println(1, "system.out",
                                    "出错了!" + e.getMessage());
                        }

                        // 用自定义的内容提供者删除通话记录
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
        // 该服务销毁时同时反注册广播接收者
        unregisterReceiver(smsreSreceiver);
        // 取消对来电的监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * 自定义的内容提供者
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
            // 删除通话记录
            deleteCallLog(number);
            getContentResolver().unregisterContentObserver(this);
        }

    }

    /**
     * 删除通话记录
     */
    public void deleteCallLog(String number) {
        // TODO Auto-generated method stub
        // 删除通话记录
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?", new String[] { number });
    }

    class SMSreceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // 获取短信的内容和发送短信的源地址
            Object[] objs = (Object[]) arg1.getExtras().get("pdus");
            for (Object object : objs) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                final String address = message.getOriginatingAddress(); // 获取发送短信的手机号
                String body = message.getMessageBody(); // 获取短信的内容

                // 判断源手机号是否在黑名单内
                BlackNumberBean bean = null;
                bean = dao.find(address);
                if (bean != null) {
                    // 获取拦截模式
                    // 1 电话拦截
                    // 2 短信拦截
                    // 3 电话 + 短信拦截
                    String mode = bean.getMode();

                    // 如果是模式1, 则不进行拦截, 否则拦截
                    if ("1".equals(mode))
                        return;
                    else
                        abortBroadcast();
                }

            }
        }

    }

}
