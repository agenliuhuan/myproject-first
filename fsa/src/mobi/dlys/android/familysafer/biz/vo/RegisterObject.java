package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.App;
import android.text.TextUtils;

/**
 * 注册信息类
 * 
 * @author rocksen
 * 
 */
public class RegisterObject extends BaseObject {
	private static final long serialVersionUID = -2174329835357635482L;

	// 昵称
	private String nickname;

	// 密码
	private String password;

	// 手机号码
	private String phone;

	// 验证码
	private String authCode;

	// 头像
	private String image;

	// 经度
	private String lng;

	// 纬度
	private String lat;

	// 位置
	private String location;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String avatarUrl) {
		this.image = avatarUrl;
	}

	public String getLng() {
		if (TextUtils.isEmpty(lng) && App.getInstance().getLocater() != null) {
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

	public void setLng(String longitude) {
		this.lng = longitude;
	}

	public String getLat() {
		if (TextUtils.isEmpty(lat) && App.getInstance().getLocater() != null) {
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

	public void setLat(String latitude) {
		this.lat = latitude;
	}

	public String getLocation() {
		if (null == location) {
			return "";
		}
		if (TextUtils.isEmpty(location) && App.getInstance().getLocater() != null) {
			return App.getInstance().getLocater().getAddress();
		}
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
