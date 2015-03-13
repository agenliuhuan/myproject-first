package mobi.dlys.android.familysafer.biz.bo.im;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.api.ApiClient;
import mobi.dlys.android.familysafer.api.ImApiClient;
import mobi.dlys.android.familysafer.api.PPNetManager;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.LastMsgIdObject;
import mobi.dlys.android.familysafer.biz.vo.MsgObject;
import mobi.dlys.android.familysafer.biz.vo.MsgTopicObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.biz.vo.upload.UploadRespObject;
import mobi.dlys.android.familysafer.db.dao.LastMsgIdObjectDao;
import mobi.dlys.android.familysafer.db.dao.MsgObjectDao;
import mobi.dlys.android.familysafer.db.dao.MsgTopicObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.player.download.DownloadManage;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.MsgTopic;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.HttpUploadHelper;
import android.os.Message;
import android.text.TextUtils;

public class ImBiz extends BaseBiz {
	public static void sendMsg(final int userId, final String token, final int friendId, final MsgObject msgObject, final int duration) {
		final int indexId = msgObject.getId();
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				String voiceFilePath = msgObject.getVoiceFilePath();
				MsgObjectDao msgDao = new MsgObjectDao();

				// upload voice
				UploadRespObject uploadResp;
				String voiceAddress = voiceFilePath;
				if (!TextUtils.isEmpty(voiceFilePath) && FileUtils.exists(voiceFilePath)) {
					uploadResp = HttpUploadHelper.upload(PPNetManager.getInstance().getUploadSOSVoiceUrl(), new File(voiceFilePath), true);
					if (null != uploadResp && uploadResp.isOK() && !TextUtils.isEmpty(uploadResp.getFileUri())) {
						voiceAddress = uploadResp.getFileUri();
						// FileUtils.renameFile(FileUtils.VOICE +
						// MD5.encoderForString(uploadResp.getFileUrl()) +
						// FileUtils.AMR, voiceFilePath);
					} else {
						msgObject.setStatus(2);
						msgDao.updateMsgId(msgObject);
						return null;
					}
				} else {
					msgObject.setStatus(2);
					msgDao.updateMsgId(msgObject);
					return null;
				}

				// send msg
				FamilySaferPb pb = ImApiClient.sendMsg(userId, token, friendId, voiceAddress, duration);
				if (ApiClient.isOK(pb)) {
					if (pb.getMsg() != null) {
						msgObject.setMsgId(pb.getMsg().getMsgId());

						// rename local file
						FileUtils.renameFile(FileUtils.VOICE + "voice_" + pb.getMsg().getMsgId() + "_" + pb.getMsg().getMsgId() + DownloadManage.MP3_TAG,
								voiceFilePath);
					}
					msgObject.setStatus(1);
					msgDao.updateMsgId(msgObject);
				} else {
					msgObject.setStatus(2);
					msgDao.updateMsgId(msgObject);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_SEND_MSG;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.arg2 = indexId;
						message.obj = ResultObject.createByPb(pb);
					} else {
						CoreModel.getInstance().setUpdateMsgList(true);
						message.arg1 = 200;
						message.arg2 = indexId;
						if (pb.getMsg() != null) {
							message.obj = pb.getMsg().getMsgId();
						}
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void msgTopicList(final int userId, final String token, final int pageNo, final int pageSize) {
		final List<MsgTopicObject> list = new ArrayList<MsgTopicObject>();

		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = ImApiClient.msgTopicList(userId, token, pageNo, pageSize);
				if (ApiClient.isOK(pb)) {
					MsgTopicObjectDao msgTopicDao = new MsgTopicObjectDao();
					if (1 == pageNo) {
						msgTopicDao.clear();
					}

					for (MsgTopic msgTopic : pb.getMsgTopicsList()) {
						list.add(MsgTopicObject.createFromPb(msgTopic));
					}
					msgTopicDao.updateAllinBatchOperation(list);

					PageInfoObject pageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_IM_MSG_TOPIC);
					PageInfoObject.updatePageInfo(pageInfo, pb.getPageInfo());
					PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_IM_MSG_TOPIC, pageInfo);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_MSG_TOPIC_LIST;
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

	public static void msgTopicCacheList(final int userId, final String token, final PageInfoObject pageInfo) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Message message = App.getInstance().obtainMessage();
				message.what = YSMSG.RESP_GET_CACHE_MSG_TOPIC_LIST;

				PageInfoObject newPageInfo = pageInfo;
				if (null == newPageInfo) {
					newPageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_IM_MSG_TOPIC);
					newPageInfo.setReadCachePageNo(1);
				}

				MsgTopicObjectDao msgTopicDao = new MsgTopicObjectDao();
				int count = msgTopicDao.count();
				int readCachePageNo = newPageInfo.getReadCachePageNo();
				if (readCachePageNo < 1 || count <= 0) {
					readCachePageNo = 1;
				}

				newPageInfo.setReadCachePageNo(readCachePageNo);
				PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_IM_MSG_TOPIC, newPageInfo);

