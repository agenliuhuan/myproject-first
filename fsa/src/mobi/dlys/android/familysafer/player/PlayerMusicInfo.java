package mobi.dlys.android.familysafer.player;

import mobi.dlys.android.core.commonutils.StringUtil;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.MsgObject;
import mobi.dlys.android.familysafer.biz.vo.MySOSObject;
import mobi.dlys.android.familysafer.player.entity.MusicInfoEntity;
import mobi.dlys.android.familysafer.player.utils.Player.Params;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/*
 * 本地音乐信息
 */
public class PlayerMusicInfo implements Parcelable {
	public int nMusicID = 0; // 歌曲ID
	public int nEventID = 0; // 通知ID
	public int nMsgID = 0; // 消息ID
	public String strAlbumName = ""; // 专辑名
	public String strSongName = ""; // 歌曲名
	public String strSingerName = ""; // 歌手名
	public String strFullPath = ""; // 完整路径
	public long lSize = 0; // 大小
	public int nBitRate = 0; // 码率
	public int nDuration = 0; // 时长
	public String strSamplingRate = ""; // 码率
	public String strOnlineHighUrl = ""; // 在线音乐地址
	public String strOnlineLowUrl = ""; // 在线音乐地址
	public String strOnlineLrcPath = ""; // 在线歌词地址
	public String strOnlineCoverPath = ""; // 在线封面地址
	public String strLocalCoverPath = ""; // 本地封面地址
	public int nLocation = Params.NET; // 是否本地

	public boolean bCache = false;

	public PlayerMusicInfo() {
	}

	public PlayerMusicInfo(Parcel in) {
		readFromParcel(in);
	}

	public static final Creator<PlayerMusicInfo> CREATOR = new Creator<PlayerMusicInfo>() {
		public PlayerMusicInfo createFromParcel(Parcel source) {
			return new PlayerMusicInfo(source);
		}

		public PlayerMusicInfo[] newArray(int size) {
			return new PlayerMusicInfo[size];
		}
	};

	public void readFromParcel(Parcel in) {
		if (null == in)
			return;

		nMusicID = in.readInt();
		nEventID = in.readInt();
		nMsgID = in.readInt();
		strAlbumName = StringUtil.newString(in.readString());
		strSongName = StringUtil.newString(in.readString());
		strSingerName = StringUtil.newString(in.readString());
		strFullPath = StringUtil.newString(in.readString());
		lSize = in.readLong();
		nBitRate = in.readInt();
		nDuration = in.readInt();
		strSamplingRate = StringUtil.newString(in.readString());
		strOnlineHighUrl = StringUtil.newString(in.readString());
		strOnlineLowUrl = StringUtil.newString(in.readString());
		strOnlineLrcPath = StringUtil.newString(in.readString());
		strOnlineCoverPath = StringUtil.newString(in.readString());
		strLocalCoverPath = StringUtil.newString(in.readString());
		nLocation = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (null == dest)
			return;

		dest.writeInt(nMusicID);
		dest.writeInt(nEventID);
		dest.writeInt(nMsgID);
		dest.writeString(strAlbumName);
		dest.writeString(strSongName);
		dest.writeString(strSingerName);
		dest.writeString(strFullPath);
		dest.writeLong(lSize);
		dest.writeInt(nBitRate);
		dest.writeInt(nDuration);
		dest.writeString(strSamplingRate);
		dest.writeString(strOnlineHighUrl);
		dest.writeString(strOnlineLowUrl);
		dest.writeString(strOnlineLrcPath);
		dest.writeString(strOnlineCoverPath);
		dest.writeString(strLocalCoverPath);
		dest.writeInt(nLocation);
	}

	public static void clone(PlayerMusicInfo dest, PlayerMusicInfo src) {
		if (null == dest || null == src)
			return;

		dest.strAlbumName = StringUtil.newString(src.strAlbumName);
		dest.strSongName = StringUtil.newString(src.strSongName);
		dest.strSingerName = StringUtil.newString(src.strSingerName);
		dest.strFullPath = StringUtil.newString(src.strFullPath);

		dest.nMusicID = src.nMusicID;
		dest.nEventID = src.nEventID;
		dest.nMsgID = src.nMsgID;
		dest.lSize = src.lSize;
		dest.nBitRate = src.nBitRate;
		dest.nDuration = src.nDuration;
		dest.strSamplingRate = StringUtil.newString(src.strSamplingRate);

		dest.strOnlineHighUrl = StringUtil.newString(src.strOnlineHighUrl);

		dest.strOnlineLowUrl = StringUtil.newString(src.strOnlineLowUrl);

		dest.strOnlineLrcPath = StringUtil.newString(src.strOnlineLrcPath);

		dest.strOnlineCoverPath = StringUtil.newString(src.strOnlineCoverPath);

		dest.strLocalCoverPath = StringUtil.newString(src.strLocalCoverPath);

		dest.nLocation = src.nLocation;
	}

	public static void Convert(PlayerMusicInfo dest, MusicInfoEntity src) {
		if (null == dest || null == src)
			return;

		dest.strAlbumName = StringUtil.newString(src.strAlbumName);
		dest.strSongName = StringUtil.newString(src.strSongName);
		dest.strSingerName = StringUtil.newString(src.strSingerName);
		dest.strFullPath = StringUtil.newString(src.strFullPath);

		dest.nMusicID = src.nMusicID;
		dest.nEventID = src.nEventID;
		dest.nMsgID = src.nMsgID;
		dest.lSize = src.lSize;
		dest.nBitRate = src.nBitRate;
		dest.nDuration = src.nDuration;
		dest.strSamplingRate = StringUtil.newString(src.strSamplingRate);

		dest.strOnlineHighUrl = StringUtil.newString(src.strOnlineHighUrl);

		dest.strOnlineLowUrl = StringUtil.newString(src.strOnlineLowUrl);

		dest.strOnlineLrcPath = StringUtil.newString(src.strOnlineLrcPath);

		dest.strOnlineCoverPath = StringUtil.newString(src.strOnlineCoverPath);

		dest.strLocalCoverPath = "";

		dest.nLocation = src.nLocation;
	}

