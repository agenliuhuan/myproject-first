package cn.changl.safe360.android.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Parcelable;
import android.view.View;

public class SystemUtils {
	public static int getSystemVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	public static boolean isMeetVersion(int version) {
		if (getSystemVersion() >= version) {
			return true;
		}
		return false;
	}

	public static void createShortCut(Activity activity, String name, int iconResId, Class<?> cls) {
		try {
			// 创建快捷方式的Intent
			Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			// 不允许重复创建
			shortcutintent.putExtra("duplicate", false);
			// 需要现实的名称
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
			// 快捷图片
			Parcelable icon = Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), iconResId);
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
			// 点击快捷图片，运行的程序主入口
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(activity.getApplicationContext(), cls));
			// 发送广播。OK
			activity.sendBroadcast(shortcutintent);
		} catch (Exception e) {
		}
	}

	public static void createShortCut(Activity activity, String name, int iconResId) {
		try {
			// 创建快捷方式的Intent
			Intent shortcutintent = new Intent();
			// 不允许重复创建
			shortcutintent.putExtra("duplicate", false);
			// 需要现实的名称
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

			String appClass = activity.getPackageName() + "." + activity.getLocalClassName();
			ComponentName comp = new ComponentName(activity.getPackageName(), appClass);
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
			// 下面的方法与上面的效果是一样的,另一种构建形式而已
			// Intent respondIntent = new Intent(this, this.getClass());
			// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, respondIntent);

			// 快捷方式的图标
			ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), iconResId);
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

			// 发送到消息队列
			activity.setResult(Activity.RESULT_OK, shortcutintent);
		} catch (Exception e) {
		}

	}

	public static void autoSetHardwareAccelerate(View view) {
		if (Build.VERSION.SDK_INT >= 11/* Build.VERSION_CODES.HONEYCOMB */
				&& view != null && !view.isHardwareAccelerated()) {
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	public static void cancelHardwareAccelerate(View view) {
		if (Build.VERSION.SDK_INT >= 11/* Build.VERSION_CODES.HONEYCOMB */
				&& view != null) {
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	public static String getAppVersion(Context context) {
		String version = "1.0.0";
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			version = packageInfo.versionName;
		} catch (NameNotFoundException ignored) {
		}
		return version;
	}
}