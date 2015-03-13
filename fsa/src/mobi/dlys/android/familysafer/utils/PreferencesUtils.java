package mobi.dlys.android.familysafer.utils;

import mobi.dlys.android.familysafer.App;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

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
	 * 设置是否可以自动登录
	 * 
	 * @param auto
	 */
	public static void setAutoLoginValid(boolean auto) {
		setFieldBooleanValue(FileUtils.APP, "AutoLoginValid", auto);
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
	 * 获取通知的接收时间
	 * 
	 * @return
	 */
	public static long getEventRecvTime() {
		return getFieldLongValue(FileUtils.APP, "EventRecvTime");
	}

	/**
	 * 设置通知的接收时间
	 * 
	 * @param eventRecvTime
	 */
	public static void setEventRecvTime(long eventRecvTime) {
		setFieldLongValue(FileUtils.APP, "EventRecvTime", eventRecvTime);
	}

	/**
	 * 获取好友请求数目的接收时间
	 * 
	 * @return
	 */
	public static long getFriendRequestRecvTime(int userId) {
		return getFieldLongValue(FileUtils.APP, userId + "FriendRequestRecvTime");
	}

	/**
	 * 设置好友请求数目的接收时间
	 * 
	 * @param eventRecvTime
	 */
	public static void setFriendRequestRecvTime(int userId, long requestRecvTime) {
		setFieldLongValue(FileUtils.APP, userId + "FriendRequestRecvTime", requestRecvTime);
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

	static final int DEFAULT_REFRESH_TOKEN_MINUTE = 9; // 10分钟刷新一次
	static final int DEFAULT_MODIFY_LOCATION_MINUTE = 4; // 5分钟刷新一次

	/**
	 * 判断是否应该刷新token
	 */
	public static boolean isTimeToRefreshToken() {
		if (getFieldIntValue(FileUtils.APP, "TimeToRefreshToken") >= DEFAULT_REFRESH_TOKEN_MINUTE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置刷新token的时间间隔
	 */
	public static void setTimeToRefreshToken() {
		int minute = getFieldIntValue(FileUtils.APP, "TimeToRefreshToken");
		if (minute >= DEFAULT_REFRESH_TOKEN_MINUTE) {
			minute = -1;
		} else {
			minute += 1;
		}
		setFieldIntValue(FileUtils.APP, "TimeToRefreshToken", minute);
	}

	/**
	 * 判断是否应该修改地理位置
	 */
	public static boolean isTimeToModifyLocation() {
		if (getFieldIntValue(FileUtils.APP, "TimeToModifyLocation") >= DEFAULT_MODIFY_LOCATION_MINUTE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置修改地理位置的时间间隔
	 */
	public static void setTimeToModifyLocation() {
		int minute = getFieldIntValue(FileUtils.APP, "TimeToModifyLocation");
		if (minute >= DEFAULT_MODIFY_LOCATION_MINUTE) {
			minute = -1;
		} else {
			minute += 1;
		}
		setFieldIntValue(FileUtils.APP, "TimeToModifyLocation", minute);
	}

	static final int DEFAULT_SHOW_UPDATE_DIALOG_TIMES = 1;

	/**
	 * 判断是否需要显示更新对话框
	 * 
	 * @param version
	 * @return
	 */
	public static boolean isAllowToShowUpdateDialog(String version) {
		if (getFieldIntValue(FileUtils.APP, "ver:" + version) >= DEFAULT_SHOW_UPDATE_DIALOG_TIMES) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 设置显示对话框的次数
	 * 
	 * @param version
	 */
	public static void setAllowToShowUpdateDialog(String version) {
		int times = getFieldIntValue(FileUtils.APP, "ver:" + version);
		if (times >= DEFAULT_SHOW_UPDATE_DIALOG_TIMES) {
			return;
		} else {
			times += 1;
			setFieldIntValue(FileUtils.APP, "ver:" + version, times);
		}
	}

	/**
	 * 判断是否可以自动登录
	 * 
	 * @return
	 */
	public static boolean autoLogin() {
		String phone = PreferencesUtils.getLoginPhone();
		String pwd = PreferencesUtils.getLoginPwd();
		if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
			return true;
		}

		return false;
	}

	/**
	 * 设置新通知数
	 * 
	 * @param userId
	 * @param count
	 */
	public static void setNewEventCount(int userId, int count) {
		setFieldIntValue(FileUtils.APP, userId + "NewEventCount", count);
	}

	/**
	 * 获取新通知数
	 * 
	 * @param userId
	 * @return
	 */
	public static int getNewEventCount(int userId) {
		return getFieldIntValue(FileUtils.APP, userId + "NewEventCount");
	}

	/**
	 * 设置新SOS通知数
	 * 
	 * @param userId
	 * @param count
	 */
	public static void setNewSOSCount(int userId, int count) {
		setFieldIntValue(FileUtils.APP, userId + "NewSOSCount", count);
	}

	/**
	 * 获取新SOS通知数
	 * 
	 * @param userId
	 * @return
	 */
	public static int getNewSOSCount(int userId) {
		return getFieldIntValue(FileUtils.APP, userId + "NewSOSCount");
	}
	
	/**
	 * 设置新拍照留证通知数
	 * 
	 * @param userId
	 * @param count
	 */
	public static void setEventClueCount(int userId, int count) {
		setFieldIntValue(FileUtils.APP, userId + "EventClueCount", count);
	}
	
	/**
	 * 获取新拍照留证通知数
	 * 
	 * @param userId
	 * @return
	 */
	public static int getEventClueCount(int userId) {
		return getFieldIntValue(FileUtils.APP, userId + "EventClueCount");
	}

	/**
	 * 设置新checkin通知数
	 * 
	 * @param userId
	 * @param count
	 */
	public static void setNewCheckinCount(int userId, int count) {
		setFieldIntValue(FileUtils.APP, userId + "NewCheckinCount", count);
	}

	/**
	 * 获取新checkin通知数
	 * 
	 * @param userId
	 * @return
	 */
	public static int getNewCheckinCount(int userId) {
		return getFieldIntValue(FileUtils.APP, userId + "NewCheckinCount");
	}

	/**
	 * 设置新confirm通知数
	 * 
	 * @param userId
	 * @param count
	 */
	public static void setNewConfirmCount(int userId, int count) {
		setFieldIntValue(FileUtils.APP, userId + "NewConfirmCount", count);
	}

	/**
	 * 获取新confirm通知数
	 * 
	 * @param userId
	 * @return
	 */
	public static int getNewConfirmCount(int userId) {
		return getFieldIntValue(FileUtils.APP, userId + "NewConfirmCount");
	}

	/**
	 * 判断当前是否进入聊天界面
	 * 
	 * @return
	 */
	public static boolean isEnterChatActivity() {
		return getFieldBooleanValue(FileUtils.APP, "EnterChatActivity");
	}

	/**
	 * 设置是否当前进入了聊天界面
	 * 
	 * @param enter
	 */
	public static void setEnterChatActivity(boolean enter) {
		setFieldBooleanValue(FileUtils.APP, "EnterChatActivity", enter);
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

}