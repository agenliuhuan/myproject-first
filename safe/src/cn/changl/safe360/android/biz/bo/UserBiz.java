package cn.changl.safe360.android.biz.bo;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import mobi.dlys.android.core.utils.LogUtils;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.api.ApiClient;
import cn.changl.safe360.android.api.GroupApiClient;
import cn.changl.safe360.android.api.PPNetManager;
import cn.changl.safe360.android.api.UserApiClient;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ContactsObject;
import cn.changl.safe360.android.biz.vo.LoginObject;
import cn.changl.safe360.android.biz.vo.ModifyPwdObject;
import cn.changl.safe360.android.biz.vo.RegisterObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.contacts.ContactDataBase;
import cn.changl.safe360.android.contacts.ContactsSyncService;
import cn.changl.safe360.android.db.DatabaseManager;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.utils.DateUtils;
import cn.changl.safe360.android.utils.FileUtils;
import cn.changl.safe360.android.utils.MD5;
import cn.changl.safe360.android.utils.PreferencesUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.ProgressListener;
import com.upyun.block.api.main.UploaderManager;
import com.upyun.block.api.utils.UpYunUtils;

public class UserBiz extends BaseBiz {
	private static final String TAG = UserBiz.class.getSimpleName();

	public static void matchContactsList(final int userId, final String token, final Activity activity) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				List<ContactsObject> contactsList = null;
				// List<ContactsObject> finalContactsList = new
				// ArrayList<ContactsObject>();
				ContactDataBase cb = null;
				try {
					cb = new ContactDataBase(activity);
					contactsList = cb.query();
					if (contactsList.size() == 0) {
						contactsList = readContactAndStore();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cb.close();
				}

				if (null == contactsList || contactsList.size() < 0) {
					return null;
				}

				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_MATCH_PHONE_PROGRESS;
					message.arg1 = 0;
					message.arg2 = contactsList.size();
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}

				// finalContactsList.addAll(contactsList);

				Safe360Pb pb = null;
				ArrayList<String> phoneList = new ArrayList<String>();
				for (int i = 0; i < contactsList.size(); i++) {
					ContactsObject contacts = contactsList.get(i);
					if (contacts != null) {
						phoneList.add(contacts.getPhone());

						if (phoneList.size() > 200) {
							pb = UserApiClient.checkUserRegist(phoneList);
							if (null != message) {
								message.what = YSMSG.RESP_MATCH_PHONE_PROGRESS;
								message.arg1 = phoneList.size() * 100 / contactsList.size();
								message.arg2 = contactsList.size();
								CoreModel.getInstance().notifyOutboxHandlers(message);
							}
							if (ApiClient.isOK(pb)) {
								List<UserInfo> userInfoList = pb.getUserInfosList();
								if (userInfoList != null && userInfoList.size() > 0) {
									for (UserInfo info : userInfoList) {
										for (ContactsObject contacts2 : contactsList) {
											if (contacts2 != null && info != null && contacts2.getPhone().equals(info.getPhone())) {
												boolean isRegist = info.getRegistStatus();
												contacts2.setType(isRegist ? 2 : 1);
												if (isRegist) {
													contacts2.setUserId(info.getUserId());
												}
											}
										}
									}
								}
							}
							phoneList.clear();
						}
					}
				}

				if (phoneList.size() > 0) {
					pb = UserApiClient.checkUserRegist(phoneList);
					if (null != message) {
						message.what = YSMSG.RESP_MATCH_PHONE_PROGRESS;
						message.arg1 = 100;
						message.arg2 = contactsList.size();
						CoreModel.getInstance().notifyOutboxHandlers(message);
					}
					if (ApiClient.isOK(pb)) {
						List<UserInfo> userInfoList = pb.getUserInfosList();
						if (userInfoList != null && userInfoList.size() > 0) {
							for (UserInfo info : userInfoList) {
								for (ContactsObject contacts2 : contactsList) {
									if (contacts2 != null && info != null && contacts2.getPhone().equals(info.getPhone())) {
										boolean isRegist = info.getRegistStatus();
										contacts2.setType(isRegist ? 2 : 1);
										if (isRegist) {
											contacts2.setUserId(info.getUserId());
										}
									}
								}
							}
						}
					}
				}

