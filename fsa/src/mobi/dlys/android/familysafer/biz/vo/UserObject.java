package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;

/**
 * 用户信息类
 * 
 * @author rocksen
 * 
 */
public class UserObject extends BaseObject {
	private static final long serialVersionUID = -1117216497280013178L;

	// 用户Id
	@DatabaseField(id = true)
	private int userId;

	// token
	@DatabaseField
	private String token;

	// 昵称
	@DatabaseField
	private String nickname;

	// 密码
	@DatabaseField
	private String password;

	// 手机号码
	@DatabaseField
	private String phone;

	// 验证码
	@DatabaseField
	private String authCode;

	// 头像
	@DatabaseField
	private String image;

	// 位置
	@DatabaseField
	private String location;

	// 修正位置
	private String location2;
	
	// 发送位置
	private String location3;

	// 备注名
	@DatabaseField
	private String remarkName;

	// 经度
	@DatabaseField
	private String lng;

	// 纬度
	@DatabaseField
	private String lat;

	// 隐藏我的地理位置
	@DatabaseField
	private boolean hideLocation;

	// 允许查看我的地址位置
	@DatabaseField
	private boolean showMyPosition;

	// 最后上报位置的时间
	private String lastMoveTime;

	public static UserObject createFromPb(UserInfo userInfo) {
		UserObject userObject = new UserObject();
		if (userInfo != null) {
			userObject.setUserId(userInfo.getUserId());
			userObject.setToken(userInfo.getToken());
			userObject.setNickname(userInfo.getNickname());
			userObject.setPassword(userInfo.getPassword());
			userObject.setPhone(userInfo.getPhone());
			userObject.setAuthCode(userInfo.getAuthCode());
			userObject.setImage(userInfo.getImage());
			userObject.setLocation(userInfo.getLocation());
			userObject.setRemarkName(userInfo.getRemarkName());
			userObject.setLng(userInfo.getLng());
			userObject.setLat(userInfo.getLat());
			userObject.setHideLocation(userInfo.getHideLocation());
			userObject.setShowMyPosition(userInfo.getShowMyPosition());
			userObject.setLastMoveTime(userInfo.getLastMoveTime());
		}

		return userObject;
	}

	public UserObject clone() {
		UserObject userObject = new UserObject();
		userObject.setUserId(this.getUserId());
		userObject.setToken(this.getToken());
		userObject.setNickname(this.getNickname());
		userObject.setPassword(this.getPassword());
		userObject.setPhone(this.getPhone());
		userObject.setAuthCode(this.getAuthCode());
		userObject.setImage(this.getImage());
		userObject.setLocation(this.getLocation());
		userObject.setRemarkName(this.getRemarkName());
		userObject.setLng(this.getLng());
		userObject.setLat(this.getLat());
		userObject.setHideLocation(this.getHideLocation());
		userObject.setShowMyPosition(this.getShowMyPosition());
		userObject.setLastMoveTime(this.getLastMoveTime());

		return userObject;
	}

	public static UserObject setUserInfo(UserObject userObject, UserInfo userInfo) {
		if (userInfo != null && userObject != null) {
			userObject.setUserId(userInfo.getUserId());
			userObject.setToken(userInfo.getToken());
			userObject.setNickname(userInfo.getNickname());
			userObject.setPassword(userInfo.getPassword());
			userObject.setPhone(userInfo.getPhone());
			userObject.setAuthCode(userInfo.getAuthCode());
			userObject.setImage(userInfo.getImage());
			userObject.setLocation(userInfo.getLocation());
			userObject.setRemarkName(userInfo.getRemarkName());
			userObject.setLng(userInfo.getLng());
			userObject.setLat(userInfo.getLat());
			userObject.setHideLocation(userInfo.getHideLocation());
			userObject.setShowMyPosition(userInfo.getShowMyPosition());
			userObject.setLastMoveTime(userInfo.getLastMoveTime());
		}

		return userObject;
	}

	public static void updateUserInfo(UserObject userObject, UserInfo userInfo) {
		if (userInfo == null) {
			return;
		}

		if (userObject == null) {
			userObject = createFromPb(userInfo);
		} else {
			userObject.setUserId(userInfo.getUserId());
			userObject.setNickname(userInfo.getNickname());
			userObject.setPhone(userInfo.getPhone());
			userObject.setImage(userInfo.getImage());
			userObject.setRemarkName(userInfo.getRemarkName());
			userObject.setLng(userInfo.getLng());
			userObject.setLat(userInfo.getLat());
			userObject.setHideLocation(userInfo.getHideLocation());
			userObject.setShowMyPosition(userInfo.getShowMyPosition());
			userObject.setLastMoveTime(userInfo.getLastMoveTime());
		}
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLocation() {
		if (location2 != null) {
			if (CoreModel.getInstance().getUserInfo() != null && userId == CoreModel.getInstance().getUserInfo().getUserId() && !TextUtils.isEmpty(location2)) {
				return location2;
			}
		}
		if (null != location) {
			if (CoreModel.getInstance().getUserInfo() != null && userId == CoreModel.getInstance().getUserInfo().getUserId() && TextUtils.isEmpty(location)
					&& App.getInstance().getLocater() != null) {
				location = App.getInstance().getLocater().getAddress();
			}
		}
		if (null == location) {
			return "";
		}
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setLocation2(String location) {
		this.location2 = location;
	}
	
	public void setLocation3(String location) {
		this.location3 = location;
	}
	
	public String getLocation3() {
		return this.location3;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public String getLng() {
		if (CoreModel.getInstance().getUserInfo() != null && userId == CoreModel.getInstance().getUserInfo().getUserId() && TextUtils.isEmpty(lng)
				&& App.getInstance().getLocater() != null) {
			lng = App.getInstance().getLocater().getLng();
		}
		return lng;
	}

	public double getLng2() {
		double value = 0.0;
		try {
			value = Double.valueOf(lng);
		} catch (Exception e) {

		}
		return value;
	}

	public void setLng(String lng) {
		if (BaiduLoc.isLngValid(lng)) {
			this.lng = lng;
		}
	}

	public String getLat() {
		if (CoreModel.getInstance().getUserInfo() != null && userId == CoreModel.getInstance().getUserInfo().getUserId() && TextUtils.isEmpty(lat)
				&& App.getInstance().getLocater() != null) {
			lat = App.getInstance().getLocater().getLat();
		}
		return lat;
	}

	public double getLat2() {
		double value = 0.0;
		try {
			value = Double.valueOf(lat);
		} catch (Exception e) {

		}
		return value;
	}

	public void setLat(String lat) {
		if (BaiduLoc.isLatValid(lat)) {
			this.lat = lat;
		}
	}

	public boolean getHideLocation() {
		return hideLocation;
	}

	public void setHideLocation(boolean hideLocation) {
		this.hideLocation = hideLocation;
	}

	public boolean getShowMyPosition() {
		return showMyPosition;
	}

	public void setShowMyPosition(boolean showMyPosition) {
		this.showMyPosition = showMyPosition;
	}

	/*
	 * 判断经纬度是否有效
	 */
	public boolean isLocationValid() {
		return BaiduLoc.isLocationValid(getLng(), getLat()) && !TextUtils.isEmpty(location);
	}

	/*
	 * 是否允许访问地理位置
	 */
	public boolean isAllowAccessLoaction() {
		return !getHideLocation() && getShowMyPosition();
	}

	public String getLastMoveTime() {
		return lastMoveTime;
	}

	public void setLastMoveTime(String lastMoveTime) {
		this.lastMoveTime = lastMoveTime;
	}
}
