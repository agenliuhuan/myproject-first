/*
 * 文件名称 : MD5.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2013-9-10, 下午7:46:26
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.commonutils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class MD5 {

	public static String md5(String key) {

		if (null == key) {
			return null;
		}

		try {
			char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] buf = key.getBytes();
			md.update(buf, 0, buf.length);
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder(32);
			for (byte b : bytes) {
				sb.append(hex[((b >> 4) & 0xF)]).append(hex[((b >> 0) & 0xF)]);
			}
			key = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return key;
	}

}
