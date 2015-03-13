/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.changl.safe360.android.im.hx.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.im.hx.model.Constant;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.TimeInfo;

public class CommonUtils {

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}

		return false;
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	public static String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
			if (message.direct == EMMessage.Direct.RECEIVE) {
				// 从sdk中提到了ui中，使用更简单不犯错的获取string方法
				// digest = EasyUtils.getAppResourceString(context,
				// "location_recv");
				digest = getStrng(context, R.string.location_recv);
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
				// digest = EasyUtils.getAppResourceString(context,
				// "location_prefix");
				digest = getStrng(context, R.string.location_prefix);
			}
			break;
		case IMAGE: // 图片消息
			digest = getStrng(context, R.string.picture);
			break;
		case VOICE:// 语音消息
			digest = getStrng(context, R.string.voice);
			break;
		case VIDEO: // 视频消息
			digest = getStrng(context, R.string.video);
			break;
		case TXT: // 文本消息
			if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = txtBody.getMessage();
			} else {
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
			}
			break;
		case FILE: // 普通文件消息
			digest = getStrng(context, R.string.file);
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}

	static String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}

	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.getClassName();
		else
			return "";
	}

	public static String getTimestampString(Date paramDate) {
		String str = null;
		long l = paramDate.getTime();
		if (isSameDay(l)) {
			Calendar localCalendar = GregorianCalendar.getInstance();
			localCalendar.setTime(paramDate);
			int i = localCalendar.get(11);
			if (i > 17)
				str = "晚上 HH:mm";
			else if ((i >= 0) && (i <= 6))
				str = "凌晨 HH:mm";
			else if ((i > 11) && (i <= 17))
				str = "下午 HH:mm";
			else
				str = "上午 HH:mm";
		} else if (isYesterday(l)) {
			str = "昨天 HH:mm";
		} else {
			str = "M月d日 HH:mm";
		}
		return new SimpleDateFormat(str, Locale.CHINA).format(paramDate);
	}

	private static boolean isSameDay(long paramLong) {
		TimeInfo localTimeInfo = getTodayStartAndEndTime();
		return ((paramLong > localTimeInfo.getStartTime()) && (paramLong < localTimeInfo.getEndTime()));
	}

	private static boolean isYesterday(long paramLong) {
		TimeInfo localTimeInfo = getYesterdayStartAndEndTime();
		return ((paramLong > localTimeInfo.getStartTime()) && (paramLong < localTimeInfo.getEndTime()));
	}

	public static TimeInfo getYesterdayStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.add(5, -1);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.add(5, -1);
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getTodayStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getBeforeYesterdayStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.add(5, -2);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.add(5, -2);
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getCurrentMonthStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.set(5, 1);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getLastMonthStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.add(2, -1);
		localCalendar1.set(5, 1);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.add(2, -1);
		localCalendar2.set(5, 1);
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		localCalendar2.roll(5, -1);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

}
