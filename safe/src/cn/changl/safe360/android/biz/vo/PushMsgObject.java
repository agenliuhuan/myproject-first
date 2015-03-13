package cn.changl.safe360.android.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;

import com.j256.ormlite.field.DatabaseField;

public class PushMsgObject extends BaseObject {
	private static final long serialVersionUID = 4112148445843747172L;

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	String msg;

	@DatabaseField
	String content;

	// 插入数据库时间
	@DatabaseField
	long insertTime;

	public PushMsgObject() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

}
