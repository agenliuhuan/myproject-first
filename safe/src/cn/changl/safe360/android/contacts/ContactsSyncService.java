package cn.changl.safe360.android.contacts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mobi.dlys.android.core.utils.HandlerUtils.StaticHandler;
import mobi.dlys.android.core.utils.LogUtils;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.api.ApiClient;
import cn.changl.safe360.android.api.GroupApiClient;
import cn.changl.safe360.android.api.UserApiClient;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ContactsObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.utils.ContactsUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class ContactsSyncService extends Service {
	private final static String TAG = ContactsSyncService.class.getSimpleName();

	public static final String ReadingStatus = "cn.changl.safe360.android.service.contacts.reading.status";
	public static int mContactsReadingStatus = 0; // 0:未开始 1:正在读取 2:读取完成

	// 延时处理同步联系人，若在延时期间，通话记录数据库未改变，即判断为联系人被改变了
	private final static int ELAPSE_TIME = 10000;

	private StaticHandler mHandler = null;

	public ContentObserver mObserver = new ContentObserver(new Handler()) {

		@Override
		public void onChange(boolean selfChange) {
			// 当系统联系人数据库发生更改时触发此操作

			// 去掉多余的或者重复的同步
			mHandler.removeMessages(0);

			// 延时ELAPSE_TIME(10秒）发送同步信号“0”
			mHandler.sendEmptyMessageDelayed(0, ELAPSE_TIME);
		}

	};

	// 当通话记录数据库发生更改时触发此操作
	private ContentObserver mCallLogObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			// 当通话记录数据库发生更改时触发此操作
			// 如果延时期间发现通话记录数据库也改变了，即判断为联系人未被改变，取消前面的同步
			mHandler.removeMessages(0);
		}

	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 注册监听通话记录数据库
		getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mCallLogObserver);
		// 注册监听联系人数据库
		getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mObserver);

		// 为了避免同步联系人时阻塞主线程，此处获取一个子线程的handler
		new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				mHandler = new StaticHandler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case 0:
							updataContact();
							break;

						default:
							break;
						}
					}
				};
				updataContact();
				Looper.loop();
			}
		}).start();

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	@Override
	public void onDestroy() {
		mContactsReadingStatus = 0;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return flags;
	}

	// 在此处处理联系人被修改后应该执行的操作
	public void updataContact() {
		mContactsReadingStatus = 1;
		Cursor cursor = null;
		ContactDataBase cb = null;
		try {
			cb = new ContactDataBase(this);
			cb.createTable();
			LogUtils.i("constantssize", "APP readContactAndStore ContactsList Cursorstart");
			Map<String, String> map = ContactsUtils.getContact(this);
			LogUtils.i("constantssize", "APP readContactAndStore ContactsList Cursorend:" + map.size());
			Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
			List<ContactsObject> contactsList = new ArrayList<ContactsObject>();
			ArrayList<String> phoneList = new ArrayList<String>();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				String phone = entry.getKey();
				String name = entry.getValue();
				ContactsObject contacts = new ContactsObject();
				contacts.setName(name);
				contacts.setPhone(phone);
				contacts.setType(1);
				cb.insertRow(contacts);
				contactsList.add(contacts);
			}

			Message message = App.getInstance().obtainMessage();
			if (null != message) {
				message.what = YSMSG.RESP_MATCH_PHONE_PROGRESS;
				message.arg1 = 0;
				message.arg2 = contactsList.size();
				CoreModel.getInstance().notifyOutboxHandlers(message);
			}

			Safe360Pb pb = null;
			for (int i = 0; i < contactsList.size(); i++) {
				ContactsObject contacts = contactsList.get(i);
				if (contacts != null) {
					phoneList.add(contacts.getPhone());

					if (phoneList.size() > 200) {
						pb = UserApiClient.checkUserRegist(phoneList);
						if (null != message) {
							message.what = YSMSG.RESP_MATCH_PHONE_PROGRESS;
							message.arg1 = phoneList.size() * 100 / contactsList.size();
							message.arg2 = contactsList.size();
							CoreModel.getInstance().notifyOutboxHandlers(message);
						}
						if (ApiClient.isOK(pb)) {
							List<UserInfo> userInfoList = pb.getUserInfosList();
							if (userInfoList != null && userInfoList.size() > 0) {
								for (UserInfo info : userInfoList) {
									if (info.getRegistStatus()) {
										cb.updateRegisterRow(info.getPhone(), info.getUserId());
									}
								}
							}
						}
						phoneList.clear();
					}
				}
			}

			if (phoneList.size() > 0) {
				pb = UserApiClient.checkUserRegist(phoneList);
				if (null != message) {
					message.what = YSMSG.RESP_MATCH_PHONE_PROGRESS;
					message.arg1 = 100;
					message.arg2 = contactsList.size();
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}

				if (ApiClient.isOK(pb)) {
					List<UserInfo> userInfoList = pb.getUserInfosList();
					if (userInfoList != null && userInfoList.size() > 0) {
						for (UserInfo info : userInfoList) {
							if (info.getRegistStatus()) {
								cb.updateRegisterRow(info.getPhone(), info.getUserId());
							}
						}
					}
				}
			}
			if (CoreModel.getInstance().getUserInfo() != null) {
				UserObject user = CoreModel.getInstance().getUserInfo();
				pb = GroupApiClient.getGroupInfo(user.getUserId(), user.getToken(), 1, 20);
				if (ApiClient.isOK(pb)) {
					List<UserInfo> userInfoList = pb.getUserInfosList();
					if (userInfoList != null && userInfoList.size() > 0) {
						for (UserInfo info : userInfoList) {
							cb.updateFriendRow(info.getPhone(), info.getUserId());
						}
					}
				}
			}
			LogUtils.i("constantssize", "APP readContactAndStore insertend");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			if (cb != null) {
				cb.close();
			}
		}

		mContactsReadingStatus = 2;
		Intent i = new Intent(ContactsSyncService.ReadingStatus);
		sendBroadcast(i);
	}

	public static boolean isReading() {
		return (mContactsReadingStatus == 1) ? true : false;
	}

	public static boolean isReadComplete() {
		return (mContactsReadingStatus == 2) ? true : false;
	}
}
