package cn.changl.safe360.android.biz.vo;

import java.util.List;

import mobi.dlys.android.core.mvc.BaseObject;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class TripObject extends BaseObject {

	private static final long serialVersionUID = -1992542498522680356L;

	int tripId;
	int userId;
	String beginTime;
	String beginLat;
	String beginLng;
	String beginAdress;
	String endLat;
	String endLng;
	String endAdress;
	boolean status;
	List<UserInfo> userinfos;

	public TripObject() {

	}

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getBeginLat() {
		return beginLat;
	}

	public void setBeginLat(String beginLat) {
		this.beginLat = beginLat;
	}

	public String getBeginLng() {
		return beginLng;
	}

	public void setBeginLng(String beginLng) {
		this.beginLng = beginLng;
	}

	public String getBeginAdress() {
		return beginAdress;
	}

	public void setBeginAdress(String beginAdress) {
		this.beginAdress = beginAdress;
	}

	public String getEndLat() {
		return endLat;
	}

	public void setEndLat(String endLat) {
		this.endLat = endLat;
	}

	public String getEndLng() {
		return endLng;
	}

	public void setEndLng(String endLng) {
		this.endLng = endLng;
	}

	public String getEndAdress() {
		return endAdress;
	}

	public void setEndAdress(String endAdress) {
		this.endAdress = endAdress;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<UserInfo> getUserinfos() {
		return userinfos;
	}

	public void setUserinfos(List<UserInfo> userinfos) {
		this.userinfos = userinfos;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
