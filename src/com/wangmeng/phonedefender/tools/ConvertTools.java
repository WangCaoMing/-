package com.wangmeng.phonedefender.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConvertTools {
	/**
	 * ��InputStreamת��ΪString���͵�ֵ
	 * @param in InputStreamΪ��ת����������
	 * @return �������е��ı���String���ͷ���
	 */
	public static String StreamToString(InputStream in)
	{
		String result = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024]; 
		int length = 0;
		try {
			while ((length = in.read(b)) != -1)
			{
				out.write(b, 0, length);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = new String(out.toByteArray());
		return result;
	}
}
