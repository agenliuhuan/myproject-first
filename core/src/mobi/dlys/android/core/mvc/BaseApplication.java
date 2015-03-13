package mobi.dlys.android.core.mvc;

import java.util.ArrayList;

import mobi.dlys.android.core.utils.HandlerUtils.StaticHandler;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Message;

public class BaseApplication extends Application {
	private ArrayList<Activity> mActivityList;
	private Activity mForegroundActivity;
	private StaticHandler mHandler;

	/**
	 * 创建
	 */
	public void onCreate() {
		mActivityList = new ArrayList<Activity>();
		mHandler = new StaticHandler();
	};

	public void addActivity(Activity activity) {
		if (activity != null && !mActivityList.contains(activity)) {
			mActivityList.add(activity);
		}
	}

	public void removeActivity(Activity activity) {
		if (activity != null) {
			mActivityList.remove(activity);
		}
	}

	public void clearActivity() {
		for (Activity activity : mActivityList) {
			activity.finish();
		}
		mActivityList.clear();
	}

	public void setForegroundActivity(Activity activity) {
		mForegroundActivity = activity;
	}

	public Activity getForegroundActivity() {
		return mForegroundActivity;
	}

	public Message obtainMessage() {
		return mHandler.obtainMessage();
	}

	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

}
