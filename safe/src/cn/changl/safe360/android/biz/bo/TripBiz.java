package cn.changl.safe360.android.biz.bo;

import java.util.List;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import android.os.Message;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.api.ApiClient;
import cn.changl.safe360.android.api.TripApiClient;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.TripObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.utils.PreferencesUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Trip;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class TripBiz extends BaseBiz {

	public static void getTripInfo(final int userId, final String token, final int tripId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = TripApiClient.getTripInfo(userId, token, tripId);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_TRIP_INFO;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = pb.getTrip();
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void StartTrip(final int userId, final String token, final String lng, final String lat, final String location, final List<UserInfo> userList) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = TripApiClient.startTrip(userId, token, lng, lat, location, userList);
				if (ApiClient.isOK(pb)) {
					Trip trip = pb.getTrip();
					if (trip != null) {
						PreferencesUtils.setTripId(trip.getTripId());
					}

					App.getInstance().setMapScanTripMode();
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_START_TRIP;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = pb.getTrip();
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void FinishTrip(final int userId, final String token, final int tripId, final String lng, final String lat, final String location) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = TripApiClient.finishTrip(userId, token, tripId, lng, lat, location);
				if (ApiClient.isOK(pb)) {
					PreferencesUtils.resetTripId();
					App.getInstance().setMapScanForegroundMode();
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_FINISH_TRIP;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = result;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void ExitTrip(final int userId, final String token, final int tripId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = TripApiClient.exitTrip(userId, token, tripId);
				if (ApiClient.isOK(pb)) {
					PreferencesUtils.resetTripId();
					App.getInstance().setMapScanForegroundMode();
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_EXIT_TRIP;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						App.getInstance().setMapScanForegroundMode();
						message.arg1 = 200;
						message.obj = result;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void modifyTrip(final int userId, final String token, final int tripId, final String lat, final String lng, final String address) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = TripApiClient.modifyTrip(userId, token, tripId, lat, lng, address);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_MODIFY_TRIP;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = result;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.REQ_GET_TRIP_INFO: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (null != userInfo && 0 != msg.arg2) {
				getTripInfo(userInfo.getUserId(), userInfo.getToken(), msg.arg2);
			}
		}
			break;
		case YSMSG.REQ_START_TRIP: {
			UserObject userObj = CoreModel.getInstance().getUserInfo();
			TripObject tripObj = (TripObject) msg.obj;
			if (null != userObj && null != tripObj) {
				StartTrip(userObj.getUserId(), userObj.getToken(), tripObj.getBeginLng(), tripObj.getBeginLat(), tripObj.getBeginAdress(),
						tripObj.getUserinfos());
			}
		}
			break;
		case YSMSG.REQ_FINISH_TRIP: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			TripObject tripObj = (TripObject) msg.obj;
			if (null != userInfo && null != tripObj) {
				FinishTrip(userInfo.getUserId(), userInfo.getToken(), tripObj.getTripId(), tripObj.getEndLng(), tripObj.getEndLat(), tripObj.getEndAdress());
			}
		}
			break;
		case YSMSG.REQ_EXIT_TRIP: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (null != userInfo && 0 != msg.arg2) {
				ExitTrip(userInfo.getUserId(), userInfo.getToken(), msg.arg2);
			}
		}
			break;
		case YSMSG.REQ_MODIFY_TRIP: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			TripObject tripObj = (TripObject) msg.obj;
			if (null != userInfo && null != tripObj) {
				modifyTrip(userInfo.getUserId(), userInfo.getToken(), tripObj.getTripId(), tripObj.getEndLat(), tripObj.getEndLng(), tripObj.getEndAdress());
			}
		}
			break;
		}
	}

}
