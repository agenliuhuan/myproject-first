package cn.changl.safe360.android.biz.bo;

import java.util.ArrayList;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import mobi.dlys.android.core.utils.AndroidConfig;
import android.os.Message;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.api.ApiClient;
import cn.changl.safe360.android.api.SysApiClient;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.PushMessageObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UpdateObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;

public class SysBiz extends BaseBiz {

	public static void checkVersion(final int userId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				// read version code
				int versionCode = AndroidConfig.getVersionCode();

				// get imei
				String imei = AndroidConfig.getIMEI();

				Safe360Pb pb = SysApiClient.checkVersion(userId, imei, versionCode);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_CHECK_VERSION;
					Safe360Pb pb = (Safe360Pb) result;
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
				Safe360Pb pb = SysApiClient.refreshToken(userId, token, password);
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
					Safe360Pb pb = (Safe360Pb) result;
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

				Safe360Pb pb = SysApiClient.getServerDynamicIp(imei, versionCode);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_SERVER_DYNAMIC_IP;
					Safe360Pb pb = (Safe360Pb) result;
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

	public static void pushMessage(final ArrayList<Integer> useridList, final String title, final String desc, final String data) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = SysApiClient.pushMessage(useridList, title, desc, data);
				return pb;
			}

			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_PUSH_MSG;
					Safe360Pb pb = (Safe360Pb) result;
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
			int userId = -1;
			if (user != null) {
				userId = user.getUserId();
			}
			checkVersion(userId);
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
		case YSMSG.REQ_PUSH_MSG: {
			PushMessageObject pushObj = (PushMessageObject) msg.obj;
			if (pushObj != null) {
				pushMessage(pushObj.getUseridList(), pushObj.getTitle(), pushObj.getDescription(), pushObj.getData());
			}
		}
			break;
		}

	}

}
