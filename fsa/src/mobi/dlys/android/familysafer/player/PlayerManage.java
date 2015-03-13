package mobi.dlys.android.familysafer.player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.player.download.DownloadManage;
import mobi.dlys.android.familysafer.player.net.HttpPlayer;
import mobi.dlys.android.familysafer.player.utils.Player.LoopStyle;
import mobi.dlys.android.familysafer.player.utils.Player.NextState;
import mobi.dlys.android.familysafer.player.utils.Player.Params;
import mobi.dlys.android.familysafer.player.utils.Player.PlayState;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.NetUtils;
import android.content.Context;
import android.content.Intent;

/*
 * 播放器管理
 */
public class PlayerManage {
	public final static String BUFFER_FILE = "BufferFile.dat", BUFFER_MUSIC_FILE_1 = "BufferRecordFile1.mp3", BUFFER_MUSIC_FILE_2 = "BufferRecordFile2.mp3",
			INTENT_ACTION_DOWNLOADSTATE = "mobi.dlys.android.familysafer.broadcast.downloadstate";

	public final static String ACTION_PLAYER = "mobi.dlys.android.familysafer.player"; // 播放
	public final static String ACTION_PLAYER_STATE = "mobi.dlys.android.familysafer.player.state"; // 播放状态
	public final static String ACTION_PLAYER_MUSICID = "mobi.dlys.android.familysafer.player.musicid"; // 音乐ID
	public final static String ACTION_PLAYER_EVENTID = "mobi.dlys.android.familysafer.player.eventid"; // 通知ID
	public final static String ACTION_PLAYER_MSGID = "mobi.dlys.android.familysafer.player.msgid"; // 消息ID
	public final static String ACTION_SYSTEM_PLAYER_PAUSE = "com.android.music.musicservicecommand.pause"; // 系统播放器暂停
	public final static String ACTION_SYSTEM_PLAYER_NEXT = "com.android.music.musicservicecommand.next"; // 系统播放器暂停
	public final static String ACTION_SYSTEM_PLAYER_PREVIOUS = "com.android.music.musicservicecommand.previous"; // 系统播放器暂停
	public final static String ACTION_SYSTEM_PLAYER_COMMAND = "com.android.music.musicservicecommand"; // 系统播放器暂停
	public final static String ACTION_NET_NOT_CONNECTION = "mobi.dlys.android.familysafer.player.not.net.connection"; // 没有网络，暂停播放
	public final static String ACTION_PLAY_NEXT_MUSIC = "mobi.dlys.android.familysafer.play.next.music";

	public final static String ACTION_PLAYER_CACHE = "mobi.dlys.android.familysafer.player.cache"; // 预加载
	public final static String EXTRA_PLAYER_CACHE_VIEWINFO = "mobi.dlys.android.familysafer.player.cache.viewinfo"; // 预加载
	public final static String EXTRA_PLAYER_CACHE_MUSICID = "mobi.dlys.android.familysafer.player.cache.musicid"; // 预加载

	public final static int FFT_SIZE = 128; // FFT大小
	public final static int UPDATE_MUSIC_OFFSET = 3; // 更新位置
	public final static int UPDATE_MUSIC_SIZE = 5; // 更新数量

	private int mnMusicIndex; // 音乐序号
	private int mnLastMusicIndex;
	private int mnLoopStyle; // 循环方式
	private int mnNextState; // 下一首 -1 后退 0 当前 1 前进
	private int mnPage = 0; // 页
	private boolean mbExit; // 是否退出
	private boolean mbNeedBuffer; // 是否需要缓冲
	private boolean mbContinue; // 继续下一首歌曲
	private List<PlayerMusicInfo> mlistMusicInfo;
	// private MusicApiManage mMusicApiManage = null;
	private HttpPlayer mHttpPlayer = null;
	private PlayerCore mPlayerCore = null;
	private PlayerBase mPlayerBase = null;
	private Context mContext = null;

	/*
	 * 构造
	 */
	public PlayerManage(Context context) {
		mContext = context;

		// mMusicApiManage = new MusicApiManage(context);
		mHttpPlayer = new HttpPlayer(context);

		mPlayerCore = new PlayerCore(context);

		mPlayerBase = mPlayerCore;

		mlistMusicInfo = new ArrayList<PlayerMusicInfo>();

		mnPage = 0;
		mnMusicIndex = 0;
		mnLastMusicIndex = 0;
		mnNextState = NextState.CURRENT;
		mbExit = false;
		mbNeedBuffer = false;
		mbContinue = false;
		mnLoopStyle = LoopStyle.ONLYONE;

		new Thread(new Runnable() {
			public void run() {
				threadBuffer();
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				threadPlay();
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				threadUpdateMusic();
			}
		}).start();

	}

