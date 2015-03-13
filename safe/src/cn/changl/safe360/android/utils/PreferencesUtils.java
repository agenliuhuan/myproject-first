package cn.changl.safe360.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import cn.changl.safe360.android.App;

/**
 * 用户偏好设置工具
 */
public class PreferencesUtils {
	public static void setFieldStringValue(String prefName, String field, String value) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		preferences.edit().putString(field, value).commit();
	}

	public static String getFieldStringValue(String prefName, String field) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		return preferences.getString(field, "");
	}

	public static void setFieldIntValue(String prefName, String field, int value) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		preferences.edit().putInt(field, value).commit();
	}

	public static int getFieldIntValue(String prefName, String field) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		return preferences.getInt(field, -1);
	}

	public static void setFieldLongValue(String prefName, String field, long value) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		preferences.edit().putLong(field, value).commit();
	}

	public static long getFieldLongValue(String prefName, String field) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		return preferences.getLong(field, -1);
	}

	public static void setFieldBooleanValue(String prefName, String field, boolean value) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		preferences.edit().putBoolean(field, value).commit();
	}

	public static boolean getFieldBooleanValue(String prefName, String field) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		return preferences.getBoolean(field, false);
	}

	public static void setEscortUserSize(String prefName, String field, int value) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		preferences.edit().putInt(field, value).commit();
	}

	public static int getEscortUserSize(String prefName, String field) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		return preferences.getInt(field, 0);
	}

	public static void setEscortUserValue(String prefName, String field, int value) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		preferences.edit().putInt(field, value).commit();
	}

	public static int getEscortUserValue(String prefName, String field) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		return preferences.getInt(field, 0);
	}

	public static void setShowGuideValue(String field, boolean value) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(FileUtils.APP, Context.MODE_PRIVATE);
		preferences.edit().putBoolean(field, value).commit();
	}

	public static boolean getShowGuideValue(String field) {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(FileUtils.APP, Context.MODE_PRIVATE);
		return preferences.getBoolean(field, true);
	}

	/**
	 * 设置是否拦截了闹钟或短信
	 * 
	 * @param value
	 */
	public static void setAudioFocusChange(boolean value) {
		setFieldBooleanValue(FileUtils.APP, "AudioFocusChange", value);
	}

	/**
	 * 是否拦截了闹钟或短信
	 * 
	 * @return
	 */
	public static boolean isAudioFocusChange() {
		return getFieldBooleanValue(FileUtils.APP, "AudioFocusChange");
	}

	/**
	 * 清楚所有信息
	 */
	public static void clear() {
		SharedPreferences preferences = App.getInstance().getSharedPreferences(FileUtils.APP, Context.MODE_PRIVATE);
		preferences.edit().clear().commit();
	}

	/**
	 * 设置登录号码
	 * 
	 * @param phone
	 */
	public static void setLoginPhone(String phone) {
		setFieldStringValue(FileUtils.APP, "LoginPhone", AES.encrypt(phone));
	}

	/**
	 * 读取登录号码
	 * 
	 * @return
	 */
	public static String getLoginPhone() {
		String phone = "";
		try {
			phone = new String(AES.decrypt(getFieldStringValue(FileUtils.APP, "LoginPhone")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return phone;
	}

	/**
	 * 设置登录密码
	 * 
	 * @param pwd
	 */
	public static void setLoginPwd(String pwd) {
		setFieldStringValue(FileUtils.APP, "LoginPwd", AES.encrypt(pwd));
	}

	/**
	 * 读取登录密码
	 * 
	 * @return
	 */
	public static String getLoginPwd() {
		String pwd = "";
		try {
			pwd = new String(AES.decrypt(getFieldStringValue(FileUtils.APP, "LoginPwd")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pwd;
	}

	/**
	 * 判断是否有新版本
	 * 
	 * @return
	 */
	public static boolean hasNewVersion() {
		return getFieldBooleanValue(FileUtils.APP, "NewVersion");
	}

	/**
	 * 设置新版本提示
	 * 
	 * @param enter
	 */
	public static void setNewVersion(boolean hasNew) {
		setFieldBooleanValue(FileUtils.APP, "NewVersion", hasNew);
	}

	/**
	 * 判断是否设置了隐藏地理位置
	 * 
	 * @return
	 */
	public static boolean isHideLocation() {
		return getFieldBooleanValue(FileUtils.APP, "HideLocation");
	}

	/**
	 * 设置隐藏地理位置
	 * 
	 * @param hide
	 */
	public static void setHideLocation(boolean hide) {
		setFieldBooleanValue(FileUtils.APP, "HideLocation", hide);
	}

	/**
	 * 设置百度推送channel id
	 * 
	 * @param channelId
	 */
	public static void setBaiduChannelId(String channelId) {
		setFieldStringValue(FileUtils.APP, "BaiduChannelId", channelId);
	}

	/**
	 * 获取百度推送channel id
	 * 
	 * @return
	 */
	public static String getBaiduChannelId() {
		return getFieldStringValue(FileUtils.APP, "BaiduChannelId");
	}

	/**
	 * 判断channel id是否有效
	 * 
	 * @return
	 */
	public static boolean isBaiduChannelIdValid() {
		if (!TextUtils.isEmpty(getBaiduChannelId())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置百度推送user id
	 * 
	 * @param userId
	 */
	public static void setBaiduUserId(String userId) {
		setFieldStringValue(FileUtils.APP, "BaiduUserId", userId);
	}

	/**
	 * 获取百度推送user id
	 * 
	 * @return
	 */
	public static String getBaiduUserId() {
		return getFieldStringValue(FileUtils.APP, "BaiduUserId");
	}

	/**
	 * 判断user id 是否有效
	 * 
	 * @return
	 */
	public static boolean isBaiduUserIdValid() {
		if (!TextUtils.isEmpty(getBaiduUserId())) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isBaiduPushBindValid() {
		return isBaiduChannelIdValid() && isBaiduUserIdValid();
	}

	public static void resetBaiduBindInfo() {
		setBaiduChannelId("");
		setBaiduUserId("");
	}

	public static void setToken(String token) {
		setFieldStringValue(FileUtils.APP, "Safe360Token", token);
	}

	public static String getToken() {
		return getFieldStringValue(FileUtils.APP, "Safe360Token");
	}

	public static void setTripId(int tripId) {
		setFieldIntValue(FileUtils.APP, "TripId", tripId);
	}

	public static int getTripId() {
		return getFieldIntValue(FileUtils.APP, "TripId");
	}

	public static void resetTripId() {
		setTripId(0);
	}

	public static boolean isTripException() {
		if (getTripId() == -1 || getTripId() != 0) {
			return true;
		}

		return false;
	}

	public static boolean isTripRunnerException() {
		return getFieldBooleanValue(FileUtils.APP, "TripRunnerException");
	}

	public static void setTripRunnerException(boolean hasNew) {
		setFieldBooleanValue(FileUtils.APP, "TripRunnerException", hasNew);
	}

	public static void setLastLoginUserId(int userId) {
		setFieldIntValue(FileUtils.APP, "LastLoginUserId", userId);
	}

	public static int getLastLoginUserId() {
		return getFieldIntValue(FileUtils.APP, "LastLoginUserId");
	}
}