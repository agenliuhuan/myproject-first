package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Friend;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;
import mobi.dlys.android.familysafer.utils.AES;

import com.j256.ormlite.field.DatabaseField;

/**
 * 好友/家人类
 * 
 * @author rocksen
 * 
 */
public class FriendObject extends BaseObject {
	private static final long serialVersionUID = -5660337273899458195L;

	// 用户Id
	@DatabaseField(id = true)
	private int userId;

	// 用户头像
	@DatabaseField
	private String image;

	// 用户昵称
	@DatabaseField
	private String nickname;

	// 备注名
	@DatabaseField
	private String remarkName;

	// 手机号码
	@DatabaseField
	private String phone;

	// 经度
	@DatabaseField
	private String lng;

	// 纬度
	@DatabaseField
	private String lat;

	// 请求状态 0.已通过 1.等待 2.拒绝
	@DatabaseField
	private int status;

	// 位置
	@DatabaseField
	private String location;

	// 最后上报位置的时间
	@DatabaseField
	private String lastMoveTime;

	// 隐藏我的地理位置
	@DatabaseField
	private boolean hideLocation;

	// 允许查看我的地址位置
	@DatabaseField
	private boolean showMyPosition;

	// 插入数据库时间
	@DatabaseField
	private long insertTime;

	public static FriendObject createFromPb(Friend friend) {
		FriendObject friendObject = new FriendObject();
		if (friend != null) {
			friendObject.setUserId(friend.getUserId());
			friendObject.setImage(friend.getImage());
			friendObject.setNickname(friend.getNickname());
			friendObject.setLng(friend.getLng());
			friendObject.setLat(friend.getLat());
			friendObject.setPhone(friend.getPhone());
			friendObject.setLocation(friend.getLocation());
			friendObject.setRemarkName(friend.getRemarkName());
			friendObject.setLastMoveTime(friend.getLastMoveTime());
			friendObject.setHideLocation(friend.getHideLocation());
			friendObject.setShowMyPosition(friend.getShowMyPosition());
			friendObject.setInsertTime(System.nanoTime());
		}

		return friendObject;
	}

	public static FriendObject createFromPb(UserInfo userInfo) {
		FriendObject friendObject = new FriendObject();
		if (userInfo != null) {
			friendObject.setUserId(userInfo.getUserId());
			friendObject.setImage(userInfo.getImage());
			friendObject.setNickname(userInfo.getNickname());
			friendObject.setLng(userInfo.getLng());
			friendObject.setLat(userInfo.getLat());
			friendObject.setPhone(userInfo.getPhone());
			friendObject.setLocation(userInfo.getLocation());
			friendObject.setRemarkName(userInfo.getRemarkName());
			friendObject.setLastMoveTime(userInfo.getLastMoveTime());
			friendObject.setHideLocation(userInfo.getHideLocation());
			friendObject.setShowMyPosition(userInfo.getShowMyPosition());
			friendObject.setInsertTime(System.nanoTime());
		}

		return friendObject;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return this.image;
	}

	public void setNickname(String name) {
		this.nickname = AES.encrypt(name);
	}

	public String getNickname() {
		return AES.decrypt(this.nickname);

	}

	public void setLng(String lng) {
		if (BaiduLoc.isLngValid(lng)) {
			this.lng = lng;
		}
	}

	public String getLng() {
		return this.lng;
	}

	public double getLng2() {
		double value = 0.0;
		try {
			value = Double.valueOf(lng);
		} catch (Exception e) {

		}
		return value;
	}

	public void setLat(String lat) {
		if (BaiduLoc.isLatValid(lat)) {
			this.lat = lat;
		}
	}

	public String getLat() {
		return this.lat;
	}

	public double getLat2() {
		double value = 0.0;
		try {
			value = Double.valueOf(lat);
		} catch (Exception e) {

		}
		return value;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return this.status;
	}

	public void setPhone(String phone) {
		this.phone = AES.encrypt(phone);
	}

	public String getPhone() {
		return AES.decrypt(this.phone);
	}

	public String getLocation() {
		if (null == location) {
			return "";
		}
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRemarkName() {
		return AES.decrypt(this.remarkName);
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = AES.encrypt(remarkName);
	}

	/*
	 * 判断经纬度是否有效
	 */
	public boolean isLocationValid() {
		return (BaiduLoc.isLocationValid(getLng(), getLat()) || !getShowMyPosition());
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public String getLastMoveTime() {
		return lastMoveTime;
	}

	public void setLastMoveTime(String lastMoveTime) {
		this.lastMoveTime = lastMoveTime;
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
}
