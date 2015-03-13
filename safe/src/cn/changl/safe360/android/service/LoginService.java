package cn.changl.safe360.android.service;

import mobi.dlys.android.core.utils.HandlerUtils.StaticHandler;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import cn.changl.safe360.android.api.ApiClient;
import cn.changl.safe360.android.api.UserApiClient;
import cn.changl.safe360.android.biz.vo.LoginObject;
import cn.changl.safe360.android.utils.PreferencesUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;

public class LoginService extends Service {
	private final static String TAG = LoginService.class.getSimpleName();

	public static final String LoginSuccess = "cn.changl.safe360.android.service.login.success";
	public static final String LoginFailed = "cn.changl.safe360.android.service.login.failed";

	private StaticHandler mHandler;

	private static final String EXTRA_Phone = "extraPhone";
	private static final String EXTRA_Password = "extraPassword";
	private static final String EXTRA_Lng = "extraLng";
	private static final String EXTRA_Lat = "extraLat";
	private static final String EXTRA_AddrName = "extraAddrName";
	private static final String EXTRA_Location = "extraLocation";
	private static final String EXTRA_GotoActivity = "extraGotoActivity";

	public static int mLoginStatus = 0; // 0:未开始 1:正在登录 2:登录成功 3:登录失败
	private LoginObject mLoginObject;

	private String mGotoActivity;

	/**
	 * 启动登录服务
	 * 
	 * @param context
	 * @param gotoActivity
	 *            跳转到哪个Activity
	 * @param phone
	 * @param password
	 * @param lng
	 * @param lat
	 * @param addrname
	 * @param location
	 */
	public static void startService(Context context, String gotoActivity, String phone, String password, String lng, String lat, String addrname,
			String location) {
		if (mLoginStatus == 1) {
			return;
		}

		Intent intent = new Intent(context, LoginService.class);
		intent.putExtra(EXTRA_Phone, phone);
		intent.putExtra(EXTRA_Password, password);
		intent.putExtra(EXTRA_Lng, lng);
		intent.putExtra(EXTRA_Lat, lat);
		intent.putExtra(EXTRA_AddrName, addrname);
		intent.putExtra(EXTRA_Location, location);
		intent.putExtra(EXTRA_GotoActivity, gotoActivity);

		context.startService(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mLoginObject = new LoginObject();

		// 为了避免阻塞主线程，此处获取一个子线程的handler
		new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				mHandler = new StaticHandler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case 0:
							break;
						default:
							break;
						}
					}
				};
				mLoginStatus = 1;
				userlogin(mLoginObject.getPhone(), mLoginObject.getPassword(), mLoginObject.getLng(), mLoginObject.getLat(), mLoginObject.getAddrname(),
						mLoginObject.getLocation());
				Looper.loop();
			}
		}).start();

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		if (mLoginObject != null) {
			mLoginObject.setPhone(intent.getStringExtra(EXTRA_Phone));
			mLoginObject.setPassword(intent.getStringExtra(EXTRA_Password));
			mLoginObject.setLat(intent.getStringExtra(EXTRA_Lat));
			mLoginObject.setLng(intent.getStringExtra(EXTRA_Lng));
			mLoginObject.setAddrname(intent.getStringExtra(EXTRA_AddrName));
			mLoginObject.setLocation(intent.getStringExtra(EXTRA_Location));
			mGotoActivity = intent.getStringExtra(EXTRA_GotoActivity);
		}
	}

	@Override
	public void onDestroy() {
		mLoginStatus = 0;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return flags;
	}

	public void userlogin(final String phone, final String password, final String lng, final String lat, final String addrname, final String location) {
		String tempLng = lng;
		String tempLat = lat;
		String tempAddrname = addrname;
		String tempLocation = location;
		if (TextUtils.isEmpty(tempLng))
			tempLng = "";
		if (TextUtils.isEmpty(tempLat)) {
			tempLat = "";
		}
		if (TextUtils.isEmpty(tempLocation)) {
			tempLocation = "";
		}
		if (TextUtils.isEmpty(tempAddrname)) {
			tempAddrname = "";
		}
		Safe360Pb pb = UserApiClient.login(phone, password, tempLng, tempLat, tempAddrname, tempLocation, PreferencesUtils.getBaiduChannelId(),
				PreferencesUtils.getBaiduUserId(), PreferencesUtils.getToken());

		if (!ApiClient.isOK(pb)) {
			mLoginStatus = 3;
			Intent i = new Intent(LoginFailed);
			sendBroadcast(i);
		} else {
			mLoginStatus = 2;
			Intent i = new Intent(LoginSuccess);
			sendBroadcast(i);
		}
	}

}
