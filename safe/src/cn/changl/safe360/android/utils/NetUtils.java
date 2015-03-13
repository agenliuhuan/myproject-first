package cn.changl.safe360.android.utils;

import mobi.dlys.android.core.utils.LogUtils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import cn.changl.safe360.android.App;

/**
 * 网络工具
 */
public class NetUtils {
	public static final String TAG = "NetUtil";
	public static boolean isProxy = false;
	public static boolean isWifi = true;
	public static String proxy;

	/** 手机网络连接不正常 */
	public static final int NET_NOT_CONNECTION = -1001;
	/** 手机网络连接正常 */
	public static final int NET_CONNECTED = -1002;

	/**
	 * 检查网络连接
	 * 
	 * @return
	 */
	public static int checkNet() {
		ConnectivityManager connectivityManager = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (null == networkInfo || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
			LogUtils.d(TAG, "无网络连接");
			return NetUtils.NET_NOT_CONNECTION;
		}

		LogUtils.d(TAG, networkInfo.getTypeName() + "网络连接");
		LogUtils.d(TAG, networkInfo.getExtraInfo() + "网络连接");

		boolean tmpIsProxy = false;
		String extraInfo = networkInfo.getExtraInfo();
		// 移动
		if ("cmwap".equals(extraInfo)) {
			tmpIsProxy = true;
			proxy = "10.0.0.172";
		}
		// 联通
		else if ("3gwap".equals(extraInfo)) {
			tmpIsProxy = true;
			proxy = "10.0.0.172";
		} else if ("uniwap".equals(extraInfo)) {
			tmpIsProxy = true;
			proxy = "10.0.0.172";
		}
		// 电信
		else if ("ctwap".equals(extraInfo)) {
			tmpIsProxy = true;
			proxy = "10.0.0.200";
		} else {
			tmpIsProxy = false;
			proxy = null;
		}

		if (isProxy != tmpIsProxy) {
			isProxy = tmpIsProxy;
			// 更新网络连接
			// Apis.createHttpClient();
		}

		isWifiEnable();

		return NetUtils.NET_CONNECTED;
	}

	private static boolean isActive() {
		ConnectivityManager connectivityManager = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (null == networkInfo || !networkInfo.isAvailable() || !networkInfo.isConnected() || networkInfo.getTypeName().endsWith("WIFI")) {
			return false;
		}
		return true;
	}

	/*
	 * 是否Wifi开启
	 */
	public static boolean isWifiEnable() {
		boolean bRet = false;

		try {
			WifiManager wifiManager = (WifiManager) App.getInstance().getSystemService(Context.WIFI_SERVICE);

			if ((wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) || (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)) {
				bRet = true;
			}
		} catch (Exception e) {
			bRet = false;
		}

		if (bRet != isWifi) {
			if (NetUtils.isActive()) {
				isWifi = bRet;
				if (!isWifi) {
					// ToastUtil.showShort(App.getInstance(),
					// R.string.set_gprs);
				}
			}
		}

		return bRet;
	}
}
