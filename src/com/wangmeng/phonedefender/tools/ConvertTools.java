package com.wangmeng.phonedefender.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConvertTools {
	/**
	 * 将InputStream转换为String类型的值
	 * @param in InputStream为待转换的输入流
	 * @return 输入流中的文本以String类型返回
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
