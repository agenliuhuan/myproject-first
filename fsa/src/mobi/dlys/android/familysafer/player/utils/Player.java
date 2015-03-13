package mobi.dlys.android.familysafer.player.utils;

import java.util.List;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.player.PlayerMusicInfo;
import mobi.dlys.android.familysafer.player.entity.MusicInfoEntity;
import mobi.dlys.android.familysafer.player.entity.MusicInfoListEntity;
import android.content.Context;
import android.content.Intent;

/*
 * 播放器
 */
public class Player {
	/*
	 * 参数
	 */
	public static class Params {
		public final static int LOCAL = 0, NET = 1, BITRATE_LOW_QUALITY = 2, BITRATE_HIGH_QUALITY = 3;
	}

	/*
	 * 播放循环方式
	 */
	public static class LoopStyle {
		public final static int CYCLEALL = 0, RANDOM = 1, CYCLEONE = 2, ONLYONE = 3;
	}

	/*
	 * 播放状态
	 */
	public static class PlayState {
		public final static int NEW_SONG = -3, BUFFER_FAIL = -2, STOP = -1, PAUSE = 0, PLAY = 1, BUFFER_PLAY = 2, BUFFER = 3, BUFFER_PAUSE = 4, PREPARE = 5,
				BUFFER_PREPARE = 6;
	}

	/*
	 * 错误值
	 */
	public static class PlayErrorCode {
		public final static int ERROR = -1, FALSE = 0, TRUE = 1;
	}

	/*
	 * 下一首方向
	 */
	public static class NextState {
		public final static int PREV = -1, CURRENT = 0, NEXT = 1, FINISH_NEXT = 2;
	}

	/*
	 * 广播参数
	 */
	public static class BroadcastParam {
		public final static String DOWNLOAD_PROGRESS = "DownLoadProgress", PLAY_PROGRESS = "PlayProgress", STATE = "State", CURRENT = "Current", MAX = "Max";
	}

	/*
	 * 暂停系统播放器
	 */
	public static void pauseSystemPlayer(Context context) {
		Intent intent = null;

		try {
			intent = new Intent();

			intent.setAction("com.android.music.musicservicecommand.pause");
			intent.putExtra("command", "pause");

			context.sendBroadcast(intent);

			intent = new Intent();

			intent.setAction("com.android.music.musicservicecommand");
			intent.putExtra("command", "stop");

			context.sendBroadcast(intent);
		} catch (Exception e) {
		}

	}

	/*
	 * 是否播放状态
	 */
	public static boolean isPlaying() {
		boolean bRet = false;

		try {
			if (App.getInstance().getPlayerInterface().IIsPlaying() == PlayErrorCode.TRUE) {
				bRet = true;
			}
		} catch (Exception e) {
			bRet = false;
		}

		return bRet;
	}

	/*
	 * 是否正在播放
	 */
	public static int getPlayState() {
		int nRet = PlayState.STOP;

		try {
			nRet = App.getInstance().getPlayerInterface().IGetPlayState();
		} catch (Exception e) {
			nRet = PlayState.STOP;
		}

		return nRet;
	}

	/*
	 * 播放
	 */
	public static void play() {
		try {
			App.getInstance().getPlayerInterface().IPlay();
		} catch (Exception e) {
		}
	}

	/*
	 * 上一首
	 */
	public static void prev() {
		try {
			App.getInstance().getPlayerInterface().IPrev();
		} catch (Exception e) {
		}
	}

	/*
	 * 下一首
	 */
	public static void next() {
		try {
			App.getInstance().getPlayerInterface().INext();
		} catch (Exception e) {
		}
	}

	/*
	 * 本地音乐下一首
	 */
	public static void localNext() {
		try {
			App.getInstance().getPlayerInterface().ILocalNext();
		} catch (Exception e) {
		}
	}

	/*
	 * 播放完成后下一首
	 */
	public static void finishNext() {
		try {
			App.getInstance().getPlayerInterface().IFinishNext();
		} catch (Exception e) {
		}
	}

	/*
	 * 暂停
	 */
	public static void pause() {
		try {
			App.getInstance().getPlayerInterface().IPause();
		} catch (Exception e) {
		}
	}

	/*
	 * 暂停
	 */
	public static void stop() {
		try {
			App.getInstance().getPlayerInterface().IStop();
		} catch (Exception e) {
		}
	}

	/*
	 * 清除
	 */
	public static void clear() {
		try {
			App.getInstance().getPlayerInterface().IClear();
		} catch (Exception e) {
		}
	}

	/*
	 * 删除播放列表
	 */
	public static void deleteMusicList(int nMusicID) {
		try {
			App.getInstance().getPlayerInterface().IDeleteMusicList(nMusicID);
		} catch (Exception e) {
		}
	}

	/*
	 * 替换当前音乐
	 */
	public static void replaceMusic(int nMusicID) {
		try {
			App.getInstance().getPlayerInterface().IReplaceMusic(nMusicID);
		} catch (Exception e) {
		}
	}

	/*
	 * 插入歌曲到当前歌曲之前
	 */
	public static void insertPrevMusic(int nMusicID) {
		try {
			App.getInstance().getPlayerInterface().IInsertPrevMusic(nMusicID);
		} catch (Exception e) {
		}
	}

	/*
	 * 替换当前音乐
	 */
	public static void replaceMusic(PlayerMusicInfo musicInfo) {
		try {
			App.getInstance().getPlayerInterface().IReplaceMusicInfo(musicInfo);
		} catch (Exception e) {
		}
	}

	/*
	 * 插入歌曲到当前歌曲之前
	 */
	public static void insertPrevMusic(PlayerMusicInfo musicInfo) {
		try {
			App.getInstance().getPlayerInterface().IInsertPrevMusicInfo(musicInfo);
		} catch (Exception e) {
		}
	}

