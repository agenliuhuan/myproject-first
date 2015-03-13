package mobi.dlys.android.familysafer.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.content.Context;

public class PinYinHelper {

	private static HashMap<String, String> hashMap = new HashMap<String, String>();

	/**
	 * 将unicode-to-hanyu-pinyin.txt文件 以unicode为key pinyin为value 加到hashmap中
	 */
	public static boolean isHashMapEmpty() {
		return hashMap.isEmpty();
	}

	private boolean strIsEnglish(String word) {
		boolean sign = true; // 初始化标志为为'true'
		for (int i = 0; i < word.length(); i++) {
			if (!(word.charAt(i) >= 'A' && word.charAt(i) <= 'Z') && !(word.charAt(i) >= 'a' && word.charAt(i) <= 'z')) {
				return false;
			}
		}
		return true;
	}

	public static void init(Context context) {
		try {
			String filename = "pinyin.txt";
			InputStream is = context.getAssets().open(filename);
			// InputStream is =
			// PinYinHelper.class.getClass().getResourceAsStream(filename);
			BufferedReader breader = new BufferedReader(new InputStreamReader(is));
			String s;
			String unicode;
			String pinyin;
			while ((s = breader.readLine()) != null) {
				// 提取文件流每行中的unicode码以及 pinyin字符串
				unicode = s.substring(0, 4);
				pinyin = s.substring(6, s.length() - 1);
				hashMap.put(unicode.toLowerCase(), pinyin);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将汉语转换为汉语拼音
	 * 
	 * @param value
	 * @return pinyin[]
	 */
	public static String[] toHanYuPinYin(String value) {
		String[] result = new String[value.length()];
		String unicode;
		String pinyin;
		for (int i = 0; i < value.length(); i++) {
			Character c = value.charAt(i);
			unicode = Integer.toHexString(c);// 得到汉字的unicode编码
			pinyin = hashMap.get(unicode);// 从hashMap中找到汉子的拼音
			result[i] = pinyin;
		}

		return result;
	}

	public static String getFirstHanYuPinYin(String value) {
		String result = "";
		String unicode;
		String pinyin;
		for (int i = 0; i < value.length(); i++) {
			Character c = value.charAt(i);
			if (c > 0x7f) {
				unicode = Integer.toHexString(c);// 得到汉字的unicode编码
				pinyin = hashMap.get(unicode);// 从hashMap中找到汉子的拼音
				if (pinyin != null && pinyin.length() > 0)
					result += String.valueOf(pinyin.charAt(0));
				else
					result += String.valueOf(c).toLowerCase();
			} else {
				result += String.valueOf(c).toLowerCase();
			}
		}
		// Log.d(TAG, "result = " + result + ", org = " + toHexString(value));
		return result;
	}

	public static String toHexString(String value) {
		String result = "";
		byte[] buffer = value.getBytes();
		for (int i = 0; i < buffer.length; i++) {
			result += String.format("0x%02x, ", (int) buffer[i] & 0x0ff);
		}
		return result;
	}

}
