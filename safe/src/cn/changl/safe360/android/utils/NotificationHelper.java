package cn.changl.safe360.android.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;

public class NotificationHelper {

	public static final int SOUND_NONE = -1;
	public static final int SOUND_SYSTEM = -2;
	public static final int ACTIVITY_REQUEST_CODE = 0;

	private NotificationManager mNotifyManager;
	private NotificationCompat.Builder mBuilder;

	private static final Object lock = new Object();
	private static NotificationHelper mInstance;

	private NotificationHelper() {

		try {
			mNotifyManager = (NotificationManager) App.getInstance().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
			if (SystemUtils.isMeetVersion(11/*
											 * android.os.Build.VERSION_CODES.
											 * HONEYCOMB
											 */)) {
				mBuilder = new Builder(App.getInstance().getApplicationContext());
				RemoteViews remoteViews = new RemoteViews(App.getInstance().getApplicationContext().getPackageName(), R.layout.comm_statusbar_notification);
				if (mBuilder != null && remoteViews != null) {
					mBuilder.setContent(remoteViews);
				}
			} else {

			}

		} catch (Exception e) {
		}

	}

	public static NotificationHelper getInstance() {
		if (null == mInstance) {
			synchronized (lock) {
				if (null == mInstance) {
					mInstance = new NotificationHelper();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 更新通知
	 * 
	 * @param id
	 * @param title
	 * @param secondTilte
	 * @param noise
	 *            如果noise为true，则开始通知提示音，否则无声音。
	 * @param intent
	 * 
	 * @return 返回通知ID
	 */
	@SuppressWarnings("deprecation")
	public int showOrUpdateNotification(int id, String tickerText, String title, String secondTitle, int sound, boolean cancelOnClick, Intent intent) {

		Notification notification = new Notification();

		switch (sound) {
		case SOUND_NONE:
			break;

		case SOUND_SYSTEM:
			notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
			break;

		default:
			// notification.sound = Uri.parse("android.resource://" +
			// LocalApplication.getInstance().getPackageName() + "/" + sound);
			// notification.defaults = Notification.DEFAULT_SOUND |
			// Notification.DEFAULT_LIGHTS;
			break;
		}

		notification.flags = Notification.FLAG_SHOW_LIGHTS;
		if (cancelOnClick) {
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
		} else {
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
		}
		notification.icon = R.drawable.ic_stat_notify;
		notification.tickerText = tickerText;

		// notification.contentIntent = contentIntent;

		PendingIntent contentIntent = PendingIntent.getActivity(App.getInstance(), ACTIVITY_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(App.getInstance().getApplicationContext(), title, secondTitle, contentIntent);

		mNotifyManager.notify(id, notification);

		return id;
	}

	/**
	 * 更新通知
	 * 
	 * @param id
	 * @param title
	 * @param secondTilte
	 * @param noise
	 *            如果noise为true，则开始通知提示音，否则无声音。
	 * @param intent
	 * 
	 * @return 返回通知ID
	 */
	public int showOrUpdateNotification(int id, RemoteViews remoteView, int sound, boolean cancelOnClick, Intent intent) {

		Notification notification = new Notification();
		notification.icon = R.drawable.ic_stat_notify;
		notification.tickerText = App.getInstance().getApplicationContext().getString(R.string.app_name);

		switch (sound) {
		case SOUND_NONE:
			break;

		case SOUND_SYSTEM:
			notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
			break;

		default:
			// notification.sound = Uri.parse("android.resource://" +
			// LocalApplication.getInstance().getPackageName() + "/" + sound);
			break;
		}

		notification.flags = Notification.FLAG_SHOW_LIGHTS;
		if (cancelOnClick) {
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
		} else {
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
		}

		PendingIntent contentIntent = PendingIntent.getActivity(App.getInstance(), ACTIVITY_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		notification.contentView = remoteView;

		mNotifyManager.notify(id, notification);

		return id;
	}

	public void cancelNotification(int notificationId) {
		mNotifyManager.cancel(notificationId);
	}

	public void cancelAllNotification() {
		mNotifyManager.cancelAll();
	}
}
