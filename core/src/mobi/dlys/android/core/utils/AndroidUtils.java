/**
 * AndroidUtils.java
 *
 * Copyright 2014 dlys.mobi All Rights Reserved.
 */
package mobi.dlys.android.core.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Android的工具类,提供一些静态通用方法. User: Wangle<87292008@qq.com> Date: 2014/5/16 0016
 * Time: 10:47
 */
public final class AndroidUtils {

	/**
	 * 判断网络是否可用.
	 * 
	 * @param context
	 *            上下文
	 * @return true代表网络可用, false代表网络不可用.
	 */
	public static boolean isNetworkValid(Context context) {
		AssertUtils.notNull(context, "Context must not be null.");
		boolean result = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == connectivityManager) {
			result = false;
		} else {
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (null == networkInfo) {
				result = false;
			} else {
				if (networkInfo.isAvailable()) {
					result = true;
				}
			}
		}
		return result;
	}

	private AndroidUtils() {
	}
}
