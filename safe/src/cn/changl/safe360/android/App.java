package cn.changl.safe360.android;

import java.util.ArrayList;

import mobi.dlys.android.core.image.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import mobi.dlys.android.core.image.universalimageloader.core.ImageLoader;
import mobi.dlys.android.core.image.universalimageloader.core.ImageLoaderConfiguration;
import mobi.dlys.android.core.image.universalimageloader.core.assist.QueueProcessingType;
import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.core.utils.HandlerUtils.StaticHandler;
import mobi.dlys.android.core.utils.LogUtils;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.Display;
import android.view.WindowManager;
import cn.changl.safe360.android.api.UserApiClient;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.contacts.ContactsSyncService;
import cn.changl.safe360.android.im.hx.controller.Safe360HXSDKHelper;
import cn.changl.safe360.android.map.BaiduLoc;
import cn.changl.safe360.android.map.BaiduLoc.MapScanType;
import cn.changl.safe360.android.map.BaiduLocListener;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.utils.FileUtils;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easemob.EMCallBack;

public class App extends FrontiaApplication implements BaiduLocListener {
	private static final String TAG = App.class.getSimpleName();

	public static Context applicationContext;

	private static App mInstance;
	private BaiduLoc mBaiduLoc = null;
	public static final String readingStatus = "com.dlys.android.familysafer.contacts.reading.status";
	private ArrayList<Activity> mActivityList = new ArrayList<Activity>();
	private Activity mForegroundActivity;
	private StaticHandler mHandler = new StaticHandler();

	public boolean mInitMainActivity = false;

	public static Safe360HXSDKHelper hxSDKHelper = new Safe360HXSDKHelper();

	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";

	public static App getInstance() {
		return mInstance;
	}

	/**
	 * 创建
	 */
	public void onCreate() {
		applicationContext = getApplicationContext();
		FrontiaApplication.initFrontiaApplication(applicationContext);
		if (getCurProcessName(App.this).equals("cn.changl.safe360.android")) {
			super.onCreate();
			mInstance = this;
			init();
		}
	};

	/**
	 * 初始化
	 */
	private void init() {
		AndroidConfig.init(applicationContext);
		CoreModel.getInstance();

		hxSDKHelper.onInit(applicationContext);

		if (true) {
			WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			LogUtils.e(TAG, "手机分辨率为: " + display.getWidth() + "x" + display.getHeight());
		}

		// 创建App目录
		FileUtils.createDirectory(FileUtils.APP);
		FileUtils.createDirectory(FileUtils.COVER);
		FileUtils.createDirectory(FileUtils.VOICE);

		// 初始化ImageLoader
		initImageLoader(applicationContext);

		// 初始化地图
		mBaiduLoc = new BaiduLoc(applicationContext);
		mBaiduLoc.addBaiduLocListener(this);
		
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 1)
				.denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public void addBaiduLocListener(BaiduLocListener baiduLocListener) {
		if (baiduLocListener != null && mBaiduLoc != null) {
			mBaiduLoc.addBaiduLocListener(baiduLocListener);
		}
	}

	public void setMapScanForegroundMode() {
		if (mBaiduLoc != null) {
			LogUtils.d(TAG, "app run on foreground");
			mBaiduLoc.changeMapScanType(MapScanType.ET_FOREGROUND);
		}
	}

	public void setMapScanBackgroundMode() {
		if (mBaiduLoc != null) {
			LogUtils.d(TAG, "app run on background");
			mBaiduLoc.changeMapScanType(MapScanType.ET_BACKGROUND);
		}
	}

	public void setMapScanTripMode() {
		if (mBaiduLoc != null) {
			LogUtils.d(TAG, "app run on trip mode");
			mBaiduLoc.changeMapScanType(MapScanType.ET_TRIP);
		}
	}

	/**
	 * 完全退出App
	 */
	public void exitApp() {
		logout(new EMCallBack() {

			@Override
			public void onSuccess() {
				exit();
			}

			@Override
			public void onProgress(int arg0, String arg1) {

			}

			@Override
			public void onError(int arg0, String arg1) {
				exit();
			}
		});

	}

	private void exit() {
		UserObject userInfo = CoreModel.getInstance().getUserInfo();
		if (userInfo != null) {
			UserApiClient.logout(userInfo.getUserId(), userInfo.getToken());
			App.getInstance().logout(null);
		}
		clearActivity();
		App.this.stopService(new Intent(App.this, ContactsSyncService.class));
		android.os.Process.killProcess(android.os.Process.myPid());
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

	public BaiduLoc getLocater() {
		return mBaiduLoc;
	}

	public void onLocationChanged(boolean result, ReverseGeoCodeResult geoResult) {

		if (result && mBaiduLoc != null) {
			if (CoreModel.getInstance().getUserInfo() != null) {
				UserObject user = CoreModel.getInstance().getUserInfo();
				user.setLocation(mBaiduLoc.getAddress());
				user.setLng(mBaiduLoc.getLng());
				user.setLat(mBaiduLoc.getLat());
				BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_MODIFY_LOCATION);
				// if (!CoreModel.getInstance().isFirstModifyLocation()) {
				//
				// CoreModel.getInstance().setFirstModifyLocation(true);
				//
				// }
			}
		}
	}

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

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 * 
	 * @param user
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 * 
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(emCallBack);
	}
}
