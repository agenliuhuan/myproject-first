package cn.changl.safe360.android.model;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.mvc.BaseModel;
import android.app.Activity;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.bo.GroupBiz;
import cn.changl.safe360.android.biz.bo.SysBiz;
import cn.changl.safe360.android.biz.bo.TripBiz;
import cn.changl.safe360.android.biz.bo.UserBiz;
import cn.changl.safe360.android.biz.vo.RegisterObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.ui.comm.DialogHelper;
import cn.changl.safe360.android.ui.login.LoginActivity;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Trip;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class CoreModel extends BaseModel {
	private static final String TAG = CoreModel.class.getSimpleName();

	private static CoreModel mCoreModel = null;
	private UserObject mUserInfo = null;
	private RegisterObject registerObj = null;
	private boolean mIsVersionChecked = false;
	private boolean mChangeUserImg = false;
	private boolean mChangeUserName = false;
	private boolean mFirstModifyLocation = false;
	private boolean mIsLogined = false;
	private ArrayList<UserInfo> friendList = null;
	private Trip trip = null;

	public String playMsgId;

	private boolean mIsImServerLogined = false;

	private boolean mHasEscorting = false;

	public boolean hasEscorting() {
		if (trip == null) {
			return false;
		}
		return mHasEscorting;
	}

	public void setEscorting(boolean mHasEscorting) {
		this.mHasEscorting = mHasEscorting;
	}

	public boolean isImServerLogined() {
		return mIsImServerLogined;
	}

	public void setImServerLogined(boolean isImServerLogined) {
		this.mIsImServerLogined = isImServerLogined;
	}

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}

	public boolean isFriend(int userId) {
		if (this.friendList.size() > 0) {
			for (UserInfo userInfo : friendList) {
				if (userInfo != null && userInfo.getUserId() == userId) {
					return true;
				}
			}
		}

		return false;
	}

	public ArrayList<UserInfo> getFriendList() {
		return friendList;
	}

	public void setFriendList(ArrayList<UserInfo> friendList) {
		this.friendList.clear();
		this.friendList.addAll(friendList);

	}

	public void clearFriendList() {
		this.friendList.clear();
	}

	public static void updateLocalFriendList(List<UserInfo> userList, List<UserInfo> localList) {
		if (localList == null || userList == null) {
			return;
		}
		boolean finded = false;
		for (UserInfo localUserInfo : localList) {
			for (UserInfo userInfo : userList) {
				if (localUserInfo.getPhone().equals(userInfo.getPhone())) {
					userList.remove(userInfo);
					userList.add(localUserInfo);
					finded = true;
					break;
				}
			}

			if (!finded) {
				userList.add(localUserInfo);
			}
		}
	}

	public static void removeLocalFriend(List<UserInfo> userList, String phone) {
		if (userList == null || userList.size() <= 0) {
			return;
		}

		for (UserInfo userInfo : userList) {
			if (userInfo.getPhone().equals(phone)) {
				userList.remove(userInfo);
				break;
			}
		}
	}

	public boolean checkUserLogined(final Activity activity) {
		if (isLogined()) {
			return true;
		} else {
			DialogHelper.showTwoDialog(activity, false, null, activity.getBaseContext().getString(R.string.dialog_login_content), activity.getBaseContext()
					.getString(R.string.dialog_login_yes), activity.getBaseContext().getString(R.string.dialog_login_no), true, new OnClickListener() {
				public void onClick(View arg0) {
					activity.finish();
					LoginActivity.startActivity(activity);
				}
			}, null);
			return false;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		UserBiz.handleMessage(msg);
		GroupBiz.handleMessage(msg);
		TripBiz.handleMessage(msg);
		SysBiz.handleMessage(msg);
		return true;
	}

	private void init() {
		friendList = new ArrayList<UserInfo>();
	}

	public void release() {

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

	public synchronized void setUserInfo(UserObject userInfo) {
		if (userInfo != null) {
			mUserInfo = userInfo;
		}
	}

	public UserObject getUserInfo() {
		return mUserInfo;
	}

	public void setRegisterObject(RegisterObject registerObject) {
		registerObj = registerObject;
	}

	public RegisterObject getRegisterObject() {
		return registerObj;
	}

	public boolean isVersionChecked() {
		return mIsVersionChecked;
	}

	public synchronized void setVersionChecked(boolean isVersionChecked) {
		this.mIsVersionChecked = isVersionChecked;
	}

	public boolean ismChangeUserImg() {
		return mChangeUserImg;
	}

	public void setmChangeUserImg(boolean mChangeUserImg) {
		this.mChangeUserImg = mChangeUserImg;
	}

	public boolean ismChangeUserName() {
		return mChangeUserName;
	}

	public void setmChangeUserName(boolean mChangeUserName) {
		this.mChangeUserName = mChangeUserName;
	}

	public boolean isFirstModifyLocation() {
		return mFirstModifyLocation;
	}

	public void setFirstModifyLocation(boolean firstModifyLocation) {
		this.mFirstModifyLocation = firstModifyLocation;
	}

	public boolean isLogined() {
		return mIsLogined;
	}

	public synchronized void setLogined(boolean isLogined) {
		this.mIsLogined = isLogined;
	}

	public String getNickname() {
		if (getUserInfo() != null) {
			if (!TextUtils.isEmpty(getUserInfo().getNickname())) {
				return getUserInfo().getNickname();
			} else if (!TextUtils.isEmpty(getUserInfo().getPhone())) {
				return getUserInfo().getPhone();
			}
		}

		return "";
	}

	public String getNickname(String imId) {
		UserInfo userInfo = null;
		for (int i = 0; i < friendList.size(); i++) {
			userInfo = friendList.get(i);
			if (userInfo != null && userInfo.getIm() != null && !TextUtils.isEmpty(userInfo.getIm().getUsername())
					&& userInfo.getIm().getUsername().equals(imId)) {
				return TextUtils.isEmpty(userInfo.getNickname()) ? userInfo.getPhone() : userInfo.getNickname();
			}
		}

		return imId;
	}

	public String getAvatar(String imId) {
		UserInfo userInfo = null;
		for (int i = 0; i < friendList.size(); i++) {
			userInfo = friendList.get(i);
			if (userInfo != null && userInfo.getIm() != null && !TextUtils.isEmpty(userInfo.getIm().getUsername())
					&& userInfo.getIm().getUsername().equals(imId)) {
				return TextUtils.isEmpty(userInfo.getImage()) ? "" : userInfo.getImage() + "!avatar.def";
			}
		}

		return "";
	}

	public UserInfo getFriendByUserid(int userid) {
		UserInfo info = null;
		for (UserInfo user : friendList) {
			if (userid == user.getUserId()) {
				info = user;
				return info;
			}
		}
		return info;
	}

}
