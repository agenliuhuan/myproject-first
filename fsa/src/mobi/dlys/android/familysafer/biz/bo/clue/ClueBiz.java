package mobi.dlys.android.familysafer.biz.bo.clue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.api.ApiClient;
import mobi.dlys.android.familysafer.api.ClueApiClient;
import mobi.dlys.android.familysafer.api.PPNetManager;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ClueImageObject;
import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.PublicClueObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.biz.vo.upload.UploadRespObject;
import mobi.dlys.android.familysafer.db.dao.ClueImageObjectDao;
import mobi.dlys.android.familysafer.db.dao.ClueObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.db.dao.PublicClueObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ActionType;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Clue;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.ClueImage;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.HttpUploadHelper;
import mobi.dlys.android.familysafer.utils.MD5;
import android.os.Message;
import android.text.TextUtils;

public class ClueBiz extends BaseBiz {

	public static void pushClue(final int userId, final String token, final String location, final String lng, final String lat,
			final List<ClueImageObject> imageList, final String msg, final String phoneModel, final boolean isEvent) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				List<ClueImage> list = uploadImage(imageList);
				if (list.size() <= 0) {
					return null;
				}

				FamilySaferPb pb = ClueApiClient.pushClue(userId, token, location, lng, lat, list, msg, phoneModel, isEvent);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_PUSH_CLUE;
					FamilySaferPb pb = (FamilySaferPb) result;
					if (!ApiClient.isOK(pb)) {
						ResultObject resultObject = ResultObject.createByPb(pb);
						if (resultObject != null && null == pb) {
							resultObject.setErrorMsg(App.getInstance().getResources().getString(R.string.toast_clue_image_upload_failed));
						}
						message.arg1 = 0;
						message.obj = resultObject;
					} else {
						message.arg1 = 200;
						message.obj = ClueObject.createFromPb(pb.getClue());
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void myClueList(final int userId, final String token, final int pageNo, final int pageSize) {
		final List<ClueObject> list = new ArrayList<ClueObject>();

		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = ClueApiClient.clueList(userId, token, pageNo, pageSize, ActionType.AT_MY_CLUE_LIST);
				if (ApiClient.isOK(pb)) {
					ClueObjectDao clueDao = new ClueObjectDao();
					if (1 == pageNo) {
						clueDao.clear();
					}

					ClueImageObjectDao clueImageDao = new ClueImageObjectDao();
					ClueObject clueObject = null;

					for (Clue clue : pb.getCluesList()) {
						clueObject = ClueObject.createFromPb(clue);
						list.add(clueObject);
						clueImageDao.updateAllinBatchOperation(clueObject.getImageList());
					}

					clueDao.updateAllinBatchOperation(list);

					PageInfoObject pageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_MY_CLUE);
					PageInfoObject.updatePageInfo(pageInfo, pb.getPageInfo());
					PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_MY_CLUE, pageInfo);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_CLUE_LIST;
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

	public static void publicClueList(final int userId, final String token, final int pageNo, final int pageSize) {
		final List<PublicClueObject> list = new ArrayList<PublicClueObject>();
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				FamilySaferPb pb = ClueApiClient.clueList(userId, token, pageNo, pageSize, ActionType.AT_CLUE_LIST);
				if (ApiClient.isOK(pb)) {
					PublicClueObjectDao clueDao = new PublicClueObjectDao();
					if (1 == pageNo) {
						clueDao.clear();
					}

					ClueImageObjectDao clueImageDao = new ClueImageObjectDao();
					PublicClueObject clueObject = null;

					for (Clue clue : pb.getCluesList()) {
						clueObject = PublicClueObject.createFromPb(clue);
						list.add(clueObject);
						clueImageDao.updateAllinBatchOperation(clueObject.getImageList());
					}

					clueDao.updateAllinBatchOperation(list);

					PageInfoObject pageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_PUBLIC_CLUE);
					PageInfoObject.updatePageInfo(pageInfo, pb.getPageInfo());
					PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_PUBLIC_CLUE, pageInfo);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_PUBLIC_CLUE_LIST;
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

	public static List<ClueImage> uploadImage(List<ClueImageObject> imageList) {
		ArrayList<ClueImage> imageUrlList = new ArrayList<ClueImage>();
		if (imageList == null || imageList.size() <= 0) {
			return imageUrlList;
		}

		UploadRespObject uploadResp;
		String imagePath;
		for (ClueImageObject clueImage : imageList) {
			// upload image
			imagePath = clueImage.getImage();
			if (!TextUtils.isEmpty(imagePath) && FileUtils.exists(imagePath)) {
				uploadResp = HttpUploadHelper.upload(PPNetManager.getInstance().getUploadClueImageUrl(), new File(imagePath), false);
				if (uploadResp != null && uploadResp.isOK() && !TextUtils.isEmpty(uploadResp.getFileUri())) {
					ClueImage.Builder cb = ClueImage.newBuilder();
					cb.setImage(uploadResp.getFileUri());
					cb.setWidth(clueImage.getWidth());
					cb.setHeight(clueImage.getHeight());
					imageUrlList.add(cb.build());
					FileUtils.renameFile(FileUtils.COVER + MD5.encoderForString(uploadResp.getFileUrl()) + FileUtils.JPG, imagePath);
				}
			}
		}
		return imageUrlList;
	}

	public static void myClueCacheList(final int userId, final String token, final PageInfoObject pageInfo) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Message message = App.getInstance().obtainMessage();
				message.what = YSMSG.RESP_GET_CACHE_CLUE_LIST;

				PageInfoObject newPageInfo = pageInfo;
				if (null == newPageInfo) {
					newPageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_MY_CLUE);
					newPageInfo.setReadCachePageNo(1);
				}

