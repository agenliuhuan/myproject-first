package cn.changl.safe360.android.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.WindowManager;

public class VibratorManager {
	/**
	 * 开始震动
	 */
	public static void startVibrator(Context context, boolean keep) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		if (keep) {
			vibrator.vibrate(new long[] { 100, 1000 }, 0);
		} else {
			vibrator.vibrate(new long[] { 100, 1000 }, -1);
		}
	}

	public static void startVibrator(Context context) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(50);
	}

	/**
	 * 停止震动
	 */
	public static void stopVibrator(Context context) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.cancel();
	}

	public static void wakeUpAndUnlock(Context context) {
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
		kl.disableKeyguard();
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
		wl.acquire();
		wl.release();

	}

	public static void wakeDownAndUnlock(Context context) {
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
		kl.reenableKeyguard();
	}

	public static String getSystemProperty(String propName) {
		String line;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {

			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {

				}
			}
		}
		return line;
	}

	public static void setActwackUpAndUnlock(Activity context) {
		context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	}

}
