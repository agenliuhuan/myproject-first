/*
 * 文件名称 : UrlUtil.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2013-9-10, 下午7:49:44
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.commonutils;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class UrlUtil {
	/**
	 * 将普通链接转化成迅雷专用链 thunder:// + base64( AA + url + ZZ)
	 * */
	public static String formatUrlToThunder(String url) {
		StringBuilder urlSb = new StringBuilder();
		urlSb.append("thunder://");
		String urlStr = "AA" + url + "ZZ";
		String encodeStr = Base64Util.encode(urlStr.getBytes());

		urlSb.append(encodeStr);
		String ThunderUrl = urlSb.toString();
		return ThunderUrl;
	}

	/**
	 * 将迅雷专用链转成普通的链接。
	 * */
	public static String getNormalUrlFromThunder(String thunderUrl) {
		String head = "thunder://";
		int urlIndex = thunderUrl.indexOf(head);
		String urlStr = thunderUrl.substring(urlIndex + head.length());
		String decodeStr = Base64Util.decode(urlStr, "UTF-8");
		int beginIndex = decodeStr.indexOf("AA");
		int endIndex = decodeStr.indexOf("ZZ");
		String normalUrl = decodeStr.substring(beginIndex + "AA".length(),
				endIndex);
		return normalUrl;
	}

	public static String getDomain(String url) {
		int index = url.indexOf("://");
		if (index != -1) {
			url = url.substring(index + 3);
			index = url.indexOf("/");
			if (index != -1) {
				url = url.substring(0, index);
				index = url.indexOf(":");
				if (index != -1) {
					url = url.substring(0, index);
				}
				return url;
			}
		}
		return null;
	}
}
