package cn.changl.safe360.android.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import cn.changl.safe360.android.utils.AES;

/**
 * 联系人类
 * 
 * @author rocksen
 * 
 */
public class ContactsObject extends BaseObject {
	private static final long serialVersionUID = -1992482498522680356L;

	// 用户Id
	private int userId;

	// 手机号码
	private String phone;

	// 名称
	private String name;

	// 关系类型(未注册/注册/好友)
	private int type = 1; // 1: 对方未注册App 2:对方已注册，但不是好友 3:已是好友
							// 4:已邀请（临时状态）5已添加（临时状态）

	private String url; // 邀请地址

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	private String encryptPhone() {
		this.phone = AES.encrypt(phone);
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	private String encryptName() {
		this.name = AES.encrypt(name);
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFriend() {
		return (type == 3) ? true : false;
	}

	public boolean isRegistered() {
		return (type == 2) ? true : false;
	}

	public boolean unRegistered() {
		return (type == 1) ? true : false;
	}

	public boolean isInvited() {
		return (type == 4) ? true : false;
	}

	public boolean isAdded() {
		return (type == 5) ? true : false;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
