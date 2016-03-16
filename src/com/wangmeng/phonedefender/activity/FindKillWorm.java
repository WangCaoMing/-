package com.wangmeng.phonedefender.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.dao.FindKillWormDao;
import com.wangmeng.phonedefender.tools.ConvertTools;
import com.wangmeng.phonedefender.tools.GetInfoTools;
import com.wangmeng.phonedefender.tools.TransportTools;

public class FindKillWorm extends Activity {
    
    private ImageView iv_scan;
    private TextView tv_stat;
    private ProgressBar pb;
    private LinearLayout ll_showwork;
    //�����ֶ�
    private static final int CHECK_START = 0;
    private static final int CHECKING = 1;
    private static final int CHECK_END = 2;
    // handler
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case CHECK_START:
                tv_stat.setText("ɱ����ʼ");
                break;

            case CHECKING:
                InfoBean bean = (InfoBean) msg.obj;
                String desc = bean.desc;
                //����һ��textview������ʾappɱ�����
                TextView tv_result = new TextView(FindKillWorm.this);
                tv_result.setText(bean.appName + ((desc != null)?(" " + desc):""));
                //Ϊ������textview����margin
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 5, 0, 0);
                tv_result.setLayoutParams(params);
                tv_result.setTextSize(18.0f);
                ll_showwork.addView(tv_result);
                sv_showwork.fullScroll(ScrollView.FOCUS_DOWN);
                pb.setProgress(msg.arg1);
                break;
            case CHECK_END:
                tv_stat.setText("ɱ�����!");
                iv_scan.setAnimation(null); // ֹͣɨ�趯��Ч��
                break;
            }
        };
    };
    private ScrollView sv_showwork;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_findkillworm);
        
        //��ʼ��UI����
        initUI();
        
        //��ʼ������
        initData();
    }

    /**
     * ��ʼ������
     */
    private void initData() {
        //�����������ݿ⵽files�ļ���
        copyAntivirusDB();
        //�����������ݿ��Dao
        final FindKillWormDao database = new FindKillWormDao(this);
    
        //�����µ��߳����ڰ�װapp�Ͳ�����ȶ�
        new Thread(){
            public void run() {
                
                //�ı���ʾ״̬
                handler.sendEmptyMessage(CHECK_START);
                
                PackageManager packageManager = FindKillWorm.this.getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
                //Ϊ�������������ֵ
                pb.setMax(installedPackages.size());
                pb.setProgress(0);
                int count = 0; //����ͳ���Դ������Ŀ��
                for (PackageInfo p : installedPackages) {
                    count++;
                  //����app�������Ϣ������ʾ
                    InfoBean bean = new InfoBean();
                    //��ȡӦ�õ�����
                    String name = p.applicationInfo.loadLabel(packageManager).toString();
                    String md5 = GetInfoTools.getFileMd5(new File(p.applicationInfo.sourceDir));
                    System.out.println("Ӧ����:" + name + ", MD5:" + md5); 
                    //��ѯ�������ݿ�
                    String result = database.CheckAppMD5(md5);
                    if (result != null)
                    {
                        bean.desc = result;
                        bean.isVirus = true;
                    }
                    bean.appName = name;
                    Message msg = Message.obtain();
                    msg.what = CHECKING;
                    msg.obj = bean;
                    msg.arg1 = count;
                    handler.sendMessage(msg);
                    
                }
                
                //��handler���ͼ�������Ϣ
                handler.sendEmptyMessage(CHECK_END);
                //�ر����ݿ�����
                database.closeDatabase();
            };
        }.start();
        
    }

    /**
     * ��ʼ�������е����
     */
    private void initUI() {
        //��ȡҳ���ϵ����
        iv_scan = (ImageView) findViewById(R.id.layout_findkillworm_fl_iv_scan);
        tv_stat = (TextView) findViewById(R.id.layout_findkillworm_tv_showstat);
        pb = (ProgressBar) findViewById(R.id.layout_findkillworm_pb);
        ll_showwork = (LinearLayout) findViewById(R.id.layout_findkillworm_ll_showwork);
        sv_showwork = (ScrollView) findViewById(R.id.layout_findkillworm_sv_showwork);
        //Ϊ��������ѭ����תЧ��
        RotateAnimation animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator()); //�����ٶ�����Ϊ����
        iv_scan.setAnimation(animation);
    }
    
    /**
     * �����������ݿ⵽files�ļ�����
     */
    public void copyAntivirusDB()
    {
        try {
            //��Ҫcopy�ļ���������
            InputStream in = this.getAssets().open("antivirus.db");
            //�������
            File file = new File(this.getFilesDir().getAbsolutePath().toString(), "antivirus.db");
            OutputStream out = new FileOutputStream(file);
            
            //ִ�п�������
            TransportTools.In2OutStream(in, out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
class InfoBean
{
    public boolean isVirus = false;
    public String appName = null;
    public String desc = null;
}
