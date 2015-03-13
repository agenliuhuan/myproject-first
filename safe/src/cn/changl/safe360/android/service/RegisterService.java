package cn.changl.safe360.android.service;

import mobi.dlys.android.core.utils.HandlerUtils.StaticHandler;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public class RegisterService extends Service {
	private final static String TAG = RegisterService.class.getSimpleName();

	public static final String RegisterStatus = "cn.changl.safe360.android.service.register.status";
	public static int mRegisterStatus = 0; // 0:未开始 1:正在注册 2:注册完成

	private StaticHandler mHandler = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

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
				register();
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
		mRegisterStatus = 0;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return flags;
	}

	public void register() {
		Intent i = new Intent(RegisterStatus);
		sendBroadcast(i);
	}

}
