package cn.changl.safe360.android.ui.comm;

import mobi.dlys.android.core.utils.DipPixelUtils;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.changl.safe360.android.R;

/**
 * 带图片的toast
 * 
 * 注意：2s内相同内容的toast会被过滤掉
 * 
 */
public class YSToast {
	private static final long DROP_DUPLICATE_TOAST_TS = 2 * 1000; // 2s

	private static String sLast = "";

	private static long sLastTs = 0;

	private static Toast mBasicToast = null;

	public static void showToast(Context context, int resId) {
		String str = context.getString(resId);
		showToast(context, str);
	}

	/**
	 * toast（带动画）-显示时间2S
	 * 
	 * @param context
	 * @param type
	 * @param str
	 */
	public static void showToast(Context context, String str) {
		long newTs = System.currentTimeMillis();
		if (str != null && (!str.equals(sLast) || newTs < sLastTs || (newTs - sLastTs) > DROP_DUPLICATE_TOAST_TS)) {
			sLast = str;
			sLastTs = newTs;
			if (mBasicToast == null) {
				mBasicToast = new Toast(context);
			}
			View toastView = LayoutInflater.from(context).inflate(R.layout.comm_toast_view, null);
			TextView txt = (TextView) toastView.findViewById(R.id.xl_toast_txt);
			txt.setText(str);
			mBasicToast.setView(toastView);
			int px = DipPixelUtils.dip2px(context, 60);
			mBasicToast.setGravity(Gravity.BOTTOM, 0, px);
			mBasicToast.setDuration(Toast.LENGTH_SHORT);// 默认只显示2S
			mBasicToast.show();
		}
	}

	/**
	 * toast（带动画）-显示时间2S
	 * 
	 * @param context
	 * @param type
	 * @param str
	 * @param maxLines
	 *            -最大字行数
	 */
	public static void showToast(Context context, String str, int maxLines) {
		long newTs = System.currentTimeMillis();
		if (str != null && (!str.equals(sLast) || newTs < sLastTs || (newTs - sLastTs) > DROP_DUPLICATE_TOAST_TS)) {
			sLast = str;
			sLastTs = newTs;
			if (mBasicToast == null) {
				mBasicToast = new Toast(context);
			}
			View toastView = LayoutInflater.from(context).inflate(R.layout.comm_toast_view, null);
			TextView txt = (TextView) toastView.findViewById(R.id.xl_toast_txt);
			txt.setText(str);

			if (maxLines > 0) {
				txt.setMaxLines(maxLines);
			}

			mBasicToast.setView(toastView);
			int px = DipPixelUtils.dip2px(context, 60);
			mBasicToast.setGravity(Gravity.BOTTOM, 0, px);
			mBasicToast.setDuration(Toast.LENGTH_SHORT);// 默认只显示2S
			mBasicToast.show();
		}
	}

	/**
	 * toast（带动画）-显示时间3.5S
	 * 
	 * @param context
	 * @param type
	 * @param str
	 */
	public static void showLongToast(Context context, String str) {
		long newTs = System.currentTimeMillis();
		if (str != null && (!str.equals(sLast) || newTs < sLastTs || (newTs - sLastTs) > DROP_DUPLICATE_TOAST_TS)) {
			sLast = str;
			sLastTs = newTs;
			if (mBasicToast == null) {
				mBasicToast = new Toast(context);
			}
			View toastView = LayoutInflater.from(context).inflate(R.layout.comm_toast_view, null);
			TextView txt = (TextView) toastView.findViewById(R.id.xl_toast_txt);
			txt.setText(str);

			mBasicToast.setView(toastView);
			int px = DipPixelUtils.dip2px(context, 60);
			mBasicToast.setGravity(Gravity.BOTTOM, 0, px);
			mBasicToast.setDuration(Toast.LENGTH_LONG);
			mBasicToast.show();
		}
	}

}