	/*
	 * 替换当前音乐
	 */
	public void replaceMusic(int nMusicID) {
		PlayerMusicInfo musicInfo = null;

		synchronized (mlistMusicInfo) {
			musicInfo = new PlayerMusicInfo();

			musicInfo.nLocation = Params.NET;
			musicInfo.nMusicID = nMusicID;

			if (mlistMusicInfo.size() > 0) {
				if (nMusicID != mlistMusicInfo.get(mnMusicIndex).nMusicID) {

					forceStop();

					if (mnMusicIndex >= 0 && mnMusicIndex < mlistMusicInfo.size())
						mlistMusicInfo.remove(mnMusicIndex);

					mlistMusicInfo.add(mnMusicIndex, musicInfo);
					mnMusicIndex = mlistMusicInfo.indexOf(musicInfo);
				} else {
					pause();
				}
			} else {
				mlistMusicInfo.add(musicInfo);

				mnMusicIndex = 0;
			}

			play();
		}
	}

	public void replaceMusic(PlayerMusicInfo musicInfo) {
		synchronized (mlistMusicInfo) {
			stop();
			clear();
			musicInfo.nLocation = Params.NET;

			if (mlistMusicInfo.size() > 0) {
				if (musicInfo.nMusicID != mlistMusicInfo.get(mnMusicIndex).nMusicID || musicInfo.nMsgID != mlistMusicInfo.get(mnMusicIndex).nMsgID) {

					forceStop();

					// if (mnMusicIndex >= 0 && mnMusicIndex <
					// mlistMusicInfo.size())
					// mlistMusicInfo.remove(mnMusicIndex);

					mnMusicIndex = 0;
					mlistMusicInfo.clear();
					mlistMusicInfo.add(mnMusicIndex, musicInfo);

					// 20121121 Ale 修改
					// ++mnMusicIndex;
					// mnMusicIndex = mlistMusicInfo.indexOf(musicInfo);
				} else {
					pause();
				}
			} else {
				mlistMusicInfo.add(musicInfo);

				mnMusicIndex = 0;
			}

			play();
		}
	}

	/*
	 * 插入歌曲到当前歌曲之前
	 */
	public void insertPrevMusic(int nMusicID) {
		PlayerMusicInfo musicInfo = null;

		synchronized (mlistMusicInfo) {
			musicInfo = new PlayerMusicInfo();

			musicInfo.nLocation = Params.NET;
			musicInfo.nMusicID = nMusicID;

			if (mlistMusicInfo.size() > 0) {
				if (nMusicID != mlistMusicInfo.get(mnMusicIndex).nMusicID) {

					forceStop();

					mlistMusicInfo.add(mnMusicIndex, musicInfo);
					mnMusicIndex = mlistMusicInfo.indexOf(musicInfo);
				} else {
					pause();
					newSong(nMusicID, musicInfo.nEventID, musicInfo.nMsgID);
				}
			} else {
				mlistMusicInfo.add(musicInfo);

				mnMusicIndex = 0;
			}

			play();
		}
	}

	public void insertPrevMusicInfo(PlayerMusicInfo musicInfo) {
		synchronized (mlistMusicInfo) {
			musicInfo.nLocation = Params.NET;

			if (mlistMusicInfo.size() > 0) {
				if (musicInfo.nMusicID != mlistMusicInfo.get(mnMusicIndex).nMusicID) {

					forceStop();

					mlistMusicInfo.add(mnMusicIndex, musicInfo);

					// 20121121 Ale 修改
					// ++mnMusicIndex;
					// mnMusicIndex = mlistMusicInfo.indexOf(musicInfo);
				} else {
					pause();
				}
			} else {
				mlistMusicInfo.add(musicInfo);

				mnMusicIndex = 0;
			}

			play();
		}
	}

