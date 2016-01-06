package com.wangmeng.phonedefender.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wangmeng.phonedefender.R;
import com.wangmeng.phonedefender.bean.BlackNumberBean;
import com.wangmeng.phonedefender.dao.BlackNumberDao;
import com.wangmeng.phonedefender.tools.DisplayTools;

/**
 * 通信卫士界面
 * 
 * @author Administrator
 * 
 */
public class CallDefenderActivity extends Activity {

    // 布局文件中的组件
    private ListView lv;
    private LinearLayout ll;
    private TextView tv_currentinfo;
    private EditText et_input;

    // 黑名单数据列表
    private List<BlackNumberBean> list;
    private MyAdapter adapter;

    // 连接数据库的Dao
    private BlackNumberDao dao;

    // 分页的数据
    private static int current_page = 0;
    private static int page_size = 9;
    private static int page_count = 0;
    private static int data_count = 0;

    // handler
    private Handler handler = new Handler() {
        
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                // 设置进度条为不可见
                ll.setVisibility(View.GONE);

                adapter = new MyAdapter();
                lv.setAdapter(adapter);

                // 更新底部工具栏的状态
                adapter.notifyDataSetChanged();
                updateToolbar();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化UI界面
        initUI();

        // 加载数据
        loadData();
    }

    /**
     * 加载数据库中的黑名单数据
     */
    private void loadData() {
        // 设置进度条为可见
        ll.setVisibility(View.VISIBLE);

        Thread thread = new Thread() {
            @Override
            public void run() {
                /**
                 * 将这些操作放在子线程中是因为考虑到数据比较大的情况, 这是一个耗时操作, 不宜放在主线程中以防主线程阻塞
                 */
                // 获取黑名单的本页数据
                list = dao.findLimit(current_page, page_size);

                // 通知主线程执行相应的操作
                handler.sendEmptyMessage(1);
                super.run();
            }
        };
        thread.start();
    }

    /**
     * 初始化UI界面
     */
    private void initUI() {

        // 加载布局文件
        setContentView(R.layout.layout_call_safe);

        // 获取布局文件中的组件
        lv = (ListView) this.findViewById(R.id.layout_call_safe_lv);
        ll = (LinearLayout) this.findViewById(R.id.layout_call_safe_ll);
        tv_currentinfo = (TextView) this
                .findViewById(R.id.layout_call_safe_bt_tv_currentinfo);
        et_input = (EditText) this
                .findViewById(R.id.layout_call_safe_bt_input_pagenumber);
        // 获取数据库dao
        dao = new BlackNumberDao(CallDefenderActivity.this);

        // 更新底部工具栏的状态信息
        updateToolbar();
        // //测试数据库的可用性, 向数据库添加测试数据
        // Random random = new Random();
        // BlackNumberDao dao = new BlackNumberDao(this);
        // dao.add("13722786995", "1");
        // for (int i = 0; i < 100; ++i)
        // {
        // long number = 13700000001l + i;
        // boolean result = dao.add(String.valueOf(number),
        // String.valueOf(random.nextInt(3) + 1));
        // System.out.println(result);
        // }
        // List<BlackNumberBean> list = dao.findAll();
        // for (int i = 0; i < list.size(); ++i)
        // {
        // BlackNumberBean bean = list.get(i);
        // String number = bean.getNumber();
        // String mode = bean.getMode();
        // System.out.println(number + ", " + mode);
        // }

    }

