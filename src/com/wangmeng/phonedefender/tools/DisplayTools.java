package com.wangmeng.phonedefender.tools;

import android.content.Context;
import android.widget.Toast;

public class DisplayTools {
	/**
	 * ��ʾһ����ʱ����Toast
	 */
	public static void ShowToast(Context context, String content)
	{
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}
}
