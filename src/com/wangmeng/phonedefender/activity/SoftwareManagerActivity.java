package com.wangmeng.phonedefender.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.bean.AppInfoBean;
import com.wangmeng.phonedefender.tools.GetInfoTools;

public class SoftwareManagerActivity extends Activity implements OnClickListener {

    // 利用xUtils库工具获取布局文件中的组件
    @ViewInject(R.id.layout_softwaremanager_tv_romfree)
    TextView tv_romfree;
    @ViewInject(R.id.layout_softwaremanager_tv_SDfree)
    TextView tv_SDfree;
    @ViewInject(R.id.layout_softwaremanager_lv)
    ListView lv;
    @ViewInject(R.id.layout_softwaremanager_tv_listview)
    private TextView tv_listview;

    // app信息list
    private List<AppInfoBean> appInfo;
    private List<AppInfoBean> sysappInfo;
    private List<AppInfoBean> userappInfo;

    // listview的适配器
    private MyListViewAdapter adapter;
    
    //listview的item点击后弹出的Popupwindow
    private PopupWindow window;

    //被点击的item的app的info
    private AppInfoBean clicked_appinfo;
    // handler
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                // 为listview设置适配器
                adapter = new MyListViewAdapter();
                lv.setAdapter(adapter);
                
                
                // 为listview设置item单击事件的监听
                lv.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
                        
                        System.out.println("item" + arg2 + "被点击了");
                        //获取被点击条目所代表的app的信息
                        Object obj = lv.getItemAtPosition(arg2);
                        if (obj instanceof AppInfoBean)
                        {
                            clicked_appinfo = (AppInfoBean) obj;
                        }
                        else 
                            return; //如果点击的不是一个app的条目, 则什么也不做
                        
                        //如果之前弹出过Popupwindow, 则销毁之前的window
                        if (window != null && window.isShowing())
                            window.dismiss();
                        //创建一个popopwindow
                        View view = View
                                .inflate(
                                        SoftwareManagerActivity.this,
                                        R.layout.layout_softwaremanager_listview_item_popwindow,
                                        null);
                        
                        //获取view中的组件
                        ImageView iv_run  = (ImageView) view.findViewById(R.id.layout_softwaremanager_listview_item_popwindow_iv_run);
                        ImageView iv_uninstall = (ImageView) view.findViewById(R.id.layout_softwaremanager_listview_item_popwindow_iv_uninstall);
                        ImageView iv_share = (ImageView) view.findViewById(R.id.layout_softwaremanager_listview_item_popwindow_iv_share);
                        
                        iv_run.setOnClickListener(SoftwareManagerActivity.this);
                        iv_uninstall.setOnClickListener(SoftwareManagerActivity.this);
                        iv_share.setOnClickListener(SoftwareManagerActivity.this);
                        
                        window = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        //必须为window设置一个背景, 否则下面的动画没有效果
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //设置一个透明背景, 以免影响显示效果
                        //获取当前被点击的item在屏幕上的位置
                        int[] location = new int[2];
                        arg1.getLocationInWindow(location);
                        
                        window.showAtLocation(findViewById(R.id.layout_softwaremanager), Gravity.LEFT + Gravity.TOP, 70, location[1]);
                        //为window设置动画效果
                        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                        scaleAnimation.setDuration(300);
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                        alphaAnimation.setDuration(300);
                        //设置动画集
                        AnimationSet animationSet = new AnimationSet(true);
                        animationSet.setDuration(300);
                        animationSet.addAnimation(scaleAnimation);
                        animationSet.addAnimation(alphaAnimation);
                        