	/*
	 * 添加播放列表
	 */
	public void addMusic(int nMusicID) {
		PlayerMusicInfo musicInfo = null;

		synchronized (mlistMusicInfo) {
			musicInfo = new PlayerMusicInfo();

			musicInfo.nLocation = Params.NET;
			musicInfo.nMusicID = nMusicID;

			if (mlistMusicInfo.size() > 0) {
				if (nMusicID != mlistMusicInfo.get(mnMusicIndex).nMusicID) {

					forceStop();

					mlistMusicInfo.add(mnMusicIndex + 1, musicInfo);

					// 20121121 Ale 修改
					// ++mnMusicIndex;
					mnMusicIndex = mlistMusicInfo.indexOf(musicInfo);
				} else {
					pause();
					newSong(nMusicID, musicInfo.nEventID, musicInfo.nMsgID);
				}
			} else {
				mlistMusicInfo.add(musicInfo);

				mnMusicIndex = 0;
			}

			play();
		}
	}

	public void addMusicInfo(PlayerMusicInfo musicInfo) {
		synchronized (mlistMusicInfo) {
			musicInfo.nLocation = Params.NET;

			if (mlistMusicInfo.size() > 0) {
				if (musicInfo.nMusicID != mlistMusicInfo.get(mnMusicIndex).nMusicID) {

					forceStop();

					mlistMusicInfo.add(mnMusicIndex + 1, musicInfo);

					// 20121121 Ale 修改
					// ++mnMusicIndex;
					mnMusicIndex = mlistMusicInfo.indexOf(musicInfo);
				} else {
					pause();
				}
			} else {
				mlistMusicInfo.add(musicInfo);

				mnMusicIndex = 0;
			}

			play();
		}
	}

	/*
	 * 添加播放列表
	 */
	public void addLocalMusic(List<PlayerMusicInfo> listLocalMusic, int nIndex) {
		synchronized (mlistMusicInfo) {
			forceStop();

			mlistMusicInfo.clear();

			for (int i = 0; i < listLocalMusic.size(); ++i) {
				mlistMusicInfo.add(listLocalMusic.get(i));
			}

			mnMusicIndex = nIndex;

			play();
		}
	}

	/*
	 * 删除播放列表项
	 */
	public void deleteMusic(int nMusicID) {
		PlayerMusicInfo musicInfo = null;

		musicInfo = new PlayerMusicInfo();

		getMusicInfo(musicInfo);

		synchronized (mlistMusicInfo) {
			for (int i = 0; i < mlistMusicInfo.size(); ++i) {
				if (nMusicID == mlistMusicInfo.get(i).nMusicID) {
					mlistMusicInfo.remove(i);

					if (mnMusicIndex > i) {
						--mnMusicIndex;
					}

					break;
				}
			}
		}

		if ((musicInfo.nMusicID == nMusicID) && (musicInfo.nMusicID != 0)) {
			next();
		}
	}

	/*
	 * 获取音乐信息
	 */
	public void getMusicInfo(PlayerMusicInfo musicInfo) {
		synchronized (mlistMusicInfo) {
			if ((mnMusicIndex < mlistMusicInfo.size()) && (mlistMusicInfo.size() > 0)) {
				PlayerMusicInfo.clone(musicInfo, mlistMusicInfo.get(mnMusicIndex));
			}
		}
	}

	/*
	 * 获取音乐数量
	 */
	private int getCount() {
		int nRet = 0;

		synchronized (mlistMusicInfo) {
			nRet = mlistMusicInfo.size();
		}

		return nRet;
	}

	/*
	 * 获取音乐位置
	 */
	private int getIndex() {
		return mnMusicIndex;
	}

	/*
	 * 获取音乐信息
	 */
	public PlayerMusicInfo getFixMusicInfo() {
		PlayerMusicInfo musicInfo = null;

		synchronized (mlistMusicInfo) {
			if (mlistMusicInfo.size() > 0) {
				musicInfo = new PlayerMusicInfo();

				mnMusicIndex = (mnMusicIndex < 0) ? mlistMusicInfo.size() - 1 : mnMusicIndex;

				mnMusicIndex = (mnMusicIndex >= mlistMusicInfo.size()) ? 0 : mnMusicIndex;

				PlayerMusicInfo.clone(musicInfo, mlistMusicInfo.get(mnMusicIndex));
			}
		}

		return musicInfo;
	}

	/*
	 * 获取音乐信息对象
	 */
	public PlayerMusicInfo getFixMusicInfoObject() {
		synchronized (mlistMusicInfo) {
			if (mlistMusicInfo.size() > 0) {

				mnMusicIndex = (mnMusicIndex < 0) ? mlistMusicInfo.size() - 1 : mnMusicIndex;

				mnMusicIndex = (mnMusicIndex >= mlistMusicInfo.size()) ? 0 : mnMusicIndex;

				return mlistMusicInfo.get(mnMusicIndex);
			}
		}

		return null;
	}