				ClueObjectDao clueDao = new ClueObjectDao();
				int count = clueDao.count();
				int readCachePageNo = newPageInfo.getReadCachePageNo();
				if (readCachePageNo < 1 || count <= 0) {
					readCachePageNo = 1;
				}

				newPageInfo.setReadCachePageNo(readCachePageNo);
				PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_MY_CLUE, newPageInfo);

				int pageNo = PageInfoObjectDao.getCurrentPageNo(PageInfoObjectDao.ID_MY_CLUE);
				int pageSize = PageInfoObjectDao.CLUE_PAGE_SIZE;
				if (count <= 0 || readCachePageNo > pageNo) {
					myClueList(userId, token, readCachePageNo, pageSize);
				} else {
					message.arg1 = 200;
					message.arg2 = readCachePageNo;
					List<ClueObject> list = clueDao.findPage(readCachePageNo, pageSize);
					ClueImageObjectDao.getClueImage(list);
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

	public static void publicClueCacheList(final int userId, final String token, final PageInfoObject pageInfo) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				Message message = App.getInstance().obtainMessage();
				message.what = YSMSG.RESP_GET_CACHE_PUBLIC_CLUE_LIST;

				PageInfoObject newPageInfo = pageInfo;
				if (null == newPageInfo) {
					newPageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_PUBLIC_CLUE);
					newPageInfo.setReadCachePageNo(1);
				}

				PublicClueObjectDao clueDao = new PublicClueObjectDao();
				int count = clueDao.count();
				int readCachePageNo = newPageInfo.getReadCachePageNo();
				if (readCachePageNo < 1 || count <= 0) {
					readCachePageNo = 1;
				}

				newPageInfo.setReadCachePageNo(readCachePageNo);
				PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_PUBLIC_CLUE, newPageInfo);

				int pageNo = PageInfoObjectDao.getCurrentPageNo(PageInfoObjectDao.ID_PUBLIC_CLUE);
				int pageSize = PageInfoObjectDao.PUBLIC_CLUE_PAGE_SIZE;
				if (count <= 0 || readCachePageNo > pageNo) {
					publicClueList(userId, token, readCachePageNo, pageSize);
				} else {
					message.arg1 = 200;
					message.arg2 = readCachePageNo;
					List<PublicClueObject> list = clueDao.findPage(readCachePageNo, pageSize);
					ClueImageObjectDao.getPublicClueImage(list);
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
		case YSMSG.REQ_PUSH_CLUE: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			ClueObject clue = (ClueObject) msg.obj;
			if (userInfo != null && clue != null) {
				pushClue(userInfo.getUserId(), userInfo.getToken(), clue.getLocation(), userInfo.getLng(), userInfo.getLat(), clue.getImageList(),
						clue.getMessage(), clue.getModel(), clue.isEvent());
			} else {

			}
		}
			break;
		case YSMSG.REQ_GET_CLUE_LIST: {
			int pageNo = 1;
			if (msg.arg1 > 1) {
				pageNo = msg.arg1;
			}
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				myClueList(userInfo.getUserId(), userInfo.getToken(), pageNo, PageInfoObjectDao.CLUE_PAGE_SIZE);
			} else {

			}
		}
			break;
		case YSMSG.REQ_GET_PUBLIC_CLUE_LIST: {
			int pageNo = 1;
			if (msg.arg1 > 1) {
				pageNo = msg.arg1;
			}
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				publicClueList(userInfo.getUserId(), userInfo.getToken(), pageNo, PageInfoObjectDao.PUBLIC_CLUE_PAGE_SIZE);
			}
		}
			break;
		case YSMSG.REQ_GET_CACHE_PUBLIC_CLUE_LIST: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			PageInfoObject pageInfo = (PageInfoObject) msg.obj;
			if (userInfo != null) {
				publicClueCacheList(userInfo.getUserId(), userInfo.getToken(), pageInfo);
			}
		}
			break;
		case YSMSG.REQ_GET_CACHE_CLUE_LIST: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			PageInfoObject pageInfo = (PageInfoObject) msg.obj;
			if (userInfo != null) {
				myClueCacheList(userInfo.getUserId(), userInfo.getToken(), pageInfo);
			}
		}
			break;
		}
	}

}
