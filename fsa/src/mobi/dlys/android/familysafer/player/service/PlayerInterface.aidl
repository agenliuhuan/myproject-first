package mobi.dlys.android.familysafer.player.service;

import mobi.dlys.android.familysafer.player.entity.MusicInfoEntity;
import mobi.dlys.android.familysafer.player.entity.MusicInfoListEntity;
import mobi.dlys.android.familysafer.player.PlayerMusicInfo;

interface PlayerInterface {
	void IPlay();
	void IPrev();
	void INext();
	void ILocalNext();
	void IFinishNext();
	void IPause();
	void IStop();	
	void IClear();
	int IIsPlaying();
	int IGetPlayState();
	void IReplaceMusic(int nMusicID);
	void IInsertPrevMusic(int nMusicID);
	void IAddMusic(in int nMusicID);
    void IReplaceMusicInfo(inout PlayerMusicInfo playerMusicInfo);
	void IInsertPrevMusicInfo(inout PlayerMusicInfo playerMusicInfo);
	void IAddMusicInfo(inout PlayerMusicInfo playerMusicInfo);
	void IAddLocalMusic(inout MusicInfoListEntity musicInfoListEntity, int nIndex);
	void IDeleteMusicList(int nMusicID);
	void IGetMusicInfo(inout MusicInfoEntity musicInfoEntity);
	int IGetDuration();
	int IGetCurrentPosition();
	int IGetBufferDuration();
	void ISetPosition(int nPosition);
	int IGetLoopStyle();
	void ISetLoopStyle(int nLoopStyle);
	int ISetNextLoopStyle();
	byte[] IGetFFTData();
	void IAddDownloadMusic(inout MusicInfoEntity musicInfoEntity);
	void IDeleteDownloadMusic(int nMusicID, int nMsgID);
	void IPauseDownloadMusic(int nMusicID);
	void IResumeDownloadMusic(int nMusicID);
}