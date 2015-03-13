/**
 * LogUtils.java
 *
 * Copyright 2014 dlys.mobi All Rights Reserved.
 */
package mobi.dlys.android.core.utils;

import android.util.Log;

/**
 * 日志工具类. User: Wangle<87292008@qq.com> Date: 2014/5/16 0016 Time: 10:16
 */
public final class LogUtils {

	private static final String TAG = "dlys";
	private static final int LEVEL = Log.VERBOSE;
	private static final boolean ENABLE = true;

	public static String getTag(Class clazz) {
		String tag = clazz.getSimpleName();
		final int length = 23;
		if (tag.length() > length) {
			tag = tag.substring(0, length);
		}
		return tag;
	}

	public static void d(String message) {
		d(TAG, message);
	}

	public static void d(String tag, String message) {
		log(tag, message, Log.DEBUG);
	}

	public static void i(String tag, String message) {
		log(tag, message, Log.INFO);
	}

	public static void w(String tag, String message) {
		log(tag, message, Log.WARN);
	}

	public static void e(String tag, String message) {
		log(tag, message, Log.ERROR);
	}

	public static boolean isLoggable(String tag, int level) {
		// return Log.isLoggable(tag, level) || Log.isLoggable(TAG, level);
		return (ENABLE && level >= LEVEL) ? true : false;
	}

	private static void log(String tag, String message, int level) {
		if (null == message) {
			message = "";
		}
		if (isLoggable(tag, level)) {
			if (null == tag) {
				tag = TAG;
			}
			Log.println(level, tag, message);
		}
	}

	private LogUtils() {
	}
}
