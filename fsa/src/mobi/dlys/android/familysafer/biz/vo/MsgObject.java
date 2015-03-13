package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Msg;

import com.j256.ormlite.field.DatabaseField;

/**
 * IM信息类
 * 
 * @author rocksen
 * 
 */
public class MsgObject extends BaseObject {
	private static final long serialVersionUID = -4405806281152010069L;
	public static final int MESSAGE_FROM = 0;
	public static final int MESSAGE_TO = 1;

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private int msgId;

	@DatabaseField
	private int fromUser;

	@DatabaseField
	private int toUser;

	@DatabaseField
	private String voice;

	@DatabaseField
	private String voiceFilePath;

	@DatabaseField
	private int duration;

	@DatabaseField
	private String createTime;

	@DatabaseField
	private int status; // 1:成功 2:失败 0:正在上传

	@DatabaseField
	private int type; // MESSAGE_FROM 表示收到的消息；MESSAGE_TO 表示发送的消息

	public static MsgObject createFromPb(Msg msg) {
		MsgObject msgObject = new MsgObject();
		if (null != msg) {
			msgObject.setMsgId(msg.getMsgId());
			if (msg.getFromUser() != null) {
				msgObject.setFromUser(msg.getFromUser().getUserId());
			}
			if (msg.getToUser() != null) {
				msgObject.setToUser(msg.getToUser().getUserId());
			}

			msgObject.setVoice(msg.getVoice());
			msgObject.setDuration(msg.getDuration());
			msgObject.setCreateTime(msg.getCreateTime());
			msgObject.setStatus(1);
		}

		return msgObject;
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	public int getFromUser() {
		return fromUser;
	}

	public void setFromUser(int fromUser) {
		this.fromUser = fromUser;
		if (CoreModel.getInstance().getUserId() == getFromUser()) {
			setType(MESSAGE_TO);
		} else {
			setType(MESSAGE_FROM);
		}
	}

	public int getToUser() {
		return toUser;
	}

	public void setToUser(int toUser) {
		this.toUser = toUser;
		if (CoreModel.getInstance().getUserId() == getToUser()) {
			setType(MESSAGE_FROM);
		} else {
			setType(MESSAGE_TO);
		}
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public boolean isRecvMsg() {
		if (CoreModel.getInstance().getUserId() == toUser) {
			return true;
		}
		return false;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getVoiceFilePath() {
		return voiceFilePath;
	}

	public void setVoiceFilePath(String voiceFilePath) {
		this.voiceFilePath = voiceFilePath;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isSuccess() {
		return (status == 1) ? true : false;
	}

	public boolean isUpLoading() {
		return (status == 0) ? true : false;
	}

	public boolean isFailed() {
		return (status == 2) ? true : false;
	}

}
