package mobi.dlys.android.familysafer.biz.bo.friend;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.api.ApiClient;
import mobi.dlys.android.familysafer.api.FriendApiClient;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.FriendRequestObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.db.dao.FriendObjectDao;
import mobi.dlys.android.familysafer.db.dao.FriendRequestObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Friend;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.FriendRequest;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import android.os.Message;
import android.text.TextUtils;

public class FriendBiz extends BaseBiz {

	public static void addFriend(final int userId, final String token, final int friendId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = FriendApiClient.addFriend(userId, token, friendId);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_ADD_FRIEND;
					FamilySaferPb pb = (FamilySaferPb) result;
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

	public static void friendReqList(final int userId, final String token, final int pageNo, final int pageSize) {
		final ArrayList<FriendRequestObject> list = new ArrayList<FriendRequestObject>();
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = FriendApiClient.friendReqList(userId, token, pageNo, pageSize);
				if (ApiClient.isOK(pb)) {
					FriendRequestObjectDao friendRequestDao = new FriendRequestObjectDao();
					if (pageNo == 1) {
						friendRequestDao.clear();
					}

					for (FriendRequest friend : pb.getFriendRequestsList()) {
						list.add(FriendRequestObject.createFromPb(friend));
					}
					friendRequestDao.updateAllinBatchOperation(list);

					if (pb.getRememberTime() != null && CoreModel.getInstance().getUserInfo() != null) {
						PreferencesUtils.setFriendRequestRecvTime(CoreModel.getInstance().getUserInfo().getUserId(), pb.getRememberTime()
								.getFriendReqReceiveTime());
					}

					PageInfoObject pageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_FRIEND_REQUEST);
					PageInfoObject.updatePageInfo(pageInfo, pb.getPageInfo());
					PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_FRIEND_REQUEST, pageInfo);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_FRIEND_REQUEST_LIST;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.arg2 = pageNo;
						message.obj = list;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void agreeFriendReq(final int userId, final String token, final int friendId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = FriendApiClient.agreeFriendReq(userId, token, friendId);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_AGREE_FRIEND_REQUEST;
					FamilySaferPb pb = (FamilySaferPb) result;
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

	public static void refuseFriendReq(final int userId, final String token, final int friendId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = FriendApiClient.refuseFriendReq(userId, token, friendId);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_REFUSE_FRIEND_REQUEST;
					FamilySaferPb pb = (FamilySaferPb) result;
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

	public static void friendList(final int userId, final String token, final int pageNo, final int pageSize) {
		final List<FriendObject> list = new ArrayList<FriendObject>();
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = FriendApiClient.friendList(userId, token, pageNo, pageSize);
				if (ApiClient.isOK(pb)) {
					FriendObjectDao friendDao = new FriendObjectDao();
					if (pageNo == 1) {
						friendDao.clear();
					}

					for (Friend friend : pb.getFrdsList()) {
						list.add(FriendObject.createFromPb(friend));
					}
					friendDao.updateAllinBatchOperation(list);

					PageInfoObject pageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_FRIEND);
					PageInfoObject.updatePageInfo(pageInfo, pb.getPageInfo());
					PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_FRIEND, pageInfo);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_FRIEND_LIST;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.arg2 = pageNo;
						message.obj = list;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void delFriend(final int userId, final String token, final int friendId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = FriendApiClient.delFriend(userId, token, friendId);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_DEL_FRIEND;
					FamilySaferPb pb = (FamilySaferPb) result;
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

	public static void setRemarkName(final int userId, final String token, final int friendId, final String remarkName) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				String tempRemarkName = remarkName;
				if (TextUtils.isEmpty(tempRemarkName)) {
					tempRemarkName = "";
				}
				FamilySaferPb pb = FriendApiClient.setRemarkName(userId, token, friendId, tempRemarkName);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_SET_REMARK_NAME;
					FamilySaferPb pb = (FamilySaferPb) result;
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

	public static void showMyPosition(final int userId, final String token, final int friendId, final boolean showMyPosition) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = FriendApiClient.showMyPosition(userId, token, friendId, showMyPosition);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_SHOW_MY_LOCATION;
					FamilySaferPb pb = (FamilySaferPb) result;
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

	public static void getNewMsgNum(final int userId, final String token, final long friendReqRecvTime) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = FriendApiClient.checkFriendRequestNum(userId, token, friendReqRecvTime);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_NEW_MSG_NUM;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						Integer friendReqCount = (pb.getTip() != null) ? pb.getTip().getFriendReqNewNum() : 0;
						Integer msgCount = (pb.getTip() != null) ? pb.getTip().getMsgNewNum() : 0;
						Integer sosCount = (pb.getTip() != null) ? pb.getTip().getSosEventNewNum() : 0;
						Integer checkinCount = (pb.getTip() != null) ? pb.getTip().getCheckInEventNewNum() : 0;
						Integer confirmCount = (pb.getTip() != null) ? pb.getTip().getConfirmEventNewNum() : 0;
						Integer clueCount = (pb.getTip() != null) ? pb.getTip().getClueEventNewNum() : 0;
						if (friendReqCount > 0) {
							CoreModel.getInstance().setFriendReqCount(friendReqCount);
						}
						if (sosCount > 0) {
							CoreModel.getInstance().setNewSOSCount(sosCount);
						}
						if (checkinCount > 0) {
							CoreModel.getInstance().setNewCheckinCount(checkinCount);
						}
						if (confirmCount > 0) {
							CoreModel.getInstance().setNewConfirmCount(confirmCount);
						}
						if (clueCount > 0) {
							CoreModel.getInstance().setNewEventClueCount(clueCount);
						}
						CoreModel.getInstance().setMsgCount(msgCount);
						message.arg1 = 200;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void friendReqCacheList(final int userId, final String token, final PageInfoObject pageInfo) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Message message = App.getInstance().obtainMessage();
				message.what = YSMSG.RESP_GET_CACHE_FRIEND_REQUEST_LIST;

				PageInfoObject newPageInfo = pageInfo;
				if (null == newPageInfo) {
					newPageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_FRIEND_REQUEST);
					newPageInfo.setReadCachePageNo(1);
				}

				FriendRequestObjectDao friendReqDao = new FriendRequestObjectDao();
				int count = friendReqDao.count();
				int readCachePageNo = newPageInfo.getReadCachePageNo();
				if (readCachePageNo < 1 || count <= 0) {
					readCachePageNo = 1;
				}

				newPageInfo.setReadCachePageNo(readCachePageNo);
				PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_FRIEND_REQUEST, newPageInfo);

				int pageNo = PageInfoObjectDao.getCurrentPageNo(PageInfoObjectDao.ID_FRIEND_REQUEST);
				int pageSize = PageInfoObjectDao.FRIEND_REQUEST_PAGE_SIZE;
				if (count <= 0 || readCachePageNo > pageNo) {
					friendReqList(userId, token, readCachePageNo, pageSize);
				} else {
					message.arg1 = 200;
					message.arg2 = readCachePageNo;
					message.obj = friendReqDao.findPage(readCachePageNo, pageSize);
					;
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
				return false;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {

			}
		};
		asyncTask.execute();
	}

	public static void friendCacheList(final int userId, final String token, final PageInfoObject pageInfo) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Message message = App.getInstance().obtainMessage();
				message.what = YSMSG.RESP_GET_CACHE_FRIEND_LIST;

				PageInfoObject newPageInfo = pageInfo;
				if (null == newPageInfo) {
					newPageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_FRIEND);
					newPageInfo.setReadCachePageNo(1);
				}

