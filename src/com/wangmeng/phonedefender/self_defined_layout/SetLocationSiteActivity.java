package com.wangmeng.phonedefender.self_defined_layout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wangmeng.phonedefender.R;

/*
 * �������ù�������ʾ��λ�õ����ý���
 */
public class SetLocationSiteActivity extends Activity {

    int start_x = 0;
    int start_y = 0;

    // �����ļ��е����
    private TextView tv_top;
    private TextView tv_buttom;
    private TextView tv_site;
    
    //˫�����й�������Ҫ����Դ
    final long[] mHits = new long[2];// ���鳤�ȱ�ʾҪ����Ĵ���
    
    // �����ļ�
    private static SharedPreferences sprefs;
    private int window_width;
    private int window_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_location_site);
        
        // ��ȡ�����ļ�
        sprefs = this.getSharedPreferences("sprefs", MODE_PRIVATE);

        // ��ȡ�ļ��е����
        tv_top = (TextView) findViewById(R.id.layout_set_location_site_tv_top);
        tv_buttom = (TextView) findViewById(R.id.layout_set_location_site_tv_buttom);
        tv_site = (TextView) findViewById(R.id.layout_set_location_site_tv_site);

        // �����ļ��ĳ�ʼֵ
        tv_buttom.setVisibility(TextView.GONE);
        
        //��ȡ��Ļ�Ŀ�Ⱥ͸߶�
        window_width = SetLocationSiteActivity.this
                .getWindowManager().getDefaultDisplay().getWidth();
        window_height = SetLocationSiteActivity.this
                .getWindowManager().getDefaultDisplay().getHeight();
       
        
        int location_l = sprefs.getInt("location_l", 0);
        int location_t = sprefs.getInt("location_t", 0);
        if (location_t < window_height / 2) // ����tv_top �� tv_buttom ����ʾ״̬
        {
            // tv_top����, tv_buttom��ʾ
            tv_top.setVisibility(TextView.INVISIBLE);
            tv_buttom.setVisibility(TextView.VISIBLE);
        } else {
            // tv_top��ʾ, tv_buttom����
            tv_top.setVisibility(TextView.VISIBLE);
            tv_buttom.setVisibility(TextView.INVISIBLE);
        }
        // ����tv_site��״̬
        System.out.println("��ʼ��:" + location_l + ", " + location_t);
        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) tv_site
                .getLayoutParams(); // ��ȡ������Ĳ�����
        params.leftMargin = location_l; // ����topMargin����
        params.topMargin = location_t; // ����bottomMargin����
        tv_site.setLayoutParams(params); // ���������ø�tv_site���
        
        // Ϊ������ü����¼�
        tv_site.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction(); // ��ȡ�¼�������

                switch (action) {
                case MotionEvent.ACTION_DOWN:
                    start_x = (int) event.getRawX();
                    start_y = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    // ���û��ƶ����ʱ��ȡ�ƶ������������
                    int end_x = (int) event.getRawX();
                    int end_y = (int) event.getRawY();

                    // ����move����ʱx�����y�����ƶ��ľ���
                    int dx = end_x - start_x;
                    int dy = end_y - start_y;

                    // ���������λ�õ�����
                    int l = tv_site.getLeft() + dx;
                    int r = tv_site.getRight() + dx;
                    int t = tv_site.getTop() + dy;
                    int b = tv_site.getBottom() + dy;

                    // �ж��������λ���Ƿ�Խ��(�������һ�����ܵ���Ļ����ȥ��)
                    if (l < 0 || r > window_width || t < 0
                            || b > window_height - 50) // ��ȥ50��Ϊ��ȥ��״̬���ĸ߶�
                        break;

                    // ����tv_top �� tv_buttom ����ʾ����
                    if (t < window_height / 2) {
                        // tv_top����, tv_buttom��ʾ
                        tv_top.setVisibility(TextView.INVISIBLE);
                        tv_buttom.setVisibility(TextView.VISIBLE);
                    } else {
                        // tv_top��ʾ, tv_buttom����
                        tv_top.setVisibility(TextView.VISIBLE);
                        tv_buttom.setVisibility(TextView.INVISIBLE);
                    }

                    // ���������λ��
                    tv_site.layout(l, t, r, b);

                    // ������������ֵ
                    start_x = (int) event.getRawX();
                    start_y = (int) event.getRawY();

                    // �����º��ֵ���浽�����ļ���
                    Editor editor = sprefs.edit();
                    editor.putInt("location_l", l);
                    editor.putInt("location_t", t);
                    editor.commit();

                    System.out.println(l + ", " + t);
                    break;

                case MotionEvent.ACTION_UP:

                    break;
                default:
                    break;
                }
                return false; // ����false������¼����������´���, �����ڴ˴���ֹ
            }
        });
            //Ϊ�������˫�����еĹ���
        tv_site.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //�˴�������androidԴ���ţ������, ����ʼ��еĽ���
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();// ������ʼ�����ʱ��
                if (mHits[0] >= (SystemClock.uptimeMillis() - 300)) {
                    //����tv_site�������
                        //��ȡ����Ŀ�͸�
                    int w = tv_site.getWidth();
                    int h = tv_site.getHeight();
                    tv_site.layout(window_width/2 - w/2, window_height/2 - h/2, window_width/2 + w/2, window_height/2 + h/2);
                }
            }
        });
    }
}
