package cn.changl.safe360.android.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

import com.j256.ormlite.field.DatabaseField;

public class LocalUserObject extends BaseObject {
	private static final long serialVersionUID = 9182346320768212299L;

	public static final int LOCAL_USER_ID = -2;

	@DatabaseField(id = true)
	private String phone;

	// 用户Id
	@DatabaseField
	private int userId;

	// 经度
	@DatabaseField
	private String lng;

	// 纬度
	@DatabaseField
	private String lat;

	@DatabaseField
	private long createTime;

	// 插入数据库时间
	@DatabaseField
	private long insertTime;

	public double getLat2() {
		double value = 0.0;
		try {
			value = Double.valueOf(lat);
		} catch (Exception e) {

		}
		return value;
	}

	public double getLng2() {
		double value = 0.0;
		try {
			value = Double.valueOf(lng);
		} catch (Exception e) {

		}
		return value;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
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

	public LocalUserObject() {

	}

	public LocalUserObject(String phone) {
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
