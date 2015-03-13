package mobi.dlys.android.familysafer.biz.bo.location;

import java.util.List;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.api.ApiClient;
import mobi.dlys.android.familysafer.api.LocationApiClient;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;
import android.os.Message;

public class LocationBiz extends BaseBiz {

	public static void getUserPosition(final int userId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = LocationApiClient.getUserPosition(userId);
				if (ApiClient.isOK(pb)) {
					List<FriendObject> friendList = CoreModel.getInstance().getFriendList();
					UserInfo userInfo = pb.getUserInfo();
					if (userInfo != null && friendList.size() > 0) {
						for (FriendObject friend : friendList) {
							if (friend.getUserId() == userInfo.getUserId()) {
								friend.setLng(userInfo.getLng());
								friend.setLat(userInfo.getLat());
								friend.setLocation(userInfo.getLocation());
								break;
							}
						}
					}
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_USER_LOCATION;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = pb.getUserInfo();
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.REQ_GET_USER_LOCATION: {
			getUserPosition(msg.arg1);
		}
			break;
		case YSMSG.REQ_GET_NPC_LOCATION: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
			} else {

			}
		}
			break;
		}
	}
}
