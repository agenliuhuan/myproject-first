package mobi.dlys.android.familysafer.utils;

import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.player.utils.Player;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * 音量控制,主要是淡入淡出
 */
public class VolumeUtils {
	private static final String TAG = "VolumeUtil";
	private static Handler handler = new Handler(Looper.getMainLooper());
	private static final int maxTime = 5000; // 音量递增或递减的时间间隔,单位ms
	private static int maxVolume = 0;

	/**
	 * 是否启用淡入淡出
	 * 
	 * @param increment
	 */
	public static boolean increment = true;

	/**
	 * 音量递增
	 */
	public static void increment() {
		if (!increment) {
			return;
		}
		increment = false;

		final AudioManager audioManager = (AudioManager) App.getInstance().getSystemService(Service.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		LogUtils.d(TAG, "maxVolume=" + maxVolume);

		if (0 != maxVolume) {
			Runnable runnable = new Runnable() {
				private int volume = 0;

				@Override
				public void run() {
					// adjustStreamVolume: 调整指定声音类型的音量
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ++volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);// 调高声音
					LogUtils.d(TAG, "volume=" + volume);
				}
			};

			int time = maxTime / maxVolume;
			for (int i = 0; i < maxVolume; i++) {
				handler.postDelayed(runnable, i * time);
			}
		}
	}

	/**
	 * 音量递减
	 */
	public static void decrease() {
		final AudioManager audioManager = (AudioManager) App.getInstance().getSystemService(Service.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		LogUtils.d(TAG, "maxVolume=" + maxVolume);

		if (0 != maxVolume) {
			Runnable runnable = new Runnable() {
				private int volume = maxVolume;

				@Override
				public void run() {
					// 第一个参数：声音类型
					// 第二个参数：调整音量的方向
					// 第三个参数：可选的标志位
					// audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					// AudioManager.ADJUST_LOWER,
					// AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);// 调低声音
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, --volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);// 调低声音
					LogUtils.d(TAG, "volume=" + volume);
					if (0 == volume) {
						Player.pause();
					}
				}
			};

			int time = maxTime / maxVolume;
			for (int i = 0; i < maxVolume; i++) {
				handler.postDelayed(runnable, i * time);
			}
		} else {
			Player.pause();
		}
	}

	/**
	 * 音乐拦截
	 * 
	 * @param context
	 * @param handler
	 */
	public static void onAudioFocusChangeListener(final Context context, Handler handler) {
		// 20121010 Ale 来电或播放音乐时暂停
		handler.postDelayed(new Runnable() {

			@TargetApi(8)
			@Override
			public void run() {
				TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				telephonyManager.listen(new PhoneStateListener() {
					@Override
					public void onCallStateChanged(int state, String incomingNumber) {
						super.onCallStateChanged(state, incomingNumber);
						// LogUtils.e("AudioManager", "CallStateChange=" +
						// state);
						switch (state) {
						case TelephonyManager.CALL_STATE_IDLE:
							if (PreferencesUtils.isAudioFocusChange()) {
								PreferencesUtils.setAudioFocusChange(false);

								if (!Player.isPlaying()) {
									VolumeUtils.increment = true;
									Player.play();
								}
							}
							break;
						case TelephonyManager.CALL_STATE_RINGING:
							if (Player.isPlaying()) {
								PreferencesUtils.setAudioFocusChange(true);
								Player.pause();
							}
							break;
						default:
							break;
						}
					}
				}, PhoneStateListener.LISTEN_CALL_STATE);
				try {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
						AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
						// 设置音乐和铃声监听
						audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {

							@Override
							public void onAudioFocusChange(int focusChange) {
								// LogUtils.e("AudioManager",
								// "STREAM_MUSIC focusChange=" + focusChange);
								switch (focusChange) {
								case AudioManager.AUDIOFOCUS_GAIN:
									if (PreferencesUtils.isAudioFocusChange()) {
										PreferencesUtils.setAudioFocusChange(false);

										if (!Player.isPlaying()) {
											VolumeUtils.increment = true;
											Player.play();
										}
									}
									break;
								case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
								case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
								case AudioManager.AUDIOFOCUS_LOSS:
									if (Player.isPlaying()) {
										PreferencesUtils.setAudioFocusChange(true);
										Player.pause();
									}
									break;
								default:
									break;
								}
							}
						}, AudioManager.STREAM_RING | AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 500);
	}
}
