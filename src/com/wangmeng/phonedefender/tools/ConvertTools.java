package com.wangmeng.phonedefender.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.R.integer;

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
	
	/**
	 * ����������ַ�����MD5ֵ
	 * @param content Ҫ����MD5���ַ���
	 * @return 16����MD5ֵ�ַ���
	 */
	public static String MD5(String content)
	{
		StringBuilder builder = new StringBuilder();
		try {
			
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] result = digest.digest(content.getBytes());
			for (byte b : result) {
				int temp = b&0xff;
				String i = Integer.toHexString(temp);
				if (i.length() < 2)
					builder.append("0" + i);
				else
					builder.append(i);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return builder.toString();
	}
}
