package mobi.dlys.android.familysafer.biz.bo.sos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.api.ApiClient;
import mobi.dlys.android.familysafer.api.PPNetManager;
import mobi.dlys.android.familysafer.api.SOSApiClient;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.MySOSObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.biz.vo.upload.UploadRespObject;
import mobi.dlys.android.familysafer.db.dao.MySOSObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ActionType;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.VoiceSOS;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.HttpUploadHelper;
import mobi.dlys.android.familysafer.utils.MD5;
import android.os.Message;
import android.text.TextUtils;

public class SOSBiz extends BaseBiz {

	public static void voiceSOS(final int userId, final String token, final String lng, final String lat, final String location, final String voiceFilePath,
			final int duration) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				// upload voice
				UploadRespObject uploadResp;
				String voiceAddress = voiceFilePath;
				if (!TextUtils.isEmpty(voiceFilePath) && FileUtils.exists(voiceFilePath)) {
					uploadResp = HttpUploadHelper.upload(PPNetManager.getInstance().getUploadSOSVoiceUrl(), new File(voiceFilePath), true);
					if (null != uploadResp && uploadResp.isOK() && !TextUtils.isEmpty(uploadResp.getFileUri())) {
						voiceAddress = uploadResp.getFileUri();
						FileUtils.renameFile(FileUtils.VOICE + MD5.encoderForString(uploadResp.getFileUrl()) + FileUtils.AMR, voiceFilePath);
					} else {
						return null;
					}
				} else {
					return null;
				}

				// post sos
				FamilySaferPb pb = SOSApiClient.voiceSOS(userId, token, lng, lat, location, voiceAddress, duration);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_VOICE_SOS;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						ResultObject resultObject = ResultObject.createByPb(pb);
						if (resultObject != null && null == pb) {
							resultObject.setErrorMsg(App.getInstance().getResources().getString(R.string.toast_sos_voice_upload_failled));
						}
						message.arg1 = 0;
						message.obj = resultObject;
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

	public static void voiceSOSConfirm(final int userId, final String token, final int voiceSOSId, final int eventId) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = SOSApiClient.voiceSOSConfirm(userId, token, voiceSOSId, eventId);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_VOICE_SOS_CONFIRM;
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

	public static void myVoiceSOS(final int userId, final String token, final int pageNo, final int pageSize) {
		final List<MySOSObject> list = new ArrayList<MySOSObject>();
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = SOSApiClient.myVoiceSOS(userId, token, ActionType.AT_MY_VOICE_SOS_LIST, pageNo, pageSize);
				if (ApiClient.isOK(pb)) {
					MySOSObjectDao mySOSDao = new MySOSObjectDao();
					if (1 == pageNo) {
						mySOSDao.clear();
					}

					MySOSObject clueObject = null;

					for (VoiceSOS voiceSOS : pb.getVoiceSOSsList()) {
						clueObject = MySOSObject.createFromPb(voiceSOS);
						list.add(clueObject);
					}

					mySOSDao.updateAllinBatchOperation(list);

					PageInfoObject pageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_MY_SOS);
					PageInfoObject.updatePageInfo(pageInfo, pb.getPageInfo());
					PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_MY_SOS, pageInfo);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_VOICE_SOS_LIST;
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

	public static void myCacheVoiceSOS(final int userId, final String token, final PageInfoObject pageInfo) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Message message = App.getInstance().obtainMessage();
				message.what = YSMSG.RESP_GET_CACHE_VOICE_SOS_LIST;

				PageInfoObject newPageInfo = pageInfo;
				if (null == newPageInfo) {
					newPageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_MY_SOS);
					newPageInfo.setReadCachePageNo(1);
				}

				MySOSObjectDao mySOSDao = new MySOSObjectDao();
				int count = mySOSDao.count();
				int readCachePageNo = newPageInfo.getReadCachePageNo();
				if (readCachePageNo < 1 || count <= 0) {
					readCachePageNo = 1;
				}

				newPageInfo.setReadCachePageNo(readCachePageNo);
				PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_MY_SOS, newPageInfo);

				int pageNo = PageInfoObjectDao.getCurrentPageNo(PageInfoObjectDao.ID_MY_SOS);
				int pageSize = PageInfoObjectDao.MY_SOS_COUNT;
				if (count <= 0 || readCachePageNo > pageNo) {
					myVoiceSOS(userId, token, readCachePageNo, pageSize);
				} else {
					message.arg1 = 200;
					message.arg2 = readCachePageNo;
					List<MySOSObject> list = mySOSDao.findPage(readCachePageNo, pageSize);
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

	public static void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.REQ_VOICE_SOS: {
			String voiceFilePath = (String) msg.obj;
			int duration = msg.arg1;
			UserObject user = CoreModel.getInstance().getUserInfo();
			if (!TextUtils.isEmpty(voiceFilePath) && null != user) {
				voiceSOS(user.getUserId(), user.getToken(), user.getLng(), user.getLat(), user.getLocation(), voiceFilePath, duration);
			} else {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_VOICE_SOS;
					message.arg1 = 0;
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		}
			break;
		case YSMSG.REQ_VOICE_SOS_CONFIRM: {
			int eventId = msg.arg1;
			int voiceSOSId = msg.arg2;
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				voiceSOSConfirm(userInfo.getUserId(), userInfo.getToken(), voiceSOSId, eventId);
			}
		}
			break;
		case YSMSG.REQ_GET_VOICE_SOS_LIST: {
			int pageNo = 1;
			if (msg.arg1 > 1) {
				pageNo = msg.arg1;
			}
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				myVoiceSOS(userInfo.getUserId(), userInfo.getToken(), pageNo, PageInfoObjectDao.MY_SOS_COUNT);
			}
		}
			break;
		case YSMSG.REQ_GET_CACHE_VOICE_SOS_LIST: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			PageInfoObject pageInfo = (PageInfoObject) msg.obj;
			if (userInfo != null) {
				myCacheVoiceSOS(userInfo.getUserId(), userInfo.getToken(), pageInfo);
			}
		}
			break;
		}
	}

}
