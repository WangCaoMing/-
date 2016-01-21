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

    // ����xUtils�⹤�߻�ȡ�����ļ��е����
    @ViewInject(R.id.layout_softwaremanager_tv_romfree)
    TextView tv_romfree;
    @ViewInject(R.id.layout_softwaremanager_tv_SDfree)
    TextView tv_SDfree;
    @ViewInject(R.id.layout_softwaremanager_lv)
    ListView lv;
    @ViewInject(R.id.layout_softwaremanager_tv_listview)
    private TextView tv_listview;

    // app��Ϣlist
    private List<AppInfoBean> appInfo;
    private List<AppInfoBean> sysappInfo;
    private List<AppInfoBean> userappInfo;

    // listview��������
    private MyListViewAdapter adapter;
    
    //listview��item����󵯳���Popupwindow
    private PopupWindow window;

    //�������item��app��info
    private AppInfoBean clicked_appinfo;
    // handler
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                // Ϊlistview����������
                adapter = new MyListViewAdapter();
                lv.setAdapter(adapter);
                
                
                // Ϊlistview����item�����¼��ļ���
                lv.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
                        
                        System.out.println("item" + arg2 + "�������");
                        //��ȡ�������Ŀ�������app����Ϣ
                        Object obj = lv.getItemAtPosition(arg2);
                        if (obj instanceof AppInfoBean)
                        {
                            clicked_appinfo = (AppInfoBean) obj;
                        }
                        else 
                            return; //�������Ĳ���һ��app����Ŀ, ��ʲôҲ����
                        
                        //���֮ǰ������Popupwindow, ������֮ǰ��window
                        if (window != null && window.isShowing())
                            window.dismiss();
                        //����һ��popopwindow
                        View view = View
                                .inflate(
                                        SoftwareManagerActivity.this,
                                        R.layout.layout_softwaremanager_listview_item_popwindow,
                                        null);
                        
                        //��ȡview�е����
                        ImageView iv_run  = (ImageView) view.findViewById(R.id.layout_softwaremanager_listview_item_popwindow_iv_run);
                        ImageView iv_uninstall = (ImageView) view.findViewById(R.id.layout_softwaremanager_listview_item_popwindow_iv_uninstall);
                        ImageView iv_share = (ImageView) view.findViewById(R.id.layout_softwaremanager_listview_item_popwindow_iv_share);
                        
                        iv_run.setOnClickListener(SoftwareManagerActivity.this);
                        iv_uninstall.setOnClickListener(SoftwareManagerActivity.this);
                        iv_share.setOnClickListener(SoftwareManagerActivity.this);
                        
                        window = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        //����Ϊwindow����һ������, ��������Ķ���û��Ч��
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //����һ��͸������, ����Ӱ����ʾЧ��
                        //��ȡ��ǰ�������item����Ļ�ϵ�λ��
                        int[] location = new int[2];
                        arg1.getLocationInWindow(location);
                        
                        window.showAtLocation(findViewById(R.id.layout_softwaremanager), Gravity.LEFT + Gravity.TOP, 70, location[1]);
                        //Ϊwindow���ö���Ч��
                        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                        scaleAnimation.setDuration(300);
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                        alphaAnimation.setDuration(300);
                        //���ö�����
                        AnimationSet animationSet = new AnimationSet(true);
                        animationSet.setDuration(300);
                        animationSet.addAnimation(scaleAnimation);
                        animationSet.addAnimation(alphaAnimation);
                        
                        //��ʼ����
                        view.startAnimation(animationSet);
                        
                    }
                    
                });

                // Ϊlistview���ù��������¼�
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
                            tv_listview.setText(" �û�Ӧ��(" + userappInfo.size()
                                    + ")");
                        } else {
                            tv_listview.setText(" ϵͳӦ��(" + sysappInfo.size()
                                    + ")");
                        }
                        
                        //�����Popupwindow����, ���ô���ʧ
                        if (window != null && window.isShowing())
                            window.dismiss();
                    }
                });

                // ����tv_listview�ĳ�ʼֵ
                tv_listview.setText(" �û�Ӧ��(" + userappInfo.size() + ")");
            }
        };

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_softwaremanager);

        // ����xUtils��Ĺ�������findviewbyid�Ĺ���
        ViewUtils.inject(this);

        // ��ʼ������
        initUI();

        // ��ʼ��listview���
        initData();
    }

    private void initData() {
        // ��ȡ����app��ϢΪδ֪�ĺ�ʱ����, ������Ҫ�ŵ����߳���ȥ
        new Thread() {
            public void run() {
                // ��ȡ���豸���а�װ��app��Ϣ
                appInfo = GetInfoTools.getAppInfo(SoftwareManagerActivity.this);
                // app��Ϣ��Ϊ����, ϵͳapp���û�app
                sysappInfo = new ArrayList<AppInfoBean>();
                userappInfo = new ArrayList<AppInfoBean>();
                for (AppInfoBean bean : appInfo) {
                    if (bean.isRom())
                        sysappInfo.add(bean);
                    else
                        userappInfo.add(bean);
                }

                // ��handler������Ϣ�Ա�ִ��֮��Ĳ���
                handler.sendEmptyMessage(0);
            };
        }.start();

    }
    
    @Override
    protected void onDestroy() {
      //�����Popupwindow����, ���ô���ʧ
        if (window != null && window.isShowing())
            window.dismiss();
        super.onDestroy();
    }
    

    /**
     * ��ʼ������
     */
    private void initUI() {

        // ��ȡ�ֻ��洢�ռ�ʣ����Ϣ
        long romfree = Environment.getDataDirectory().getFreeSpace(); // ��ȡrom��ʣ��ռ�
        String str_romfree = Formatter.formatFileSize(this, romfree); // ��ʽ��romʣ��ռ���Ϣ
        long SDfree = Environment.getExternalStorageDirectory().getFreeSpace();
        String str_SDfree = Formatter.formatFileSize(this, SDfree);

        // ����ȡ������Ϣ���õ������
        tv_romfree.setText("�ڴ����:" + str_romfree);
        tv_SDfree.setText("SD������:" + str_SDfree);

    }

    /**
     * listview��������
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
                // ����listview���û�Ӧ�õı���
                TextView tv_userapp = new TextView(SoftwareManagerActivity.this);
                tv_userapp.setText(" �û�Ӧ��(" + userappInfo.size() + ")");
                tv_userapp.setTextColor(Color.WHITE);
                tv_userapp.setBackgroundColor(new Color().rgb(153, 204, 0));
                tv_userapp.setTextSize(18);
                return tv_userapp;
            } else if (arg0 == userappInfo.size() + 1) {
                // ����listview��ϵͳӦ�õı���
                TextView tv_sysapp = new TextView(SoftwareManagerActivity.this);
                tv_sysapp.setText(" ϵͳӦ��(" + sysappInfo.size() + ")");
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

                // ��viewholder����arg1��
                arg1.setTag(viewHolder);

            }

            // ���û�Ӧ�ú�ϵͳӦ����ȡ����Ӧ��bean���в���
            AppInfoBean bean = null;
            if (arg0 <= userappInfo.size())
                bean = userappInfo.get(arg0 - 1);
            else if (arg0 > userappInfo.size() + 1) {
                int location = userappInfo.size() + 2;
                bean = sysappInfo.get(arg0 - location);
            }

            // ���������ֵ
            viewHolder.iv_icon.setImageDrawable(bean.getIcon());
            viewHolder.tv_name.setText(bean.getName());
            viewHolder.tv_isrom.setText("��װλ��:"
                    + (bean.isLocationSD() ? "SD��" : "�ֻ��ڴ�"));
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
     * ʵ��OnClickListener�еķ���.
     * @param v
     */
    @Override
    public void onClick(View v) {
        
        //�ر�popupwindow
        window.dismiss();
        Intent intent = null;
        //��ѯ����ľ�����Ŀ
        switch (v.getId()) {
        case R.id.layout_softwaremanager_listview_item_popwindow_iv_run:
            System.out.println("ִ������");
            //ִ������
            intent = new Intent();
            PackageManager pm = getPackageManager();
            
            //�ж�����Ƿ��Ѿ�����
            Intent intent_launched = pm.getLaunchIntentForPackage(clicked_appinfo.getPackagename());
            if (intent_launched != null)
            {
                startActivity(intent_launched);
                return;
            }
            
            //���û��������ִ������Ĵ���
            try {
            PackageInfo packInfo = pm.getPackageInfo(clicked_appinfo.getPackagename(), PackageManager.GET_ACTIVITIES);
                    ActivityInfo [] acivityInfos = packInfo.activities; //��ȡapp������activity����Ϣ
                    if(acivityInfos != null&&acivityInfos.length>0){
                        ActivityInfo activityInfo = acivityInfos[0];
                        intent.setClassName(clicked_appinfo.getPackagename(), activityInfo.name);
                        startActivity(intent);
                    }else{
                        Toast.makeText(this, "�������û�н���", 0).show();
                    }
                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(this, "���Ӧ���޷�����", 0).show();
                }
            break;
        case R.id.layout_softwaremanager_listview_item_popwindow_iv_uninstall:
            System.out.println("ִ��ж��");
            //ִ��ж��
            intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:"+clicked_appinfo.getPackagename()));
            startActivityForResult(intent, 1); 
            break;
        case R.id.layout_softwaremanager_listview_item_popwindow_iv_share:
            System.out.println("ִ�з���");
            
            intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ�һ��������У�"+clicked_appinfo.getName()+",���ص�ַ��ccc"+clicked_appinfo.getPackagename());
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
        case 1: //ִ��ж�غ�Ĳ���
            
            //ˢ�½���
            initUI();
            initData();
            adapter.notifyDataSetChanged();
            
            break;

        default:
            break;
        }
    }

    
    
}
