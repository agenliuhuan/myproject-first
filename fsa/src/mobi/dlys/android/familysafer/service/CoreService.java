package mobi.dlys.android.familysafer.service;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.api.ApiClient;
import mobi.dlys.android.familysafer.api.FriendApiClient;
import mobi.dlys.android.familysafer.api.PPNetManager;
import mobi.dlys.android.familysafer.api.SysApiClient;
import mobi.dlys.android.familysafer.api.UserApiClient;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.db.DatabaseManager;
import mobi.dlys.android.familysafer.db.dao.FriendObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Friend;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.HostInfo;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.HostType;
import mobi.dlys.android.familysafer.ui.login.LoginHelper;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

public class CoreService extends Service {
	private static final String TAG = "CoreService";
	private static int DEFAULT_TIME_OUT = 5 * 1000;
	private boolean mLogining = false;
	/*
	 * aidl接口
	 */
	CoreInterface.Stub mStub = new CoreInterface.Stub() {
		/*
		 * auto login
		 */
		public void IAutoLogin() throws RemoteException {
			mLogining = true;

			TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

				@Override
				protected Object doInBackground() throws Exception {
					// get server dynamic ip
					PPNetManager.getInstance().setConnectTimeOut(DEFAULT_TIME_OUT);
					PPNetManager.getInstance().setSoTimeOut(DEFAULT_TIME_OUT);
					int versionCode = AndroidConfig.getVersionCode();
					String imei = AndroidConfig.getIMEI();

					FamilySaferPb pb = SysApiClient.getServerDynamicIp(imei, versionCode);
					if (ApiClient.isOK(pb) && pb.getEnvironment() != null) {
						LogUtils.e(TAG, "getServerDynamicIp");
						List<HostInfo> hostInfoList = pb.getEnvironment().getHostInfosList();
						if (hostInfoList != null && hostInfoList.size() > 0) {
							HostInfo hostInfo = null;
							for (int i = 0; i < hostInfoList.size(); i++) {
								hostInfo = hostInfoList.get(i);
								if (hostInfo != null) {
									if (hostInfo.getHostType() == HostType.API) {
										PPNetManager.getInstance().setIp(hostInfo.getDomain());
										PPNetManager.getInstance().setPort(hostInfo.getPort());
									} else if (hostInfo.getHostType() == HostType.RES && !TextUtils.isEmpty(hostInfo.getDomain())) {
										PPNetManager.getInstance().setUploadIpPort(hostInfo.getDomain() + ":" + hostInfo.getPort());
									}
								}
							}
						}
					}

					// login
					PPNetManager.getInstance().resetTimeOut();
					String phone = PreferencesUtils.getLoginPhone();
					String pwd = PreferencesUtils.getLoginPwd();
					if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
						LogUtils.e(TAG, "auto login");
						// 自动登录

						String tempLng = "";
						String tempLat = "";
						String tempLocation = "";

						pb = UserApiClient.login(phone, pwd, tempLng, tempLat, tempLocation);
						if (ApiClient.isOK(pb)) {
							DatabaseManager.getInstance().initHelper(pb.getUserInfo().getUserId());
							CoreModel.getInstance().setLogined(true);
							UserObject.setUserInfo(CoreModel.getInstance().getUserInfo(), pb.getUserInfo());
							UserObject user = CoreModel.getInstance().getUserInfo();
							if (user != null) {
								user.setPhone(phone);
								user.setPassword(pwd);
								PreferencesUtils.setLoginPhone(user.getPhone());
								PreferencesUtils.setLoginPwd(user.getPassword());

								// request the first page friend
								int pageNo = 1;
								int pageSize = PageInfoObjectDao.FRIEND_PAGE_SIZE;
								FamilySaferPb pb2 = FriendApiClient.friendList(user.getUserId(), user.getToken(), pageNo, pageSize);
								if (ApiClient.isOK(pb2)) {
									FriendObjectDao friendDao = new FriendObjectDao();
									if (pageNo == 1) {
										friendDao.clear();
									}
									List<FriendObject> list = new ArrayList<FriendObject>();
									for (Friend friend : pb2.getFrdsList()) {
										list.add(FriendObject.createFromPb(friend));
									}
									try {
										friendDao.updateAllinBatchOperation(list);
									} catch (Exception e) {
									}

									PageInfoObject pageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_FRIEND);
									PageInfoObject.updatePageInfo(pageInfo, pb.getPageInfo());
									PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_FRIEND, pageInfo);
								}

							}

							// LogUtils.e(TAG, "enter MainActivity");
							// Intent intent = new Intent(getBaseContext(),
							// MainActivity.class);
							// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							// intent.putExtra("movetasktoback", true);
							// getApplication().startActivity(intent);

							Message message = App.getInstance().obtainMessage();
							if (null != message) {
								message.what = YSMSG.RESP_LOGIN;
								message.arg1 = 200;
								message.obj = CoreModel.getInstance().getUserInfo();
								CoreModel.getInstance().notifyOutboxHandlers(message);
							}

						} else {
							CoreModel.getInstance().setLogined(false);
							Message message = App.getInstance().obtainMessage();
							if (null != message) {
								message.what = YSMSG.RESP_LOGIN;
								message.arg1 = 0;
								message.obj = ResultObject.createByPb(pb);
								PreferencesUtils.setLoginPhone("");
								PreferencesUtils.setLoginPwd("");
								CoreModel.getInstance().notifyOutboxHandlers(message);
							}
						}
					} else {
						BaseController.getInstance().notifyOutboxHandlers(YSMSG.RESP_GET_SERVER_DYNAMIC_IP);
					}

					mLogining = false;
					return null;
				}

				@Override
				protected void onPostExecuteSafely(Object result, Exception e) throws Exception {

				}
			};
			asyncTask.execute();

		}

		public boolean IIsLogin() throws RemoteException {
			return CoreModel.getInstance().isLogined();
		}
	};

	/*
	 * 邦定
	 */
	public IBinder onBind(Intent arg0) {
		return mStub;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mLogining = false;
		CoreModel.getInstance().release();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.e(TAG, "onStartCommand");
		if (!CoreModel.getInstance().isLogined() && !mLogining) {
			LogUtils.e(TAG, "autoLogin");
			LoginHelper.autoLogin(mStub);
		}
		return super.onStartCommand(intent, flags, startId);
	}

}
