package mobi.dlys.android.familysafer.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密
 */
public class MD5 {
	/**
	 * 加密为MD5
	 * 
	 * @param source
	 * @return
	 */
	public static String encoderForString(String source) {
		byte[] md5Bytes = encoderForBytes(source);
		if (md5Bytes != null) {
			StringBuilder hexValue = new StringBuilder();
			for (byte md5Byte : md5Bytes) {
				int val = (md5Byte) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString().toUpperCase();
		}
		return null;
	}

	/**
	 * 加密为MD5
	 * 
	 * @param source
	 * @return
	 */
	public static byte[] encoderForBytes(String source) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			char[] charArray = source.toCharArray();
			byte[] byteArray = new byte[charArray.length];
			for (int i = 0; i < charArray.length; i++)
				byteArray[i] = (byte) charArray[i];
			return digest.digest(byteArray);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}