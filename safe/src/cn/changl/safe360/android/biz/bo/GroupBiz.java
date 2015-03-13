package cn.changl.safe360.android.biz.bo;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import android.os.Message;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.api.ApiClient;
import cn.changl.safe360.android.api.GroupApiClient;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ContactsObject;
import cn.changl.safe360.android.biz.vo.LocalUserObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.contacts.ContactDataBase;
import cn.changl.safe360.android.db.dao.LocalUserObjectDao;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class GroupBiz extends BaseBiz {
	public static void getGroupInfo(final int userId, final String token, final int pageNo, final int pageSize) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = GroupApiClient.getGroupInfo(userId, token, pageNo, pageSize);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_GROUP_INFO;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						List<UserInfo> newList = new ArrayList<UserInfo>();
						List<UserInfo> list = pb.getUserInfosList();
						LocalUserObjectDao localUserDao = new LocalUserObjectDao();
						ContactDataBase cb = new ContactDataBase(App.getInstance());
						for (UserInfo info : list) {
							cb.updateFriendRow(info.getPhone(), info.getUserId());
						}
						if (list != null && list.size() > 0) {
							newList.addAll(list);
							if (localUserDao.count() > 0) {
								for (UserInfo userInfo : localUserDao.getLocalUserInfoList()) {
									newList.add(userInfo);
								}
							}
						}
						message.obj = newList;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void inviteFriend(final int userId, final String token, final int friendId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = GroupApiClient.inviteFriend(userId, token, friendId);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_INVITE_FRIEND;
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

	public static void inviteTempFriend(final int userId, final String token, final String phone) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = GroupApiClient.inviteTempFriend(userId, token, phone);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_INVITE_TEMP_FRIEND;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						ContactsObject newContacts = new ContactsObject();
						newContacts.setPhone(phone);
						if (pb.getUserInfo() != null) {
							newContacts.setUrl(pb.getUserInfo().getUrl());
						}
						message.arg1 = 200;
						message.obj = newContacts;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void getTempFriendLocation(final int userId, final String token, final String phone) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = GroupApiClient.getTempUserLocation(userId, token, phone);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_TEMP_FRIEND_LOCATION;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						UserInfo userInfo = (UserInfo) pb.getUserInfo();
						LocalUserObjectDao localUserDao = new LocalUserObjectDao();
						LocalUserObject userObj = localUserDao.getLocalUserByPhone(phone);
						if (userInfo != null && userObj != null) {
							userObj.setCreateTime(System.currentTimeMillis());
							userObj.setLat(userInfo.getLocation().getLat());
							userObj.setLng(userInfo.getLocation().getLng());
							localUserDao.update(userObj);
						}
						message.arg1 = 200;
						message.obj = phone;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void removeFriend(final int userId, final String token, final int friendId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = GroupApiClient.removeFriend(userId, token, friendId);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_REMOVE_FRIEND;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = pb;
						UserInfo info = CoreModel.getInstance().getFriendByUserid(friendId);
						ContactDataBase cb = new ContactDataBase(App.getInstance());
						cb.updateRegisterRow(info.getPhone(), info.getUserId());
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void joinGroup(final int userId, final String token, final int friendId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = GroupApiClient.joinGroup(userId, token, friendId);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_JOIN_GROUP;
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

	public static void exitGroup(final int userId, final String token) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = GroupApiClient.exitGroup(userId, token);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_EXIT_GROUP;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = pb;
						ContactDataBase cb = new ContactDataBase(App.getInstance());
						for (UserInfo info : CoreModel.getInstance().getFriendList()) {
							cb.updateRegisterRow(info.getPhone(), info.getUserId());
						}
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void getIdleFriend(final int userId, final String token) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = GroupApiClient.getidleFri(userId, token);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_IDLE_FRIEND;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = pb.getUserInfosList();
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.REQ_GET_GROUP_INFO: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (null != userInfo) {
				getGroupInfo(userInfo.getUserId(), userInfo.getToken(), 1, 20);
			}
		}
			break;
		case YSMSG.REQ_INVITE_FRIEND: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (null != userInfo && 0 != msg.arg2) {
				inviteFriend(userInfo.getUserId(), userInfo.getToken(), msg.arg2);
			}
		}
			break;
		case YSMSG.REQ_REMOVE_FRIEND: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (null != userInfo && 0 != msg.arg2) {
				removeFriend(userInfo.getUserId(), userInfo.getToken(), msg.arg2);
			}
		}
			break;
		case YSMSG.REQ_JOIN_GROUP: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (null != userInfo && 0 != msg.arg2) {
				joinGroup(userInfo.getUserId(), userInfo.getToken(), msg.arg2);
			}
		}
			break;
		case YSMSG.REQ_EXIT_GROUP: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (null != userInfo) {
				exitGroup(userInfo.getUserId(), userInfo.getToken());
			}
		}
			break;
		case YSMSG.REQ_INVITE_TEMP_FRIEND: {
			String phone = (String) msg.obj;
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (null != userInfo) {
				inviteTempFriend(userInfo.getUserId(), userInfo.getToken(), phone);
			}
		}
			break;
		case YSMSG.REQ_GET_TEMP_FRIEND_LOCATION: {
			String phone = (String) msg.obj;
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (null != userInfo) {
				getTempFriendLocation(userInfo.getUserId(), userInfo.getToken(), phone);
			}
		}
			break;
		case YSMSG.REQ_GET_IDLE_FRIEND: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (null != userInfo) {
				getIdleFriend(userInfo.getUserId(), userInfo.getToken());
			}
		}
			break;

		}
	}

}