	/*
	 * 销毁
	 */
	public void destroy() {
		forceStop();

		mPlayerCore.destroy();
		mHttpPlayer.destroy();

		mbExit = true;
	}

	/*
	 * 是否播放状态
	 */
	public int isPlaying() {
		return mPlayerBase.isPlaying();
	}

	/*
	 * 获取播放状态
	 */
	public int getPlayState() {
		return mPlayerBase.getPlayState();
	}

	/*
	 * 获取循环方式
	 */
	public int getLoopStyle() {
		return mnLoopStyle;
	}

	/*
	 * 设置循环方式
	 */
	public void setLoopStyle(int nLoopStyle) {
		mnLoopStyle = nLoopStyle;
	}

	/*
	 * 设置下一个循环方式
	 */
	public int setNextLoopStyle() {
		++mnLoopStyle;

		if (mnLoopStyle > LoopStyle.ONLYONE) {
			mnLoopStyle = LoopStyle.CYCLEALL;
		}

		return mnLoopStyle;
	}

	/*
	 * 前一首
	 */
	public void prev() {
		mnNextState = NextState.PREV;
		mbContinue = true;
	}

	/*
	 * 暂停
	 */
	public void pause() {
		mPlayerBase.pause();
	}

	/*
	 * 停止
	 */
	public void stop() {
		mPlayerBase.stop();
	}

	/*
	 * 清除
	 */
	public void clear() {
		mPlayerBase.clear();
		synchronized (mlistMusicInfo) {
			mnMusicIndex = 0;
			mlistMusicInfo.clear();
		}
	}

	/*
	 * 下一首
	 */
	public void next() {
		mnNextState = NextState.NEXT;
		mbContinue = true;
	}

	/*
	 * 修正下一首
	 */
	private void fixNext() {
		if (NetUtils.NET_NOT_CONNECTION == NetUtils.checkNet()) {
			if (mnLastMusicIndex < mnMusicIndex) {
				mnLastMusicIndex = mnMusicIndex;
			}

			if (mnMusicIndex > 0) {
				mnMusicIndex = getCount();
			}
		} else if ((mnLastMusicIndex >= 1) && (mnLastMusicIndex > mnMusicIndex)) {
			mnMusicIndex = mnLastMusicIndex - 1;
		}
	}

	/*
	 * 本地音乐下一首
	 */
	public void localNext() {
		PlayerMusicInfo musicInfo = null;

		musicInfo = getFixMusicInfo();

		if ((musicInfo != null) && (musicInfo.nLocation == Params.LOCAL) && (PlayState.PLAY == isPlaying())) {
			next();
		}
	}

	/*
	 * 播放完成后下一首
	 */
	public void finishNext() {
		mnNextState = NextState.FINISH_NEXT;
		mbContinue = true;
	}

	/*
	 * 播放
	 */
	public void play() {
		switch (mPlayerBase.isPlaying()) {
		case PlayState.STOP: {
			mnNextState = NextState.CURRENT;
			mbContinue = true;
		}
			break;

		case PlayState.BUFFER_FAIL:
		case PlayState.PAUSE: {
			mPlayerBase.play();
		}
			break;
		}
	}

	/*
	 * 获取总长度
	 */
	public int getDuration() {
		return mPlayerBase.getDuration();
	}

	/*
	 * 获取缓冲总长度
	 */
	public int getBufferDuration() {
		return mPlayerBase.getBufferDuration();
	}

	/*
	 * 获取播放位置
	 */
	public int getCurrentPosition() {
		return mPlayerBase.getCurrentPosition();
	}

	/*
	 * 设置播放位置
	 */
	public void setPosition(int nPosition) {
		mPlayerBase.setPosition(nPosition);
	}

	/*
	 * 强制停止播放
	 */
	public void forceStop() {
		mPlayerBase.forceStop();
	}

	/*
	 * 获取频谱数据
	 */
	public byte[] getFFTData() {
		return mPlayerBase.getFFTData();
	}

