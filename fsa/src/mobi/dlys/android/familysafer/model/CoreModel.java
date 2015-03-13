package mobi.dlys.android.familysafer.model;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.mvc.BaseModel;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.bo.checkin.CheckinBiz;
import mobi.dlys.android.familysafer.biz.bo.clue.ClueBiz;
import mobi.dlys.android.familysafer.biz.bo.friend.FriendBiz;
import mobi.dlys.android.familysafer.biz.bo.im.ImBiz;
import mobi.dlys.android.familysafer.biz.bo.location.LocationBiz;
import mobi.dlys.android.familysafer.biz.bo.sos.SOSBiz;
import mobi.dlys.android.familysafer.biz.bo.sys.SysBiz;
import mobi.dlys.android.familysafer.biz.bo.user.UserBiz;
import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.biz.vo.ContactsObject;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.FriendRequestObject;
import mobi.dlys.android.familysafer.biz.vo.LoginObject;
import mobi.dlys.android.familysafer.biz.vo.RegisterObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.db.dao.ClueObjectDao;
import mobi.dlys.android.familysafer.db.dao.EventObjectDao;
import mobi.dlys.android.familysafer.db.dao.FriendObjectDao;
import mobi.dlys.android.familysafer.db.dao.FriendRequestObjectDao;
import mobi.dlys.android.familysafer.ui.comm.ContactDataBase;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import android.os.Message;

public class CoreModel extends BaseModel {
	private static final String TAG = CoreModel.class.getSimpleName();

	public static final int PAGE_SIZE = 200;

	private static CoreModel mCoreModel = null;

	private RegisterObject mRegisterObject = null;
	private LoginObject mLoginObject = null;

	private UserObject mUserInfo = null;

	private int mFriendReqCount = 0;
	private int mEventCount = 0;
	private int mMsgCount = 0;

	private boolean mIsLogined = false;
	private boolean mIsVersionChecked = false;
	private boolean mUpdateClueList = false;
	private boolean mUpdateEventList = false;
	private boolean mUpdateFriendList = false;
	private boolean mUpdateFriendRequestList = false;
	private boolean mUpdateMsgList = false;
	private boolean mUpdateMyClueList = false;
	private boolean mChangeUserImg = false;
	
	private String CheckinMessage;

	public String getCheckinMessage() {
        return CheckinMessage;
    }

    public void setCheckinMessage(String checkinMessage) {
        CheckinMessage = checkinMessage;
    }

    public boolean ismChangeUserImg() {
		return mChangeUserImg;
	}

	public void setmChangeUserImg(boolean mChangeUserImg) {
		this.mChangeUserImg = mChangeUserImg;
	}

	public boolean ismUpdateMyClueList() {
		return mUpdateMyClueList;
	}

	public void setmUpdateMyClueList(boolean mUpdateMyClueList) {
		this.mUpdateMyClueList = mUpdateMyClueList;
	}

	private boolean mFirstModifyLocation = false;

	public boolean isFirstModifyLocation() {
		return mFirstModifyLocation;
	}

	public void setFirstModifyLocation(boolean firstModifyLocation) {
		this.mFirstModifyLocation = firstModifyLocation;
	}

	public boolean isUpdateFriendList() {
		return mUpdateFriendList;
	}

	public void setUpdateFriendList(boolean updateFriendList) {
		this.mUpdateFriendList = updateFriendList;
	}

	public int getMsgCount() {
		return mMsgCount;
	}

	public void setMsgCount(int msgCount) {
		if (mMsgCount <= 0 && msgCount > 0) {
			this.mMsgCount = msgCount;
			Message message = App.getInstance().obtainMessage();
			message.what = YSMSG.RESP_SHOW_NEW_MSG_NOTIFICATION;
			CoreModel.getInstance().notifyOutboxHandlers(message);
			return;
		}

		this.mMsgCount = msgCount;
	}

	public boolean isUpdateMsgList() {
		return mUpdateMsgList;
	}

	public void setUpdateMsgList(boolean updateMsgList) {
		this.mUpdateMsgList = updateMsgList;
	}

	public boolean isUpdateFriendRequestList() {
		return mUpdateFriendRequestList;
	}

	public void setUpdateFriendRequestList(boolean updateFriendRequestList) {
		mUpdateFriendRequestList = updateFriendRequestList;
	}

	public boolean isUpdateEventList() {
		return mUpdateEventList;
	}

	public void setUpdateEventList(boolean updateEventList) {
		this.mUpdateEventList = updateEventList;
	}

	public boolean isUpdateClueList() {
		return mUpdateClueList;
	}

	public void setUpdateClueList(boolean updateClueList) {
		this.mUpdateClueList = updateClueList;
	}

	public boolean isVersionChecked() {
		return mIsVersionChecked;
	}

	public synchronized void setVersionChecked(boolean isVersionChecked) {
		this.mIsVersionChecked = isVersionChecked;
	}

	public int getFriendReqCount() {
		return mFriendReqCount;
	}

	public synchronized void setFriendReqCount(int friendReqCount) {
		this.mFriendReqCount = friendReqCount;
	}

