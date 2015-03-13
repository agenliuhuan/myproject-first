package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;

/**
 * 新信息提示类
 * 
 * @author rocksen
 * 
 */
public class NewMsgObject extends BaseObject {
	private static final long serialVersionUID = -4123319216654407977L;

	// 新的好友请求数
	private int friendReqCount;

	// 新的通知数
	private int eventCount;

	// 新的消息数
	private int msgCount;

	public int getFriendReqCount() {
		return friendReqCount;
	}

	public void setFriendReqCount(int friendReqCount) {
		this.friendReqCount = friendReqCount;
	}

	public int getEventCount() {
		return eventCount;
	}

	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}

	public int getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}

}
