package mobi.dlys.android.core.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * 剪切板实用类
 * 
 * @author rocksen
 * 
 */
public class ClipboardUtils {

	/**
	 * 复制到剪贴板
	 * 
	 * @param content
	 * @param context
	 */
	public static void copy(String content, Context context) {
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setPrimaryClip(ClipData.newPlainText("", content.trim()));
	}

	/**
	 * 得到剪贴板内容
	 * 
	 * @param context
	 * @return
	 */
	public static String paste(Context context) {
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getPrimaryClip().toString().trim();
	}
}