	public static void Convert(MusicInfoEntity dest, PlayerMusicInfo src) {
		if (null == dest || null == src)
			return;

		dest.strAlbumName = StringUtil.newString(src.strAlbumName);
		dest.strSongName = StringUtil.newString(src.strSongName);
		dest.strSingerName = StringUtil.newString(src.strSingerName);
		dest.strFullPath = StringUtil.newString(src.strFullPath);

		dest.nMusicID = src.nMusicID;
		dest.nEventID = src.nEventID;
		dest.nMsgID = src.nMsgID;
		dest.lSize = src.lSize;
		dest.nBitRate = src.nBitRate;
		dest.nDuration = src.nDuration;
		dest.strSamplingRate = StringUtil.newString(src.strSamplingRate);

		dest.strOnlineHighUrl = StringUtil.newString(src.strOnlineHighUrl);

		dest.strOnlineLowUrl = StringUtil.newString(src.strOnlineLowUrl);

		dest.strOnlineLrcPath = StringUtil.newString(src.strOnlineLrcPath);

		dest.strOnlineCoverPath = StringUtil.newString(src.strOnlineCoverPath);

		dest.nLocation = src.nLocation;
	}

	public static PlayerMusicInfo convert(EventObjectEx eventObject) {
		if (null == eventObject || eventObject.getType() == 1 || eventObject.getType() == 2 || null == eventObject.getContent()
				|| null == eventObject.getContent().getSOSVoice() || TextUtils.isEmpty(eventObject.getContent().getSOSVoice().voiceUrl)) {
			return null;
		}

		PlayerMusicInfo dest = new PlayerMusicInfo();
		dest.strAlbumName = StringUtil.newString("");
		dest.strSongName = StringUtil.newString("");
		dest.strSingerName = StringUtil.newString("");
		dest.strFullPath = StringUtil.newString("");

		dest.nMusicID = eventObject.getContent().getSOSVoice().voiceSosId;
		dest.nEventID = eventObject.getEventId();
		dest.nMsgID = 0;
		dest.lSize = 0;
		dest.nBitRate = 0;
		dest.nDuration = 0;
		dest.strSamplingRate = StringUtil.newString("");

		dest.strOnlineHighUrl = StringUtil.newString(eventObject.getContent().getSOSVoice().voiceUrl);
		dest.strOnlineLowUrl = StringUtil.newString(eventObject.getContent().getSOSVoice().voiceUrl);
		dest.strOnlineLrcPath = StringUtil.newString("");
		dest.strOnlineCoverPath = StringUtil.newString("");

		return dest;
	}

	public static PlayerMusicInfo convert(MySOSObject mySOSObject) {
		if (null == mySOSObject || TextUtils.isEmpty(mySOSObject.getVoiceUrl())) {
			return null;
		}

		PlayerMusicInfo dest = new PlayerMusicInfo();
		dest.strAlbumName = StringUtil.newString("");
		dest.strSongName = StringUtil.newString("");
		dest.strSingerName = StringUtil.newString("");
		dest.strFullPath = StringUtil.newString("");

		dest.nMusicID = mySOSObject.getVoiceSosId();
		dest.nEventID = 0;
		dest.nMsgID = 0;
		dest.lSize = 0;
		dest.nBitRate = 0;
		dest.nDuration = 0;
		dest.strSamplingRate = StringUtil.newString("");

		dest.strOnlineHighUrl = StringUtil.newString(mySOSObject.getVoiceUrl());
		dest.strOnlineLowUrl = StringUtil.newString(mySOSObject.getVoiceUrl());
		dest.strOnlineLrcPath = StringUtil.newString("");
		dest.strOnlineCoverPath = StringUtil.newString("");

		return dest;
	}

	public static PlayerMusicInfo convert(MsgObject msgObject) {
		if (null == msgObject || (TextUtils.isEmpty(msgObject.getVoice()) && TextUtils.isEmpty(msgObject.getVoiceFilePath()))) {
			return null;
		}

		PlayerMusicInfo dest = new PlayerMusicInfo();
		dest.strAlbumName = StringUtil.newString("");
		dest.strSongName = StringUtil.newString("");
		dest.strSingerName = StringUtil.newString("");
		dest.strFullPath = StringUtil.newString("");

		dest.nMusicID = msgObject.getMsgId();
		dest.nEventID = 0;
		dest.nMsgID = msgObject.getMsgId();
		dest.lSize = 0;
		dest.nBitRate = 0;
		dest.nDuration = 0;
		dest.strSamplingRate = StringUtil.newString("");

		if (TextUtils.isEmpty(msgObject.getVoice())) {
			dest.strOnlineHighUrl = StringUtil.newString(msgObject.getVoiceFilePath());
			dest.strOnlineLowUrl = StringUtil.newString(msgObject.getVoiceFilePath());
		} else {
			dest.strOnlineHighUrl = StringUtil.newString(msgObject.getVoice());
			dest.strOnlineLowUrl = StringUtil.newString(msgObject.getVoice());
		}
		dest.strOnlineLrcPath = StringUtil.newString("");
		dest.strOnlineCoverPath = StringUtil.newString("");

		return dest;
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
