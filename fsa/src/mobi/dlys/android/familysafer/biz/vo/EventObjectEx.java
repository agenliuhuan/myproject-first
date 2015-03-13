package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.core.net.extendcmp.ojm.OJMFactory;
import mobi.dlys.android.familysafer.biz.vo.event.EventContent;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Event;
import mobi.dlys.android.familysafer.utils.DateUtils;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;

/**
 * 通知类
 * 
 * @author rocksen
 * 
 */
public class EventObjectEx extends BaseObject {
	private static final long serialVersionUID = -1046133524045836400L;

	// 通知Id
	@DatabaseField(id = true)
	private int eventId;

	// 用户Id
	@DatabaseField
	private int userId;

	// 头像
	@DatabaseField
	private String image;

	// 昵称
	@DatabaseField
	private String nickname;

	// 通知时间
	@DatabaseField
	private String createTime;

	// 通知类型
	@DatabaseField
	private int type; // 1:表示收到的到达通知, 其content为json结构
						// 2:标识 对方确认我的到达通知, 其content为string
						// 3:语音求救消息,我收到对方的语音通知 其content为json结构
						// 4:语音求救消息,对方确认我的语音通知 其content为json结构
						// 5:拍照留证消息，content无效，clue有效

	// 通知内容
	private EventContent content; // 参考type

	// 位置
	@DatabaseField
	private String location;

	// 通知状态 0:未读 1:已读 2:已确认
	@DatabaseField
	private int status;

	@DatabaseField
	private int contentId;

	@DatabaseField
	private String stringContent; // 仅当type = 2时有效

	@DatabaseField
	private int clueId; // 仅当type == 5时有效

	private ClueObject clue; // 仅当type == 5时有效

	// 插入数据库时间
	@DatabaseField
	private long insertTime;

	public static EventObjectEx createFromPb(Event event) {
		EventObjectEx eventObject = new EventObjectEx();
		EventContent eventContent = new EventContent();
		ClueObject clue = new ClueObject();
		if (event != null) {
			if (event.hasUserInfo() && event.getUserInfo() != null) {
				eventObject.setUserId(event.getUserInfo().getUserId());
				eventObject.setImage(event.getUserInfo().getImage());
				eventObject.setNickname(event.getUserInfo().getNickname());
			}
			eventObject.setEventId(event.getEventId());
			eventObject.setType(event.getEventType().getNumber());
			eventObject.setCreateTime(event.getCreateTime());
			try {
				if (eventObject.getType() == 2) {
					eventContent.setContent(event.getContent());
					eventObject.setContent(eventContent);
					eventObject.setStringContent(event.getContent());
				} else if (eventObject.getType() == 5) {
					clue = OJMFactory.createOJM().fromJson(event.getContent(), ClueObject.class);
				} else {
					eventContent = OJMFactory.createOJM().fromJson(event.getContent(), EventContent.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (eventContent != null) {
				if (eventContent.getSOSVoice() != null) {
					eventContent.setVoiceSosId(eventContent.getSOSVoice().voiceSosId);
				}
				eventContent.setUserId(event.getUserInfo().getUserId());
				eventObject.setContentId(eventContent.getId());
			}
			if (clue != null) {
				clue.setClueId();
				clue.setInsertTime(System.nanoTime());
				eventObject.setClueId(clue.getClueId());
			}
			eventObject.setClue(clue);
			eventObject.setContent(eventContent);
			eventObject.setLocation(eventContent.getLocation());
			eventObject.setStatus(event.getEventStatus().getNumber());
			eventObject.setInsertTime(System.nanoTime());
		}

		return eventObject;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public int getEventId() {
		return eventId;
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

	public void setNickname(String name) {
		this.nickname = name;
	}

	public String getCreateTime() {
		try {
			long time = DateUtils.getDate(createTime, "yyyy-MM-dd HH:mm:ss").getTime();
			return DateUtils.getRelativeDateTimeString(time);
		} catch (Exception e) {
		}
		return "";
	}

	public void setCreateTime(String date) {
		this.createTime = date;
	}

	public EventContent getContent() {
		return content;
	}

	public void setContent(EventContent content) {
		this.content = content;
	}

	public String getLocation() {
		if (null == location) {
			return "";
		}
		if (TextUtils.isEmpty(location)) {
			return "";
		}
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDuration() {
		if (null != this.content && null != this.content.getSOSVoice()) {
			return this.content.getSOSVoice().duration + "\"";
		}
		return "0\"";
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public boolean isConfirmed() {
		if (status == 3) {
			return true;
		}

		return false;
	}

	public void setConfirmed() {
		this.status = 3;
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public int getContentId() {
		return contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public String getStringContent() {
		return stringContent;
	}

	public void setStringContent(String stringContent) {
		this.stringContent = stringContent;
	}

	public ClueObject getClue() {
		return clue;
	}

	public void setClue(ClueObject clue) {
		this.clue = clue;
	}

	public int getClueId() {
		return clueId;
	}

	public void setClueId(int clueId) {
		this.clueId = clueId;
	}

}
