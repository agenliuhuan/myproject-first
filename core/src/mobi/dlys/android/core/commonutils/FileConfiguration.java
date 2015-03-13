package mobi.dlys.android.core.commonutils;

import java.io.File;
import java.util.Calendar;

import mobi.dlys.android.core.utils.SDCardUtils;

public class FileConfiguration {

	public static String getRootPath() {
		String sdcard = SDCardUtils.getPrimarySDCard();
		if (null == sdcard) {
			return null;
		}
		String log = null;
		if (null != sdcard) {
			log = sdcard + "wthink/";
		}
		return log;
	}

	public static String getCrashPath() {
		String root = getRootPath();
		if (null != root) {
			return root + "log/crash/";
		} else {
			return null;
		}
	}

	public static String getLogFile() {
		String root = getRootPath();

		if (null == root) {
			return null;
		}

		long time = System.currentTimeMillis();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);

		StringBuffer sb = new StringBuffer();
		sb.append(root).append("log/msg/");

		File file = new File(sb.toString());
		if (!file.exists() && !file.mkdirs()) {
			return null;
		}

		sb.append("log-").append(calendar.get(Calendar.YEAR)).append("-")
				.append(calendar.get(Calendar.MONTH)).append("-")
				.append(calendar.get(Calendar.DAY_OF_MONTH)).append(".txt");

		// file = new File(sb.toString());
		// if(!file.exists()) {
		// try {
		// if(!file.createNewFile()) {
		// return null;
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// return null;
		// }
		// }

		return sb.toString();
	}

	public static String getDownloadPath() {
		String root = getRootPath();
		if (null != root) {
			return root + "download/";
		} else {
			return null;
		}
	}

}
