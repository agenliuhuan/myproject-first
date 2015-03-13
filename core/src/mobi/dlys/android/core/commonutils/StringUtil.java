package mobi.dlys.android.core.commonutils;

import android.text.TextUtils;
import android.webkit.URLUtil;

public class StringUtil {

	public static String truncateString(final String string, final String postFix) {
		if (!string.endsWith(postFix)) {
			return null;
		}
		final String subString = string.substring(0, string.length() - postFix.length());
		return subString;
	}

	/**
	 * 过滤字符串种的特殊字符，允许的字符集是： [a-zA-z0-9\\s\u4E00-\u9FA5] 的任意组合。 (字母+数字+空格+中文)
	 * 
	 * @param str
	 * @return
	 */
	public static String textFilter(String str) {
		if (null == str) {
			return null;
		}
		String reg = "[^a-zA-z0-9\\s\u4E00-\u9FA5]";
		int length = str.length();
		StringBuffer sb = new StringBuffer(str.length());
		for (int i = 0; i < length; i++) {
			String c = str.substring(i, i + 1);
			if (!c.matches(reg)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 获取URL主域名
	 * 
	 * @param ULR
	 * @return 主域名
	 */
	public static String getMainDomain(String URL) {
		String back;
		int pos = URL.indexOf("://");
		if (pos <= 0) {
			pos = 0;
			pos = URL.indexOf("/", pos + 1);
		} else {
			pos = URL.indexOf("/", pos + 3);
		}

		if (pos > 0)
			back = URL.substring(0, pos);
		else
			back = URL;
		return back;
	}

	/**
	 * 获取URL 截取遇到第一个flag字符串前面的URL
	 * 
	 * @param ULR
	 * @return 主域名
	 */
	public static String getDomainBefore(String URL, String flag) {
		String back;
		int pos = URL.indexOf(flag);
		if (pos > 0) {
			back = URL.substring(0, pos);
		} else
			back = URL;
		return back;
	}

	public static String parseFileName(String url) {
		final String PR_HTTP = "http://";
		final String PR_HTTPS = "https://";
		final String PR_FTP = "ftp://";
		final String PR_THUNDER = "thunder://";

		String filename = null;

		// 分析文件名称
		if (TextUtils.isEmpty(url)) {
			filename = null;
		} else if (url.startsWith(PR_HTTP) || url.startsWith(PR_HTTPS) || url.startsWith(PR_FTP)) {
			filename = URLUtil.guessFileName(url, null, null);
		} else if (url.startsWith(PR_THUNDER)) {
			// 先做解码
			filename = URLUtil.guessFileName(Base64Util.getFromBase64(url.substring(PR_THUNDER.length()), "UTF-8"), null, null);
		} else {
			// 其他协议，暂不支持
		}
		return filename;
	}

	/**
	 * 验证字符串是否为合法的IPv4地址。（0.0.0.0 ~ 255.255.255.255）
	 * 
	 * @param ipString
	 * @return
	 */
	public static boolean matchIPAddress(String ipString) {
		String reg = "\\b(([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\b";
		return null != ipString && ipString.matches(reg);
	}

	/**
	 * 验证字符串是否为合法的IPv4地址。（0.0.0.1 ~ 255.255.255.254），不包含0.0.0.0和255.255.255.255
	 * 
	 * @param ipString
	 * @return
	 */
	public static boolean matchIPInternetAddress(String ipString) {
		return null != ipString && !ipString.equals("0.0.0.0") && !ipString.equals("255.255.255.255") && matchIPAddress(ipString);
	}

	/*
	 * 新字符串
	 */
	public static String newString(String strContent) {
		return new String((null == strContent) ? "" : strContent);
	}

}
