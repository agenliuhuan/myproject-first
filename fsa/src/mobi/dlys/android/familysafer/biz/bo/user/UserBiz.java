package mobi.dlys.android.familysafer.biz.bo.user;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.async.TinyAsyncTask;
import mobi.dlys.android.core.mvc.BaseBiz;
import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.api.ApiClient;
import mobi.dlys.android.familysafer.api.FriendApiClient;
import mobi.dlys.android.familysafer.api.PPNetManager;
import mobi.dlys.android.familysafer.api.UserApiClient;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ContactsObject;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.LoginObject;
import mobi.dlys.android.familysafer.biz.vo.ModifyPwdObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.RegisterObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.biz.vo.upload.UploadRespObject;
import mobi.dlys.android.familysafer.db.DatabaseManager;
import mobi.dlys.android.familysafer.db.dao.FriendObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Friend;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;
import mobi.dlys.android.familysafer.service.ReadContactsService;
import mobi.dlys.android.familysafer.ui.comm.ContactDataBase;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.HttpUploadHelper;
import mobi.dlys.android.familysafer.utils.MD5;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import android.app.Activity;
import android.database.Cursor;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class UserBiz extends BaseBiz {
    private static final String TAG = UserBiz.class.getSimpleName();

    public static void regist(final String phone, final String password, final String authCode, final String image, final String nickname, final String lng, final String lat, final String location) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                String tempLng = lng;
                String tempLat = lat;
                String tempImage = image;
                String tempLocation = location;

                // upload avatar
                UploadRespObject uploadResp;
                if (!TextUtils.isEmpty(image) && FileUtils.exists(image)) {
                    uploadResp = HttpUploadHelper.upload(PPNetManager.getInstance().getUploadAvatarUrl(), new File(image), false);
                    if (null != uploadResp && uploadResp.isOK() && !TextUtils.isEmpty(uploadResp.getFileUri())) {
                        tempImage = uploadResp.getFileUri();
                        FileUtils.renameFile(FileUtils.COVER + MD5.encoderForString(uploadResp.getFileUrl()) + FileUtils.JPG, image);
                    } else {
                        tempImage = null;
                    }
                } else {
                    tempImage = null;
                }

                if (TextUtils.isEmpty(tempLng))
                    tempLng = "";
                if (TextUtils.isEmpty(tempLat)) {
                    tempLat = "";
                }
                if (TextUtils.isEmpty(tempImage)) {
                    tempImage = "";
                }

                if (TextUtils.isEmpty(location)) {
                    tempLocation = "";
                }

                FamilySaferPb pb = UserApiClient.registCommon(phone, password, authCode, tempImage, nickname, tempLng, tempLat, tempLocation);
                if (ApiClient.isOK(pb)) {
                    if (pb.getUserInfo() != null) {
                        DatabaseManager.getInstance().initHelper(pb.getUserInfo().getUserId());
                    }
                    CoreModel.getInstance().setLogined(true);
                    PreferencesUtils.setLoginPhone(phone);
                    PreferencesUtils.setLoginPwd(password);
                    UserObject.setUserInfo(CoreModel.getInstance().getUserInfo(), pb.getUserInfo());
                    UserObject user = CoreModel.getInstance().getUserInfo();
                    if (user != null) {
                        user.setPhone(phone);
                        user.setPassword(password);
                        FamilySaferPb pb2 = UserApiClient.userInfo(user.getUserId(), user.getToken());
                        if (ApiClient.isOK(pb2)) {
                            UserObject.updateUserInfo(user, pb2.getUserInfo());
                        }

                    }
                    modifyLocation();
                }
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_REG_USER_ACCOUNT;
                    FamilySaferPb pb = (FamilySaferPb) result;
                    if (!ApiClient.isOK(pb)) {
                        CoreModel.getInstance().setLogined(false);
                        message.arg1 = 0;
                        message.obj = ResultObject.createByPb(pb);
                    } else {
                        message.arg1 = 200;
                        message.obj = CoreModel.getInstance().getUserInfo();
                    }
                    CoreModel.getInstance().notifyOutboxHandlers(message);
                }
            }
        };
        asyncTask.execute();
    }

    public static void login(final String phone, final String password, final String lng, final String lat, final String location) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                String tempLng = lng;
                String tempLat = lat;
                String tempLocation = location;

                if (TextUtils.isEmpty(tempLng))
                    tempLng = "";

                if (TextUtils.isEmpty(tempLat)) {
                    tempLat = "";
                }

                if (TextUtils.isEmpty(tempLocation)) {
                    tempLocation = "";
                }

                FamilySaferPb pb = UserApiClient.login(phone, password, tempLng, tempLat, tempLocation);
                if (ApiClient.isOK(pb)) {
                    DatabaseManager.getInstance().initHelper(pb.getUserInfo().getUserId());
                    CoreModel.getInstance().setLogined(true);
                    UserObject.setUserInfo(CoreModel.getInstance().getUserInfo(), pb.getUserInfo());
                    UserObject user = CoreModel.getInstance().getUserInfo();
                    if (user != null) {
                        user.setPhone(phone);
                        user.setPassword(password);
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
                            friendDao.updateAllinBatchOperation(list);

                            PageInfoObject pageInfo = PageInfoObjectDao.getPageInfo(PageInfoObjectDao.ID_FRIEND);
                            PageInfoObject.updatePageInfo(pageInfo, pb.getPageInfo());
                            PageInfoObjectDao.setPageInfo(PageInfoObjectDao.ID_FRIEND, pageInfo);
                        }

                    }
                    modifyLocation();
                }
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_LOGIN;
                    FamilySaferPb pb = (FamilySaferPb) result;
                    if (!ApiClient.isOK(pb)) {
                        CoreModel.getInstance().setLogined(false);
                        message.arg1 = 0;
                        message.obj = ResultObject.createByPb(pb);
                        PreferencesUtils.setLoginPhone("");
                        PreferencesUtils.setLoginPwd("");
                    } else {
                        message.arg1 = 200;
                        message.obj = CoreModel.getInstance().getUserInfo();
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
                FamilySaferPb pb = UserApiClient.logout(userId, token);
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_LOGOUT;
                    FamilySaferPb pb = (FamilySaferPb) result;
                    if (!ApiClient.isOK(pb)) {
                        message.arg1 = 0;
                        message.obj = ResultObject.createByPb(pb);
                    } else {
                        CoreModel.getInstance().setLogined(false);
                        message.arg1 = 200;
                        message.obj = result;
                        PreferencesUtils.setLoginPhone("");
                        PreferencesUtils.setLoginPwd("");
                    }
                    CoreModel.getInstance().notifyOutboxHandlers(message);
                }
            }
        };
        asyncTask.execute();
    }

    public static void getAuthCode4Regist(final String phone) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.getAuthCode4Regist(phone);
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_GET_REG_AUTH_CODE;
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

    public static void getAuthCode4LostPwd(final String phone) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.getAuthCode4LostPwd(phone);
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_GET_LOST_PASSWORD_AUTH_CODE;
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

    public static void getAuthCode4ModifyPhone(final String phone) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.getAuthCode4ModifyPhone(phone);
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_GET_MODIFY_PHONE_AUTH_CODE;
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

    public static void verifyAuthCode(final String phone, final String authCode) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.verifyAuthCode(phone, authCode);
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_VERIFY_AUTH_CODE;
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

    public static void setNewPwd(final String phone, final String password, final String authCode) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.setNewPwd(phone, password, authCode);
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_SET_NEW_PASSWORD;
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

    public static void modifyPwd(final int userId, final String token, final String oldPassword, final String newPassword) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.modifyPwd(userId, token, oldPassword, newPassword);
                if (ApiClient.isOK(pb) && CoreModel.getInstance().getUserInfo() != null && !TextUtils.isEmpty(newPassword)) {
                    if (pb.getUserInfo() != null) {
                        CoreModel.getInstance().getUserInfo().setToken(pb.getUserInfo().getToken());
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

    public static void getUserInfo(final int userId, final String token) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.userInfo(userId, token);
                if (ApiClient.isOK(pb)) {
                    if (userId == CoreModel.getInstance().getUserInfo().getUserId()) {
                        UserObject.updateUserInfo(CoreModel.getInstance().getUserInfo(), pb.getUserInfo());
                        PreferencesUtils.setHideLocation(CoreModel.getInstance().getUserInfo().getHideLocation());

                    } else {
                        new FriendObjectDao().refresh(FriendObject.createFromPb(pb.getUserInfo()));
                    }
                }
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_GET_USER_INFO;
                    FamilySaferPb pb = (FamilySaferPb) result;
                    if (!ApiClient.isOK(pb)) {
                        message.arg1 = 0;
                        message.obj = ResultObject.createByPb(pb);
                    } else {
                        message.arg1 = 200;
                        message.obj = UserObject.createFromPb(pb.getUserInfo());
                    }
                    CoreModel.getInstance().notifyOutboxHandlers(message);
                }
            }
        };
        asyncTask.execute();
    }

    public static void modifyUserInfo(final int userId, final String token, final String nickname, final String image) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                String tempImage = image;
                // upload avatar
                UploadRespObject uploadResp;
                if (!TextUtils.isEmpty(image) && FileUtils.exists(image) && !image.startsWith(PPNetManager.HTTP)) {
                    uploadResp = HttpUploadHelper.upload(PPNetManager.getInstance().getUploadAvatarUrl(), new File(image), false);
                    if (null != uploadResp && uploadResp.isOK() && !TextUtils.isEmpty(uploadResp.getFileUri())) {
                        tempImage = uploadResp.getFileUri();
                        FileUtils.renameFile(FileUtils.COVER + MD5.encoderForString(uploadResp.getFileUrl()) + FileUtils.JPG, image);
                        if (CoreModel.getInstance().getUserInfo() != null) {
                            CoreModel.getInstance().getUserInfo().setImage(uploadResp.getFileUrl());
                        }
                    } else {
                        return null;
                    }
                }

                FamilySaferPb pb = UserApiClient.modifyUser(userId, token, nickname, tempImage);
                if (ApiClient.isOK(pb)) {
                    if (CoreModel.getInstance().getUserInfo() != null) {
                        if (!TextUtils.isEmpty(nickname)) {
                            CoreModel.getInstance().getUserInfo().setNickname(nickname);
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
                    FamilySaferPb pb = (FamilySaferPb) result;
                    if (!ApiClient.isOK(pb)) {
                        ResultObject resultObject = ResultObject.createByPb(pb);
                        if (resultObject != null && null == pb) {
                            resultObject.setErrorMsg(App.getInstance().getResources().getString(R.string.toast_avatar_upload_failed));
                        }
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

    public static void verifyPwd4ModifyPhone(final int userId, final String token, final String password) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.verifyPwd4ModifyPhone(userId, token, password);
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_VERIFY_PWD_FOR_MODIFY_PHONE;
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

    public static void modifyPhone(final int userId, final String token, final String password, final String phone, final String authCode) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.modifyPhone(userId, token, password, phone, authCode);
                if (ApiClient.isOK(pb)) {
                    if (CoreModel.getInstance().getUserInfo() != null && !TextUtils.isEmpty(phone)) {
                        CoreModel.getInstance().getUserInfo().setPhone(phone);
                        CoreModel.getInstance().getUserInfo().setPassword(password);
                        PreferencesUtils.setLoginPhone(phone);
                        PreferencesUtils.setLoginPwd(password);
                        if (pb.getUserInfo() != null) {
                            CoreModel.getInstance().getUserInfo().setToken(pb.getUserInfo().getToken());
                        }
                    }
                }
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_MODIFY_PHONE;
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

    public static void modifyLocation(final int userId, final String token, final String lng, final String lat, final String location) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                String tempLocation = location;
                if (TextUtils.isEmpty(tempLocation)) {
                    tempLocation = "";
                }
                FamilySaferPb pb = UserApiClient.modifyLocation(userId, token, lng, lat, tempLocation);
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_MODIFY_LOCATION;
                    FamilySaferPb pb = (FamilySaferPb) result;
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

    public static void checkUserRegist(final List<String> phoneList) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.checkUserRegist(phoneList);
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_CHECK_USER_REGISTER;
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

    public static void readContactsList(final Activity activity) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                List<ContactsObject> contactsList = null;
                ContactDataBase cb = null;

                try {
                    cb = new ContactDataBase(activity);

                    contactsList = cb.query();
                    LogUtils.i("constantssize", contactsList.size() + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null == contactsList) {
                    return null;
                }

                if (contactsList.size() > 0) {
                    return contactsList;
                } else {
                    return null;
                }
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_READ_PHONE_CONTACTS_LIST;
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

    public static void matchContactsList(final boolean fromRegister, final int userId, final String token, final Activity activity) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                List<ContactsObject> contactsList = null;
                ContactDataBase cb = null;
                LogUtils.i("constantssize", "matchContactsList querystart");
                try {
                    cb = new ContactDataBase(activity);
                    contactsList = cb.query();
                    if (contactsList.size() == 0) {
                        contactsList = readContactAndStore();
                    }
                    LogUtils.i("constantssize", "matchContactsList queryend:" + contactsList.size());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cb.close();
                }

                if (null == contactsList) {
                    return null;
                }

                if (contactsList.size() > 0) {
                    ArrayList<String> phoneList = new ArrayList<String>();
                    for (int i = 0; i < contactsList.size(); i++) {
                        ContactsObject contacts = contactsList.get(i);
                        if (contacts != null) {
                            phoneList.add(contacts.getPhone());
                        }
                    }
                    if (fromRegister) {
                        FamilySaferPb pb = UserApiClient.checkUserRegist(phoneList);
                        if (ApiClient.isOK(pb)) {
                            List<UserInfo> userInfoList = pb.getUserInfosList();
                            if (userInfoList != null && userInfoList.size() > 0) {
                                for (UserInfo info : userInfoList) {
                                    for (ContactsObject contacts : contactsList) {
                                        if (contacts != null && info != null && contacts.getPhone().equals(info.getPhone())) {
                                            contacts.setUserId(info.getUserId());
                                            if (!contacts.isFriend()) {
                                                contacts.setType((info.getRegistStatus()) ? 2 : 1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        FamilySaferPb pb = UserApiClient.checkUserRelation(userId, token, phoneList);
                        if (ApiClient.isOK(pb)) {
                            List<UserInfo> userInfoList = pb.getUserInfosList();
                            if (userInfoList != null && userInfoList.size() > 0) {
                                for (UserInfo info : userInfoList) {
                                    for (ContactsObject contacts : contactsList) {
                                        if (contacts != null && info != null && contacts.getPhone().equals(info.getPhone())) {
                                            contacts.setUserId(info.getUserId());
                                            contacts.setType(info.getRelation().getNumber());
                                        }
                                    }
                                }
                            }
                        }

                    }
                    LogUtils.i("constantssize", "matchContactsList sortend:" + contactsList.size());
                    return contactsList;
                } else {
                    return null;
                }
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
            cursor = App.getInstance().getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, "mimetype='vnd.android.cursor.item/phone_v2'", null, null);
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
        ReadContactsService.mContactsReadingStatus = 2;
        return contactsList;
    }

    public static void hideLocation(final int userId, final String token, final boolean hideLocation) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.hideLocation(userId, token, hideLocation);
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

    public static void autoLogin(final String phone, final String password, final String lng, final String lat, final String location) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                String tempLng = lng;
                String tempLat = lat;
                String tempLocation = location;

                if (TextUtils.isEmpty(tempLng))
                    tempLng = "";

                if (TextUtils.isEmpty(tempLat)) {
                    tempLat = "";
                }

                if (TextUtils.isEmpty(tempLocation)) {
                    tempLocation = "";
                }

                FamilySaferPb pb = UserApiClient.login(phone, password, tempLng, tempLat, tempLocation);
                if (ApiClient.isOK(pb)) {
                    CoreModel.getInstance().setLogined(true);
                    UserObject.setUserInfo(CoreModel.getInstance().getUserInfo(), pb.getUserInfo());
                    UserObject user = CoreModel.getInstance().getUserInfo();
                    if (user != null) {
                        user.setPhone(phone);
                        user.setPassword(password);
                        PreferencesUtils.setLoginPhone(user.getPhone());
                        PreferencesUtils.setLoginPwd(user.getPassword());
                        FamilySaferPb pb2 = UserApiClient.userInfo(user.getUserId(), user.getToken());
                        if (ApiClient.isOK(pb2)) {
                            UserObject.updateUserInfo(user, pb2.getUserInfo());
                        }
                    }
                }
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_AUTO_LOGIN;
                    FamilySaferPb pb = (FamilySaferPb) result;
                    if (!ApiClient.isOK(pb)) {
                        LogUtils.e(TAG, "自动登录失败");
                        message.arg1 = 0;
                        message.obj = ResultObject.createByPb(pb);
                    } else {
                        LogUtils.e(TAG, "自动登录成功");
                        message.arg1 = 200;
                        message.obj = CoreModel.getInstance().getUserInfo();
                    }
                    CoreModel.getInstance().notifyOutboxHandlers(message);
                }
            }
        };
        asyncTask.execute();
    }

    public static void checkUserRelation(final int userId, final String token, final List<String> phoneList) {
        TinyAsyncTask<Object> asyncTask = new TinyAsyncTask<Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                FamilySaferPb pb = UserApiClient.checkUserRelation(userId, token, phoneList);
                return pb;
            }

            @Override
            protected void onPostExecuteSafely(Object result, Exception e) throws Exception {
                Message message = App.getInstance().obtainMessage();
                if (null != message) {
                    message.what = YSMSG.RESP_CHECK_USER_RELATION;
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

    public static void modifyLocation() {
        UserObject userInfo = CoreModel.getInstance().getUserInfo();
        if (userInfo != null && !PreferencesUtils.isHideLocation() && userInfo.isLocationValid()) {
            modifyLocation(userInfo.getUserId(), userInfo.getToken(), userInfo.getLng(), userInfo.getLat(), userInfo.getLocation());
        }
    }

    @SuppressWarnings("unchecked")
    public static void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.REQ_GET_REG_AUTH_CODE: {
            RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
            if (regObject != null) {
                getAuthCode4Regist(regObject.getPhone());
            }
        }
            break;
        case YSMSG.REQ_REG_USER_ACCOUNT: {
            RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
            if (regObject != null) {
                regist(regObject.getPhone(), regObject.getPassword(), regObject.getAuthCode(), regObject.getImage(), regObject.getNickname(), regObject.getLng(), regObject.getLat(), regObject.getLocation());
            }
        }
            break;
        case YSMSG.REQ_LOGIN: {
            LoginObject loginObject = CoreModel.getInstance().getLoginObject();
            if (loginObject != null) {
                login(loginObject.getPhone(), loginObject.getPassword(), loginObject.getLng(), loginObject.getLat(), loginObject.getLocation());
            }
        }
            break;
        case YSMSG.REQ_LOGOUT: {
            UserObject userInfo = CoreModel.getInstance().getUserInfo();
            if (userInfo != null) {
                logout(userInfo.getUserId(), userInfo.getToken());
            } else {

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
        case YSMSG.REQ_GET_MODIFY_PHONE_AUTH_CODE: {
            RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
            if (regObject != null) {
                getAuthCode4ModifyPhone(regObject.getPhone());
            }
        }
            break;
        case YSMSG.REQ_VERIFY_AUTH_CODE: {
            RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
            if (regObject != null) {
                verifyAuthCode(regObject.getPhone(), regObject.getAuthCode());
            }
        }
            break;
        case YSMSG.REQ_SET_NEW_PASSWORD: {
            RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
            if (regObject != null) {
                setNewPwd(regObject.getPhone(), (String) msg.obj, regObject.getAuthCode());
            } else {

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
        case YSMSG.REQ_GET_USER_INFO: {
            UserObject userInfo = CoreModel.getInstance().getUserInfo();
            if (userInfo != null) {
                if (msg.arg1 != 0) {
                    getUserInfo(msg.arg1, userInfo.getToken());
                } else {
                    getUserInfo(userInfo.getUserId(), userInfo.getToken());
                }
            } else {

            }
        }
            break;
        case YSMSG.REQ_MODIFY_USER_INFO: {
            UserObject userInfo = (UserObject) msg.obj;
            if (userInfo != null) {
                modifyUserInfo(userInfo.getUserId(), userInfo.getToken(), userInfo.getNickname(), userInfo.getImage());
            }
        }
            break;
        case YSMSG.REQ_VERIFY_PWD_FOR_MODIFY_PHONE: {
            RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
            UserObject userInfo = CoreModel.getInstance().getUserInfo();
            if (regObject != null && userInfo != null) {
                verifyPwd4ModifyPhone(userInfo.getUserId(), userInfo.getToken(), regObject.getPassword());
            }
        }
            break;
        case YSMSG.REQ_MODIFY_PHONE: {
            UserObject userInfo = CoreModel.getInstance().getUserInfo();
            RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
            if (userInfo != null && regObject != null) {
                modifyPhone(userInfo.getUserId(), userInfo.getToken(), regObject.getPassword(), regObject.getPhone(), regObject.getAuthCode());
            }
        }
            break;
        case YSMSG.REQ_MODIFY_LOCATION: {
            UserObject userInfo = CoreModel.getInstance().getUserInfo();
            if (userInfo != null && !PreferencesUtils.isHideLocation() && userInfo.isLocationValid()) {
                modifyLocation(userInfo.getUserId(), userInfo.getToken(), userInfo.getLng(), userInfo.getLat(), userInfo.getLocation());
            }
        }
            break;
        case YSMSG.REQ_CHECK_USER_REGISTER: {
            if (msg.obj instanceof ArrayList) {
                checkUserRegist((ArrayList<String>) msg.obj);
            }
        }
            break;
        case YSMSG.REQ_READ_PHONE_CONTACTS_LIST: {
            if (msg.obj instanceof Activity) {
                readContactsList((Activity) msg.obj);
            }
        }
            break;
        case YSMSG.REQ_MATCH_PHONE_CONTACTS_LIST: {
            if (msg.obj instanceof Activity) {
                if (msg.arg1 == 100) { // 注册流程的联系人匹配
                    matchContactsList(true, 0, "", (Activity) msg.obj);
                } else if (msg.arg1 == 200) { // 添加好友流程的联系人匹配
                    if (CoreModel.getInstance().getUserInfo() != null)
                        matchContactsList(false, CoreModel.getInstance().getUserInfo().getUserId(), CoreModel.getInstance().getUserInfo().getToken(), (Activity) msg.obj);
                }
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
        case YSMSG.REQ_AUTO_LOGIN: {
            LoginObject loginObject = CoreModel.getInstance().getLoginObject();
            if (loginObject != null) {
                autoLogin(loginObject.getPhone(), loginObject.getPassword(), loginObject.getLng(), loginObject.getLat(), loginObject.getLocation());
            }
        }
            break;
        case YSMSG.REQ_CHECK_USER_RELATION: {
            List<String> phoneList = (List<String>) msg.obj;
            UserObject userInfo = CoreModel.getInstance().getUserInfo();
            if (userInfo != null && phoneList != null) {
                checkUserRelation(userInfo.getUserId(), userInfo.getToken(), phoneList);
            }
        }
            break;
        }
    }
}
