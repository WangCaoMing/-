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
 * ͨ����ʿ����
 * 
 * @author Administrator
 * 
 */
public class CallDefenderActivity extends Activity {

    // �����ļ��е����
    private ListView lv;
    private LinearLayout ll;
    private TextView tv_currentinfo;
    private EditText et_input;

    // �����������б�
    private List<BlackNumberBean> list;
    private MyAdapter adapter;

    // �������ݿ��Dao
    private BlackNumberDao dao;

    // ��ҳ������
    private static int current_page = 0;
    private static int page_size = 9;
    private static int page_count = 0;
    private static int data_count = 0;

    // handler
    private Handler handler = new Handler() {
        
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                // ���ý�����Ϊ���ɼ�
                ll.setVisibility(View.GONE);

                adapter = new MyAdapter();
                lv.setAdapter(adapter);

                // ���µײ���������״̬
                adapter.notifyDataSetChanged();
                updateToolbar();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ��ʼ��UI����
        initUI();

        // ��������
        loadData();
    }

    /**
     * �������ݿ��еĺ���������
     */
    private void loadData() {
        // ���ý�����Ϊ�ɼ�
        ll.setVisibility(View.VISIBLE);

        Thread thread = new Thread() {
            @Override
            public void run() {
                /**
                 * ����Щ�����������߳�������Ϊ���ǵ����ݱȽϴ�����, ����һ����ʱ����, ���˷������߳����Է����߳�����
                 */
                // ��ȡ�������ı�ҳ����
                list = dao.findLimit(current_page, page_size);

                // ֪ͨ���߳�ִ����Ӧ�Ĳ���
                handler.sendEmptyMessage(1);
                super.run();
            }
        };
        thread.start();
    }

    /**
     * ��ʼ��UI����
     */
    private void initUI() {

        // ���ز����ļ�
        setContentView(R.layout.layout_call_safe);

        // ��ȡ�����ļ��е����
        lv = (ListView) this.findViewById(R.id.layout_call_safe_lv);
        ll = (LinearLayout) this.findViewById(R.id.layout_call_safe_ll);
        tv_currentinfo = (TextView) this
                .findViewById(R.id.layout_call_safe_bt_tv_currentinfo);
        et_input = (EditText) this
                .findViewById(R.id.layout_call_safe_bt_input_pagenumber);
        // ��ȡ���ݿ�dao
        dao = new BlackNumberDao(CallDefenderActivity.this);

        // ���µײ���������״̬��Ϣ
        updateToolbar();
        // //�������ݿ�Ŀ�����, �����ݿ���Ӳ�������
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
     * ��дListView��������
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
            // ����һ��ViewHolder
            ViewHolder holder;
            
            //������¼�����ʹ��
            final int current_position = position;
            
            // �ж��Ƿ�convertViewΪ��
            if (convertView == null) {
                // ����һ��view
                convertView = View.inflate(CallDefenderActivity.this,
                        R.layout.layout_calldefender_item, null);
                // ����һ��ViewHolder���ڴ��Ҫ����ֵ�����
                holder = new ViewHolder();
                // ����������ViewHolder��
                holder.tv_number = (TextView) convertView
                        .findViewById(R.id.layout_calldefender_item_tv_number);
                holder.tv_mode = (TextView) convertView
                        .findViewById(R.id.layout_calldefender_item_tv_mode);
                holder.iv_delete = (ImageView) convertView
                        .findViewById(R.id.layout_calldefender_item_iv_delete);
                // ��ViewHolder�ŵ�view�����Tag��
                convertView.setTag(holder);
            } else {
                // ���convertView��Ϊ��ֱ����.
                holder = (ViewHolder) convertView.getTag();
            }

            // ��ȡ����
            BlackNumberBean bean = list.get(position);
            
            convertView.setClickable(false);
            
            // ����ȡ�����������õ������
            holder.tv_number.setText(bean.getNumber());

            // ��������ģʽ
            String mode = bean.getMode();
            if ("1".equals(mode)) {
                holder.tv_mode.setText("�绰����");
            } else if ("2".equals(mode)) {
                holder.tv_mode.setText("��������");
            } else if ("3".equals(mode)) {
                holder.tv_mode.setText("�绰 +��������");
            }
            
            //ΪdeleteͼƬ���õ�������¼�
            holder.iv_delete.setClickable(true);
            holder.iv_delete.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallDefenderActivity.this);
                    builder.setTitle("ɾ������");
                    builder.setMessage("are you ���Ҫɾ������Ŀ��?���������?");
                    builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            
                            //��ȡ��ǰ����������Ŀ
                            BlackNumberBean bean = list.get(current_position);
                            //ִ��ɾ��
                            dao.delete(bean.getNumber());
                            //ˢ�½���
                            loadData();
                        }
                    });
                    
                    //�����Ի���
                    AlertDialog dialog = builder.create();
                    
                    //��ʾ�Ի���
                    dialog.show();
                    
                }
            });
            return convertView;
        }

    }

    /**
     * һ���ڲ���̬��, ���ڴ�����ɵ������������, ���Ҹ��������Է�ֹOOM
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
     * ��һҳ��ť�ĵ���¼�
     * 
     * @param view
     */
    public void Next(View view) {
        if (current_page + 1 >= page_count) {
            current_page = page_count - 1;
            DisplayTools.ShowToast(this, "�Ѿ������һҳ��");
        } else {
            // ��ǰҳ��1�����½���
            ++current_page;
            loadData();
        }
    }

    /**
     * ��һҳ��ť�ĵ���¼�
     * 
     * @param view
     */
    public void Prior(View view) {
        if (current_page <= 0) {
            current_page = 0;
            DisplayTools.ShowToast(this, "�Ѿ��ǵ�һҳ��Ŷ��");
        } else {
            // ��ǰҳ��1�����½���
            --current_page;
            loadData();
        }
    }

    /**
     * ��ת��ť�ĵ���¼�
     * 
     * @param view
     */
    public void Jump(View view) {
        // ��ȡ�û������ҳ��
        String input = et_input.getText().toString();
        if (TextUtils.isEmpty(input)) {
            DisplayTools.ShowToast(this, "Ҫ����ҳ�������תŶ��");
            return;
        } else if (!TextUtils.isDigitsOnly(input)) {
            DisplayTools.ShowToast(this, "Ҫ������ȷ�����ֲ�����תŶ��");
            return;
        }

        // ת��StringΪint
        int page_number = Integer.valueOf(input) - 1;

        // ˢ��ҳ������
        updateToolbar();

        // �ж��û������ҳ���Ƿ��ں���Χ��
        if ((page_number < 0) || (page_number > (page_count - 1))) {
            DisplayTools.ShowToast(this, "Ҫ������ȷ��ҳ�������תŶ��");
            return;
        } else {
            // �����������û����������
            et_input.setText("");
            et_input.clearFocus();
            
            // ��ת��ָ��ҳ��
            current_page = page_number;
            loadData();
        }
    }

    /**
     * ���µײ��������е���ʾ״̬
     * @param view
     */
    public void updateToolbar() {
        // ��ȡ���ݿ������ݵ�����
        data_count = dao.Count();
        page_count = (data_count % page_size == 0) ? data_count / page_size
                : data_count / page_size + 1;
        tv_currentinfo.setText((current_page + 1) + "/" + page_count);
    }
}
