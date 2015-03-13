package cn.changl.safe360.android.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;

public class MessageDataObject extends BaseObject {
	private static final long serialVersionUID = 7284819711557523128L;
	public static final String TYPE_ARRIVATE="arrivate";
	public static final String TYPE_NOCHANGE="nochange";
	public static final String TYPE_REFUSE="refuse";
	
	private String type;
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userid) {
		this.userId = userid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
