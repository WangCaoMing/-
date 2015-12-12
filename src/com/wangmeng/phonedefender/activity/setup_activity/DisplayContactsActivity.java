package com.wangmeng.phonedefender.activity.setup_activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wangmeng.phonedefender.R;

/**
 * 展示手机的所有联系人
 * 
 * @author Administrator
 * 
 */
public class DisplayContactsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_display_contacts);

		// 获取组件
		ListView lv_contacts = (ListView) findViewById(R.id.lv_contacts);

		// 获取联系人列表存储在arraylist中
		final ArrayList<HashMap<String, String>> list = getContacts();

		// 为listview设置填充器
		lv_contacts.setAdapter(new SimpleAdapter(this, list,
				R.layout.view_contacts_list, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone }));
		
		// 为listview设置item的点击监听的事件
		lv_contacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//获取被点击的item的联系人的name和phone的值
				HashMap<String, String> contact = list.get(arg2);
				String name = contact.get("name");
				String phone = contact.get("phone");
				Intent intent = new Intent(); //创建一个intent用于回传获得的数据
				intent.putExtra("name", name);
				intent.putExtra("phone", phone);
				DisplayContactsActivity.this.setResult(Activity.RESULT_OK, intent);
				finish(); //关闭此界面放回到上一个页面
			}
			
		});
	}

	/**
	 * 从系统的联系人数据库中获取联系人
	 */
	private ArrayList<HashMap<String, String>> getContacts() {
		// 写出contact应用的数据库表的uri
		Uri raw_contacts_uri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Cursor contacts_cursor = getContentResolver().query(raw_contacts_uri,
				new String[] { "contact_id" }, null, null, null);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(); // 用于存放查询到的联系人用于返回
		while (contacts_cursor.moveToNext()) // 遍历查询到的id
		{
			String id = contacts_cursor.getString(contacts_cursor
					.getColumnIndex("contact_id")); // 获取当前行中的联系人id
			if (id != null) {
				Uri data_uri = Uri.parse("content://com.android.contacts/data");
				HashMap<String, String> contact = new HashMap<String, String>();
				String data = "";
				String type = "";
				Cursor data_cursor = getContentResolver().query(data_uri,
						new String[] { "data1", "mimetype" },
						"raw_contact_id=?", new String[] { id }, null);
				if (data_cursor != null)
					while (data_cursor.moveToNext()) {
						data = data_cursor.getString(data_cursor
								.getColumnIndex("data1"));
						type = data_cursor.getString(data_cursor
								.getColumnIndex("mimetype"));
						if (type.equals("vnd.android.cursor.item/phone_v2")) {
							System.out.println("电话号码:" + data);
							contact.put("phone", data);
						} else if (type.equals("vnd.android.cursor.item/name")) {
							System.out.println("姓名:" + data);
							contact.put("name", data);
						}
					}
				data_cursor.close();
				list.add(contact); // 将一个联系人的信息添加到list中
			}
		}
		contacts_cursor.close();

		return list;
	}
}
