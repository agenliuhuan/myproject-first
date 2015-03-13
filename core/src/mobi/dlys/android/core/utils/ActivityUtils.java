/*
 * 文件名称 : ActivityUtil.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2013-10-12, 上午11:28:49
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.utils;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * 对Activity基本判断的工具
 * <p>
 */
public class ActivityUtils {

	/**
	 * 判断程序是否在前台.
	 */
	public static boolean isAppInForeground(Context context) {
		boolean result = false;
		String packageName = context.getPackageName();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> task_list = activityManager.getRunningTasks(1);
		if (task_list.size() > 0) {
			if (task_list.get(0).topActivity.getPackageName().trim().equals(packageName)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * 判断程序是否在后台运行
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isAppOnBackground(Context ctx) {
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		if (appProcesses != null) {
			String package_name = ctx.getPackageName();
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.processName.equals(package_name) && appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND
						&& appProcess.importance != RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检测该Context是否在栈顶
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isTop(Context activity) {
		boolean isTop = false;
		ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = am.getRunningTasks(Integer.MAX_VALUE);
		if (runningTasks != null && runningTasks.size() > 0) {
			RunningTaskInfo taskInfo = runningTasks.get(0);
			String temp = taskInfo.topActivity.getClassName();
			if (activity.getClass().getName().equals(temp)) {
				return true;
			}
		}
		return isTop;
	}

	public static boolean isActivityForeground(Context ctx, Class<?> cls) {
		boolean result = false;
		String clsName = cls.getName();
		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> task_list = activityManager.getRunningTasks(1);
		if (task_list.size() > 0) {
			String topName = task_list.get(0).topActivity.getClassName();
			if (clsName.equals(topName)) {
				result = true;
			}
		}
		return result;
	}

	public static void startActivityWithAnimation(Activity fromActivity, Class<?> activityClass) {
		Intent intent = new Intent(fromActivity, activityClass);
		fromActivity.startActivity(intent);
	}

	public static void finishActivityWithAnimation(Activity activity) {
		activity.finish();
	}

	public static boolean isAppOnForeground(Context ctx) {
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		if (appProcesses != null) {
			String package_name = ctx.getPackageName();
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.processName.equals(package_name) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断activity是否存在
	 * 
	 * @param ctx
	 * @param pkgName
	 * @param cls
	 * @return
	 */
	public static boolean isActivityExist(Context ctx, String pkgName, String cls) {
		Intent intent = new Intent();
		intent.setClassName(pkgName, cls);
		if (intent.resolveActivity(ctx.getPackageManager()) == null) {
			// 说明系统中不存在这个activity
			return false;
		}

		return true;
	}

	/**
	 * 切换应用到前台
	 * 
	 * @param activity
	 */
	public static void moveTaskToFront(Activity activity) {
		if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
			ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
			am.moveTaskToFront(activity.getTaskId(), 0);
		}
	}

	public static void startActivityUrlView(Context context, String url) {
		Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(it);
	}

	public static void startActivity(Context context, Class<?> cls) {
		ActivityUtils.startActivity(context, cls, null);
	}

	public static void startActivity(Context context, Class<?> cls, int flags) {
		Intent intent = new Intent(context, cls);
		intent.setFlags(flags);
		context.startActivity(intent);
	}

	public static void startActivity(Context context, Class<?> cls, Bundle extras) {
		Intent intent = new Intent(context, cls);
		if (null != extras && !extras.isEmpty()) {
			intent.putExtras(extras);
		}
		context.startActivity(intent);
	}

	public static void startActivity(Context context, Class<?> cls, Bundle extras, int flags) {
		Intent intent = new Intent(context, cls);
		intent.setFlags(flags);
		if (null != extras && !extras.isEmpty()) {
			intent.putExtras(extras);
		}
		context.startActivity(intent);
	}

	public static void startActivityForResult(Activity activity, Class<?> cls, int requestCode) {
		ActivityUtils.startActivityForResult(activity, cls, null, requestCode);
	}

	public static void startActivityForResult(Activity activity, Class<?> cls, Bundle extras, int requestCode) {
		Intent intent = new Intent(activity, cls);
		if (null != extras && !extras.isEmpty()) {
			intent.putExtras(extras);
		}
		activity.startActivityForResult(intent, requestCode);
	}

}
