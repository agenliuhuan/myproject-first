package mobi.dlys.android.familysafer.service;

import mobi.dlys.android.familysafer.receiver.HeadsetPlugReceiver;
import mobi.dlys.android.familysafer.receiver.MediaButtonReceiver;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;

public class HeadsetPlugService extends Service {
	private static final String TAG = "PlayMusicService";
	private final HeadsetPlugReceiver receiver = new HeadsetPlugReceiver();
	private AudioManager mAudioManager;
	private ComponentName mbCN;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		registerMediaButtonReceiver();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		unregisterMediaButtonReceiver();
		super.onDestroy();
	}

	private void registerMediaButtonReceiver() {
		// 获得AudioManager对象
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// 构造一个ComponentName，指向MediaoButtonReceiver类
		// 下面为了叙述方便，我直接使用ComponentName类来替代MediaoButtonReceiver类
		mbCN = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
		// 注册一个MedioButtonReceiver广播监听
		mAudioManager.registerMediaButtonEventReceiver(mbCN);
	}

	private void unregisterMediaButtonReceiver() {
		// 取消注册的方法
		mAudioManager.unregisterMediaButtonEventReceiver(mbCN);
	}
}
