package mobi.dlys.android.familysafer;

import java.util.ArrayList;

import mobi.dlys.android.core.image.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import mobi.dlys.android.core.image.universalimageloader.core.ImageLoader;
import mobi.dlys.android.core.image.universalimageloader.core.ImageLoaderConfiguration;
import mobi.dlys.android.core.image.universalimageloader.core.assist.QueueProcessingType;
import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLocListener;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.player.service.PlayerInterface;
import mobi.dlys.android.familysafer.player.service.PlayerService;
import mobi.dlys.android.familysafer.receiver.TimeTickReceiver;
import mobi.dlys.android.familysafer.service.ReadContactsService;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.Display;
import android.view.WindowManager;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class App extends Application implements BaiduLocListener {
	private static final String TAG = App.class.getSimpleName();

	public static boolean DEBUG = false;

	// 定位
	private BaiduLoc mBaiduLoc = null;

	// 播放服务
	public PlayerService mPlayerService = null;
	public PlayerInterface mPlayerInterface = null;

	private ArrayList<Activity> mActivityList;

	private TimeTickReceiver mTimeTickReceiver;

	private Activity mForegroundActivity;

	private static App mInstance;

	public static final String readingStatus = "com.dlys.android.familysafer.contacts.reading.status";
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (getCurProcessName(App.this).equals("mobi.dlys.android.familysafer")) {
					LogUtils.i("constantssize", "handler start service");
					App.this.startService(new Intent(App.this, ReadContactsService.class));
				}
				break;
			default:
				break;
			}
		}
	};

	public static App getInstance() {
		return mInstance;
	}

	/**
	 * 创建
	 */
	public void onCreate() {
		if (getCurProcessName(App.this).equals("mobi.dlys.android.familysafer")) {
			mInstance = this;
			init();
		}
	};

	/**
	 * 初始化
	 */
	private void init() {
		AndroidConfig.init(getApplicationContext());
		CoreModel.getInstance();

		if (DEBUG) {
			WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			LogUtils.e(TAG, "手机分辨率为: " + display.getWidth() + "x" + display.getHeight());
		}

		// 初始化通讯录数据库
		LogUtils.i("constantssize", "start service");
		App.this.startService(new Intent(App.this, ReadContactsService.class));

		getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mObserver);
		getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mCallLogObserver);
		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, new SMSContentObserver(this, new Handler()));

		// 创建App目录
		FileUtils.createDirectory(FileUtils.APP);
		FileUtils.createDirectory(FileUtils.COVER);
		FileUtils.createDirectory(FileUtils.VOICE);

		// 音乐播放器服务
		bindService(new Intent(App.getInstance(), PlayerService.class), mServiceConnection, BIND_AUTO_CREATE);

		// 定位
		mBaiduLoc = new BaiduLoc(getApplicationContext());
		mBaiduLoc.addBaiduLocListener(this);

		// 初始化ImageLoader
		initImageLoader(getApplicationContext());

		// 注册一分钟一次系统广播
//		IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
//		mTimeTickReceiver = new TimeTickReceiver();
//		getApplicationContext().registerReceiver(mTimeTickReceiver, filter);

		mActivityList = new ArrayList<Activity>();
		PreferencesUtils.setEnterChatActivity(false);
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 1)
				.denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public void setForegroundActivity(Activity activity) {
		mForegroundActivity = activity;
	}

	public Activity getForegroundActivity() {
		return mForegroundActivity;
	}

	/**
	 * 服务连接
	 */
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {

		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			App app = App.getInstance();
			app.mPlayerInterface = PlayerInterface.Stub.asInterface(service);
		}
	};

	public void setPlayerInterface(PlayerInterface playerInterface) {
		this.mPlayerInterface = playerInterface;
	}

	public PlayerInterface getPlayerInterface() {
		return mPlayerInterface;
	}

	/**
	 * 完全退出App
	 */
	public void exitApp() {
		clearActivity();
		android.os.Process.killProcess(android.os.Process.myPid());
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

	public Message obtainMessage() {
		return handler.obtainMessage();
	}

	public BaiduLoc getLocater() {
		return mBaiduLoc;
	}

	@Override
	public void onLocationChanged(boolean result, ReverseGeoCodeResult geoResult) {
		UserObject user = CoreModel.getInstance().getUserInfo();
		if (result && mBaiduLoc != null && user != null) {
			if (CoreModel.getInstance().getUserInfo() != null) {
				user.setLocation(mBaiduLoc.getAddress());
				user.setLng(mBaiduLoc.getLng());
				user.setLat(mBaiduLoc.getLat());

				if (!CoreModel.getInstance().isFirstModifyLocation()) {
					CoreModel.getInstance().setFirstModifyLocation(true);
					BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_MODIFY_LOCATION);
				}
			}
		}
	}

	public ContentObserver mObserver = new ContentObserver(new Handler()) {
		public void onChange(boolean selfChange) {
			if (getCurProcessName(App.this).equals("mobi.dlys.android.familysafer")) {
				LogUtils.i("constantssize", "change");
				handler.removeMessages(0);
				handler.sendEmptyMessageDelayed(0, 5 * 1000);
			}
		}
	};

	private ContentObserver mCallLogObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			if (getCurProcessName(App.this).equals("mobi.dlys.android.familysafer")) {
				LogUtils.i("constantssize", "mCallLog");
				handler.removeMessages(0);
			}
		}
	};

	public class SMSContentObserver extends ContentObserver {
		private Context mContext;
		String[] projection = new String[] { "address", "body", "date", "type", "read" };

		public SMSContentObserver(Context context, Handler handler) {
			super(handler);
			mContext = context;
		}

		@Override
		public void onChange(boolean selfChange) {
			if (getCurProcessName(App.this).equals("mobi.dlys.android.familysafer")) {
				handler.removeMessages(0);
			}
		}
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
