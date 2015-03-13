package mobi.dlys.android.familysafer.biz.bo.checkin;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.api.ApiClient;
import mobi.dlys.android.familysafer.api.CheckInApiClient;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.biz.vo.event.SOSVoice;
import mobi.dlys.android.familysafer.db.dao.ClueImageObjectDao;
import mobi.dlys.android.familysafer.db.dao.ClueObjectDao;
import mobi.dlys.android.familysafer.db.dao.EventContentDao;
import mobi.dlys.android.familysafer.db.dao.EventObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.db.dao.SOSVoiceDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Event;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.EventType;
import android.os.Message;
import android.text.TextUtils;

public class CheckinBiz extends BaseBiz {

	public static void checkIn2All(final int userId, final String token, final String lng, final String lat, final String location, final String msg) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = CheckInApiClient.checkIn2All(userId, token, lng, lat, location, msg);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_CHECKIN_TO_ALL;
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

	public static void checkIn2SomeFriend(final int userId, final String token, final String lng, final String lat, final String location,
			final List<Integer> friendIdList, final String msg) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = CheckInApiClient.checkIn2SomeFriend(userId, token, lng, lat, location, friendIdList, msg);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_CHECKIN_TO_SAME_ONE;
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

	public static void checkInMsgList(final int userId, final String token, final int pageNo, final int pageSize, final List<EventType> eventTypeList,
			final int eventId) {
		final List<EventObjectEx> list = new ArrayList<EventObjectEx>();
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = CheckInApiClient.checkInMsgList(userId, token, pageNo, pageSize, false, eventTypeList);
				if (ApiClient.isOK(pb)) {
					EventObjectDao eventDao = new EventObjectDao();
					if (1 == pageNo) {
						eventDao.clear(eventId);
					}

					EventContentDao eventContentDao = new EventContentDao();
					SOSVoiceDao sosVoiceDao = new SOSVoiceDao();
					ClueObjectDao clueDao = new ClueObjectDao();
					ClueImageObjectDao clueImageDao = new ClueImageObjectDao();

					EventObjectEx eventObject = null;
					SOSVoice sosVoice = null;

					for (Event event : pb.getEventsList()) {
						eventObject = EventObjectEx.createFromPb(event);
						list.add(eventObject);

						if (eventObject.getContent() != null) {
							eventContentDao.updateMsgId(eventObject.getContent());
							sosVoice = eventObject.getContent().getSOSVoice();
							if (sosVoice != null) {
								sosVoiceDao.updateMsgId(sosVoice);
							}
						}
						if (eventObject.getClue() != null) {
							clueDao.updateMsgId(eventObject.getClue());
							if (eventObject.getClue().getImageList() != null) {
								clueImageDao.updateAllinBatchOperation(eventObject.getClue().getImageList());
							}
						}
					}

					eventDao.updateAllinBatchOperation(list);

					PageInfoObject pageInfo = PageInfoObjectDao.getPageInfo(eventId);
					PageInfoObject.updatePageInfo(pageInfo, pb.getPageInfo());
					PageInfoObjectDao.setPageInfo(eventId, pageInfo);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_CHECKIN_MSG_LIST;
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

	public static void checkInNewMsgList(final int userId, final String token, final int pageNo, final int pageSize, final List<EventType> eventTypeList) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = CheckInApiClient.checkInMsgList(userId, token, pageNo, pageSize, true, eventTypeList);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_NEW_EVENT_LIST;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						List<EventObjectEx> list = new ArrayList<EventObjectEx>();
						for (Event event : pb.getEventsList()) {
							list.add(EventObjectEx.createFromPb(event));
						}
						message.arg1 = 200;
						message.obj = list;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void checkInConfirm(final int userId, final String token, final int checkInId, final int eventId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = CheckInApiClient.checkInConfirm(userId, token, checkInId, eventId);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_CHECK_IN_CONFIRM;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = null;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void checkInCacheList(final int userId, final String token, final PageInfoObject pageInfo, final List<EventType> eventTypeList,
			final int eventId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Message message = App.getInstance().obtainMessage();
				message.what = YSMSG.RESP_GET_CACHE_CHECKIN_MSG_LIST;

				PageInfoObject newPageInfo = pageInfo;
				if (null == newPageInfo) {
					newPageInfo = PageInfoObjectDao.getPageInfo(eventId);
					newPageInfo.setReadCachePageNo(1);
				}

				EventObjectDao eventDao = new EventObjectDao();
				int count = eventDao.count(eventId);
				int readCachePageNo = newPageInfo.getReadCachePageNo();
				if (readCachePageNo < 1 || count <= 0) {
					readCachePageNo = 1;
				}

				newPageInfo.setReadCachePageNo(readCachePageNo);
				PageInfoObjectDao.setPageInfo(eventId, newPageInfo);

				int pageNo = PageInfoObjectDao.getCurrentPageNo(eventId);
				int pageSize = PageInfoObjectDao.EVENT_PAGE_SIZE;
				if (count <= 0 || readCachePageNo > pageNo) {
					checkInMsgList(userId, token, readCachePageNo, pageSize, eventTypeList, eventId);
				} else {
					message.arg1 = 200;
					message.arg2 = readCachePageNo;
					List<EventObjectEx> list = eventDao.findPage(eventId, readCachePageNo, pageSize);
					EventContentDao.getEventContent(list);
					ClueObjectDao.getClueObject(list);
					message.obj = list;
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

	@SuppressWarnings("unchecked")
	public static void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.REQ_CHECKIN_TO_ALL: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			String message = CoreModel.getInstance().getCheckinMessage();
			if (userInfo != null) {
				if (TextUtils.isEmpty(message)) {
					message = "";
				}
				checkIn2All(userInfo.getUserId(), userInfo.getToken(), userInfo.getLng(), userInfo.getLat(), userInfo.getLocation3(), message);
			} else {

			}
		}
			break;
		case YSMSG.REQ_CHECKIN_TO_SAME_ONE: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			String message = CoreModel.getInstance().getCheckinMessage();
			List<Integer> mSelectedList = (List<Integer>) msg.obj;
			if (userInfo != null) {
				if (TextUtils.isEmpty(message)) {
					message = "";
				}
				checkIn2SomeFriend(userInfo.getUserId(), userInfo.getToken(), userInfo.getLng(), userInfo.getLat(), userInfo.getLocation3(), mSelectedList,
						message);
			} else {

			}
		}
			break;
		case YSMSG.REQ_GET_CHECKIN_MSG_LIST: {
			int pageNo = 1;
			if (msg.arg1 > 1) {
				pageNo = msg.arg1;
			}
			List<EventType> eventTypeList = new ArrayList<EventType>();
			PageInfoObject pageInfo = (PageInfoObject) msg.obj;
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null && pageInfo != null) {
				EventType type = EventType.valueOf(pageInfo.getEventType());
				if (type != null) {
					eventTypeList.add(type);
				} else {
					eventTypeList.add(EventType.ET_CHECK_IN_CONFIRM);
					eventTypeList.add(EventType.ET_VOICE_SOS_CONFIRM);
				}
				checkInMsgList(userInfo.getUserId(), userInfo.getToken(), pageNo, PageInfoObjectDao.EVENT_PAGE_SIZE, eventTypeList, pageInfo.getTypeId());
			} else {

			}
		}
			break;
		case YSMSG.REQ_CHECK_IN_CONFIRM: {
			Integer eventId = msg.arg1;
			Integer checkInId = msg.arg2;
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				checkInConfirm(userInfo.getUserId(), userInfo.getToken(), checkInId, eventId);
			}
		}
			break;
		case YSMSG.REQ_GET_NEW_EVENT_LIST: {
			List<EventType> eventTypeList = new ArrayList<EventType>();
			PageInfoObject pageInfo = (PageInfoObject) msg.obj;
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null && pageInfo != null) {
				EventType type = EventType.valueOf(pageInfo.getEventType());
				if (type != null) {
					eventTypeList.add(type);
				} else {
					eventTypeList.add(EventType.ET_CHECK_IN_CONFIRM);
					eventTypeList.add(EventType.ET_VOICE_SOS_CONFIRM);
				}
				checkInNewMsgList(userInfo.getUserId(), userInfo.getToken(), 1, 1, eventTypeList);
			} else {

			}
		}
			break;
		case YSMSG.REQ_GET_CACHE_CHECKIN_MSG_LIST: {
			List<EventType> eventTypeList = new ArrayList<EventType>();
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			PageInfoObject pageInfo = (PageInfoObject) msg.obj;
			if (userInfo != null && pageInfo != null) {
				EventType type = EventType.valueOf(pageInfo.getEventType());
				if (type != null) {
					eventTypeList.add(type);
				} else {
					eventTypeList.add(EventType.ET_CHECK_IN_CONFIRM);
					eventTypeList.add(EventType.ET_VOICE_SOS_CONFIRM);
				}
				checkInCacheList(userInfo.getUserId(), userInfo.getToken(), pageInfo, eventTypeList, pageInfo.getTypeId());
			}
		}
			break;
		}
	}
}
