package cn.changl.safe360.android.ui.login;

import java.util.List;

import mobi.dlys.android.core.mvc.BaseActivity;
import mobi.dlys.android.core.utils.ActivityUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.api.PPNetManager;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.LoginObject;
import cn.changl.safe360.android.biz.vo.PushMsgObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.db.dao.PushMsgObjectDao;
import cn.changl.safe360.android.receiver.MyPushMessageReceiver;
import cn.changl.safe360.android.service.PushMsgHandleService;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.ui.main.MainActivity;
import cn.changl.safe360.android.utils.PreferencesUtils;
import cn.changl.safe360.android.utils.UpdateVersionUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.HostInfo;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.HostType;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class SplashActivity extends BaseActivity {
	private static int ENTER_MINI_TIME = 3 * 1000;
	private static int ENTER_TIMEOUT = 20 * 1000;
	private static int DEFAULT_TIME_OUT = 5 * 1000;
	private long time = 0L;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, SplashActivity.class);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		PPNetManager.getInstance().setConnectTimeOut(DEFAULT_TIME_OUT);
		PPNetManager.getInstance().setSoTimeOut(DEFAULT_TIME_OUT);
		// sendEmptyMessage(YSMSG.REQ_GET_SERVER_DYNAMIC_IP);
		initData();
	}

	private boolean isLoginOnNewVersion = false;

	private void initData() {
		App.getInstance().logout(null);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MyPushMessageReceiver.BindSuccess);
		intentFilter.addAction(MyPushMessageReceiver.BindFailed);
		registerReceiver(myBroadcastReceiver, intentFilter);

		PPNetManager.getInstance().resetTimeOut();

//		if (PreferencesUtils.isBaiduPushBindValid()) {
//			login();
//		} else {
//		}
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "jHyiWS2aV2VgcZBnfjqUrXEB");

	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {

		}
		return versionName;
	}

	private Runnable startRun = new Runnable() {
		public void run() {
			startMain();
		}
	};

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_GET_SERVER_DYNAMIC_IP: {
			if (msg.arg1 == 200 && msg.obj instanceof Safe360Pb) {
				Safe360Pb pb = (Safe360Pb) msg.obj;
				if (pb.getEnvironment() != null) {
					List<HostInfo> hostInfoList = pb.getEnvironment().getHostInfosList();
					if (hostInfoList != null && hostInfoList.size() > 0) {
						HostInfo hostInfo = null;
						for (int i = 0; i < hostInfoList.size(); i++) {
							hostInfo = hostInfoList.get(i);
							if (hostInfo != null) {
								if (hostInfo.getHostType() == HostType.API) {
									PPNetManager.getInstance().setIp(hostInfo.getDomain());
									PPNetManager.getInstance().setPort(hostInfo.getPort());
								} else if (hostInfo.getHostType() == HostType.RES && !TextUtils.isEmpty(hostInfo.getDomain())) {
									PPNetManager.getInstance().setUploadIpPort(hostInfo.getDomain() + ":" + hostInfo.getPort());
								}
							}
						}
					}
				}
			}

			initData();
		}
			break;
		case YSMSG.RESP_LOGIN: {
			mHandler.removeCallbacks(startRun);
			if (msg.arg1 == 200) {
				// 登录成功
				if (getIntent() != null) {
					String message = (String) getIntent().getStringExtra(PushMsgHandleService.MSG);
					String content = (String) getIntent().getStringExtra(PushMsgHandleService.CONTENT);
					if (!TextUtils.isEmpty(message)) {
						PushMsgObject msgObj = new PushMsgObject();
						msgObj.setMsg(message);
						msgObj.setContent(content);
						new PushMsgObjectDao().insert(msgObj);
					}
				}

				mHandler.removeCallbacks(startRun);
				long curtime = System.currentTimeMillis();
				long dfsdtime = curtime - time;
//				if (dfsdtime < ENTER_MINI_TIME) {
//					mHandler.postDelayed(new Runnable() {
//						public void run() {
//							startMain();
//
//						}
//					}, ENTER_MINI_TIME - dfsdtime);
//				} else {
//				}
				startMain();
			} else {
				// 登录失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
				LoginActivity.startActivity(SplashActivity.this);
				finish();
				UpdateVersionUtils.firstCheckVersion();
			}
		}
			break;
		}
	}

	private void startMain() {
		// if (isLoginOnNewVersion) {
		// Intent intent = new Intent(SplashActivity.this,
		// Guide2Activity.class);
		// intent.putExtra("LoginOnNewVersion", true);
		// startActivity(intent);
		// SplashActivity.this.finish();
		// UpdateVersionUtils.firstCheckVersion();
		// } else {
		// Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		// startActivity(intent);
		// SplashActivity.this.finish();
		// UpdateVersionUtils.firstCheckVersion();
		// }
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		SplashActivity.this.finish();
		UpdateVersionUtils.firstCheckVersion();
	}

	private void login() {
		String appFirst = "app_first_run_" + getAppVersionName(this.getApplicationContext());
		SharedPreferences userInfo = getSharedPreferences("application_user_info", 0);
		boolean isAppFirst = userInfo.getBoolean(appFirst, true);
		if (isAppFirst) {
			userInfo.edit().putBoolean(appFirst, false).commit();
			String phone = PreferencesUtils.getLoginPhone();
			String pwd = PreferencesUtils.getLoginPwd();
			if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
				isLoginOnNewVersion = true;
				LoginObject loginObject = new LoginObject();
				loginObject.setPhone(phone);
				loginObject.setPassword(pwd);
				loginObject.setLat(String.valueOf(App.getInstance().getLocater().getLat()));
				loginObject.setLng(String.valueOf(App.getInstance().getLocater().getLng()));
				loginObject.setLocation(App.getInstance().getLocater().getAddress());

				sendMessage(YSMSG.REQ_LOGIN, 0, 0, loginObject);
				time = System.currentTimeMillis();
			} else {
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
						startActivity(intent);
						finish();
						// UpdateVersionUtils.firstCheckVersion();
					}
				}, 2 * 1000);
			}
		} else {
			String phone = PreferencesUtils.getLoginPhone();
			String pwd = PreferencesUtils.getLoginPwd();
			if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
				// 自动登录
				LoginObject loginObject = new LoginObject();
				loginObject.setPhone(phone);
				loginObject.setPassword(pwd);
				loginObject.setLat(String.valueOf(App.getInstance().getLocater().getLat()));
				loginObject.setLng(String.valueOf(App.getInstance().getLocater().getLng()));
				loginObject.setLocation(App.getInstance().getLocater().getAddress());

				sendMessage(YSMSG.REQ_LOGIN, 0, 0, loginObject);
				time = System.currentTimeMillis();

			} else {
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						GuideActivity.startActivity(SplashActivity.this);
						finish();
						UpdateVersionUtils.firstCheckVersion();
					}
				}, 2 * 1000);
			}
		}
	}

	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MyPushMessageReceiver.BindSuccess)) {
				login();
			} else if (intent.getAction().equals(MyPushMessageReceiver.BindFailed)) {
				YSToast.showToast(mActivity, R.string.baidu_push_bind_failed);
			}
		}
	};

	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(myBroadcastReceiver);
	}
}