                        //开始动画
                        view.startAnimation(animationSet);
                        
                    }
                    
                });

                // 为listview设置滚动监听事件
                lv.setOnScrollListener(new OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(AbsListView view,
                            int scrollState) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onScroll(AbsListView view,
                            int firstVisibleItem, int visibleItemCount,
                            int totalItemCount) {
                        if (firstVisibleItem <= userappInfo.size()) {
                            tv_listview.setText(" 用户应用(" + userappInfo.size()
                                    + ")");
                        } else {
                            tv_listview.setText(" 系统应用(" + sysappInfo.size()
                                    + ")");
                        }
                        
                        //如果有Popupwindow存在, 则让此消失
                        if (window != null && window.isShowing())
                            window.dismiss();
                    }
                });

                // 设置tv_listview的初始值
                tv_listview.setText(" 用户应用(" + userappInfo.size() + ")");
            }
        };

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_softwaremanager);

        // 利用xUtils库的工具来简化findviewbyid的工作
        ViewUtils.inject(this);

        // 初始化界面
        initUI();

        // 初始化listview组件
        initData();
    }

    private void initData() {
        // 读取所有app信息为未知的耗时操作, 所以需要放到子线程中去
        new Thread() {
            public void run() {
                // 获取该设备所有安装的app信息
                appInfo = GetInfoTools.getAppInfo(SoftwareManagerActivity.this);
                // app信息分为两类, 系统app和用户app
                sysappInfo = new ArrayList<AppInfoBean>();
                userappInfo = new ArrayList<AppInfoBean>();
                for (AppInfoBean bean : appInfo) {
                    if (bean.isRom())
                        sysappInfo.add(bean);
                    else
                        userappInfo.add(bean);
                }

                // 向handler发送消息以便执行之后的操作
                handler.sendEmptyMessage(0);
            };
        }.start();

    }
    
    @Override
    protected void onDestroy() {
      //如果有Popupwindow存在, 则让此消失
        if (window != null && window.isShowing())
            window.dismiss();
        super.onDestroy();
    }
    

    /**
     * 初始化界面
     */
    private void initUI() {

        // 获取手机存储空间剩余信息
        long romfree = Environment.getDataDirectory().getFreeSpace(); // 获取rom的剩余空间
        String str_romfree = Formatter.formatFileSize(this, romfree); // 格式化rom剩余空间信息
        long SDfree = Environment.getExternalStorageDirectory().getFreeSpace();
        String str_SDfree = Formatter.formatFileSize(this, SDfree);

        // 将获取到的信息设置到组件上
        tv_romfree.setText("内存可用:" + str_romfree);
        tv_SDfree.setText("SD卡可用:" + str_SDfree);

    }

    /**
     * listview的适配器
     * 
     * @author Administrator
     * 
     */
    class MyListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return appInfo.size() + 2;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            if (arg0 == 0 || arg0 == userappInfo.size() + 1)
                return null;
            else {
                if (arg0 <= userappInfo.size())
                    return userappInfo.get(arg0 - 1);
                else if (arg0 > userappInfo.size() + 1) {
                    int location = userappInfo.size() + 2;
                    return sysappInfo.get(arg0 - location);
                } else
                    return null;
            }

        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {

            if (arg0 == 0) {
                // 设置listview中用户应用的标题
                TextView tv_userapp = new TextView(SoftwareManagerActivity.this);
                tv_userapp.setText(" 用户应用(" + userappInfo.size() + ")");
                tv_userapp.setTextColor(Color.WHITE);
                tv_userapp.setBackgroundColor(new Color().rgb(153, 204, 0));
                tv_userapp.setTextSize(18);
                return tv_userapp;
            } else if (arg0 == userappInfo.size() + 1) {
                // 设置listview中系统应用的标题
                TextView tv_sysapp = new TextView(SoftwareManagerActivity.this);
                tv_sysapp.setText(" 系统应用(" + sysappInfo.size() + ")");
                tv_sysapp.setTextColor(Color.WHITE);
                tv_sysapp.setBackgroundColor(new Color().rgb(153, 204, 0));
                tv_sysapp.setTextSize(18);
                return tv_sysapp;
            }

            ViewHolder viewHolder;
            if (arg1 != null && arg1 instanceof LinearLayout) {
                viewHolder = (ViewHolder) arg1.getTag();

            } else {
                arg1 = View.inflate(SoftwareManagerActivity.this,
                        R.layout.layout_softwaremanager_listview_item, null);

                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) arg1
                        .findViewById(R.id.layout_softwaremanager_listview_item_iv_icon);
                viewHolder.tv_name = (TextView) arg1
                        .findViewById(R.id.layout_softwaremanager_listview_item_tv_name);
                viewHolder.tv_isrom = (TextView) arg1
                        .findViewById(R.id.layout_softwaremanager_listview_item_tv_isRom);
                viewHolder.tv_size = (TextView) arg1
                        .findViewById(R.id.layout_softwaremanager_listview_item_tv_size);

                // 将viewholder放入arg1中
                arg1.setTag(viewHolder);

            }

            // 从用户应用和系统应用中取出对应的bean进行操作
            AppInfoBean bean = null;
            if (arg0 <= userappInfo.size())
                bean = userappInfo.get(arg0 - 1);
            else if (arg0 > userappInfo.size() + 1) {
                int location = userappInfo.size() + 2;
                bean = sysappInfo.get(arg0 - location);
            }

            // 设置组件的值
            viewHolder.iv_icon.setImageDrawable(bean.getIcon());
            viewHolder.tv_name.setText(bean.getName());
            viewHolder.tv_isrom.setText("安装位置:"
                    + (bean.isLocationSD() ? "SD卡" : "手机内存"));
            viewHolder.tv_size.setText(bean.getSize());
            return arg1;
        }
    }

    class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_name;
        public TextView tv_isrom;
        public TextView tv_size;
        public Button bt_uninstall;
    }

    /**
     * 实现OnClickListener中的方法.
     * @param v
     */
    @Override
    public void onClick(View v) {
        
        //关闭popupwindow
        window.dismiss();
        Intent intent = null;
        //查询点击的具体条目
        switch (v.getId()) {
        case R.id.layout_softwaremanager_listview_item_popwindow_iv_run:
            System.out.println("执行启动");
            //执行启动
            intent = new Intent();
            PackageManager pm = getPackageManager();
            
            //判断软件是否已经启动
            Intent intent_launched = pm.getLaunchIntentForPackage(clicked_appinfo.getPackagename());
            if (intent_launched != null)
            {
                startActivity(intent_launched);
                return;
            }
            
            //如果没有启动则执行下面的代码
            try {
            PackageInfo packInfo = pm.getPackageInfo(clicked_appinfo.getPackagename(), PackageManager.GET_ACTIVITIES);
                    ActivityInfo [] acivityInfos = packInfo.activities; //获取app中所有activity的信息
                    if(acivityInfos != null&&acivityInfos.length>0){
                        ActivityInfo activityInfo = acivityInfos[0];
                        intent.setClassName(clicked_appinfo.getPackagename(), activityInfo.name);
                        startActivity(intent);
                    }else{
                        Toast.makeText(this, "这个程序没有界面", 0).show();
                    }
                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(this, "这个应用无法启动", 0).show();
                }
            break;
        case R.id.layout_softwaremanager_listview_item_popwindow_iv_uninstall:
            System.out.println("执行卸载");
            //执行卸载
            intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:"+clicked_appinfo.getPackagename()));
            startActivityForResult(intent, 1); 
            break;
        case R.id.layout_softwaremanager_listview_item_popwindow_iv_share:
            System.out.println("执行分享");
            
            intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "推荐一款软件名叫："+clicked_appinfo.getName()+",下载地址：ccc"+clicked_appinfo.getPackagename());
            startActivity(intent) ;
            break;
        default:
            break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode) {
        case 1: //执行卸载后的操作
            
            //刷新界面
            initUI();
            initData();
            adapter.notifyDataSetChanged();
            
            break;

        default:
            break;
        }
    }

    
    
}
