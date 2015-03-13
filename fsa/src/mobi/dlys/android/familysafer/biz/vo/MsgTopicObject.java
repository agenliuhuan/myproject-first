package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.MsgTopic;

import com.j256.ormlite.field.DatabaseField;

/**
 * IM信息类
 * 
 * @author rocksen
 * 
 */
public class MsgTopicObject extends BaseObject {
	private static final long serialVersionUID = -8873572871536531507L;

	@DatabaseField(id = true)
	private int msgTopicId;

	@DatabaseField
	private int userId;

	@DatabaseField
	private String phone;

	@DatabaseField
	private String image;

	@DatabaseField
	private String nickname;

	@DatabaseField
	private int unReadCount;

	@DatabaseField
	private String lastTime;

	// 插入数据库时间
	@DatabaseField
	private long insertTime;

	public static MsgTopicObject createFromPb(MsgTopic msgTopic) {
		MsgTopicObject msgTopicObject = new MsgTopicObject();
		if (null != msgTopic) {
			msgTopicObject.setMsgTopicId(msgTopic.getMsgTopicId());
			if (msgTopic.getUserInfo() != null) {
				msgTopicObject.setUserId(msgTopic.getUserInfo().getUserId());
				msgTopicObject.setPhone(msgTopic.getUserInfo().getPhone());
				msgTopicObject.setImage(msgTopic.getUserInfo().getImage());
				msgTopicObject.setNickname(msgTopic.getUserInfo().getNickname());
			}
			msgTopicObject.setUnReadCount(msgTopic.getUnreadCount());
			msgTopicObject.setLastTime(msgTopic.getLastTime());
			msgTopicObject.setInsertTime(System.nanoTime());
		}

		return msgTopicObject;
	}

	public int getMsgTopicId() {
		return msgTopicId;
	}

	public void setMsgTopicId(int msgTopicId) {
		this.msgTopicId = msgTopicId;
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

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getUnReadCount() {
		return unReadCount;
	}

	public void setUnReadCount(int unReadCount) {
		this.unReadCount = unReadCount;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

}
