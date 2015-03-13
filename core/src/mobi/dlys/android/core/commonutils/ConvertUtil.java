/*
 * 文件名称 : ByteConvert.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2013-9-10, 下午7:34:24
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.commonutils;

import java.math.BigDecimal;

import mobi.dlys.android.core.utils.LogUtils;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class ConvertUtil {

	// 产品要求单位都使用一个字节
	private static final float BASE_B = 1; // 转换为字节基数
	private static final float BASE_KB = 1024; // 转换为KB
	private static final int BASE_MB = 1024 * 1024; // 转换为M的基数
	private static final int BASE_GB = 1024 * 1024 * 1024; // 转换为G的基数
	private static final long BASE_TB = 1024 * 1024 * 1024 * 1024L; // 转换为T的基数
	private static final long BASE_PB = 1024 * 1024 * 1024 * 1024 * 1024L; // 转换为T的基数
	public static final String UNIT_BYTE = "B";
	public static final String UNIT_KB = "KB";
	public static final String UNIT_MB = "MB";
	public static final String UNIT_GB = "GB";
	public static final String UNIT_TB = "TB";
	public static final String UNIT_PB = "PB";

	/**
	 * 通过等级，算升级所需的经验值
	 * 
	 * @param level
	 * @return
	 */
	public static int levelToScore(int level) {
		return 50 * (level + 1) * (level + 4);
	}

	/**
	 * 通过当前经验值算等级
	 * 
	 * @param score
	 * @return
	 */
	public static int scoreToLevel(int score) {
		int rank = 0;

		while (true) {
			if (score < 50 * rank * (rank + 3))
				break;
			rank += 1;
		}

		return rank > 1 ? rank - 1 : 0;
	}

	public static long stringToLong(String string) {
		long value = 0;
		try {
			value = Long.valueOf(string);
		} catch (Exception e) {
			LogUtils.e("stringToLong", "get invalid params, string = " + string);
			value = 0;
		}
		return value;
	}

	public static int stringToInt(String string) {
		int value = 0;
		try {
			value = Integer.valueOf(string);
		} catch (Exception e) {
			LogUtils.e("stringToInt", "get invalid params, string = " + string);
			value = 0;
		}
		return value;
	}

	/**
	 * 将String类型的IP地址转换成int32类型。将192.168.11.101转换为1812703424 注：int32 高位在前，地位在后
	 * 
	 * @param IPAddress
	 *            String类型的IP地址，如：192.168.11.101
	 * @return int32类型的IP地址，如：1812703424, 如果IPAddress为null或者不合法，则返回0
	 */
	public static int ipAddrToInt(String IPAddress) {
		if (null == IPAddress) {
			// throw new NullPointerException("IPAddress is null.");
			// NullPointerException 如果IPAddress为null
			return 0;
		}
		if (!IPAddress
				.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$")) {
			// throw new
			// RuntimeException("IPAddress is not valid...");RuntimeException
			// 如果String类型的IP地址格式不合法。
			return 0;
		}
		String[] array = IPAddress.split("\\.");
		int a = Integer.parseInt(array[0]);
		int b = Integer.parseInt(array[1]) << 8;
		int c = Integer.parseInt(array[2]) << 16;
		int d = Integer.parseInt(array[3]) << 24;
		return a | b | c | d;
	}

	/**
	 * 速度转为字符串
	 * 
	 * @param speed
	 *            速度，以B为单位
	 * @param precision
	 *            保留小数点位数
	 * @return String[2] 其中ret[0]是数字 ret[1]是单位
	 */
	public static String[] convertSpeeds(long speed, int precision) {
		String[] ret = new String[2];
		String str = convertFileSize(speed, 0);
		// 单位是一个字节
		String unit = str.substring(str.length() - 1);
		ret[0] = str.substring(0, str.lastIndexOf(unit));
		// ret[1] = unit + "/s";
		// 调整单位格式统一设置为B/s、KB/s、MB/s、GB/s
		if (unit.equals(UNIT_BYTE)) {
			ret[1] = unit + "/s";
		} else {
			ret[1] = unit + "B/s";
		}

		return ret;
	}

	/**
	 * 小数转换为百分值
	 * 
	 * @param value
	 *            待转换
	 * @param scale
	 *            小数位数 10.1% 10.01%
	 * @param zeroStr
	 *            0的返回值
	 * @return e.g. 10.5
	 */
	public static String convertPercent(float value, int scale, String zeroStr) {
		BigDecimal b = new BigDecimal(value * 100);
		value = b.divide(new BigDecimal(1), scale, BigDecimal.ROUND_HALF_UP)
				.floatValue();
		if (Float.compare(value, (float) Math.pow(10, -scale)) < 0) {
			return zeroStr;
		} else {
			return String.valueOf(value);
		}
	}

	public static String convertFileSize(long file_size, int precision) {
		long int_part = 0;
		double fileSize = file_size;
		long temp = file_size;
		int i = 0;
		double base = 1;
		String baseUnit = "M";
		String fileSizeStr = null;

		while (temp / 1000 > 0) {
			int_part = temp / 1000;
			temp = int_part;
			i++;
		}

		switch (i) {
		// case 0:
		// // B
		// base = BASE_B;
		// baseUnit = UNIT_BYTE;
		// // return "小于1" + baseUnit;
		// break;

		case 0:
		case 1:
			// KB
			base = BASE_KB;
			baseUnit = UNIT_KB;
			precision = 0;
			break;

		case 2:
			// MB
			base = BASE_MB;
			baseUnit = UNIT_MB;
			break;

		case 3:
			// GB
			base = BASE_GB;
			baseUnit = UNIT_GB;
			break;
		case 4:
			// TB
			base = BASE_TB;
			baseUnit = UNIT_TB;
			break;
		default:
			// PB
			base = BASE_PB;
			baseUnit = UNIT_PB;
			break;
		}

		double size = fileSize / base;
		fileSizeStr = String.valueOf(size);

		int indexMid = fileSizeStr.indexOf('.');
		String result = null;

		if (precision == 0) {
			result = (-1 == indexMid ? fileSizeStr : fileSizeStr.substring(0,
					indexMid)) + baseUnit;
		} else {
			String pre = -1 == indexMid ? fileSizeStr : fileSizeStr.substring(
					0, indexMid);
			String sub = -1 == indexMid ? "" : fileSizeStr
					.substring(indexMid + 1);
			if (sub.length() > precision) {
				sub = sub.substring(0, precision);
			}
			result = pre + "." + sub + baseUnit;
		}

		return result;
	}

	/**
	 * 将int32类型的IP地址转换成四个8字节的String类型。
	 * 
	 * @param addr
	 * @return
	 */
	public static String ipAddressToString(int addr) {
		StringBuffer buf = new StringBuffer(16);
		buf.append(addr & 0xff).append('.').append((addr >>>= 8) & 0xff)
				.append('.').append((addr >>>= 8) & 0xff).append('.')
				.append((addr >>>= 8) & 0xff);
		return buf.toString();
	}

	static public int parseInt(String mEpisodeIdStr2) {
		int i, result = 0;
		if (null == mEpisodeIdStr2)
			return 0;
		for (i = 0; i < mEpisodeIdStr2.length(); i++)
			if ((mEpisodeIdStr2.charAt(i) >= '0')
					&& mEpisodeIdStr2.charAt(i) <= '9')
				result = result * 10 + (mEpisodeIdStr2.charAt(i) - '0');
		return result;
	}

	public static int Str2Int(String strInt, int defaultValue) {
		if (null == strInt)
			return defaultValue;
		String newStrInt = strInt.trim();
		int result = 0;
		try {
			result = Integer.parseInt(newStrInt);
		} catch (NumberFormatException e) {
			result = defaultValue;
		}
		return result;
	}

	public static String second2HMS(long seconds) {
		StringBuffer text = new StringBuffer();
		long remainder = seconds;
		long days = remainder / (60 * 60 * 24);
		remainder = remainder % (60 * 60 * 24);

		long hours = remainder / (60 * 60);
		remainder = remainder % (60 * 60);

		long minites = remainder / 60;
		remainder = remainder % 60;

		if (days > 0) {
			text.append(days).append("天");
		}

		if (hours > 0 || days > 0) {
			text.append(hours).append("小时");
		}

		if (minites > 0 || hours > 0 || days > 0) {
			text.append(minites).append("分");
		}

		text.append(remainder).append("秒");

		return text.toString();
	}

}
