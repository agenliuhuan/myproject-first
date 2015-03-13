package cn.changl.safe360.android.sensor;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

/*
 * 重力感应
 */
public class SensorManage implements SensorEventListener {
	public static final String ACTION_NET_NOT_CONNECTION = "sensormanage.action.net.not.connection";
	public static final String ACTION_DO_ACTION = "sensormanage.action.do.action";

	// private SensorManager mSensorManager = null; // 传感器管理器
	// private Sensor mSensor = null; // 传感器
	// private float mfLastX = 0; // 加速方向 X
	// private float mfLastY = 0; // 加速方向 Y
	// private float mfLastZ = 0; // 加速方向 Z
	// private boolean mbReady = false; // 加速准备
	// private Context mContext = null; // 上下文
	private Vibrator mVibrator = null; // 震动

	// 速度阈值，当摇晃速度达到这值后产生作用
	private static final int SPEED_SHRESHOLD = 4000;
	// 两次检测的时间间隔
	private static final int UPTATE_INTERVAL_TIME = 70;
	// 传感器管理器
	private SensorManager sensorManager;
	// 传感器
	private Sensor sensor;
	// 上下文
	private Context mContext;
	// 手机上一个位置时重力感应坐标
	private float lastX;
	private float lastY;
	private float lastZ;
	// 上次检测时间
	private long lastUpdateTime;

	/*
	 * 构造
	 */
	public SensorManage(Context context) {
		mContext = context;

		// mSensorManager = (SensorManager)
		// mContext.getSystemService(Context.SENSOR_SERVICE);
		mVibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
		// mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// mSensorManager.registerListener(this, mSensor,
		// SensorManager.SENSOR_DELAY_NORMAL);

		// 获得传感器管理器
		sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			// 获得重力传感器
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		// 注册
		if (sensor != null) {
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		}
	}

	/*
	 * 销毁
	 */
	public void destroy() {
		// mSensorManager.unregisterListener(this);
		sensorManager.unregisterListener(this);
	}

	/*
	 * 传感器精度更改
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	private int count = 0;

	/*
	 * 传感器更改
	 */
	public void onSensorChanged(SensorEvent event) {
		if (mPause) {
			return;
		}

		// float fX, fY, fZ, fTotal = 0.0f;
		//
		// fX = event.values[0];
		// fY = event.values[1];
		// fZ = event.values[2];
		//
		// if ((mfLastX == 0.0f) && (mfLastY == 0.0f) && (mfLastZ == 0.0f)) {
		// mfLastX = fX;
		// mfLastY = fY;
		// mfLastZ = fZ;
		// }
		//
		// if ((mfLastX != fX) || (mfLastY != fY) || (mfLastZ != fZ)) {
		// fTotal = Math.abs(fX - mfLastX) + Math.abs(fY - mfLastY) +
		// Math.abs(fZ - mfLastZ);
		//
		// mfLastX = fX;
		// mfLastY = fY;
		// mfLastZ = fZ;
		//
		// }
		//
		// // 如果两次加速值都超过15, 认为是甩动
		// // if (fTotal > 15 && event.sensor.getType() ==
		// // Sensor.TYPE_ACCELEROMETER) {
		// // if (mbReady) {
		// // // 连续两次甩动后触发
		// // count++;
		// // if (count > 1) {
		// // count = 0;
		// // if (ActivityUtils.isAppInForeground(mContext)) {
		// // if (NetUtils.NET_NOT_CONNECTION == NetUtils.checkNet()) {
		// // if (null != mContext) {
		// // notNetWork();
		// // }
		// // mbReady = false;
		// // return;
		// // }
		// // doAction();
		// // vibrator();
		// // }
		// //
		// // mbReady = false;
		// // }
		// // } else {
		// // // count = 0;
		// // mbReady = true;
		// // }
		// // } else {
		// // count = 0;
		// // mbReady = false;
		// // }
		//
		// int sensorType = event.sensor.getType();
		// // values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		// float[] values = event.values;
		// if (sensorType == Sensor.TYPE_ACCELEROMETER) {
		// if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 ||
		// Math.abs(values[2]) > 17)) {
		// count++;
		// if (count == 2) {
		// count = 0;
		// doAction();
		// vibrator();
		// }
		// } else {
		// count = 0;
		// }
		//
		// }

		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次检测的时间间隔
		long timeInterval = currentUpdateTime - lastUpdateTime;
		// 判断是否达到了检测时间间隔
		if (timeInterval < UPTATE_INTERVAL_TIME)
			return;
		// 现在的时间变成last时间
		lastUpdateTime = currentUpdateTime;

		// 获得x,y,z坐标
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		// 获得x,y,z的变化值
		float deltaX = x - lastX;
		float deltaY = y - lastY;
		float deltaZ = z - lastZ;

		// 将现在的坐标变成last坐标
		lastX = x;
		lastY = y;
		lastZ = z;

		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
		// 达到速度阀值，发出提示
		if (speed >= SPEED_SHRESHOLD && ActivityUtils.isAppInForeground(mContext)) {
			doAction();
			vibrator();
		}
	}

	/*
	 * 广播没有网络状态
	 */
	private void notNetWork() {
		if (mContext == null)
			return;

		Intent intent = null;

		try {
			intent = new Intent();
			intent.setAction(ACTION_NET_NOT_CONNECTION);
			mContext.sendBroadcast(intent);
		} catch (Exception e) {
		}
	}

	private boolean mPause = false;

	/**
	 * 设置暂停摇动触发动作
	 * 
	 * @param pause
	 */
	public void setPause(boolean pause) {
		this.mPause = pause;
	}

	private void vibrator() {
		mVibrator.vibrate(500);
	}

	private void doAction() {
		if (mContext == null)
			return;

		Intent intent = null;

		try {
			intent = new Intent();
			intent.setAction(ACTION_DO_ACTION);
			mContext.sendBroadcast(intent);
		} catch (Exception e) {
		}
	}
}