	/*
	 * 如果在线歌曲本地缓冲完成, 使用本地歌曲
	 */
	public static void fixLocalDownloadMusic(PlayerMusicInfo musicInfo) {
		File fileDownload = null;
		String strPath = "";
		String strOldPath = "";

		try {
			musicInfo.strFullPath = "";

//			strPath = strOldPath = new String(FileUtils.VOICE + musicInfo.nMusicID + "_" + musicInfo.nMsgID + DownloadManage.MP3_TAG);
			strPath = new String(FileUtils.VOICE + "voice_" + musicInfo.nMusicID + "_" + musicInfo.nMsgID + DownloadManage.MP3_TAG);

			if (FileUtils.exists(strOldPath)) {
				FileUtils.renameFile(strPath, strOldPath);
			}

			fileDownload = new File(strPath);

			if (fileDownload.exists() && fileDownload.isFile()) {
				musicInfo.strFullPath = new String(strPath);

				musicInfo.nLocation = Params.LOCAL;
			}
		} catch (Exception e) {
		}
	}

	/*
	 * 缓冲线程
	 */
	private void threadBuffer() {
		PlayerMusicInfo musicInfo = null;

		while (!mbExit) {
			try {
				if (mbNeedBuffer) {
					mPlayerBase.forceStop();

					// musicInfo = getFixMusicInfo();
					musicInfo = getFixMusicInfoObject();

					if (musicInfo != null) {
						if (musicInfo.nLocation == Params.NET) {
							fixLocalDownloadMusic(musicInfo);
						}

						mPlayerBase = (musicInfo.nLocation == Params.LOCAL) ? mPlayerCore : mHttpPlayer;

						if (musicInfo.nLocation == Params.LOCAL) {
							newSong(musicInfo.nMusicID, musicInfo.nEventID, musicInfo.nMsgID);

							mPlayerBase.buffer(musicInfo);
						} else {
							// 没有网络提示用户
							if (NetUtils.NET_NOT_CONNECTION == NetUtils.checkNet()) {
								mPlayerBase.pause();
								YSToast.showToast(mContext, "没有缓冲文件");
								try {
									Thread.sleep(102);
								} catch (Exception e) {
								}
							} else {
								newSong(musicInfo.nMusicID, musicInfo.nEventID, musicInfo.nMsgID);

								mPlayerBase.buffer(musicInfo);
							}

							/*
							 * try { strUrl = mMusicApiManage .confirmDownload(
							 * "" + musicInfo.nMusicId, NetUtil.isProxy ?
							 * Parameter.SUBITEMTYPE_MP3_LOW_QUALITY :
							 * Parameter.SUBITEMTYPE_MP3_HIGH_QUALITY); } catch
							 * (Exception e) { strUrl = null; } if (strUrl !=
							 * null) { musicInfo.strOnlinelMusicPath = new
							 * String( strUrl); mPlayerBase.buffer(musicInfo); }
							 */
						}
					}
					/*
					 * bufferMusicFile(musicInfo.strFullPath, (musicInfo.nLocal
					 * == Params.LOCAL_MUSIC) ? true : false);
					 */

					mbNeedBuffer = false;
				}
			} catch (Exception e) {
				mbNeedBuffer = false;
			}

			try {
				Thread.sleep(102);
			} catch (Exception e) {
			}
		}
	}

	/*
	 * 更新当前序号
	 */
	private void updateMusicIndex(boolean bNext) {
		synchronized (mlistMusicInfo) {
			if (mlistMusicInfo.size() > 0) {
				mnMusicIndex += bNext ? 1 : -1;

				mnMusicIndex = (mnMusicIndex < 0) ? mlistMusicInfo.size() - 1 : mnMusicIndex;

				mnMusicIndex = (mnMusicIndex >= mlistMusicInfo.size()) ? 0 : mnMusicIndex;
			}
		}
	}

	/*
	 * 更新播放器状态
	 */
	private void newSong(int nMusicId, int nEventId, int nMsgId) {
		Intent intent = null;

		try {
			intent = new Intent();

			intent.setAction(PlayerManage.ACTION_PLAYER);
			intent.putExtra(PlayerManage.ACTION_PLAYER_STATE, PlayState.NEW_SONG);
			intent.putExtra(PlayerManage.ACTION_PLAYER_MUSICID, nMusicId);
			intent.putExtra(PlayerManage.ACTION_PLAYER_EVENTID, nEventId);
			intent.putExtra(PlayerManage.ACTION_PLAYER_MSGID, nMsgId);

			mContext.sendBroadcast(intent);
		} catch (Exception e) {
		}
	}

