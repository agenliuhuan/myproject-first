package mobi.dlys.android.familysafer.player.net;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.player.PlayerBase;
import mobi.dlys.android.familysafer.player.PlayerManage;
import mobi.dlys.android.familysafer.player.PlayerMusicInfo;
import mobi.dlys.android.familysafer.player.download.DownloadManage.DownLoadInfo;
import mobi.dlys.android.familysafer.player.download.DownloadManage.DownloadState;
import mobi.dlys.android.familysafer.player.download.PoolDownload;
import mobi.dlys.android.familysafer.player.download.PoolDownload.PoolDownloadInfo;
import mobi.dlys.android.familysafer.player.net.HttpDownLoad.CancelState;
import mobi.dlys.android.familysafer.player.utils.Player;
import mobi.dlys.android.familysafer.player.utils.Player.Params;
import mobi.dlys.android.familysafer.player.utils.Player.PlayState;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.VolumeUtils;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/*
 * 播放器增强
 */
public class HttpPlayer extends PlayerBase implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
		MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
	private final static String TAG = HttpPlayer.class.getSimpleName();
	public final static String BUFFER_MUSIC = FileUtils.BUFFER + "record.buf"; // 音乐ID

	public final static int FFT_SIZE = 128; // FFT大小

	private final static int PLAY_HIGH_QUALITY_BUFFER_SIZE = 128 * 1024; // 高品质播放缓冲大小
	private final static int PAUSE_HIGH_QUALITY_BUFFER_SIZE = 512 * 1024; // 高品质播放缓冲大小
	private final static int PLAY_LOW_QUALITY_BUFFER_SIZE = 32 * 1024; // 低品质播放缓冲大小
	private final static int PAUSE_LOW_QUALITY_BUFFER_SIZE = 128 * 1024; // 低品质播放缓冲大小

	/*
	 * 下载错误信息
	 */
	public class DownLoadError {
		public final static int FINISH = -2; // 已经完成下载
		public final static int RANGE_MODE = -2; // 断点续传模式
		public final static int NORMAL_MODE = -1; // 正常下载模式
		public final static int SUCCESS = 0; // 成功
		public final static int FAIL = 1; // 失败
		public final static int GET_URL_ERROR = 2; // 无法取得歌曲URL地址
		public final static int CONNECT_URL_ERROR = 3; // 连接歌曲URL地址失败
		public final static int SOCKET_ERROR = 4; // 网络出现异常
		public final static int FILE_SIZE_ERROR = 5; // 文件大小不一致
	}

	private List<DownLoadInfo> mlistDownloadInfo = null; // 下载文件信息
	// private MusicApiManage mMusicApiManage = null; // 音乐API
	private DownLoadInfo mDownLoadInfo = null; // 下载信息
	private Context mContext = null; // 上下文
	private int mnCancelState = CancelState.DOWNLOAD; // 取消

	private MediaPlayer mMediaPlayer = null; // 播放器
	private Visualizer mVisualizer = null; // 波形
	private PlayerMusicInfo mMusicInfo = null;
	private Handler mHandler = null;
	private File mFile = null;

	private byte mbyFFTData[] = null; // 波形数据
	private int mnCurrentPosition = 0; // 当前位置
	private int mnBufferDuration = 0; // 缓冲长度
	private int mnDuration = 0; // 总长度
	private int mnPlayState = PlayState.STOP; // 播放状态
	private int mnTotal = 0; // 文件总长度
	private int mnCurrent = 0;// 当前大小
	private int mnPause = 0; // 暂停大小
	private int mnMusicID = 0; // 音乐ID
	private int mnQuality = Params.BITRATE_HIGH_QUALITY; // 音乐品质
	private int mnPlayBuffer = 0;
	private int mnPauseBuffer = 0;
	private int mnDownloadState = 0;
	private boolean mbExit = false;

	/*
	 * 构造
	 */
	public HttpPlayer(Context context) {
		mContext = context;

		mlistDownloadInfo = new ArrayList<DownLoadInfo>();
		mDownLoadInfo = new DownLoadInfo();
		// mMusicApiManage = new MusicApiManage(context);
		mbyFFTData = new byte[FFT_SIZE];

		// mMediaPlayer = new MediaPlayer();
		mMusicInfo = new PlayerMusicInfo();

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				updateBufferInfo(msg);
			}
		};

		mbExit = false;

		refreshMediaPlayer();

		new Thread(new Runnable() {
			public void run() {
				updateCurrentPosition();
			}
		}).start();
	}

	/*
	 * 缓冲消息
	 */
	private void updateBufferInfo(Message message) {
		FileInputStream fileInputStream = null;
		Bundle bundle = null;
		int nMusicID = 0, nID = 0, nMsgID = 0;
		boolean bStatus = false;
		int i;

		try {
			bundle = message.getData();
			nMusicID = bundle.getInt(PoolDownload.POOLDOWNLOAD_MUSICID);
			nMsgID = bundle.getInt(PoolDownload.POOLDOWNLOAD_MSGID);

			if (nMusicID == mMusicInfo.nMusicID && nMsgID == mMusicInfo.nMsgID) {
				nID = bundle.getInt(PoolDownload.POOLDOWNLOAD_ID);

				mnTotal = bundle.getInt(PoolDownload.POOLDOWNLOAD_TOTAL);
				mnCurrent = bundle.getInt(PoolDownload.POOLDOWNLOAD_CURRENT);
				mnQuality = (bundle.getInt(PoolDownload.POOLDOWNLOAD_BITRATE) == Params.BITRATE_HIGH_QUALITY) ? Params.BITRATE_HIGH_QUALITY
						: Params.BITRATE_LOW_QUALITY;

				mnDownloadState = bundle.getInt(PoolDownload.POOLDOWNLOAD_STATE);

				if (mnDownloadState == DownloadState.FAIL) {
					if (mnPlayState == PlayState.BUFFER) {
						setPlayState(PlayState.BUFFER_FAIL);
					}
				} else if (mnPlayState != PlayState.BUFFER_FAIL) {
					if (mnQuality == Params.BITRATE_LOW_QUALITY) {
						mnPlayBuffer = PLAY_LOW_QUALITY_BUFFER_SIZE;
						mnPauseBuffer = PAUSE_LOW_QUALITY_BUFFER_SIZE;
					} else {
						mnPlayBuffer = PLAY_HIGH_QUALITY_BUFFER_SIZE;
						mnPauseBuffer = PAUSE_HIGH_QUALITY_BUFFER_SIZE;
					}

					switch (mnPlayState) {
					case PlayState.BUFFER: {
						if (mnTotal <= mnPlayBuffer) {
							prepareMusic(nID, nMsgID);
						} else {
							if (mnCurrent >= mnPlayBuffer) {
								prepareMusic(nID, nMsgID);
							}
						}
					}
						break;

					case PlayState.BUFFER_PAUSE: {
						if ((mnCurrent >= mnTotal) || ((mnCurrent - mnPause) > mnPauseBuffer)) {
							prepareMusic(nID, nMsgID);
						}
					}
						break;
					}

					if (mnDuration > 0) {
						mnBufferDuration = (int) ((float) mnCurrent / (float) mnTotal * (float) (mnDuration));
					}
				}
			}
		} catch (Exception e) {
		}
	}

	/*
	 * 预处理文件
	 */
	private void prepareMusic(int nID, int msgID) {
		FileInputStream fileInputStream = null;
		boolean bStatus = false;
		int nCounter = 0;

		mnPlayState = PlayState.BUFFER_PREPARE;

		try {
			mFile = new File(PoolDownload.getMusic(nID, msgID));

			fileInputStream = new FileInputStream(mFile);

			// refreshMediaPlayer();
			// enableVisualizer(false);

			mMediaPlayer.reset();

			bStatus = false;

			for (nCounter = 0; nCounter < 200; ++nCounter) {
				try {
					mMediaPlayer.setDataSource(fileInputStream.getFD());

					bStatus = true;

					break;
				} catch (Exception e) {
				}

				try {
					Thread.sleep(50);
				} catch (Exception e) {
				}
			}

			LogUtils.e(TAG, "setDataSource bStatus: " + (bStatus ? "true" : "false") + " nCounter: " + nCounter);

			if (bStatus) {
				mMediaPlayer.prepare();
			} else {
				mnPlayState = PlayState.BUFFER;
			}
		} catch (Exception e) {
		}

		if (!bStatus) {
			mnPlayState = PlayState.BUFFER;
		}
	}

	/*
	 * 初始化播放器
	 */
	private void refreshMediaPlayer() {
		MediaPlayer mediaPlayer = null;

		try {
			mediaPlayer = mMediaPlayer;

			if (mediaPlayer != null) {
				// enableVisualizer(false);

				mMediaPlayer.reset();
				mnPlayState = PlayState.STOP;

				mnBufferDuration = 0;

				updatePlayerState();
			}

			mnPlayState = PlayState.STOP;

			mMediaPlayer = new MediaPlayer();

			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnInfoListener(this);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnSeekCompleteListener(this);

			if (mediaPlayer != null) {
				mediaPlayer.release();
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "PlayerCore - refreshMediaPlayer: " + e.getMessage());
		}
	}

	/*
	 * 强制停止下载
	 */
	public void cancel(int nMusicID) {
		if (mDownLoadInfo.nMusicID == nMusicID) {
			mnCancelState = CancelState.CANCEL;
		}
	}

	/*
	 * 强制删除
	 */
	public void delete(int nMusicID) {
		if (mDownLoadInfo.nMusicID == nMusicID) {
			mnCancelState = CancelState.DELETE;
		}
	}

	/*
	 * 下载歌曲
	 */
	void updateCurrentPosition() {
		int nCurrentPosition;

		while (!mbExit) {
			try {
				if (mnBufferDuration - mnCurrentPosition >= 20000 && mnPlayState == PlayState.BUFFER_PAUSE) {
					setPlayState(PlayState.BUFFER_PLAY);
				}

				if (((PlayState.PLAY == mnPlayState) || (PlayState.PAUSE == mnPlayState)) && (mnDuration > 0)) {
					nCurrentPosition = mMediaPlayer.getCurrentPosition();

					if (((nCurrentPosition != mnDuration) || (mnBufferDuration >= mnDuration)) && (nCurrentPosition <= mnDuration)) {
						mnCurrentPosition = nCurrentPosition;
					}
				}
			} catch (Exception e) {
			}

			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	}

	/*
	 * 广播下载状态
	 */
	/*
	 * private void sendDownloadState(DownLoadInfo downLoadInfo) { Intent intent
	 * = null; try { intent = new Intent(); intent.setAction(ACTION_DOWNLOAD);
	 * intent.putExtra(ACTION_DOWNLOAD_MUSICID, downLoadInfo.nMusicID);
	 * intent.putExtra(ACTION_DOWNLOAD_TOTAL, downLoadInfo.lSize);
	 * intent.putExtra(ACTION_DOWNLOAD_CURRENT, downLoadInfo.nProgress);
	 * intent.putExtra(ACTION_DOWNLOAD_STATE, downLoadInfo.nDownloadState);
	 * intent.putExtra(ACTION_DOWNLOAD_RESULT, downLoadInfo.nDownLoadResult);
	 * mContext.sendBroadcast(intent); updateDownload(downLoadInfo);
	 * NotificationUtil.updateDownloadNotification(downLoadInfo.nMusicID,
	 * downLoadInfo.nDownLoadResult, downLoadInfo.lSize,
	 * downLoadInfo.nProgress); } catch (Exception e) { } }
	 */

	/*
	 * 设置下载状态
	 */
	private void setDownloadInfo(DownLoadInfo downLoadInfo) {
		synchronized (mlistDownloadInfo) {
			for (int i = 0; i < mlistDownloadInfo.size(); ++i) {
				if (mlistDownloadInfo.get(i).nMusicID == downLoadInfo.nMusicID) {
					DownLoadInfo.clone(mlistDownloadInfo.get(i), downLoadInfo);

					break;
				}
			}
		}
	}

	/*
	 * 获取下载信息
	 */
	public DownLoadInfo getDownLoadInfo() {
		return mDownLoadInfo;
	}

	/*
	 * 中止下载
	 */
	public void forceCancel() {
		cancel(mDownLoadInfo.nMusicID);
	}

	/*
	 * 获取播放状态
	 */
	public int getPlayState() {
		int nPlayState;

		if ((mnPlayState == PlayState.BUFFER_PAUSE) || (mnPlayState == PlayState.BUFFER_PLAY) || (mnPlayState == PlayState.PREPARE)
				|| (mnPlayState == PlayState.BUFFER_PREPARE)) {
			nPlayState = PlayState.PLAY;
		} else if (mnPlayState == PlayState.BUFFER_FAIL) {
			nPlayState = PlayState.STOP;
		} else {
			nPlayState = mnPlayState;
		}

		return nPlayState;
	}

	/*
	 * 退出
	 */
	public void exit() {
		cancel(mDownLoadInfo.nMusicID);
	}

	/*
	 * 波形数据
	 */
	public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
		synchronized (mbyFFTData) {
			for (int i = 0; i < fft.length; ++i) {
				mbyFFTData[i] = fft[i];
			}
		}
	}

	/*
	 * 波形数据
	 */
	public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

	}

	/*
	 * 定位完成
	 */
	public void onSeekComplete(MediaPlayer mp) {
		LogUtils.e(TAG, "HttpPlayer -- onSeekComplete mnPlayState: " + mnPlayState);

		if (PlayState.BUFFER_PLAY == mnPlayState) {
			LogUtils.e(TAG, "HttpPlayer -- onSeekComplete mp.getCurrentPosition(): " + mp.getCurrentPosition() + " mnCurrentPosition: " + mnCurrentPosition);

			if (mp.getCurrentPosition() == mnCurrentPosition) {
				setPlayState(PlayState.PLAY);
			} else {
				mnPause = mnCurrent;

				setPlayState(PlayState.BUFFER_PAUSE);
			}
		} else if (PlayState.BUFFER_PAUSE == mnPlayState) {
			setPlayState(PlayState.PLAY);
		}
	}

	/*
	 * 预处理完成
	 */
	public void onPrepared(MediaPlayer mp) {
		LogUtils.e(TAG, "HttpPlayer -- onPrepared");

		mnDuration = mMediaPlayer.getDuration();

		mnBufferDuration = (int) ((float) mnCurrent / (float) mnTotal * (float) (mnDuration));

		// if (mnPlayState == PlayState.BUFFER_PAUSE) {
		setPlayState(PlayState.BUFFER_PLAY);
		// } else {
		// setPlayState(PlayState.PLAY);
		// }
	}

	/*
	 * 出现错误
	 */
	public boolean onError(MediaPlayer mp, int what, int extra) {
		LogUtils.e(TAG, "PlayerCore onError - what: " + what + "extra: " + extra);

		refreshMediaPlayer();

		return true;
	}

	/*
	 * 播放完成
	 */
	public void onCompletion(MediaPlayer mp) {
		LogUtils.e(TAG, "HttpPlayer -- onCompletion mnPlayState: " + mnPlayState);

		if ((mnCurrent > 0) && (mnCurrent >= mnTotal) && (mnPlayState == PlayState.PLAY)) {
			setPlayState(PlayState.STOP);

			Player.finishNext();
		} else if ((mnPlayState != PlayState.BUFFER_PAUSE) && (mnPlayState != PlayState.BUFFER_PLAY)) {
			mnPause = mnCurrent;

			/*
			 * mnCurrentPosition = mp.getCurrentPosition(); mnCurrentPosition =
			 * (mnCurrentPosition > mnBufferDuration) ? mnBufferDuration :
			 * mnCurrentPosition;
			 */

			setPlayState(PlayState.BUFFER_PAUSE);
		}
	}

	/*
	 * 缓冲完成
	 */
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
	}

	/*
	 * 播放信息
	 */
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		LogUtils.e(TAG, "HttpPlayer -- onInfo");
		return false;
	}

	/*
	 * 获取播放位置
	 */
	public int getCurrentPosition() {
		/*
		 * int nCurrentPosition = 0; if ((PlayState.PLAY == mnPlayState) ||
		 * (PlayState.PAUSE == mnPlayState)) { nCurrentPosition =
		 * mMediaPlayer.getCurrentPosition(); if ((nCurrentPosition ==
		 * mnDuration) && (mnBufferDuration < mnDuration)) { nCurrentPosition =
		 * mnCurrentPosition; } } else { nCurrentPosition = mnCurrentPosition; }
		 */

		return mnCurrentPosition;
	}

	/*
	 * 获取缓冲总长度
	 */
	public int getBufferDuration() {
		if ((mnTotal > 0) && (mnCurrent > 0) && (mnDuration > 0)) {
			mnBufferDuration = (int) ((float) mnCurrent / (float) mnTotal * (float) mnDuration);
		}

		return mnBufferDuration;
	}

	/*
	 * 获取总长度
	 */
	public int getDuration() {
		if ((PlayState.PLAY == mnPlayState) || (PlayState.PAUSE == mnPlayState)) {
			mnDuration = mMediaPlayer.getDuration();
		}

		return mnDuration;
	}

	/*
	 * 设置播放位置
	 */
	public void setPosition(int nPosition) {
		if (((PlayState.STOP != mnPlayState) || mMediaPlayer.isPlaying()) && (nPosition < mnBufferDuration)) {
			mMediaPlayer.seekTo(nPosition);
		}

		/*
		 * if (((PlayState.STOP != mnPlayState) || mMediaPlayer.isPlaying()) &&
		 * (nPosition > mnBufferDuration)) {
		 * mMediaPlayer.seekTo(mnBufferDuration); }
		 */
	}

	/*
	 * 强制停止播放
	 */
	public void forceStop() {
		setPlayState(PlayState.STOP);
	}

	/*
	 * 是否播放状态
	 */
	public int isPlaying() {
		return mnPlayState;
	}

	/*
	 * 获取频谱数据
	 */
	public byte[] getFFTData() {
		byte byFFTData[] = null;

		synchronized (mbyFFTData) {
			try {
				byFFTData = new byte[mbyFFTData.length];

				for (int i = 0; i < mbyFFTData.length; ++i) {
					byFFTData[i] = mbyFFTData[i];
				}
			} catch (Exception e) {
				byFFTData = null;
			}
		}

		return byFFTData;
	}

	/*
	 * 销毁
	 */
	public void destroy() {
		mMediaPlayer.release();

		mbExit = true;
	}

	/*
	 * 播放
	 */
	public void play() {
		if ((PlayState.PAUSE == mnPlayState) && (!mMediaPlayer.isPlaying())) {
			setPlayState(PlayState.BUFFER_PLAY);
		} else if (PlayState.BUFFER_FAIL == mnPlayState) {
			addDownload();
		}
	}

	/*
	 * 暂停
	 */
	public void pause() {
		if (((PlayState.PLAY == mnPlayState) && mMediaPlayer.isPlaying()) || (PlayState.BUFFER_PAUSE == mnPlayState)) {
			setPlayState(PlayState.PAUSE);
		}
	}

	/*
	 * 停止
	 */
	public void stop() {
		setPlayState(PlayState.STOP);
	}

	/*
	 * 清除
	 */
	public void clear() {
		setPlayState(PlayState.STOP);
	}

	/*
	 * 播放本地歌曲
	 */
	public void buffer(PlayerMusicInfo musicInfo) {
		try {
			PlayerMusicInfo.clone(mMusicInfo, musicInfo);

			addDownload();

			/*
			 * mMediaPlayer.setOnCompletionListener(this);
			 * mMediaPlayer.setOnBufferingUpdateListener(this);
			 * mMediaPlayer.setOnErrorListener(this);
			 * mMediaPlayer.setOnInfoListener(this);
			 * mMediaPlayer.setOnPreparedListener(this);
			 * mMediaPlayer.setOnSeekCompleteListener(this);
			 */

		} catch (Exception e) {
			setPlayState(PlayState.STOP);

			LogUtils.e(TAG, "MediaPlayerEx.buffer: " + e.getMessage());
		}
	}

	/*
	 * 添加到下载列表
	 */
	private void addDownload() {
		PoolDownloadInfo poolDownloadInfo = new PoolDownloadInfo();

		poolDownloadInfo.nMusicID = mMusicInfo.nMusicID;
		poolDownloadInfo.nMsgID = mMusicInfo.nMsgID;
		poolDownloadInfo.handler = mHandler;
		poolDownloadInfo.strSongName = mMusicInfo.strSongName;
		poolDownloadInfo.strUrl = mMusicInfo.strOnlineHighUrl;

		setPlayState(PlayState.BUFFER);

		PoolDownload.addPoolDownload(poolDownloadInfo);
	}

	/*
	 * 播放器 播放 暂停 停止
	 */
	private void setPlayState(int nPlayState) {
		try {
			switch (nPlayState) {
			case PlayState.BUFFER_FAIL:
			case PlayState.STOP: {
				if (mMediaPlayer.isPlaying() || (PlayState.STOP != mnPlayState)) {
					mMediaPlayer.stop();
					mMediaPlayer.reset();
				}

				mnPlayState = nPlayState;

				mnCurrent = 0;
				mnPause = 0;
				mnCurrentPosition = 0;
				mnBufferDuration = 0;
				mnDuration = 0;
			}
				break;

			case PlayState.PREPARE: {
				if (mMediaPlayer.isPlaying() || (PlayState.STOP != mnPlayState)) {
					mMediaPlayer.stop();
					mMediaPlayer.reset();
				}

				mnPlayState = PlayState.PREPARE;
			}
				break;

			case PlayState.PLAY: {
				mMediaPlayer.start();

				mnPlayState = PlayState.PLAY;

				VolumeUtils.increment();
			}
				break;

			case PlayState.BUFFER_PLAY: {
				mnPlayState = PlayState.BUFFER_PLAY;

				mMediaPlayer.seekTo(mnCurrentPosition);
			}
				break;

			case PlayState.BUFFER: {
				mnPlayState = PlayState.BUFFER;
			}
				break;

			case PlayState.BUFFER_PAUSE: {
				mMediaPlayer.pause();
				mnPlayState = PlayState.BUFFER_PAUSE;
			}
				break;

			case PlayState.PAUSE: {
				mMediaPlayer.pause();
				mnPlayState = PlayState.PAUSE;
			}
				break;

			default: {
				mMediaPlayer.start();
				mnPlayState = PlayState.PLAY;
			}
				break;
			}
		} catch (Exception e) {
			mnPlayState = PlayState.STOP;
		}

		if (PlayState.PLAY == mnPlayState) {
			// enableVisualizer(true);
		} else {
			// enableVisualizer(false);
		}

		if ((mnPlayState != PlayState.BUFFER_PAUSE) || (mnPlayState != PlayState.BUFFER_PLAY)) {
			updatePlayerState();
		}
	}

	/*
	 * 更新播放器状态
	 */
	private void updatePlayerState() {
		Intent intent = null;
		int nPlayState = 0;

		try {
			intent = new Intent();

			if ((mnPlayState == PlayState.BUFFER_PAUSE) || (mnPlayState == PlayState.BUFFER_PLAY) || (mnPlayState == PlayState.PREPARE)
					|| (mnPlayState == PlayState.BUFFER_PREPARE)) {
				nPlayState = PlayState.PLAY;
			} else if (mnPlayState == PlayState.BUFFER_FAIL) {
				nPlayState = PlayState.STOP;
			} else {
				nPlayState = mnPlayState;
			}

			intent.setAction(PlayerManage.ACTION_PLAYER);
			intent.putExtra(PlayerManage.ACTION_PLAYER_STATE, nPlayState);
			intent.putExtra(PlayerManage.ACTION_PLAYER_MUSICID, mMusicInfo.nMusicID);
			intent.putExtra(PlayerManage.ACTION_PLAYER_EVENTID, mMusicInfo.nEventID);
			intent.putExtra(PlayerManage.ACTION_PLAYER_MSGID, mMusicInfo.nMsgID);

			mContext.sendBroadcast(intent);

			// NotificationUtil.updatePlayerNotification(mMusicInfo.strSongName,
			// mnPlayState);
		} catch (Exception e) {
		}
	}

}