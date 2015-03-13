package mobi.dlys.android.familysafer.player;

import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.player.utils.Player;
import mobi.dlys.android.familysafer.player.utils.Player.Params;
import mobi.dlys.android.familysafer.player.utils.Player.PlayState;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.NetUtils;
import mobi.dlys.android.familysafer.utils.VolumeUtils;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

/*
 * 播放器
 */
public class PlayerCore extends PlayerBase implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
		MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
	private static final String TAG = PlayerCore.class.getSimpleName();

	public final static int FFT_SIZE = 128; // FFT大小
	private int mnPlayState = PlayState.STOP; // 播发状态 -1 未播放 0 暂停 1 播放
	private MediaPlayer mMediaPlayer = null; //
	private PlayerMusicInfo mMusicInfo = null;
	private Context mContext = null;
	private byte mbyFFTData[] = null;
	private int mnBufferDuration = 0;

	/*
	 * 构造
	 */
	public PlayerCore(Context context) {
		mContext = context;

		mMusicInfo = new PlayerMusicInfo();
		mbyFFTData = new byte[FFT_SIZE];

		refreshMediaPlayer();
	}

	/*
	 * 初始化播放器
	 */
	private void refreshMediaPlayer() {
		MediaPlayer mediaPlayer = null;

		try {
			mediaPlayer = mMediaPlayer;

			if (mediaPlayer != null) {
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
	 * 是否播放状态
	 */
	public int isPlaying() {
		return mnPlayState;
	}

	/*
	 * 播放器 播放 暂停 停止
	 */
	protected void setPlayState(int nPlayState) {
		try {
			switch (nPlayState) {
			case PlayState.STOP: {
				if ((PlayState.STOP != mnPlayState) || mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
					mMediaPlayer.reset();
				}

				mnBufferDuration = 0;

				mnPlayState = PlayState.STOP;
			}
				break;

			case PlayState.PREPARE: {
				if ((PlayState.STOP != mnPlayState) || mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
					mMediaPlayer.reset();
				}

				mnPlayState = PlayState.PREPARE;
			}
				break;

			case PlayState.PLAY: {
				mMediaPlayer.start();
				mnPlayState = PlayState.PLAY;

				// 歌曲播放淡入淡出
				VolumeUtils.increment();
			}
				break;

			case PlayState.PAUSE: {
				mMediaPlayer.pause();
				mnPlayState = PlayState.PAUSE;
			}
				break;

			case PlayState.BUFFER: {
				if ((PlayState.PLAY == mnPlayState) && mMediaPlayer.isPlaying()) {
					mMediaPlayer.pause();
				}

				mnPlayState = PlayState.BUFFER;
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

		if (mnPlayState == PlayState.PLAY) {
			// enableVisualizer(true);
		} else {
			// enableVisualizer(false);
		}

		updatePlayerState();
	}

	/*
	 * 更新播放器状态
	 */
	private void updatePlayerState() {
		Intent intent = null;

		try {
			intent = new Intent();

			intent.setAction(PlayerManage.ACTION_PLAYER);
			intent.putExtra(PlayerManage.ACTION_PLAYER_STATE, mnPlayState);
			intent.putExtra(PlayerManage.ACTION_PLAYER_MUSICID, mMusicInfo.nMusicID);
			intent.putExtra(PlayerManage.ACTION_PLAYER_EVENTID, mMusicInfo.nEventID);
			intent.putExtra(PlayerManage.ACTION_PLAYER_MSGID, mMusicInfo.nMsgID);

			mContext.sendBroadcast(intent);

			// NotificationUtil.updatePlayerNotification(mMusicInfo.strSongName,
			// mnPlayState);
		} catch (Exception e) {
		}
	}

	/*
	 * 获取播放状态
	 */
	public int getPlayState() {
		return mnPlayState;
	}

	/*
	 * 播放
	 */
	public void play() {
		if ((PlayState.PAUSE == mnPlayState) && (!mMediaPlayer.isPlaying())) {
			setPlayState(PlayState.PLAY);
		}
	}

	/*
	 * 暂停
	 */
	public void pause() {
		if (mMediaPlayer.isPlaying()) {
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
	 * 获取总长度
	 */
	public int getDuration() {
		int nRet = 0;

		if ((PlayState.PLAY == mnPlayState) || (PlayState.PAUSE == mnPlayState)) {
			nRet = mMediaPlayer.getDuration();
		}

		return nRet;
	}

	/*
	 * 获取播放位置
	 */
	public int getCurrentPosition() {
		int nRet = 0;

		if (PlayState.PREPARE == mnPlayState) {
			nRet = 0;
		} else if ((PlayState.PLAY == mnPlayState) || (PlayState.PAUSE == mnPlayState)) {
			nRet = mMediaPlayer.getCurrentPosition();
		}

		return nRet;
	}

	/*
	 * 设置播放位置
	 */
	public void setPosition(int nPosition) {
		if ((PlayState.STOP != mnPlayState) || mMediaPlayer.isPlaying()) {
			mMediaPlayer.seekTo(nPosition);
		}
	}

	/*
	 * 强制停止播放
	 */
	public void forceStop() {
		setPlayState(PlayState.STOP);
	}

	/*
	 * 显示信息
	 */
	protected void toast(final String strMessage) {
		Handler handle = null;

		try {
			handle = new Handler(Looper.getMainLooper());

			handle.post(new Runnable() {
				public void run() {
				}
			});
		} catch (Exception e) {
		}
	}

	/*
	 * 获取缓冲总长度
	 */
	public int getBufferDuration() {
		return mnBufferDuration;
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
	 * 缓冲播放
	 */
	public void buffer(PlayerMusicInfo musicInfo) {
		try {
			PlayerMusicInfo.clone(mMusicInfo, musicInfo);

			if (mMusicInfo.nLocation == Params.LOCAL) {
				setPlayState(PlayState.PREPARE);

				if (FileUtils.exists(musicInfo.strFullPath)) {
					mMediaPlayer.reset();
					mMediaPlayer.setDataSource(musicInfo.strFullPath);
					mMediaPlayer.prepare();
				} else {
					setPlayState(PlayState.STOP);

					// toast(App.getInstance().getString(R.string.MusicNotFound,
					// mMusicInfo.strSongName));
				}
			} else {
				// 没有网络提示用户
				if (NetUtils.NET_NOT_CONNECTION == NetUtils.checkNet()) {
					setPlayState(PlayState.STOP);
					YSToast.showToast(mContext, "没有缓冲文件");

					try {
						Thread.sleep(102);
					} catch (Exception e) {
					}
				} else {
					setPlayState(PlayState.BUFFER);

					mMediaPlayer.reset();
					mMediaPlayer.setDataSource(NetUtils.isWifiEnable() ? musicInfo.strOnlineHighUrl : musicInfo.strOnlineLowUrl);
					mMediaPlayer.prepare();
				}
			}
		} catch (Exception e) {
			setPlayState(PlayState.STOP);

			LogUtils.e("com.aiting.music", "LocalPlayer buffer Exception: " + e.getMessage());
		}
	}

	/*
	 * 销毁
	 */
	public void destroy() {
	}

	public void onSeekComplete(MediaPlayer mp) {
	}

	public void onPrepared(MediaPlayer mp) {
		mnBufferDuration = mMediaPlayer.getDuration();

		setPlayState(PlayState.PLAY);
	}

	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		return false;
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		LogUtils.e(TAG, "PlayerCore onError - what: " + what + "extra: " + extra);

		refreshMediaPlayer();

		return true;
	}

	public void onCompletion(MediaPlayer mp) {
		setPlayState(PlayState.STOP);

		Player.finishNext();
	}

	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		mnBufferDuration = (int) ((float) mMediaPlayer.getDuration() * ((float) percent / 100.00f));
	}
}