				pb = GroupApiClient.getGroupInfo(userId, token, 1, 20);
				if (ApiClient.isOK(pb)) {
					List<UserInfo> userInfoList = pb.getUserInfosList();
					if (userInfoList != null && userInfoList.size() > 0) {
						for (UserInfo info : userInfoList) {
							for (ContactsObject contacts : contactsList) {
								if (contacts != null && info != null && contacts.getPhone().equals(info.getPhone())) {
									contacts.setUserId(info.getUserId());
									contacts.setType(3);
								}
							}
						}
					}
				}

				return contactsList;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_MATCH_PHONE_CONTACTS_LIST;
					if (null == result) {
						message.arg1 = 0;
						message.obj = null;
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

	private static List<ContactsObject> readContactAndStore() {
		List<ContactsObject> contactsList = new ArrayList<ContactsObject>();
		Cursor cursor = null;
		ContactDataBase cb = null;
		try {
			cb = new ContactDataBase(App.getInstance());
			cb.createTable();
			cursor = App.getInstance().getContentResolver()
					.query(ContactsContract.Data.CONTENT_URI, null, "mimetype='vnd.android.cursor.item/phone_v2'", null, null);
			while (cursor.moveToNext()) {
				int displayNameColumn = cursor.getColumnIndex("display_name");
				int phone = cursor.getColumnIndex("data1");
				String name = cursor.getString(displayNameColumn);
				String phonenum = cursor.getString(phone);
				if (phonenum.startsWith("+86")) {
					phonenum = phonenum.replace("+86", "").trim();
				}
				phonenum = phonenum.replace("-", "");
				phonenum = phonenum.replace(" ", "");
				phonenum = phonenum.trim();
				ContactsObject contacts = new ContactsObject();
				contacts.setName(name);
				contacts.setPhone(phonenum);
				contacts.setType(0);
				if (!cb.phoneIsExists(phonenum)) {
					contactsList.add(contacts);
					cb.insertRow(contacts);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			cb.close();
		}
		ContactsSyncService.mContactsReadingStatus = 2;
		return contactsList;
	}

	public static void userRegister(final String phone, final String password, final String authCode, final String image, final String nickname,
			final int gender, final String lng, final String lat, final String addrName, final String location) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {
			protected Object doInBackground() throws Exception {
				String tempLng = lng;
				String tempLat = lat;
				String tempImage = image;
				String tempAddrname = addrName;
				String tempLocation = location;
				// upload avatar

				// UploadRespObject uploadResp;
				// if (!TextUtils.isEmpty(image) && FileUtils.exists(image)) {
				// uploadResp =
				// HttpUploadHelper.upload(PPNetManager.getInstance().getUploadAvatarUrl(),
				// new File(image), false);
				// if (null != uploadResp && uploadResp.isOK() &&
				// !TextUtils.isEmpty(uploadResp.getFileUri())) {
				// tempImage = uploadResp.getFileUri();
				// FileUtils.renameFile(FileUtils.COVER +
				// MD5.encoderForString(uploadResp.getFileUrl()) +
				// FileUtils.JPG, image);
				// } else {
				// tempImage = null;
				// }
				// } else {
				// tempImage = null;
				// }

				if (TextUtils.isEmpty(tempLng))
					tempLng = "";
				if (TextUtils.isEmpty(tempLat)) {
					tempLat = "";
				}
				if (TextUtils.isEmpty(tempImage)) {
					tempImage = "";
				}
				if (TextUtils.isEmpty(tempLocation)) {
					tempLocation = "";
				}
				if (TextUtils.isEmpty(tempAddrname)) {
					tempAddrname = "";
				}
				Safe360Pb pb = UserApiClient.regist(phone, password, authCode, tempImage, nickname, gender, tempLng, tempLat, tempAddrname, tempLocation,
						PreferencesUtils.getBaiduChannelId(), PreferencesUtils.getBaiduUserId());
				if (ApiClient.isOK(pb)) {
					if (pb.getUserInfo() != null) {
						PreferencesUtils.setToken(pb.getUserInfo().getToken());
						DatabaseManager.getInstance().initHelper(pb.getUserInfo().getUserId());
					}
					CoreModel.getInstance().setLogined(true);
					PreferencesUtils.setLoginPhone(phone);
					PreferencesUtils.setLoginPwd(password);
					UserObject user = UserObject.createFromPb(pb.getUserInfo());
					if (user != null) {
						user.setPhone(phone);
						user.setPassword(password);
						CoreModel.getInstance().setUserInfo(user);

						if (needToUploadAvatar(image)) {
							uploadAvatar(user.getUserId(), user.getToken(), user.getGender(), user.getNickname(), tempImage);
						}
						Safe360Pb pb2 = UserApiClient.userInfo(user.getUserId(), user.getToken());
						if (ApiClient.isOK(pb2)) {
							UserObject.updateUserInfo(CoreModel.getInstance().getUserInfo(), pb2.getUserInfo());
						}
					}
					modifyLocation(0);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_REG_USER_ACCOUNT;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						CoreModel.getInstance().setLogined(false);
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						CoreModel.getInstance().setLogined(true);
						message.arg1 = 200;
						message.obj = CoreModel.getInstance().getUserInfo();
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void modifyLocation(final int userId, final String token, final String lng, final String lat, final String location,
			final String locationName, final int power, final int tripid) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				String tempLocation = location;
				if (TextUtils.isEmpty(tempLocation)) {
					tempLocation = "";
				}
				String addrName = locationName;
				if (TextUtils.isEmpty(tempLocation)) {
					addrName = "";
				}
				Safe360Pb pb = UserApiClient.modifyLocation(userId, token, lng, lat, addrName, tempLocation, power, tripid);
				if (ApiClient.isOK(pb)) {
					UserObject user = CoreModel.getInstance().getUserInfo();
					user.setLat(lat);
					user.setLng(lng);
					user.setLocation(location);
					user.setLocationChangeTime(DateUtils.getCurrDateString("yyyy-MM-dd HH:mm:ss"));
					CoreModel.getInstance().setUserInfo(user);
					// Safe360Pb pb2 = UserApiClient.userInfo(user.getUserId(),
					// user.getToken());
					// if (ApiClient.isOK(pb2)) {
					// UserObject.updateUserInfo(user, pb2.getUserInfo());
					// }
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_MODIFY_LOCATION;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {

						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						message.obj = result;
						CoreModel.getInstance().setFirstModifyLocation(true);
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void modifyLocation(int tripid) {
		UserObject userInfo = CoreModel.getInstance().getUserInfo();
		if (userInfo != null && !userInfo.getHideLocation() && userInfo.isLocationValid() && !TextUtils.isEmpty(userInfo.getLocation())) {
			modifyLocation(userInfo.getUserId(), userInfo.getToken(), userInfo.getLng(), userInfo.getLat(), userInfo.getLocation(), userInfo.getLocation2(),
					userInfo.getPower(), tripid);
		}
	}

	public static void userlogin(final String phone, final String password, final String lng, final String lat, final String addrname, final String location) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				String tempLng = lng;
				String tempLat = lat;
				String tempAddrname = addrname;
				String tempLocation = location;
				if (TextUtils.isEmpty(tempLng))
					tempLng = "";
				if (TextUtils.isEmpty(tempLat)) {
					tempLat = "";
				}
				if (TextUtils.isEmpty(tempLocation)) {
					tempLocation = "";
				}
				if (TextUtils.isEmpty(tempAddrname)) {
					tempAddrname = "";
				}
				Safe360Pb pb = UserApiClient.login(phone, password, tempLng, tempLat, tempAddrname, tempLocation, PreferencesUtils.getBaiduChannelId(),
						PreferencesUtils.getBaiduUserId(), PreferencesUtils.getToken());
				if (ApiClient.isOK(pb)) {
					if (pb.getUserInfo() != null) {
						PreferencesUtils.setToken(pb.getUserInfo().getToken());
						PreferencesUtils.setLastLoginUserId(pb.getUserInfo().getUserId());
					}
					UserObject user = UserObject.createFromPb(pb.getUserInfo());
					if (user != null) {
						user.setPassword(password);
						CoreModel.getInstance().setUserInfo(user);
						CoreModel.getInstance().setLogined(true);
						PreferencesUtils.setLoginPhone(phone);
						PreferencesUtils.setLoginPwd(password);

						DatabaseManager.getInstance().initHelper(user.getUserId());
					}

					loginImServer();
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_LOGIN;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						PreferencesUtils.setLastLoginUserId(-1);
						CoreModel.getInstance().setLogined(false);
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
						PreferencesUtils.setLoginPhone("");
						PreferencesUtils.setLoginPwd("");
					} else {
						message.arg1 = 200;
						message.obj = CoreModel.getInstance().getUserInfo();
						CoreModel.getInstance().setLogined(true);
						// 初始化通讯录数据库
						App.getInstance().getApplicationContext()
								.startService(new Intent(App.getInstance().getApplicationContext(), ContactsSyncService.class));
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void loginImServer() {
		CoreModel.getInstance().setImServerLogined(false);
		if (CoreModel.getInstance().getUserInfo() == null) {
			return;
		}

		// final String username = "rocksen";
		// final String password = "123456";
		final String username = CoreModel.getInstance().getUserInfo().getImUserName();
		final String password = CoreModel.getInstance().getUserInfo().getImPassword();
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			return;
		}
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(username, password, new EMCallBack() {

			@Override
			public void onSuccess() {
				// 登陆成功，保存用户名密码
				App.getInstance().setUserName(username);
				App.getInstance().setPassword(password);

				try {
					// ** 第一次登录或者之前logout后，加载所有本地群和回话
					// ** manually load all local groups and
					// conversations in case we are auto login
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();

					// 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
					EMGroupManager.getInstance().getGroupsFromServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(App.currentUserNick);

				CoreModel.getInstance().setImServerLogined(true);
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(final int code, final String message) {
				App.getInstance().logout(null);
				App.getInstance().setUserName("");
				App.getInstance().setPassword("");
				CoreModel.getInstance().setImServerLogined(false);
			}
		});
	}

	public static void getRegistAuthCode(final String phone) {

		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = UserApiClient.getAuthCode4Regist(phone);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_REG_AUTH_CODE;
					if (null == result) {
						message.arg1 = 0;
						message.obj = null;
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

	public static void verifyAuthCode(final String phone, final String authCode) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			protected Object doInBackground() throws Exception {
				Safe360Pb pb = UserApiClient.verifyAuthCode(phone, authCode);
				return pb;
			}

			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_VERIFY_AUTH_CODE;
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

	public static void checkUserRegist(final List<String> phoneList) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = UserApiClient.checkUserRegist(phoneList);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_CHECK_USER_REGISTER;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						List<UserInfo> userInfoList = pb.getUserInfosList();
						message.obj = userInfoList;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void setNewPwd(final String phone, final String password, final String authCode) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = UserApiClient.setNewPwd(phone, password, authCode);
				if (ApiClient.isOK(pb) && CoreModel.getInstance().getUserInfo() != null && !TextUtils.isEmpty(password)) {
					if (pb.getUserInfo() != null) {
						CoreModel.getInstance().getUserInfo().setToken(pb.getUserInfo().getToken());
						PreferencesUtils.setToken(pb.getUserInfo().getToken());
					}
					CoreModel.getInstance().getUserInfo().setPassword(password);
					PreferencesUtils.setLoginPwd(password);
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_SET_NEW_PASSWORD;
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

	public static boolean needToUploadAvatar(String image) {
		if (!TextUtils.isEmpty(image) && FileUtils.exists(image) && !image.startsWith(PPNetManager.HTTP)) {
			return true;
		} else {
			return false;
		}
	}

	public static void uploadAvatar(final int userId, final String token, final int gender, final String nickname, final String image) {

		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				// upload avatar
				if (!TextUtils.isEmpty(image) && FileUtils.exists(image) && !image.startsWith(PPNetManager.HTTP)) {
					try {
						// 空间名
						String bucket = "images0";
						// 表单密钥
						String formApiSecret = "ZupBgLG08aTy6Avodzig7q+zHGY=";

						String fileSuffix = null;
						int start = image.lastIndexOf(".");
						if (start != -1) {
							fileSuffix = image.substring(start + 1, image.length());
						}

						Calendar c = Calendar.getInstance();
						int year = c.get(Calendar.YEAR);
						int month = c.get(Calendar.MONTH) + 1;
						int day = c.get(Calendar.DAY_OF_MONTH);
						String strMonth = (month < 10) ? "0" + month : "" + month;

						// 保存到又拍云的路径
						final String savePath = "/" + year + strMonth + "/" + day + "/" + fileSuffix + "/up_" + MD5.encoderForString(System.nanoTime() + "")
								+ "." + fileSuffix;
						LogUtils.d(savePath);

						File localFile = new File(image);

						/*
						 * 设置进度条回掉函数
						 * 
						 * 注意：由于在计算发送的字节数中包含了图片以外的其他信息，最终上传的大小总是大于图片实际大小，
						 * 为了解决这个问题，代码会判断如果实际传送的大小大于图片
						 * ，就将实际传送的大小设置成'fileSize-1000'（最小为0）
						 */
						ProgressListener progressListener = new ProgressListener() {
							@Override
							public void transferred(long transferedBytes, long totalBytes) {
								// do something...
								LogUtils.d("trans:" + transferedBytes + "; total:" + totalBytes);
							}
						};

						CompleteListener completeListener = new CompleteListener() {
							@Override
							public void result(boolean isComplete, String result, String error) {
								// do something...
								LogUtils.d("isComplete:" + isComplete + ";result:" + result + ";error:" + error);
								if (TextUtils.isEmpty(error)) {
									modifyUserInfo(userId, token, gender, nickname, savePath);
								} else {
									YSToast.showToast(App.getInstance().getApplicationContext(),
											App.getInstance().getApplicationContext().getString(R.string.toast_avatar_upload_failed) + error);
								}
							}
						};

						UploaderManager uploaderManager = UploaderManager.getInstance(bucket);
						uploaderManager.setConnectTimeout(60);
						uploaderManager.setResponseTimeout(60);
						Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, savePath);
						// paramsMap.put("return_url", "");
						// signature & policy 建议从服务端获取
						String policyForInitial = UpYunUtils.getPolicy(paramsMap);
						String signatureForInitial = UpYunUtils.getSignature(paramsMap, formApiSecret);
						uploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, completeListener);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				return null;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
			}
		};
		asyncTask.execute();

	}

	public static void modifyUserInfo(final int userId, final String token, final int gender, final String nickname, final String image) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				String tempImage = image;

				Safe360Pb pb = UserApiClient.modifyUser(userId, token, gender, nickname, tempImage);
				if (ApiClient.isOK(pb)) {
					Safe360Pb pb2 = UserApiClient.userInfo(userId, token);
					if (ApiClient.isOK(pb2)) {
						if (CoreModel.getInstance().getUserInfo() != null && pb2.getUserInfo() != null) {
							if (!TextUtils.isEmpty(pb2.getUserInfo().getNickname())) {
								CoreModel.getInstance().getUserInfo().setNickname(pb2.getUserInfo().getNickname());
								CoreModel.getInstance().setmChangeUserName(true);
							}
							if (!TextUtils.isEmpty(pb2.getUserInfo().getImage())) {
								CoreModel.getInstance().getUserInfo().setImage(pb2.getUserInfo().getImage());
								CoreModel.getInstance().setmChangeUserImg(true);
							}
							CoreModel.getInstance().getUserInfo().setGender(pb2.getUserInfo().getGender());
						}
					}
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_MODIFY_USER_INFO;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						ResultObject resultObject = ResultObject.createByPb(pb);
						message.arg1 = 0;
						message.obj = resultObject;
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

	public static void modifyPwd(final int userId, final String token, final String oldPassword, final String newPassword) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = UserApiClient.modifyPwd(userId, token, oldPassword, newPassword);
				if (ApiClient.isOK(pb) && CoreModel.getInstance().getUserInfo() != null && !TextUtils.isEmpty(newPassword)) {
					if (pb.getUserInfo() != null) {
						CoreModel.getInstance().getUserInfo().setToken(pb.getUserInfo().getToken());
						PreferencesUtils.setToken(pb.getUserInfo().getToken());
					}
					CoreModel.getInstance().getUserInfo().setPassword(newPassword);
					PreferencesUtils.setLoginPwd(newPassword);
				}

				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_MODIFY_USER_PASSWORD;
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

	public static void logout(final int userId, final String token) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = UserApiClient.logout(userId, token);
				App.getInstance().logout(null);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_LOGOUT;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						PreferencesUtils.setLastLoginUserId(-1);
						CoreModel.getInstance().setLogined(false);
						message.arg1 = 200;
						message.obj = result;
						// PreferencesUtils.setLoginPhone("");
						PreferencesUtils.setLoginPwd("");
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void hideLocation(final int userId, final String token, final boolean hideLocation) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = UserApiClient.hideLocation(userId, token, hideLocation);
				if (ApiClient.isOK(pb)) {
					if (CoreModel.getInstance().getUserInfo() != null) {
						CoreModel.getInstance().getUserInfo().setHideLocation(hideLocation);
						PreferencesUtils.setHideLocation(CoreModel.getInstance().getUserInfo().getHideLocation());
					}
				}
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_HIDE_LOCATION;
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

	public static void getAuthCode4LostPwd(final String phone) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = UserApiClient.getAuthCode4LostPwd(phone);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_LOST_PASSWORD_AUTH_CODE;
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

	public static void sendSOS(final int userid, final String token, final String lng, final String lat, final String addrName, final String location) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				String tempLng = lng;
				String tempLat = lat;
				String tempAddrname = addrName;
				String tempLocation = location;
				if (TextUtils.isEmpty(tempLng))
					tempLng = "";
				if (TextUtils.isEmpty(tempLat)) {
					tempLat = "";
				}
				if (TextUtils.isEmpty(tempLocation)) {
					tempLocation = "";
				}
				if (TextUtils.isEmpty(tempAddrname)) {
					tempAddrname = "";
				}
				Safe360Pb pb = UserApiClient.sendSOS(userid, token, tempLng, tempLat, tempAddrname, tempLocation);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_SEND_SOS;
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

	public static void getuserLocation(final int userid) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = UserApiClient.getUserLocation(userid);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_USER_LOCATION;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						UserInfo info = pb.getUserInfo();
						UserObject obj = UserObject.createFromPb(info);
						message.obj = obj;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	public static void getuserInfo(final int userid, final String token) {
		TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				Safe360Pb pb = UserApiClient.userInfo(userid, token);
				return pb;
			}

			@Override
			protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
				Message message = App.getInstance().obtainMessage();
				if (null != message) {
					message.what = YSMSG.RESP_GET_USER_INFO;
					Safe360Pb pb = (Safe360Pb) result;
					if (!ApiClient.isOK(pb)) {
						message.arg1 = 0;
						message.obj = ResultObject.createByPb(pb);
					} else {
						message.arg1 = 200;
						UserInfo info = pb.getUserInfo();
						UserObject obj = UserObject.createFromPb(info);
						message.obj = obj;
					}
					CoreModel.getInstance().notifyOutboxHandlers(message);
				}
			}
		};
		asyncTask.execute();
	}

	@SuppressWarnings("unchecked")
	public static void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.REQ_MATCH_PHONE_CONTACTS_LIST: {
			if (msg.obj instanceof Activity) {
				UserObject userInfo = CoreModel.getInstance().getUserInfo();
				if (null != userInfo) {
					matchContactsList(userInfo.getUserId(), userInfo.getToken(), (Activity) msg.obj);
				}

			}
		}
			break;
		case YSMSG.REQ_REG_USER_ACCOUNT: {
			RegisterObject registerObj = (RegisterObject) msg.obj;
			if (null != registerObj) {
				userRegister(registerObj.getPhone(), registerObj.getPassword(), registerObj.getAuthCode(), registerObj.getImage(), registerObj.getNickname(),
						registerObj.getGender(), registerObj.getLng(), registerObj.getLat(), registerObj.getAddrname(), registerObj.getLocation());
			}
		}
			break;
		case YSMSG.REQ_LOGIN: {
			LoginObject loginObj = (LoginObject) msg.obj;
			if (null != loginObj) {
				userlogin(loginObj.getPhone(), loginObj.getPassword(), loginObj.getLng(), loginObj.getLat(), loginObj.getAddrname(), loginObj.getLocation());
			}
		}
			break;
		case YSMSG.REQ_GET_REG_AUTH_CODE: {
			RegisterObject registerObj = CoreModel.getInstance().getRegisterObject();
			if (null != registerObj) {
				getRegistAuthCode(registerObj.getPhone());
			}
		}
			break;
		case YSMSG.REQ_VERIFY_AUTH_CODE: {
			RegisterObject registerObj = CoreModel.getInstance().getRegisterObject();
			if (null != registerObj) {
				verifyAuthCode(registerObj.getPhone(), registerObj.getAuthCode());
			}
		}
			break;
		case YSMSG.REQ_CHECK_USER_REGISTER: {
			if (msg.obj instanceof ArrayList) {
				checkUserRegist((ArrayList<String>) msg.obj);
			}
		}
			break;
		case YSMSG.REQ_SET_NEW_PASSWORD: {
			RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
			if (regObject != null) {
				setNewPwd(regObject.getPhone(), regObject.getPassword(), regObject.getAuthCode());
			}
		}
			break;
		case YSMSG.REQ_MODIFY_USER_INFO: {
			UserObject userInfo = (UserObject) msg.obj;
			if (userInfo != null) {
				if (needToUploadAvatar(userInfo.getImage())) {
					uploadAvatar(userInfo.getUserId(), userInfo.getToken(), userInfo.getGender(), userInfo.getNickname(), userInfo.getImage());
				} else {
					modifyUserInfo(userInfo.getUserId(), userInfo.getToken(), userInfo.getGender(), userInfo.getNickname(), userInfo.getUploadImage());
				}
			}
		}
			break;
		case YSMSG.REQ_MODIFY_USER_PASSWORD: {
			ModifyPwdObject password = (ModifyPwdObject) msg.obj;
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (password != null && userInfo != null) {
				modifyPwd(userInfo.getUserId(), userInfo.getToken(), password.getOldPassword(), password.getNewPassword());
			}
		}
			break;
		case YSMSG.REQ_LOGOUT: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null) {
				logout(userInfo.getUserId(), userInfo.getToken());
			}
		}
			break;
		case YSMSG.REQ_HIDE_LOCATION: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (msg.obj instanceof Boolean && userInfo != null) {
				hideLocation(userInfo.getUserId(), userInfo.getToken(), (Boolean) msg.obj);
			}
		}
			break;
		case YSMSG.REQ_GET_LOST_PASSWORD_AUTH_CODE: {
			RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
			if (regObject != null) {
				getAuthCode4LostPwd(regObject.getPhone());
			}
		}
			break;
		case YSMSG.REQ_MODIFY_LOCATION: {
			modifyLocation(msg.arg2);
		}
			break;
		case YSMSG.REQ_GET_USER_LOCATION: {
			if (msg.arg1 != 0) {
				getuserLocation(msg.arg1);
			}
		}
			break;
		case YSMSG.REQ_SEND_SOS: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			UserObject sosObj = (UserObject) msg.obj;
			if (userInfo != null && sosObj != null) {
				sendSOS(userInfo.getUserId(), userInfo.getToken(), sosObj.getLng(), sosObj.getLat(), sosObj.getLocation3(), sosObj.getLocation());
			}
		}
			break;
		case YSMSG.REQ_GET_USER_INFO: {
			UserObject userInfo = CoreModel.getInstance().getUserInfo();
			if (userInfo != null && msg.arg2 != 0) {
				getuserInfo(msg.arg2, userInfo.getToken());
			}
		}
			break;
		}
	}
}
