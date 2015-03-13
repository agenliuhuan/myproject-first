package mobi.dlys.android.familysafer.player.entity;

import mobi.dlys.android.core.commonutils.StringUtil;
import mobi.dlys.android.familysafer.player.utils.Player.Params;
import android.os.Parcel;
import android.os.Parcelable;

/*
 * 音乐信息
 */
public class MusicInfoEntity implements Parcelable {

	public int nMusicID = 0;
	public int nEventID = 0;
	public int nMsgID = 0;
	public String strAlbumName = "";
	public String strSongName = "";
	public String strSingerName = "";
	public String strFullPath = "";
	public long lSize = 0;
	public int nBitRate = 0;
	public int nDuration = 0;
	public String strSamplingRate = "";
	public String strOnlineHighUrl = "";
	public String strOnlineLowUrl = "";
	public String strOnlineLrcPath = "";
	public String strOnlineCoverPath = "";
	public int nLocation = Params.LOCAL;

	public MusicInfoEntity() {
	}

	public MusicInfoEntity(Parcel in) {
		readFromParcel(in);
	}

	public int describeContents() {
		return 0;
	}

	public void readFromParcel(Parcel in) {
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
		nLocation = in.readInt();
	}

	public void writeToParcel(Parcel dest, int flags) {
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
		dest.writeInt(nLocation);
	}

	public static final Creator<MusicInfoEntity> CREATOR = new Creator<MusicInfoEntity>() {
		public MusicInfoEntity createFromParcel(Parcel source) {
			return new MusicInfoEntity(source);
		}

		public MusicInfoEntity[] newArray(int size) {
			return new MusicInfoEntity[size];
		}
	};
}