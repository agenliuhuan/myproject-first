package mobi.dlys.android.familysafer.player.entity;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.player.PlayerMusicInfo;
import android.os.Parcel;
import android.os.Parcelable;

public class MusicInfoListEntity implements Parcelable {
	private List<MusicInfoEntity> mMusicInfoEntityList = new ArrayList<MusicInfoEntity>();

	public MusicInfoListEntity() {
	}

	public MusicInfoListEntity(Parcel in) {
		readFromParcel(in);
	}

	public int describeContents() {
		return 0;
	}

	public void readFromParcel(Parcel in) {
		in.readTypedList(mMusicInfoEntityList, MusicInfoEntity.CREATOR);
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(mMusicInfoEntityList);
	}

	public static final Creator<MusicInfoListEntity> CREATOR = new Creator<MusicInfoListEntity>() {
		public MusicInfoListEntity createFromParcel(Parcel source) {
			return new MusicInfoListEntity(source);
		}

		public MusicInfoListEntity[] newArray(int size) {
			return new MusicInfoListEntity[size];
		}
	};

	public void addMusicInfoEntity(List<PlayerMusicInfo> listMusicInfo) {
		MusicInfoEntity musicInfoEntity = null;

		for (int i = 0; i < listMusicInfo.size(); ++i) {
			musicInfoEntity = new MusicInfoEntity();

			PlayerMusicInfo.Convert(musicInfoEntity, listMusicInfo.get(i));

			mMusicInfoEntityList.add(musicInfoEntity);
		}
	}

	public List<MusicInfoEntity> getMusicInfoEntity() {
		return mMusicInfoEntityList;
	}
}