/**
 * StringUtils.java
 *
 * Copyright 2014 cokosoft.com. All Rights Reserved.
 */
package mobi.dlys.android.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 字符串工具类. User: Wangle<87292008@qq.com> Date: 2014/5/16 0016 Time: 10:55
 */
public final class StringUtils {

	private static final Pattern EMAILER = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

	private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMATER = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private static final ThreadLocal<SimpleDateFormat> DATE_FORMATER = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	/**
	 * @param sDate
	 *            yyyy-MM-dd HH:mm:ss
	 * @return 将字符串转位日期类型
	 */
	public static Date toDate(String sDate) {
		try {
			return DATE_TIME_FORMATER.get().parse(sDate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * @return 以友好的方式显示时间
	 */
	public static String friendlyTime(String sDate) {
		Date time = toDate(sDate);
		if (null == time) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = DATE_FORMATER.get().format(cal.getTime());
		String paramDate = DATE_FORMATER.get().format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0) {
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			} else {
				ftime = hour + "小时前";
			}
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0) {
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			} else {
				ftime = hour + "小时前";
			}
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = DATE_FORMATER.get().format(time);
		}
		return ftime;
	}

	/**
	 * @return 判断给定字符串时间是否为今日
	 */
	public static boolean isToday(String sDate) {
		boolean b = false;
		Date time = toDate(sDate);
		Date today = new Date();
		if (time != null) {
			String nowDate = DATE_FORMATER.get().format(today);
			String timeDate = DATE_FORMATER.get().format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * @return 返回long类型的今天的日期
	 */
	public static long getToday() {
		Calendar cal = Calendar.getInstance();
		String curDate = DATE_FORMATER.get().format(cal.getTime());
		curDate = curDate.replace("-", "");
		return Long.parseLong(curDate);
	}

	/**
	 * @return 判断给定字符串是否空白串.空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串,返回true
	 */
	public static boolean isEmpty(String input) {
		if (!hasLength(input)) {
			return true;
		}

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return 判断是不是一个合法的电子邮件地址
	 */
	public static boolean isEmail(String email) {
		return hasLength(email) && EMAILER.matcher(email).matches();
	}

	/**
	 * @param defValue
	 *            默认值
	 * @return 字符串转整数
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception ignored) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null) {
			return 0;
		}
		return toInt(obj.toString(), 0);
	}

	/**
	 * 对象转整数
	 * 
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception ignored) {
		}
		return 0;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception ignored) {
		}
		return false;
	}

	/**
	 * @return 将一个InputStream流转换成字符串
	 */
	public static String toConvertString(InputStream is) {
		StringBuilder res = new StringBuilder();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader read = new BufferedReader(isr);
		try {
			String line;
			line = read.readLine();
			while (line != null) {
				res.append(line);
				line = read.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				isr.close();
				isr.close();
				read.close();
				read = null;
				if (null != is) {
					is.close();
					is = null;
				}
			} catch (IOException ignored) {
			}
		}
		return res.toString();
	}

	/**
	 * 判断字符串的长度是否大于0,如果是返回true,否则返回false.
	 */
	public static boolean hasLength(CharSequence str) {
		return null != str && str.length() > 0;
	}

	/**
	 * 判断字符串是否有可视字符.如果有返回true,否则返回false.
	 */
	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	private StringUtils() {
	}
}
