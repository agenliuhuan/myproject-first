package mobi.dlys.android.familysafer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

	/**
	 * 时间转字符串
	 * 
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static String getDateString(long time, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date(time));
	}

	/**
	 * 获取当前时间的字符串
	 * 
	 * @param pattern
	 * @return
	 */
	public static String getCurrDateString(String pattern) {
		return getDateString(DateUtils.currentTimeMillis(), pattern);
	}

	private static long mFirstSysTimemillis = 0;
	private static long mFirstNtpTimemillis = 0;

	/**
	 * 获取当前时间<br/>
	 * 使用ntp作为标准时间
	 * 
	 * @return
	 */
	public static long currentTimeMillis() {
		long currentTimemillis = currentTimeMillisBySys();
		if (0 == mFirstNtpTimemillis) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					mFirstNtpTimemillis = currentTimeMillisByNtp();
					mFirstSysTimemillis = currentTimeMillisBySys();
				}
			}).start();
		} else {
			return mFirstNtpTimemillis + currentTimemillis - mFirstSysTimemillis;
		}
		return currentTimemillis;
	}

	/**
	 * 获取当前手机系统时间
	 * 
	 * @return
	 */
	private static long currentTimeMillisBySys() {
		return System.currentTimeMillis() - TimeZone.getDefault().getRawOffset() + TimeZone.getTimeZone("Asia/Shanghai").getRawOffset();
	}

	/**
	 * 获取NTP系统时间
	 * 
	 * @return
	 */
	private static long currentTimeMillisByNtp() {
		SntpClient client = new SntpClient();
		if (client.requestTime("ntp.fudan.edu.cn", 30000)) {
			return (client.getNtpTime() + System.nanoTime() / 1000 - client.getNtpTimeReference()) - TimeZone.getDefault().getRawOffset()
					+ TimeZone.getTimeZone("Asia/Shanghai").getRawOffset();
		}
		return 0;
	}

	/**
	 * 字符串转日期
	 * 
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static Date getDate(String time, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(time);
		} catch (ParseException e) {
		}
		return null;
	}

	/*
	 * 获取时间标记字符串
	 */
	public static String getTimeMillisString(int nTimeMillis) {
		int nHours = 0, nMinutes = 0, nSeconds = 0;
		String strTime = "";

		try {
			nTimeMillis = nTimeMillis / 1000;

			nSeconds = nTimeMillis % 60;

			nTimeMillis -= nSeconds;

			if (nTimeMillis > 0) {
				nMinutes = nTimeMillis / 60;

				if (nMinutes > 0) {
					nHours = nMinutes / 60;
					nMinutes = nMinutes % 60;
				}
			}

			if (nHours > 0) {
				strTime = String.format("%02d:%02d:%02d", nHours, nMinutes, nSeconds);
			} else {
				strTime = String.format("%02d:%02d", nMinutes, nSeconds);
			}
		} catch (Exception e) {
		}

		return strTime;
	}

	public static String getTimeMillisStringHMS(int nTimeMillis) {
		int nHours = 0, nMinutes = 0, nSeconds = 0;
		String strTime = "";

		try {
			nTimeMillis = nTimeMillis / 1000;

			nSeconds = nTimeMillis % 60;

			nTimeMillis -= nSeconds;

			if (nTimeMillis > 0) {
				nMinutes = nTimeMillis / 60;

				if (nMinutes > 0) {
					nHours = nMinutes / 60;
					nMinutes = nMinutes % 60;
				}
			}

			strTime = String.format("%02d:%02d:%02d", nHours, nMinutes, nSeconds);
		} catch (Exception e) {
		}

		return strTime;
	}

	// 刚刚,1分钟前,3分钟前,5分钟前,30分钟前,1小时前,2小时前,5小时前,12小时前,1天前,2天前,1周前,1个月前
	public static final long MINUTE_1 = 60 * 1000;
	public static final long MINUTE_3 = MINUTE_1 * 3;
	public static final long MINUTE_5 = MINUTE_1 * 5;
	public static final long MINUTE_30 = MINUTE_1 * 30;
	public static final long HOUR_1 = MINUTE_1 * 60;
	public static final long HOUR_2 = HOUR_1 * 2;
	public static final long HOUR_5 = HOUR_1 * 5;
	public static final long HOUR_12 = HOUR_1 * 12;
	public static final long DAY_1 = HOUR_1 * 24;
	public static final long DAY_2 = DAY_1 * 2;
	public static final long WEEK_1 = DAY_1 * 7;
	public static final long MONTH_1 = DAY_1 * 30;

	/**
	 * @return 获取相对时间文字描述
	 */
	public static String getRelativeDateTimeString(long timestamp) {
		Date date = new Date();
		long diff = date.getTime() - timestamp;
		if (diff <= MINUTE_1) {
			return "刚刚";
		}
		if (diff > MINUTE_1 && diff <= MINUTE_3) {
			return "1分钟前";
		}
		if (diff > MINUTE_3 && diff <= MINUTE_5) {
			return "3分钟前";
		}
		if (diff > MINUTE_5 && diff <= MINUTE_30) {
			return "5分钟前";
		}
		if (diff > MINUTE_30 && diff <= HOUR_1) {
			return "30分钟前";
		}
		if (diff > HOUR_1 && diff <= HOUR_2) {
			return "1小时前";
		}
		if (diff > HOUR_2 && diff <= HOUR_5) {
			return "2小时前";
		}
		if (diff > HOUR_5 && diff <= HOUR_12) {
			return "5小时前";
		}
		if (diff > HOUR_12 && diff <= DAY_1) {
			return "12小时前";
		}
		if (diff > DAY_1 && diff <= DAY_2) {
			return "1天前";
		}
		if (diff > DAY_2 && diff <= WEEK_1) {
			return "2天前";
		}
		if (diff > WEEK_1 && diff <= MONTH_1) {
			return "1周前";
		}
		if (diff > MONTH_1) {
			return "1个月前";
		}
		return "很久以前";
	}
}