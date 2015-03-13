package cn.changl.safe360.android.receiver;

import mobi.dlys.android.core.utils.LogUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.e(TAG, "网络状态改变");

		boolean success = false;

		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// State state = connManager.getActiveNetworkInfo().getState();

		// 获取WIFI网络连接状态
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		// 判断是否正在使用WIFI网络
		if (State.CONNECTED == state) {
			success = true;
		}

		// 获取GPRS网络连接状态
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		// 判断是否正在使用GPRS网络
		if (State.CONNECTED != state) {
			success = true;
		}

		if (!success) {
			// Toast.makeText(context,
			// context.getString(R.string.network_error),
			// Toast.LENGTH_SHORT).show();
		} else {
			autoLogin();
		}
	}

	private void autoLogin() {
		// if (!CoreModel.getInstance().isLogined()) {
		// String phone = PreferencesUtils.getLoginPhone();
		// String pwd = PreferencesUtils.getLoginPwd();
		// if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
		// LoginObject loginObject = new LoginObject();
		// loginObject.setPhone(phone);
		// loginObject.setPassword(pwd);
		// CoreModel.getInstance().setLoginObject(loginObject);
		//
		// LogUtils.e(TAG, "网络状态改变, 开始自动登录");
		// BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_AUTO_LOGIN);
		// }
		// }
	}
}