	/*
	 * 广播没有网络状态
	 */
	private void notNetWork() {
		Intent intent = null;

		try {
			intent = new Intent();

			intent.setAction(PlayerManage.ACTION_NET_NOT_CONNECTION);

			mContext.sendBroadcast(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 播放下一首歌曲
	 */
	private boolean playNextMusic() {
		return false;
	}

	/*
	 * 播放线程
	 */
	private void threadPlay() {
		while (!mbExit) {
			try {
				if (mbContinue) {
					/*
					 * 播放循环类型
					 */
					switch (mnLoopStyle) {
					/*
					 * 所有循环
					 */
					case LoopStyle.CYCLEALL: {
						switch (mnNextState) {
						case NextState.FINISH_NEXT:
						case NextState.NEXT: {
							if (NetUtils.NET_NOT_CONNECTION == NetUtils.checkNet()) {
								mPlayerBase.pause();
								notNetWork();
								break;
							} else {
								playNextMusic();
								fixNext();
								updateMusicIndex(true);
							}
						}
							break;

						case NextState.PREV: {
							updateMusicIndex(false);
						}
							break;
						}
					}
						break;

					/*
					 * 随机播放
					 */
					case LoopStyle.RANDOM: {
						switch (mnNextState) {
						case NextState.FINISH_NEXT:
						case NextState.NEXT:
						case NextState.PREV: {
							if (NetUtils.NET_NOT_CONNECTION == NetUtils.checkNet()) {
								mPlayerBase.pause();
								notNetWork();
								break;
							} else {
								playNextMusic();
								// mnMusicIndex = (int) (Math.random() *
								// mlistMusicInfo
								// .size());
							}
						}
							break;
						}
					}
						break;

					/*
					 * 单曲循环
					 */
					case LoopStyle.CYCLEONE: {
						switch (mnNextState) {
						case NextState.NEXT: {
							fixNext();
							updateMusicIndex(true);
						}
							break;

						case NextState.PREV: {
							updateMusicIndex(false);
						}
							break;
						}
					}
						break;

					/*
					 * 单曲
					 */
					case LoopStyle.ONLYONE: {
						switch (mnNextState) {
						case NextState.NEXT:
						case NextState.FINISH_NEXT: {
							mPlayerBase.pause();
							clear();
						}
							break;
						}
						if (NetUtils.NET_NOT_CONNECTION == NetUtils.checkNet()) {
							notNetWork();
						}
					}
						break;

					}

					mbNeedBuffer = true;
					mbContinue = false;
				}
			} catch (Exception e) {
			}

			try {
				Thread.sleep(103);
			} catch (Exception e) {
			}
		}
	}

	/*
	 * 更新音乐线程
	 */
	private void threadUpdateMusic() {

	}

	private List<PlayerMusicInfo> mCacheMusicInfos = new ArrayList<PlayerMusicInfo>();
	private Thread mCacheThread;

	/**
	 * 预加载 歌曲故事内容
	 * 
	 * @param mHandler
	 * @param cacheLen
	 */
	public boolean cacheMusicData(int musicId, int cacheLen) {
		boolean isCache = false;

		for (int index = getIndex(), cache = -1, len = mlistMusicInfo.size(); index < len && cache < cacheLen; index++) {
			PlayerMusicInfo playerMusicInfo = mlistMusicInfo.get(index);
			// 从指定的歌曲开始预加载
			if (musicId == playerMusicInfo.nMusicID) {
				cache = 0;
				isCache = playerMusicInfo.bCache;
				if (isCache)
					LogUtils.e("CacheMusicData", musicId + " 已经缓存");
			}
			if (cache != -1) {
				// 开始预加载
				if (!mCacheMusicInfos.contains(playerMusicInfo)) {
					mCacheMusicInfos.add(playerMusicInfo);
				}
				cache++;
			}
		}

		if (null == mCacheThread || !mCacheThread.isAlive()) {
			mCacheThread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (mCacheMusicInfos.size() > 0) {
						PlayerMusicInfo playerMusicInfo = mCacheMusicInfos.get(0);
						if (null != playerMusicInfo) {
							try {

								int musicId = playerMusicInfo.nMusicID;

								// 获取数据后发送广播，通知界面刷新
								Intent intent = new Intent(PlayerManage.ACTION_PLAYER_CACHE);
								intent.putExtra(EXTRA_PLAYER_CACHE_MUSICID, musicId);
								mContext.sendBroadcast(intent);

								playerMusicInfo.bCache = true;
								mCacheMusicInfos.remove(playerMusicInfo);
								LogUtils.e("CacheMusicData", musicId + " 已经缓存");
							} catch (Exception e) {

								e.printStackTrace();
								playerMusicInfo.bCache = false;
							}
						}

						try {
							Thread.sleep(2 * 1000);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
					}
				}
			});
			mCacheThread.start();
		}

		return isCache;
	}
}
