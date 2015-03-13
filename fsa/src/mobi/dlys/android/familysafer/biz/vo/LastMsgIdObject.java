package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;

import com.j256.ormlite.field.DatabaseField;

/**
 * 最新消息Id类
 * 
 * @author rocksen
 * 
 */
public class LastMsgIdObject extends BaseObject {
	private static final long serialVersionUID = 4103051360419047437L;

	@DatabaseField(id = true)
	private int friendId;

	@DatabaseField
	private int lastMsgId;

	public LastMsgIdObject() {

	}

	public LastMsgIdObject(int friendId, int lastMsgId) {
		this.friendId = friendId;
		this.lastMsgId = lastMsgId;
	}

	public int getFriendId() {
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public int getLastMsgId() {
		return lastMsgId;
	}

	public void setLastMsgId(int lastMsgId) {
		this.lastMsgId = lastMsgId;
	}

}
