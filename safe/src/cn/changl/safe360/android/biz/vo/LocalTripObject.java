package cn.changl.safe360.android.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

import com.j256.ormlite.field.DatabaseField;

public class LocalTripObject extends BaseObject {
	private static final long serialVersionUID = 8780784746173458371L;

	public static final int LOCAL_USER_ID = -2;

	@DatabaseField(id = true)
	private int tripId;

	@DatabaseField
	private String phone;

	// 用户Id
	@DatabaseField
	private int userId;

	// 插入数据库时间
	@DatabaseField
	private long insertTime;

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalTripObject() {

	}

	public LocalTripObject(int trip, String phone) {
		this.tripId = trip;
		this.phone = phone;
		this.userId = LOCAL_USER_ID;
		this.insertTime = System.nanoTime();
	}

	public UserInfo toUserInfo() {
		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setPhone(phone);
		ub.setUserId(userId);
		ub.setNickname(phone);

		return ub.build();
	}

}
