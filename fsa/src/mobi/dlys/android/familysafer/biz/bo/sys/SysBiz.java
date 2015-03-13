package mobi.dlys.android.familysafer.biz.bo.sys;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.api.ApiClient;
import mobi.dlys.android.familysafer.api.SysApiClient;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UpdateObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import android.os.Message;

public class SysBiz extends BaseBiz {

	public static void checkVersion(final int userId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				// read version code
				int versionCode = AndroidConfig.getVersionCode();

				// get imei
				String imei = AndroidConfig.getIMEI();

				FamilySaferPb pb = SysApiClient.checkVersion(userId, imei, versionCode);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_CHECK_VERSION;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = UpdateObject.createFromPb(pb.getClientVersion());
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void refreshToken(final int userId, final String token, final String password) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = SysApiClient.refreshToken(userId, token, password);
				if (ApiClient.isOK(pb) && pb.getUserInfo() != null && CoreModel.getInstance().getUserInfo() != null) {
					CoreModel.getInstance().getUserInfo().setToken(pb.getUserInfo().getToken());
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_REFRESH_TOKEN;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
					}
					// CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void getServerDynamicIp() {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				// read version code
				int versionCode = AndroidConfig.getVersionCode();

				// get imei
				String imei = AndroidConfig.getIMEI();

				FamilySaferPb pb = SysApiClient.getServerDynamicIp(imei, versionCode);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_SERVER_DYNAMIC_IP;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = pb;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.REQ_CHECK_VERSION: {
			UserObject user = CoreModel.getInstance().getUserInfo();
			if (user != null) {
				checkVersion(user.getUserId());
			}
		}
			break;
		case YSMSG.REQ_REFRESH_TOKEN: {
			UserObject user = CoreModel.getInstance().getUserInfo();
			if (user != null) {
				refreshToken(user.getUserId(), user.getToken(), user.getPassword());
			}
		}
			break;
		case YSMSG.REQ_GET_SERVER_DYNAMIC_IP: {
			getServerDynamicIp();
		}
			break;
		}
	}

}
