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
 * չʾ�ֻ���������ϵ��
 * 
 * @author Administrator
 * 
 */
public class DisplayContactsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_display_contacts);

		// ��ȡ���
		ListView lv_contacts = (ListView) findViewById(R.id.lv_contacts);

		// ��ȡ��ϵ���б�洢��arraylist��
		final ArrayList<HashMap<String, String>> list = getContacts();

		// Ϊlistview���������
		lv_contacts.setAdapter(new SimpleAdapter(this, list,
				R.layout.view_contacts_list, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone }));
		
		// Ϊlistview����item�ĵ���������¼�
		lv_contacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//��ȡ�������item����ϵ�˵�name��phone��ֵ
				HashMap<String, String> contact = list.get(arg2);
				String name = contact.get("name");
				String phone = contact.get("phone");
				Intent intent = new Intent(); //����һ��intent���ڻش���õ�����
				intent.putExtra("name", name);
				intent.putExtra("phone", phone);
				DisplayContactsActivity.this.setResult(Activity.RESULT_OK, intent);
				finish(); //�رմ˽���Żص���һ��ҳ��
			}
			
		});
	}

	/**
	 * ��ϵͳ����ϵ�����ݿ��л�ȡ��ϵ��
	 */
	private ArrayList<HashMap<String, String>> getContacts() {
		// д��contactӦ�õ����ݿ���uri
		Uri raw_contacts_uri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Cursor contacts_cursor = getContentResolver().query(raw_contacts_uri,
				new String[] { "contact_id" }, null, null, null);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(); // ���ڴ�Ų�ѯ������ϵ�����ڷ���
		while (contacts_cursor.moveToNext()) // ������ѯ����id
		{
			String id = contacts_cursor.getString(contacts_cursor
					.getColumnIndex("contact_id")); // ��ȡ��ǰ���е���ϵ��id
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
							System.out.println("�绰����:" + data);
							contact.put("phone", data);
						} else if (type.equals("vnd.android.cursor.item/name")) {
							System.out.println("����:" + data);
							contact.put("name", data);
						}
					}
				data_cursor.close();
				list.add(contact); // ��һ����ϵ�˵���Ϣ��ӵ�list��
			}
		}
		contacts_cursor.close();

		return list;
	}
}