    /**
     * 重写ListView的适配器
     * 
     * @author Administrator
     * 
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return list.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 声明一个ViewHolder
            ViewHolder holder;
            
            //给点击事件函数使用
            final int current_position = position;
            
            // 判断是否convertView为空
            if (convertView == null) {
                // 创建一个view
                convertView = View.inflate(CallDefenderActivity.this,
                        R.layout.layout_calldefender_item, null);
                // 创建一个ViewHolder用于存放要设置值的组件
                holder = new ViewHolder();
                // 将组件存放入ViewHolder中
                holder.tv_number = (TextView) convertView
                        .findViewById(R.id.layout_calldefender_item_tv_number);
                holder.tv_mode = (TextView) convertView
                        .findViewById(R.id.layout_calldefender_item_tv_mode);
                holder.iv_delete = (ImageView) convertView
                        .findViewById(R.id.layout_calldefender_item_iv_delete);
                // 将ViewHolder放到view对象的Tag中
                convertView.setTag(holder);
            } else {
                // 如果convertView不为空直接拿.
                holder = (ViewHolder) convertView.getTag();
            }

            // 获取数据
            BlackNumberBean bean = list.get(position);
            
            convertView.setClickable(false);
            
            // 将获取到的数据设置到组件上
            holder.tv_number.setText(bean.getNumber());

            // 设置拦截模式
            String mode = bean.getMode();
            if ("1".equals(mode)) {
                holder.tv_mode.setText("电话拦截");
            } else if ("2".equals(mode)) {
                holder.tv_mode.setText("短信拦截");
            } else if ("3".equals(mode)) {
                holder.tv_mode.setText("电话 +短信拦截");
            }
            
            //为delete图片设置点击监听事件
            holder.iv_delete.setClickable(true);
            holder.iv_delete.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallDefenderActivity.this);
                    builder.setTitle("删除警告");
                    builder.setMessage("are you 真的要删除此条目吗?想好了吗亲?");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            
                            //获取当前黑名单的条目
                            BlackNumberBean bean = list.get(current_position);
                            //执行删除
                            dao.delete(bean.getNumber());
                            //刷新界面
                            loadData();
                        }
                    });
                    
                    //创建对话框
                    AlertDialog dialog = builder.create();
                    
                    //显示对话框
                    dialog.show();
                    
                }
            });
            return convertView;
        }

    }

    /**
     * 一个内部静态类, 用于存放生成的两个组件对象, 并且复用他们以防止OOM
     * 
     * @author Administrator
     * 
     */
    static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }

    /**
     * 下一页按钮的点击事件
     * 
     * @param view
     */
    public void Next(View view) {
        if (current_page + 1 >= page_count) {
            current_page = page_count - 1;
            DisplayTools.ShowToast(this, "已经是最后一页了");
        } else {
            // 当前页加1并更新界面
            ++current_page;
            loadData();
        }
    }

    /**
     * 上一页按钮的点击事件
     * 
     * @param view
     */
    public void Prior(View view) {
        if (current_page <= 0) {
            current_page = 0;
            DisplayTools.ShowToast(this, "已经是第一页了哦亲");
        } else {
            // 当前页加1并更新界面
            --current_page;
            loadData();
        }
    }

    /**
     * 跳转按钮的点击事件
     * 
     * @param view
     */
    public void Jump(View view) {
        // 获取用户输入的页码
        String input = et_input.getText().toString();
        if (TextUtils.isEmpty(input)) {
            DisplayTools.ShowToast(this, "要输入页码才能跳转哦亲");
            return;
        } else if (!TextUtils.isDigitsOnly(input)) {
            DisplayTools.ShowToast(this, "要输入正确的数字才能跳转哦亲");
            return;
        }

        // 转换String为int
        int page_number = Integer.valueOf(input) - 1;

        // 刷新页面总数
        updateToolbar();

        // 判断用户输入的页码是否在合理范围内
        if ((page_number < 0) || (page_number > (page_count - 1))) {
            DisplayTools.ShowToast(this, "要输入正确的页码才能跳转哦亲");
            return;
        } else {
            // 清除输入框中用户输入的数字
            et_input.setText("");
            et_input.clearFocus();
            
            // 跳转到指定页面
            current_page = page_number;
            loadData();
        }
    }

    /**
     * 更新底部工具栏中的显示状态
     * @param view
     */
    public void updateToolbar() {
        // 获取数据库中数据的总数
        data_count = dao.Count();
        page_count = (data_count % page_size == 0) ? data_count / page_size
                : data_count / page_size + 1;
        tv_currentinfo.setText((current_page + 1) + "/" + page_count);
    }
}
