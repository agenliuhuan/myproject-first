package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;

/**
 * 注册信息类
 * 
 * @author rocksen
 * 
 */
public class ModifyPwdObject extends BaseObject {
	private static final long serialVersionUID = 4955686984514093951L;

	// 旧密码
	private String oldPassword;

	// 新密码
	private String newPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String password) {
		this.oldPassword = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String password) {
		this.newPassword = password;
	}

}
