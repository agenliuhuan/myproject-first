package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.VoiceSOS;
import mobi.dlys.android.familysafer.utils.DateUtils;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;

/**
 * 我的SOS类
 * 
 * @author rocksen
 * 
 */
public class MySOSObject extends BaseObject {
	private static final long serialVersionUID = -1046133524045836400L;

	// 通知Id
	@DatabaseField(id = true)
	private int voiceSosId;

	@DatabaseField
	public String voiceUrl;

	@DatabaseField
	public int duration;

	// 经度
	@DatabaseField
	public String lng;

	// 纬度
	@DatabaseField
	public String lat;

	// 位置
	@DatabaseField
	private String location;

	// 通知时间
	@DatabaseField
	private String createTime;

	// 插入数据库时间
	@DatabaseField
	private long insertTime;

	public static MySOSObject createFromPb(VoiceSOS voiceSOS) {
		MySOSObject sosObject = new MySOSObject();
		if (voiceSOS != null) {
			sosObject.setVoiceSosId(voiceSOS.getVoiceSOSId());
			sosObject.setVoiceUrl(voiceSOS.getVoice());
			sosObject.setDuration(voiceSOS.getDuration());
			sosObject.setCreateTime(voiceSOS.getCreateTime());
			sosObject.setLat(voiceSOS.getLat());
			sosObject.setLng(voiceSOS.getLng());
			sosObject.setLocation(voiceSOS.getLocation());
			sosObject.setInsertTime(System.nanoTime());
		}
		return sosObject;
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

	public String getLocation() {
		if (null == location) {
			return "";
		}
		if (TextUtils.isEmpty(location)) {
			return "";
		}
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDuration() {
		return this.duration + "\"";
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public int getVoiceSosId() {
		return voiceSosId;
	}

	public void setVoiceSosId(int voiceSosId) {
		this.voiceSosId = voiceSosId;
	}

	public String getVoiceUrl() {
		return voiceUrl;
	}

	public void setVoiceUrl(String voiceUrl) {
		this.voiceUrl = voiceUrl;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getUserId() {
		return CoreModel.getInstance().getUserId();
	}

	public String getImage() {
		if (CoreModel.getInstance().getUserInfo() != null) {
			return CoreModel.getInstance().getUserInfo().getImage();
		} else {
			return "";
		}
	}
}