				int pageNo = PageInfoObjectDao.getCurrentPageNo(PageInfoObjectDao.ID_IM_MSG_TOPIC);
				int pageSize = PageInfoObjectDao.IM_MSG_TOPIC_COUNT;
				if (count <= 0 || readCachePageNo > pageNo) {
					msgTopicList(userId, token, readCachePageNo, pageSize);
				} else {
					message.arg1 = 200;
					message.arg2 = readCachePageNo;
					message.obj = msgTopicDao.findPage(readCachePageNo, pageSize);
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

	public static void msgList(final int userId, final String token, final int pageNo, final int pageSize, final int friendId) {
		final List<MsgObject> list = new ArrayList<MsgObject>();
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			int newCount = 0;

			@Override
			protected Object doInBackground() throws Exception {
				int tempPageSize = pageSize;
				LastMsgIdObjectDao lastMsgIdDao = new LastMsgIdObjectDao();
				LastMsgIdObject lastMsgIdObject = lastMsgIdDao.findById(friendId);
				if (null == lastMsgIdObject) {
					lastMsgIdObject = new LastMsgIdObject(friendId, 0);
					tempPageSize = PageInfoObjectDao.IM_MSG_COUNT;
				}

				FamilySaferPb pb = ImApiClient.msgList(userId, token, pageNo, tempPageSize, friendId, lastMsgIdObject.getLastMsgId());
				if (ApiClient.isOK(pb)) {
					if (pb.getMsgsCount() > 0) {
						newCount = pb.getMsgsCount();
						lastMsgIdObject.setLastMsgId(pb.getMsgsList().get(0).getMsgId());
						lastMsgIdDao.updateMsgId(lastMsgIdObject);

						List<MsgObject> msgList = new ArrayList<MsgObject>();
						for (int i = pb.getMsgsCount() - 1; i >= 0; i--) {
							msgList.add(MsgObject.createFromPb(pb.getMsgsList().get(i)));
						}
						MsgObjectDao msgDao = new MsgObjectDao();
						msgDao.updateAllinBatchOperation(msgList);
						list.clear();
						list.addAll(msgDao.findPage(0, PageInfoObjectDao.IM_MSG_COUNT, friendId));
					}

					PageInfoObject pageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_IM_MSG);
					PageInfoObject.updatePageInfo(pageInfo, pb.getPageInfo());
					pageInfo.setReadCachePageNo(1);
					PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_IM_MSG, pageInfo);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_MSG_LIST;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.arg2 = newCount;
						message.obj = list;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void msgCacheList(final int userId, final String token, final int showedCount, final int friendId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				Message message = App.getInstance().obtainMessage();
				message.what = YSMSG.RESP_GET_CACHE_MSG_LIST;

				MsgObjectDao msgDao = new MsgObjectDao();
				int count = msgDao.count(friendId);
				if (count > showedCount) {
					message.obj = msgDao.findPage(showedCount, PageInfoObjectDao.IM_MSG_COUNT, friendId);
				}

				message.arg1 = 200;
				CoreModel.getInstance().notifyOutboxHandlers(message);
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
		case YSMSG.REQ_SEND_MSG: {
			MsgObject msgObject = (MsgObject) msg.obj;
			int duration = msg.arg1;
			int friendId = msg.arg2;
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				sendMsg(userInfo.getUserId(), userInfo.getToken(), friendId, msgObject, duration);
			}
		}
			break;
		case YSMSG.REQ_GET_MSG_TOPIC_LIST: {
			int pageNo = 1;
			if (msg.arg1 > 1) {
				pageNo = msg.arg1;
			}
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				msgTopicList(userInfo.getUserId(), userInfo.getToken(), pageNo, PageInfoObjectDao.IM_MSG_TOPIC_COUNT);
			}
		}
			break;
		case YSMSG.REQ_GET_CACHE_MSG_TOPIC_LIST: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			PageInfoObject pageInfo = (PageInfoObject) msg.obj;
			if (userInfo != null) {
				msgTopicCacheList(userInfo.getUserId(), userInfo.getToken(), pageInfo);
			}
		}
			break;
		case YSMSG.REQ_GET_MSG_LIST: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			int friendId = msg.arg2;
			if (userInfo != null) {
				msgList(userInfo.getUserId(), userInfo.getToken(), 1, 100, friendId);
			}
		}
			break;
		case YSMSG.REQ_GET_CACHE_MSG_LIST: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			int showedCount = msg.arg1;
			int friendId = msg.arg2;
			if (userInfo != null) {
				msgCacheList(userInfo.getUserId(), userInfo.getToken(), showedCount, friendId);
			}
		}
			break;
		}
	}

}
