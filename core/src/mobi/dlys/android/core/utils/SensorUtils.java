/*
 * 文件名称 : SensorUtil.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2013-10-12, 下午12:22:07
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.utils;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * 手机用到的传感器工具
 * 
 * @author
 * 
 */
public class SensorUtils {

	/**
	 * 让手机震动
	 * 
	 * @param activity
	 * @param milliseconds
	 */
	public static void Vibrate(final Activity activity, long milliseconds) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

}
