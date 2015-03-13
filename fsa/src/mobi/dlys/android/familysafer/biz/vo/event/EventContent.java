package mobi.dlys.android.familysafer.biz.vo.event;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;

public class EventContent extends BaseObject {
	private static final long serialVersionUID = -1455826757318891789L;

	@DatabaseField(id = true)
	public int id;

	@DatabaseField
	public int userId;

	@DatabaseField
	public String lng;

	@DatabaseField
	public String lat;

	@DatabaseField
	public String location;

	@DatabaseField
	public String actionType;

	@DatabaseField
	public long createTime;
	
	@DatabaseField
	public String msg;

	@DatabaseField
	public int status;

	@DatabaseField
	public int voiceSosId;

	public SOSVoice sosVoice;

	/*
	 * 通知内容 type = 2, 标识check in的确认消息，content为string.
	 */
	String content;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public String getLng() {
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
		this.lng = lng;
	}

	public String getLat() {
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
		this.lat = lat;
	}

	public String getLocation() {
		if (TextUtils.isEmpty(location)) {
			return "";
		}
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public SOSVoice getSOSVoice() {
		return sosVoice;
	}

	public void setSOSVoice(SOSVoice voice) {
		this.sosVoice = voice;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long time) {
		this.createTime = time;
	}

	/*
	 * 判断经纬度是否有效
	 */
	public boolean isLocationValid() {
		return BaiduLoc.isLocationValid(getLng(), getLat());
	}

	public int getVoiceSosId() {
		return voiceSosId;
	}

	public void setVoiceSosId(int voiceSosId) {
		this.voiceSosId = voiceSosId;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