				FriendObjectDao friendDao = new FriendObjectDao();
				int count = friendDao.count();
				int readCachePageNo = newPageInfo.getReadCachePageNo();
				if (readCachePageNo < 1 || count <= 0) {
					readCachePageNo = 1;
				}

				newPageInfo.setReadCachePageNo(readCachePageNo);
				PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_FRIEND, newPageInfo);

				int pageNo = PageInfoObjectDao.getCurrentPageNo(PageInfoObjectDao.ID_FRIEND);
				int pageSize = PageInfoObjectDao.FRIEND_PAGE_SIZE;
				if (count <= 0 || readCachePageNo > pageNo) {
					friendList(userId, token, readCachePageNo, pageSize);
				} else {
					message.arg1 = 200;
					message.arg2 = readCachePageNo;
					message.obj = friendDao.findPage(readCachePageNo, pageSize);
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
				return false;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {

			}
		};
		asyncTask.execute();
	}

	public static void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.REQ_GET_FRIEND_LIST: {
			int pageNo = 1;
			if (msg.arg1 > 1) {
				pageNo = msg.arg1;
			}
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				friendList(userInfo.getUserId(), userInfo.getToken(), pageNo, PageInfoObjectDao.FRIEND_PAGE_SIZE);
			} else {

			}
		}
			break;
		case YSMSG.REQ_GET_FRIEND_REQUEST_LIST: {
			int pageNo = 1;
			if (msg.arg1 > 1) {
				pageNo = msg.arg1;
			}
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				friendReqList(userInfo.getUserId(), userInfo.getToken(), pageNo, PageInfoObjectDao.FRIEND_REQUEST_PAGE_SIZE);
			} else {

			}
		}
			break;
		case YSMSG.REQ_ADD_FRIEND: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				addFriend(userInfo.getUserId(), userInfo.getToken(), msg.arg1);
			} else {

			}
		}
			break;
		case YSMSG.REQ_AGREE_FRIEND_REQUEST: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				agreeFriendReq(userInfo.getUserId(), userInfo.getToken(), msg.arg1);
			} else {

			}
		}
			break;
		case YSMSG.REQ_REFUSE_FRIEND_REQUEST: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				refuseFriendReq(userInfo.getUserId(), userInfo.getToken(), msg.arg1);
			} else {

			}
		}
			break;
		case YSMSG.REQ_DEL_FRIEND: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				delFriend(userInfo.getUserId(), userInfo.getToken(), msg.arg1);
			} else {

			}
		}
			break;
		case YSMSG.REQ_SET_REMARK_NAME: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				setRemarkName(userInfo.getUserId(), userInfo.getToken(), msg.arg1, (String) msg.obj);
			} else {

			}
		}
			break;
		case YSMSG.REQ_SHOW_MY_LOCATION: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				showMyPosition(userInfo.getUserId(), userInfo.getToken(), msg.arg1, (Boolean) msg.obj);
			} else {

			}
		}
			break;
		case YSMSG.REQ_GET_NEW_MSG_NUM: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				long friendRequestRecvTime = PreferencesUtils.getFriendRequestRecvTime(userInfo.getUserId());
				if (friendRequestRecvTime != -1) {
					getNewMsgNum(userInfo.getUserId(), userInfo.getToken(), friendRequestRecvTime);
				} else {
					getNewMsgNum(userInfo.getUserId(), userInfo.getToken(), 0);
				}
			} else {

			}
		}
			break;
		case YSMSG.REQ_GET_CACHE_FRIEND_LIST: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			PageInfoObject pageInfo = (PageInfoObject) msg.obj;
			if (userInfo != null) {
				friendCacheList(userInfo.getUserId(), userInfo.getToken(), pageInfo);
			}

		}
			break;
		case YSMSG.REQ_GET_CACHE_FRIEND_REQUEST_LIST: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			PageInfoObject pageInfo = (PageInfoObject) msg.obj;
			if (userInfo != null) {
				friendReqCacheList(userInfo.getUserId(), userInfo.getToken(), pageInfo);
			}
		}
		}
	}
}