	/*
	 * 添加播放列表
	 */
	public static void addMusic(int nMusicID) {
		try {
			App.getInstance().getPlayerInterface().IAddMusic(nMusicID);
		} catch (Exception e) {
		}
	}

	public static void addMusicInfo(PlayerMusicInfo musicInfo) {
		try {
			App.getInstance().getPlayerInterface().IAddMusicInfo(musicInfo);
		} catch (Exception e) {
		}
	}

	/*
	 * 添加播放列表
	 */
	public static void addLocalMusic(List<PlayerMusicInfo> listMusicInfo, int nIndex) {
		MusicInfoListEntity musicInfoListEntity = null;

		try {
			musicInfoListEntity = new MusicInfoListEntity();

			musicInfoListEntity.addMusicInfoEntity(listMusicInfo);

			App.getInstance().getPlayerInterface().IAddLocalMusic(musicInfoListEntity, nIndex);
		} catch (Exception e) {
		}
	}

	/*
	 * 添加播放列表
	 */
	public static void addLocalMusic(MusicInfoListEntity musicInfoListEntity, int nIndex) {
		try {
			App.getInstance().getPlayerInterface().IAddLocalMusic(musicInfoListEntity, nIndex);
		} catch (Exception e) {
		}
	}

	/*
	 * 获取音乐信息
	 */
	public static void getMusicInfo(MusicInfoEntity musicInfoEntity) {
		try {
			App.getInstance().getPlayerInterface().IGetMusicInfo(musicInfoEntity);
		} catch (Exception e) {
		}

	}

	/*
	 * 获取音乐信息
	 */
	public static void getMusicInfo(PlayerMusicInfo musicInfo) {
		MusicInfoEntity musicInfoEntity = null;

		try {
			musicInfoEntity = new MusicInfoEntity();

			App.getInstance().getPlayerInterface().IGetMusicInfo(musicInfoEntity);

			PlayerMusicInfo.Convert(musicInfo, musicInfoEntity);
		} catch (Exception e) {
		}
	}

	/*
	 * 获取播放时长
	 */
	public static int getDuration() {
		int nRet = PlayErrorCode.ERROR;

		try {
			nRet = App.getInstance().getPlayerInterface().IGetDuration();
		} catch (Exception e) {
			nRet = PlayErrorCode.ERROR;
		}

		return nRet;
	}

	/*
	 * 获取缓冲位置
	 */
	public static int getBufferDuration() {
		int nRet = PlayErrorCode.ERROR;

		try {
			nRet = App.getInstance().getPlayerInterface().IGetBufferDuration();
		} catch (Exception e) {
			nRet = PlayErrorCode.ERROR;
		}

		return nRet;
	}

	/*
	 * 获取播放位置
	 */
	public static int getCurrentPosition() {
		int nRet = PlayErrorCode.ERROR;

		try {
			nRet = App.getInstance().getPlayerInterface().IGetCurrentPosition();
		} catch (Exception e) {
			nRet = PlayErrorCode.ERROR;
		}

		return nRet;
	}

	/*
	 * 设置播放位置
	 */
	public static void setPosition(int nPosition) {
		try {
			App.getInstance().getPlayerInterface().ISetPosition(nPosition);
		} catch (Exception e) {
		}
	}

	/*
	 * 获取播放位置
	 */
	public static int getLoopStyle() {
		int nRet = LoopStyle.CYCLEALL;

		try {
			nRet = App.getInstance().getPlayerInterface().IGetLoopStyle();
		} catch (Exception e) {
			nRet = LoopStyle.CYCLEALL;
		}

		return nRet;
	}

	/*
	 * 设置播放位置
	 */
	public static void setLoopStyle(int nLoopStyle) {
		try {
			App.getInstance().getPlayerInterface().ISetLoopStyle(nLoopStyle);
		} catch (Exception e) {
		}
	}

	/*
	 * 设置下一个循环方式
	 */
	public static int setNextLoopStyle() {
		int nRet = LoopStyle.CYCLEALL;

		try {
			nRet = App.getInstance().getPlayerInterface().ISetNextLoopStyle();
		} catch (Exception e) {
			nRet = LoopStyle.CYCLEALL;
		}

		return nRet;
	}

	/*
	 * 获取频谱数据
	 */
	public static byte[] getFFTData() {
		byte byRet[] = null;

		try {
			byRet = App.getInstance().getPlayerInterface().IGetFFTData();
		} catch (Exception e) {
			byRet = null;
		}

		return byRet;
	}

	/*
	 * 增加下载歌曲
	 */
	public static void AddDownloadMusic(MusicInfoEntity musicInfoEntity) {
		try {
			App.getInstance().getPlayerInterface().IAddDownloadMusic(musicInfoEntity);
		} catch (Exception e) {
		}
	}

	/*
	 * 删除下载歌曲
	 */
	public static void deleteDownloadMusic(int nMusicID, int nMsgID) {
		try {
			App.getInstance().getPlayerInterface().IDeleteDownloadMusic(nMusicID, nMsgID);
		} catch (Exception e) {
		}
	}

	/*
	 * 暂停下载歌曲
	 */
	public static void pauseDownloadMusic(int nMusicID) {
		try {
			App.getInstance().getPlayerInterface().IPauseDownloadMusic(nMusicID);
		} catch (Exception e) {
		}
	}

	/*
	 * 暂停下载歌曲
	 */
	public static void resumeDownloadMusic(int nMusicID) {
		try {
			App.getInstance().getPlayerInterface().IResumeDownloadMusic(nMusicID);
		} catch (Exception e) {
		}
	}

}
