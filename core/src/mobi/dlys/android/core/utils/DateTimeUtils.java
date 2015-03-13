/*
 * 文件名称 : DateTimeUtil.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2013-10-12, 上午9:33:31
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

/**
 * 时间，日志之间的格式和转换工具
 * <p>
 */
public class DateTimeUtils {

	private static final String TAG = DateTimeUtils.class.getSimpleName();

	public static String getDate() {
		// Calendar cd = Calendar.getInstance();
		// int year = cd.get(Calendar.YEAR);
		// int month = cd.get(Calendar.MONTH);
		// int day = cd.get(Calendar.DAY_OF_MONTH);

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		return format.format(new Date());
		// return String.format("%d.%d.%d", year, month, day);
	}

	public static String getMillTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
		return format.format(new Date());
	}

	public static String getDateTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		return format.format(new Date());
	}

	public static String mToStr(int m) {
		m = m / 1000;
		int hour = m / 3600;
		int minute = (m - 3600 * hour) / 60;
		int second = m % 60;
		return String.format("%02d:%02d:%02d", hour, minute, second);

	}

	@SuppressLint("DefaultLocale")
	public static String getFormatTime(int mCurSec) {
		String formattedTime = "00:00:00";
		if (mCurSec == 0) {
			return formattedTime;

		}
		int hour = mCurSec / 60 / 60;
		int leftSec = mCurSec % (60 * 60);
		int min = leftSec / 60;
		int sec = leftSec % 60;
		String strHour = String.format("%d", hour);
		String strMin = String.format("%d", min);
		String strSec = String.format("%d", sec);

		if (hour < 10) {
			strHour = "0" + hour;
		}
		if (min < 10) {
			strMin = "0" + min;
		}
		if (sec < 10) {
			strSec = "0" + sec;
		}
		formattedTime = strHour + ":" + strMin + ":" + strSec;
		return formattedTime;
	}

	public static String getTimeDiscribe(String timeString) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = dateFormat.parse(timeString);
		} catch (Exception e) {
			LogUtils.e(TAG, "failed parse : " + timeString);
			return "";
		}
		long time = System.currentTimeMillis() - date.getTime();
		if (time <= 0) {
			LogUtils.e(TAG, "invalid time : " + time);
			return "";
		}

		long second = time / 1000;
		long minute = second / 60;
		long hour = minute / 60;
		long day = hour / 24;
		LogUtils.e(TAG, "day = " + day + "; hour = " + hour + "; minute = " + minute + "; second = " + second);

		if (day >= 30) {
			return "一个月";
		}
		if (day > 0) {
			return "" + day + "天";
		}
		if (hour > 0) {
			return "" + hour + "小时";
		}
		if (minute > 0) {
			return "" + minute + "分钟";
		}
		return "1分钟";
	}

	/**
	 * 将 yyyy-MM-dd hh:mm:ss 格式的时间字符串转成 long时间
	 * 
	 * @param strTime
	 * @return
	 */
	public static long getMillTimeByString(String strTime) {

		long timeMill = 0;

		SimpleDateFormat[] formats = new SimpleDateFormat[] { new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"), new SimpleDateFormat("yyyy-MM-dd") };

		for (int i = 0; i < formats.length; i++) {

			try {
				timeMill = formats[i].parse(strTime).getTime();
				// 没有异常，返回
				break;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return timeMill;
	}

	public static String[] formatTime(long time) {
		long hour = 0;
		long minute = 0;
		final long HOUR_TIME = 3600;
		final long MINUTE_TIME = 60;
		if (time > HOUR_TIME) {
			hour = time / HOUR_TIME;
			time = time - HOUR_TIME * hour;
		}
		if (time > MINUTE_TIME) {
			minute = time / MINUTE_TIME;
			time = time - MINUTE_TIME * minute;
		}
		if (time > 30) {
			minute += 1;
		}
		String[] strs = new String[] { hour + "", "小时", minute + "", "分钟" };
		return strs;
	}

	public static String getDateTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		return format.format(new Date(time));
	}
}
