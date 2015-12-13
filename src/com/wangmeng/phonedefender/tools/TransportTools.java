package com.wangmeng.phonedefender.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TransportTools {
	
	/**
	 * ���������е����봫�䵽�����
	 * @param in
	 */
	public static void In2OutStream(InputStream in, OutputStream out)
	{
		int length = 0;
		byte[] b = new byte[1024];
		try {
			while ((length = in.read(b)) != -1)
			{
				out.write(b, 0, length);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
