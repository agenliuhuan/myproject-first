package cn.changl.safe360.android.service;

import mobi.dlys.android.core.net.extendcmp.ojm.OJMFactory;
import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.LogUtils;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.biz.vo.PushMessageObject;
import cn.changl.safe360.android.biz.vo.PushMsgObject;
import cn.changl.safe360.android.db.DatabaseManager;
import cn.changl.safe360.android.db.dao.PushMsgObjectDao;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.login.SplashActivity;
import cn.changl.safe360.android.ui.main.NotifyId;
import cn.changl.safe360.android.utils.NotificationHelper;
import cn.changl.safe360.android.utils.PreferencesUtils;

public class PushMsgHandleService extends IntentService {
	public static final String TAG = PushMsgHandleService.class.getSimpleName();
	public static final String MSG = "msg";
	public static final String CONTENT = "content";

	public final static String ACTION_PUSH_MSG = "cn.changl.safe360.android.service.pushmsg";

	public enum CMD {
		invite_friend, remove_friend, join_group, exit_group, start_trip, finish_trip, exit_trip, sos, close_hide, open_hide, online, offline, client_custom, NOVALUE;

		public static CMD toCmd(String str) {
			try {
				return valueOf(str);
			} catch (Exception ex) {
				return NOVALUE;
			}
		}
	}

	public PushMsgHandleService() {
		// 设置子线程名称
		super("push msg handle thread");
	}

	public static Intent createPushMsgIntent(Context context, String msg, String content) {
		Intent intent = new Intent(context, PushMsgHandleService.class);
		intent.putExtra(MSG, msg);
		intent.putExtra(CONTENT, content);

		return intent;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final String msg = intent.getStringExtra(MSG);
		final String content = intent.getStringExtra(CONTENT);

		LogUtils.d(TAG, "message: " + msg + " content:" + content);

		PushMessageObject pushMsg = null;
		try {
			pushMsg = OJMFactory.createOJM().fromJson(msg, PushMessageObject.class);
		} catch (Exception e) {
		}
		if (pushMsg != null) {
			switch (CMD.toCmd(pushMsg.getCmd())) {
			case invite_friend: {
				if (!CoreModel.getInstance().isLogined()
						|| !ActivityUtils.isActivityExist(getApplicationContext(), "mobi.dlys.android.familysafer",
								"mobi.dlys.android.familysafer.ui.main.MainActivity") || !App.getInstance().mInitMainActivity) {
					LogUtils.d(TAG, "save message: " + msg + " content:" + content);
					if (PreferencesUtils.getLastLoginUserId() != -1) {
						DatabaseManager.getInstance().initHelper(PreferencesUtils.getLastLoginUserId());
						PushMsgObject msgObj = new PushMsgObject();
						msgObj.setMsg(msg);
						msgObj.setContent(content);
						new PushMsgObjectDao().insert(msgObj);
						return;
					}
				}
			}
				break;
			case start_trip:
			case sos: {
				if (!CoreModel.getInstance().isLogined()
						|| !ActivityUtils.isActivityExist(getApplicationContext(), "mobi.dlys.android.familysafer",
								"mobi.dlys.android.familysafer.ui.main.MainActivity")) {
					LogUtils.d(TAG, "MainActivity is not exist");
					// PushMsgObject msgObj = new PushMsgObject();
					// msgObj.setMsg(msg);
					// msgObj.setContent(content);
					// new PushMsgObjectDao().insert(msgObj);

					Intent intent2 = new Intent(getApplicationContext(), SplashActivity.class);
					intent2.putExtra(MSG, msg);
					intent2.putExtra(CONTENT, content);
					NotificationHelper.getInstance().showOrUpdateNotification(NotifyId.ID_PUSH_TRIP_MESSAGE, "暖途：" + pushMsg.getTitle(), pushMsg.getTitle(),
							pushMsg.getDescription(), 0, true, intent2);
					return;
				}
				LogUtils.d(TAG, "MainActivity is exist");
			}
				break;
			}
		}

		if (ActivityUtils.isAppOnForeground(getApplicationContext())) {
			LogUtils.d(TAG, "App isAppOnForeground");
			Intent intent2 = new Intent(ACTION_PUSH_MSG);
			intent2.putExtra(MSG, msg);
			intent2.putExtra(CONTENT, content);
			sendBroadcast(intent2);
		} else if (ActivityUtils.isAppOnBackground(getApplicationContext())) {
			LogUtils.d(TAG, "App isAppOnBackround");
			Intent intent2 = new Intent(ACTION_PUSH_MSG);
			intent2.putExtra(MSG, msg);
			intent2.putExtra(CONTENT, content);
			sendBroadcast(intent2);
		} else {
			LogUtils.d(TAG, "App is no running");
			Intent intent2 = new Intent(getApplicationContext(), SplashActivity.class);
			startActivity(intent2);
		}

	}
}
