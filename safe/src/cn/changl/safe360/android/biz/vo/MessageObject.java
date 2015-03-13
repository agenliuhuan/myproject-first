package cn.changl.safe360.android.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;

public class MessageObject extends BaseObject {
	private static final long serialVersionUID = 528796607297435855L;

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	String title;

	@DatabaseField
	String description;

	@DatabaseField
	String data;

	@DatabaseField
	String time;

	@DatabaseField
	int type;// 1是有地图，2是无地图

	@DatabaseField
	int userId;

	@DatabaseField
	String userName;

	@DatabaseField
	String userAvatar;

	@DatabaseField
	String serverTime;

	@DatabaseField
	String lat;

	@DatabaseField
	String lng;

	@DatabaseField
	String AddressName; // 建筑名称

	@DatabaseField
	String location;

	// 插入数据库时间
	@DatabaseField
	private long insertTime;

	public MessageObject() {

	}

	public void parseData() {
		try {
			JSONObject json = new JSONObject(data);
			setUserId(json.getInt("userId"));
			if (!TextUtils.isEmpty(json.getString("lat"))) {
				setLat(json.getString("lat"));
			}
			if (!TextUtils.isEmpty(json.getString("lng"))) {
				setLng(json.getString("lng"));
			}
			if (!TextUtils.isEmpty(json.getString("location"))) {
				setLocation(json.getString("location"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}

	public String getServerTime() {
		return serverTime;
	}

	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public String getAddressName() {
		return AddressName;
	}

	public void setAddressName(String addressName) {
		AddressName = addressName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
