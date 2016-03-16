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
    //常量字段
    private static final int CHECK_START = 0;
    private static final int CHECKING = 1;
    private static final int CHECK_END = 2;
    // handler
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case CHECK_START:
                tv_stat.setText("杀毒开始");
                break;

            case CHECKING:
                InfoBean bean = (InfoBean) msg.obj;
                String desc = bean.desc;
                //创建一个textview用于显示app杀毒结果
                TextView tv_result = new TextView(FindKillWorm.this);
                tv_result.setText(bean.appName + ((desc != null)?(" " + desc):""));
                //为创建的textview设置margin
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 5, 0, 0);
                tv_result.setLayoutParams(params);
                tv_result.setTextSize(18.0f);
                ll_showwork.addView(tv_result);
                sv_showwork.fullScroll(ScrollView.FOCUS_DOWN);
                pb.setProgress(msg.arg1);
                break;
            case CHECK_END:
                tv_stat.setText("杀毒完成!");
                iv_scan.setAnimation(null); // 停止扫描动画效果
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
        
        //初始化UI界面
        initUI();
        
        //初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //拷贝病毒数据库到files文件中
        copyAntivirusDB();
        //创建病毒数据库的Dao
        final FindKillWormDao database = new FindKillWormDao(this);
    
        //开启新的线程用于安装app和病毒库比对
        new Thread(){
            public void run() {
                
                //改变显示状态
                handler.sendEmptyMessage(CHECK_START);
                
                PackageManager packageManager = FindKillWorm.this.getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
                //为进度条设置最大值
                pb.setMax(installedPackages.size());
                pb.setProgress(0);
                int count = 0; //用来统计以处理的条目数
                for (PackageInfo p : installedPackages) {
                    count++;
                  //创建app检查结果信息用于显示
                    InfoBean bean = new InfoBean();
                    //获取应用的名字
                    String name = p.applicationInfo.loadLabel(packageManager).toString();
                    String md5 = GetInfoTools.getFileMd5(new File(p.applicationInfo.sourceDir));
                    System.out.println("应用名:" + name + ", MD5:" + md5); 
                    //查询病毒数据库
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
                
                //向handler发送检查完成消息
                handler.sendEmptyMessage(CHECK_END);
                //关闭数据库连接
                database.closeDatabase();
            };
        }.start();
        
    }

    /**
     * 初始化界面中的组件
     */
    private void initUI() {
        //获取页面上的组件
        iv_scan = (ImageView) findViewById(R.id.layout_findkillworm_fl_iv_scan);
        tv_stat = (TextView) findViewById(R.id.layout_findkillworm_tv_showstat);
        pb = (ProgressBar) findViewById(R.id.layout_findkillworm_pb);
        ll_showwork = (LinearLayout) findViewById(R.id.layout_findkillworm_ll_showwork);
        sv_showwork = (ScrollView) findViewById(R.id.layout_findkillworm_sv_showwork);
        //为动画设置循环旋转效果
        RotateAnimation animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator()); //设置速度类型为线性
        iv_scan.setAnimation(animation);
    }
    
    /**
     * 拷贝病毒数据库到files文件夹中
     */
    public void copyAntivirusDB()
    {
        try {
            //打开要copy文件的输入流
            InputStream in = this.getAssets().open("antivirus.db");
            //打开输出流
            File file = new File(this.getFilesDir().getAbsolutePath().toString(), "antivirus.db");
            OutputStream out = new FileOutputStream(file);
            
            //执行拷贝动作
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