	public int getEventCount() {
		int count = 0;
		count += (getNewSOSCount() > 0) ? getNewSOSCount() : 0;
		count += (getNewCheckinCount() > 0) ? getNewCheckinCount() : 0;
		count += (getNewConfirmCount() > 0) ? getNewConfirmCount() : 0;
		count += (getNewEventClueCount() > 0) ? getNewEventClueCount() : 0;

		return count;
	}

	public synchronized void setEventCount(int eventCount) {

	}

	// 获取拍照留证通知数
	public int getNewEventClueCount() {
		return PreferencesUtils.getEventClueCount(getUserId());
	}

	// 设置拍照留证通知数
	public void setNewEventClueCount(int eventClueCount) {
		PreferencesUtils.setEventClueCount(getUserId(), eventClueCount);
	}

	public int getNewSOSCount() {
		return PreferencesUtils.getNewSOSCount(getUserId());
	}

	public synchronized void setNewSOSCount(int eventCount) {
		PreferencesUtils.setNewSOSCount(getUserId(), eventCount);
	}

	public int getNewCheckinCount() {
		return PreferencesUtils.getNewCheckinCount(getUserId());
	}

	public synchronized void setNewCheckinCount(int eventCount) {
		PreferencesUtils.setNewCheckinCount(getUserId(), eventCount);
	}

	public int getNewConfirmCount() {
		return PreferencesUtils.getNewConfirmCount(getUserId());
	}

	public synchronized void setNewConfirmCount(int eventCount) {
		PreferencesUtils.setNewConfirmCount(getUserId(), eventCount);
	}

	public int getUserId() {
		if (mUserInfo != null) {
			return mUserInfo.getUserId();
		}
		return 0;
	}

	@Override
	public boolean handleMessage(Message msg) {
		UserBiz.handleMessage(msg);
		FriendBiz.handleMessage(msg);
		SOSBiz.handleMessage(msg);
		LocationBiz.handleMessage(msg);
		CheckinBiz.handleMessage(msg);
		ClueBiz.handleMessage(msg);
		SysBiz.handleMessage(msg);
		ImBiz.handleMessage(msg);

		return true;
	}

	public FriendObject getFriendObjectByUserId(int userid) {
		return new FriendObjectDao().findById(userid);
	}

	public void setLoginObject(LoginObject loginObject) {
		mLoginObject = loginObject;
	}

	public LoginObject getLoginObject() {
		return mLoginObject;
	}

	public void setRegisterObject(RegisterObject registerObject) {
		mRegisterObject = registerObject;
	}

	public RegisterObject getRegisterObject() {
		return mRegisterObject;
	}

	public synchronized void setUserInfo(UserObject userInfo) {
		if (userInfo != null) {
			mUserInfo = userInfo;
		}
	}

	public UserObject getUserInfo() {
		return mUserInfo;
	}

	public List<FriendObject> getFriendList() {
		List<FriendObject> list = new FriendObjectDao().findAll();
		if (list != null) {
			return list;
		}

		return new ArrayList<FriendObject>();
	}

	private List<FriendRequestObject> getFriendRequestList() {
		List<FriendRequestObject> list = new FriendRequestObjectDao().findAll();
		if (list != null) {
			return list;
		}

		return new ArrayList<FriendRequestObject>();
	}

	public List<ClueObject> getClueList() {
		List<ClueObject> list = new ClueObjectDao().findAll();
		if (list != null) {
			return list;
		}

		return new ArrayList<ClueObject>();
	}

	private List<EventObjectEx> getCheckInList() {
		List<EventObjectEx> list = new EventObjectDao().findAll();
		if (list != null) {
			return list;
		}

		return new ArrayList<EventObjectEx>();
	}

	public boolean isLogined() {
		return mIsLogined;
	}

	public synchronized void setLogined(boolean isLogined) {
		this.mIsLogined = isLogined;
	}

	/**
	 * 获取手机联系人名字
	 * 
	 * @return
	 */
	public String getContactsName(String phoneNumber) {
		String name = "";
		ContactDataBase cb = null;
		try {
			cb = new ContactDataBase(App.getInstance().getApplicationContext());

			ContactsObject contacts = cb.query(phoneNumber);
			if (contacts != null) {
				name = contacts.getName();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cb.close();
		}
		return name;
	}

	private void init() {
		mUserInfo = new UserObject();
	}

	public void release() {
		mIsLogined = false;
		mIsVersionChecked = false;

		mFriendReqCount = 0;
		mEventCount = 0;
		mMsgCount = 0;

		mLoginObject = null;
		mRegisterObject = null;
		mUserInfo = null;

		mCoreModel = null;
	}

	/**
	 * 获取模型单例
	 */
	public static CoreModel getInstance() {
		if (null == mCoreModel) {
			mCoreModel = new CoreModel();
			BaseController.getInstance().setModel(mCoreModel);
		}

		return mCoreModel;
	}

	/**
	 * 私有构造方法
	 */
	private CoreModel() {
		init();

	}

}
